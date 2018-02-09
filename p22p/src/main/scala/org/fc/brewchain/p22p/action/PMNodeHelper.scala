package org.fc.brewchain.p22p.action

import org.fc.brewchain.p22p.node.PNode
import org.fc.brewchain.p22p.node.LinkNode
import org.fc.brewchain.p22p.pbgens.P22P.PMNodeInfo
import org.fc.brewchain.p22p.pbgens.P22P.PMNodeInfoOrBuilder

trait PMNodeHelper {

  def toPMNode(_pn: PNode): PMNodeInfo.Builder = {
    val n = _pn.asInstanceOf[LinkNode]
    PMNodeInfo.newBuilder().setAddress(n.address).setNodeName(n.name).setPort(n.port)
      .setPubKey(n.pub_key).setStartupTime(n.startup_time).setTryNodeIdx(n.try_node_idx)

  }
}