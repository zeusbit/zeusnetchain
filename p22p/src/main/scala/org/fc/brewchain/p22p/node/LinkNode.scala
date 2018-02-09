package org.fc.brewchain.p22p.node

import org.fc.brewchain.p22p.exception.NodeInfoDuplicated

class LinkNode(_protocol: String, name: String, _address: String, _port: Int, _startup_time: Long = System.currentTimeMillis(), //
  _pub_key: String = null, _try_node_idx: Int = 0, _node_idx: Int = 0) //
    extends PNode(name) {

  var pendingNodes = Map.empty[String, LinkNode];
  val protocal: String = _protocol;
  val address: String = _address; //地址
  val port: Int = _port; //端口
  val startup_time = _startup_time; //启动时间
  val pub_key = _pub_key; //该节点的公钥
  val try_node_idx = _try_node_idx; //节点的随机id
  val node_idx = _node_idx; //全网确定之后的节点id
  val uri = protocal + "://" + address + ":" + port;

  def addPendingNode(node: LinkNode) {
    this.synchronized {
      if (node.name == name) {
        return ;
      }
      if (directNode.contains(node.name)) {
        throw new NodeInfoDuplicated("directNode exists Pending name=" + node.name + "@" + name);
      }
      pendingNodes = (pendingNodes + (node.name -> node));
    }
  }
  def isLocal() = (NodeInstance.curnode == this)

  override def processMessage(msg: String, from: String) = {
    if (isLocal()) {
      log.debug("proc Local message");
    } else {
      log.debug("need to Send Message");

    }
  }
}



