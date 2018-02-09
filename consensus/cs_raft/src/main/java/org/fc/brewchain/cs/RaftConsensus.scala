package org.fc.brewchain.cs

import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.traits.OLog
import org.apache.commons.codec.digest.Md5Crypt
import java.lang.Long
import onight.tfw.mservice.ThreadContext
import org.apache.commons.lang3.StringUtils
import java.util.HashMap
import scala.collection.JavaConversions._
import onight.tfw.mservice.NodeHelper
import java.util.HashSet
import sun.security.provider.MD5
import org.fc.brewchain.bcapi.crypto.EncHelper

// http://localhost:8001/pee/pbinf.do?fh=VINFSVC000000J00&bd={}&gcmd=INFSVC

@Component(immediate = true)
@Instantiate(name = "raft_cs")
@Provides(specifications = CSServiceProvider.class, strategy = "SINGLETON")
@Slf4j
class RaftConsensus extends OLog with PBUtils {


  var bundleContext:BundleContext;

  def RaftConsensus(btx:BundleContext ){
      bundleContext=btx;

      oetcd=new EtcdImpl(btx);
  }
  //
  var oetcd;


  def safeGet(url_base64: String, groups: String = ""): (String, Long, String) = {
    val addr = EncHelper.hashAddr(groups + url_base64);
    val real_path = "/zippo/addr/xdn/" + addr + "/current";
    var vtrade = oetcd.get(real_path); //get current value
    if (vtrade != null && vtrade.get != null) {
      val arr = vtrade.get.getValue.split(",");
      if (arr.length > 3)
        return (arr(0), Long.parseLong(arr(2)), arr(3));
      return (arr(0), Long.parseLong(arr(2)), "0000");
    }
    null;
  }
  lazy val vote_count = Daos.props.get("xdn.vote.size", 2)

  def compareAndMake(url_base64: String, hash: String, timest: Long, groups: String = "", extInfo: String = ""): Boolean = {
    val addr = EncHelper.hashAddr(groups + url_base64);
    val real_path = "/zippo/addr/xdn/" + addr + "/current";
    val vote_path = "/zippo/addr/xdn/" + addr + "/votes";

    //    var vtrade = oetcd.compareAndSwap(real_path, hash + ",0", null);
    //    if (vtrade != null && vtrade.get != null) return true;
    var vtrade = oetcd.get(real_path); //get current value
    var start = 0L;
    if (vtrade != null && vtrade.get != null) {
      if (StringUtils.equals(hash, vtrade.get.getValue.split(",")(0))) {
        log.debug("same hash return @" + hash + ",@addr=" + addr);
        return true;
      }
      start = Long.parseLong(vtrade.get.getValue.split(",")(1))
    }else{
      var vtradestart = oetcd.get(vote_path); //get current value
      if (vtradestart != null && vtradestart.get != null) {
        start = vtradestart.get.getModifiedIndex;
      }
    }
    vtrade = oetcd.post(vote_path, hash + "==>" + NodeHelper.getCurrNodeID + "==>" + timest)
    if(start==0){
      start = vtrade.get.getModifiedIndex - 2000;
    }
    //post 
    val currentClusterModIdx: Long = vtrade.get.getModifiedIndex;
    var cid: Long = 0;
    try {
      ThreadContext.setContext("sorted", 1);
      //vote
      val vote_map = new HashMap[String, HashSet[String]]();
      val vote_bad_map = new HashMap[String, HashSet[String]]();
      var cc = 10;
      for (cid <- start to currentClusterModIdx) {
        if (vote_map.size() < 5) {
          var ret = oetcd.get(vote_path + "/" + StringUtils.leftPad("" + cid, 20, "0")) //get all pending
          if (ret == null || ret.get == null) {
            ret = oetcd.get(vote_path + "/" + cid) //get all pending
          }

          if (ret != null && ret.get != null) {
            log.debug("pending found:" + ret.get + ",@url=" + url_base64 + ",cc=" + cc + ",mapsize" + vote_map.size());
            val penv = ret.get.getValue.split("==>")
            if (!vote_map.containsKey(penv(0))) {
              val hashset = new HashSet[String];
              hashset.add(penv(1));
              vote_map.put(penv(0), hashset)
            } else { //vote.
              val hashset = vote_map.get(penv(0));
              hashset.add(penv(1))
              vote_map.put(penv(0), hashset)
            }
          }
        }
      }
      vote_map.filter(_._2.size() < vote_count).map(kv => {
        log.debug("cannot get converge:" + addr + ",hash=" + kv._1 + ",vote_nodes=" + kv._2 + "," + timest + "," + extInfo);
        var bad_set = vote_bad_map.get(addr);
        if (bad_set == null) {
          bad_set = new HashSet[String];
          vote_bad_map.put(addr, bad_set);
        }
        bad_set.add(kv._1);
      })
      vote_map.filter(_._2.size() >= vote_count).map(kv => {
        log.debug("get converge:" + addr + ",hash=" + hash + ",vote_nodes=" + kv._2 + "," + timest + "," + extInfo);
        oetcd.put(real_path, hash + "," + (currentClusterModIdx + 2) + "," + timest + "," + extInfo);
        oetcd.deleteDir(vote_path)
      })
      vote_bad_map.filter(_._2.size() >= vote_count).map(kv => {
        log.debug("remove bad hash for addr=:" + addr + ",hash=" + kv._2);
        oetcd.deleteDir(vote_path)
      })
      return true;
    } catch {
      case t: Throwable =>
        log.error("error In Fetch:" + t);
        false
    } finally {
      ThreadContext.cleanContext()
    }

    false;
  }

  //fetch 
}