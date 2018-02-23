package org.fc.brewchain.p22p.tasks

import java.util.concurrent.TimeUnit
import onight.oapi.scala.traits.OLog
import org.fc.brewchain.p22p.node.NodeInstance
import org.fc.brewchain.p22p.pbgens.P22P.PMNodeInfo
import org.fc.brewchain.p22p.pbgens.P22P.PBVoteNodeIdx
import java.math.BigInteger
import org.fc.brewchain.p22p.node.LinkNode
import org.apache.commons.lang3.StringUtils
import org.fc.brewchain.p22p.core.MessageSender
import org.fc.brewchain.p22p.pbgens.P22P.PSJoin
import onight.tfw.otransio.api.beans.FramePacket
import onight.tfw.async.CallBack
import org.fc.brewchain.p22p.pbgens.P22P.PRetJoin

//投票决定当前的节点
object LayerNodeTask extends OLog {

  def initTask() {
    Scheduler.scheduleWithFixedDelay(JoinNetwork, 5, 60, TimeUnit.SECONDS)
    Scheduler.scheduleWithFixedDelay(VoteNodeMap, 10, 10, TimeUnit.SECONDS)
  }
  lazy val currPMNodeInfo = PMNodeInfo.newBuilder().setAddress(NodeInstance.curnode.address) //
    .setNodeName(NodeInstance.curnode.name).setPort(NodeInstance.curnode.port)
    .setProtocol("tcp")
    .setPubKey(NodeInstance.curnode.pub_key).setStartupTime(NodeInstance.curnode.startup_time).setTryNodeIdx(NodeInstance.curnode.try_node_idx)
    .setNodeIdx(NodeInstance.curnode.node_idx);

}