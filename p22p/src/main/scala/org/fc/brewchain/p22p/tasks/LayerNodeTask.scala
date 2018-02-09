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
object LayerNodeTask extends Runnable with OLog {

  def initTask() {
    Scheduler.scheduleWithFixedDelay(LayerNodeTask, 100, 10, TimeUnit.SECONDS)
  }
  lazy val currPMNodeInfo = PMNodeInfo.newBuilder().setAddress(NodeInstance.curnode.address) //
    .setNodeName(NodeInstance.curnode.name).setPort(NodeInstance.curnode.port)
    .setPubKey(NodeInstance.curnode.pub_key).setStartupTime(NodeInstance.curnode.startup_time).setTryNodeIdx(NodeInstance.curnode.try_node_idx)
    .setNodeIdx(NodeInstance.curnode.node_idx);

  def run() = {
    log.debug("LayerNodeTask:Run----Try to Vote Node Maps");
    Thread.currentThread().setName("LayerNodeTask");
    log.info("CurrentLinkNodes:PendingSize=" + NodeInstance.curnode.pendingNodes.size + ",DirectNodeSize=" + NodeInstance.curnode.directNode.size);
    val vbody = PBVoteNodeIdx.newBuilder();
    var bits = NodeInstance.curnode.node_bits;
    NodeInstance.curnode.pendingNodes.values.map(n =>
      if (StringUtils.isNotBlank(n.pub_key)) { //get a new one
        if (bits.testBit(n.try_node_idx)) {
          log.debug("error in try_node_idx @n=" + n.name + ",try=" + n.try_node_idx + ",bits=" + bits);
        } else {
          bits = bits.setBit(n.try_node_idx);
          val b = PMNodeInfo.newBuilder().setAddress(n.address).setNodeName(n.name).setPort(n.port)
            .setPubKey(n.pub_key).setStartupTime(n.startup_time).setTryNodeIdx(n.try_node_idx)
          vbody.addNodes(b);
        }
      } else { //no pub keys
        val joinbody = PSJoin.newBuilder().setOp(PSJoin.Operation.NODE_CONNECT).setMyInfo(currPMNodeInfo);
        MessageSender.sendMessage("JINPZP", joinbody.build(), n, new CallBack[FramePacket] {
          def onSuccess(fp: FramePacket) = {
            log.debug("send JINPZP success:to " + n.uri+",body="+fp.getBody)
            val retjoin=PRetJoin.newBuilder().mergeFrom(fp.getBody);
            log.debug("get nodes:"+retjoin);
          }
          def onFailed(e: java.lang.Exception, fp: FramePacket) {
            
          }
        });
      })

    vbody.setNodeBitsEnc(bits.toString(16));

    Thread.sleep((Math.random() * 10000).asInstanceOf[Int]);

    log.debug("LayerNodeTask:Run-----[END]"); //256台机器，
  }
  //Scheduler.scheduleWithFixedDelay(new Runnable, initialDelay, delay, unit)
}