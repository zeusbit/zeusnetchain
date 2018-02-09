package dpos.consensus

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


@Component(immediate = true)
@Instantiate(name = "dpos")
@Provides(specifications = CSServiceProvider.class, strategy = "SINGLETON")
@Slf4j
class DPosProvider extends OLog with PBUtils {


  var bundleContext:BundleContext;

  def DPosProvider(btx:BundleContext ){
      bundleContext=btx;

  }
  //
  var module:QoraLikeConsensusModule;


  def safeGet(url_base64: String, groups: String = ""): (String, Long, String) = {
    val addr = EncHelper.hashAddr(groups + url_base64);
    val real_path = "/zippo/dpos/block/" + addr + "/current";
    var vtrade = module.get(real_path); //get current value
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
    val real_path = "/zippo/dpos/block/" + addr + "/current";
    val vote_path = "/zippo/dpos/block/" + addr + "/votes";

    //    var vtrade = module.compareAndSwap(real_path, hash + ",0", null);
    //    if (vtrade != null && vtrade.get != null) return true;
    var vtrade = module.get(real_path); //get current value
    
    vtrade = module.post(vote_path, hash + "==>" + NodeHelper.getCurrNodeID + "==>" + timest)
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