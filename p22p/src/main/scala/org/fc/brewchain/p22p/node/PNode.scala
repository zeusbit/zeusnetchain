package org.fc.brewchain.p22p.node

import onight.oapi.scala.traits.OLog
import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable.Set
import java.math.BigInteger
import org.fc.brewchain.p22p.exception.NodeInfoDuplicated
import onight.tfw.mservice.NodeHelper
import com.google.protobuf.MessageOrBuilder
import com.google.protobuf.Message

class PNode(_name: String, _bit_idx: Int) extends OLog {
  val name = _name;
  var directNode = Map.empty[String, PNode];
  var directNodeByIdx = Map.empty[Int, PNode];
  var node_bits = new BigInteger("0");

  var bits_count = 32; //32*8=256，默认最大网络承载量是256台机器
  var node_idx: Int = _bit_idx;
  val counter = CCSet();
  def getDiv(n: Int): (Int, Int) = {
    val d: Int = Math.sqrt(n.asInstanceOf[Double]).asInstanceOf[Int];
    var i = d;
    for (i <- d until 1 by -1) {
      if (n % i == 0) {
        //        log.debug("d=" + d + "==>" + (i, n / i));
        return (i, n / i);
      }
    }
    //    log.debug("d.1=" + d + "==>" + (1, n));
    (1, n);
  }
  def nodeByName(name: String) = directNode.get(name);
  def nodeByIdx(idx: Int) = directNodeByIdx.get(idx);

  def addDNode(node: PNode) {
    if (node.name == name) {
      return ;
    }
    if (!directNode.contains(node.name)) {
      directNode = (directNode + (node.name -> node));
      if (node.node_idx > 0) {
        node_bits.setBit(node.node_idx);
        directNodeByIdx = (directNodeByIdx + (node.node_idx -> node));
      }
    } else {
      throw new NodeInfoDuplicated("name=" + node.name + ",bits=" + node_bits + "@" + name + "/" + node_idx);
    }
  }
  def getRand() = Math.random()

  def processMessage(gcmd: String, msg: Message, from: PNode) = {
    log.debug("procMessage:" + msg + ",size=" + "@" + name + ",from=" + from);
  }

  def forwardMessage(gcmd: String, msg: Message, ranSets: Iterable[String] = Set.empty[String], from: PNode = this) {

    if (name != "root") {
      counter.recv.incrementAndGet();
      processMessage(gcmd, msg, from);
    }
    if (ranSets.size > 2) {
      val div = getDiv(ranSets.size);
      var i: Int = 1 - 0;
      var ran = (getRand() * ranSets.size % div._1).asInstanceOf[Int];
      val mapSets = scala.collection.mutable.Map.empty[Int, (String, Set[String])]; //leader==>follow
      ranSets.filter({ nodename =>
        i = i + 1;
        var setid = (i % div._1);
        mapSets.get(setid) match {
          case Some(v: (String, Set[String])) =>
            if (v._1 != name) {
              v._2.add(nodename);
            }
          case None =>
            mapSets.put(setid, (nodename, Set.empty[String]));
        }
        setid == ran
      })
      mapSets.map { ns =>
        //                log.debug("sets==>" + ns._1 + ",leader=" + ns._2._1 + ",flowsize=" + ns._2._2.size);
        directNode.get(ns._2._1) match {
          case Some(node: PNode) =>
            counter.send.incrementAndGet();
            node.forwardMessage(gcmd, msg, ns._2._2, from)
          case None =>
            log.debug("unknow nodeName=" + ns._2._1)
        }
      }
    } else {
      ranSets.map({ nodename =>
        directNode.get(nodename) match {
          case Some(node: PNode) =>
            counter.send.incrementAndGet();
            node.forwardMessage(gcmd, msg, Set.empty, from)
          case None =>
            log.debug("unknow nodeName=" + nodename)
        }
      })
    }

  }
}

case class CCSet(
  recv: AtomicLong = new AtomicLong(0),
  send: AtomicLong = new AtomicLong(0))



