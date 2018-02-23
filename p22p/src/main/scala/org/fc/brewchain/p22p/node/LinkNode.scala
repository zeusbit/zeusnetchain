package org.fc.brewchain.p22p.node

import org.fc.brewchain.p22p.exception.NodeInfoDuplicated
import java.net.URL
import org.apache.commons.lang3.StringUtils
import onight.tfw.mservice.NodeHelper

class LinkNode(_protocol: String, name: String, _address: String, _port: Int, _startup_time: Long = System.currentTimeMillis(), //
  _pub_key: String = null, _try_node_idx: Int = NodeHelper.getCurrNodeIdx, _node_idx: Int = 0) //
    extends PNode(name) {

  var pendingNodes = Map.empty[String, LinkNode];
  val protocol: String = _protocol;
  val address: String = _address; //地址
  val port: Int = _port; //端口
  val startup_time = _startup_time; //启动时间
  val pub_key = _pub_key; //该节点的公钥
  val try_node_idx = _try_node_idx; //节点的随机id
  val node_idx = _node_idx; //全网确定之后的节点id
  val uri = protocol + "://" + address + ":" + port;

  def addPendingNode(node: LinkNode) {
    this.synchronized {
      if (node.name == name) {
        throw new NodeInfoDuplicated("same node with currnt node" + node.name + "@" + name);
      }
      if (directNode.contains(node.name)) {
        throw new NodeInfoDuplicated("directNode exists Pending name=" + node.name + "@" + name);
      }
      pendingNodes = (pendingNodes + (node.name -> node));
      log.debug("addpending:"+pendingNodes.size+",p="+pendingNodes)
    }
  }
  def isLocal() = (NodeInstance.curnode == this)

  override def toString(): String = {
    "LinkNode(" + uri + "," + startup_time + "," + pub_key + "," + try_node_idx + "," + node_idx + ",)@" + this.hashCode()
  }
  override def processMessage(msg: String, from: String) = {
    if (isLocal()) {
      log.debug("proc Local message");
    } else {
      log.debug("need to Send Message");

    }
  }
  def equals(v: LinkNode): Boolean = {
    StringUtils.equals(v.uri, uri)
  }
}

object LinkNode {
  def fromURL(url: String): LinkNode = {
    val u = new URL(url);
    val n = new LinkNode(u.getProtocol, u.getHost, u.getHost, u.getPort, 0)
    n
  }
}



