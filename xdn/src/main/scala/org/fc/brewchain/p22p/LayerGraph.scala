package org.fc.brewchain.p22p

import scala.beans.BeanProperty
import onight.oapi.scala.traits.OLog
import java.util.concurrent.atomic.AtomicLong
import scala.collection.mutable.Set
import org.fc.brewchain.peerxdn.Scheduler

case class LayerNode(name: String) extends OLog {

  var directNode = Map.empty[String, LayerNode];

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
  def addDNode(node: LayerNode) {
    if (node.name != name)
      directNode = (directNode + (node.name -> node));
  }
  def forwardMessage(msg: String, ranSets: Iterable[String] = Set.empty[String]) {
    log.debug("getMessage:" + msg + ",size=" + ranSets.size + "@" + name);
    if (name != "root")
      CCSet.recv.incrementAndGet();
    if (ranSets.size > 2) {
      val div = getDiv(ranSets.size);
      var i: Int = 1 - 0;
      var ran = (Math.random() * ranSets.size % div._1).asInstanceOf[Int];
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
        //        log.debug("sets==>" + ns._1 + ",leader=" + ns._2._1 + ",flowsize=" + ns._2._2.size);
        directNode.get(ns._2._1) match {
          case Some(node: LayerNode) =>
            CCSet.send.incrementAndGet();
            node.forwardMessage(msg, ns._2._2)
          case None =>
            log.debug("unknow nodeName=" + ns._2._1)
        }
      }
    } else {
      ranSets.map({ nodename =>
        directNode.get(nodename) match {
          case Some(node: LayerNode) =>
            CCSet.send.incrementAndGet();
            node.forwardMessage(msg)
          case None =>
            log.debug("unknow nodeName=" + nodename)
        }
      })
    }

  }
}

object CCSet {
  val recv = new AtomicLong(0);
  val send = new AtomicLong(0);
}

object LayerGraph extends OLog {
  val root = new LayerNode("root");
  def main(args: Array[String]): Unit = {
    for (i <- 1 to 100) {
      root.addDNode(new LayerNode("a" + i));
    }
    root.directNode.map(f => {
      root.directNode.map(f2 => {
        f._2.addDNode(f2._2);
      })
    })
    var start = System.currentTimeMillis();
//    for (i <- 1 to 10) {
//      root.forwardMessage("test", root.directNode.keys)
//    }
    log.debug("cost=" + (System.currentTimeMillis() - start))
    println("cc==recv=" + CCSet.recv.get + ",send=" + CCSet.send.get)
    println("get=" + root.directNode.get("a33"));
  }
}
