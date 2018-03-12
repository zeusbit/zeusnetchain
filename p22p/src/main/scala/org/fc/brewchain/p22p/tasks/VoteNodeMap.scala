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
import java.net.URL
import org.apache.felix.framework.URLHandlers
import org.fc.brewchain.bcapi.URLHelper
import org.fc.brewchain.p22p.action.PMNodeHelper
import org.fc.brewchain.bcapi.crypto.BitMap
import org.fc.brewchain.p22p.pbgens.P22P.PVBase
import org.fc.brewchain.p22p.pbgens.P22P.PBFTStage
import org.fc.brewchain.p22p.node.ViewState

//投票决定当前的节点
object VoteNodeMap extends Runnable with OLog with PMNodeHelper {
  def run() = {
    log.debug("VoteNodeMap :Run----Try to Vote Node Maps");
    Thread.currentThread().setName("VoteNodeMap");
    log.info("CurrentLinkNodes:PendingSize=" + NodeInstance.curnode.pendingNodes.size + ",DirectNodeSize=" + NodeInstance.curnode.directNode.size);
    val vbase = PVBase.newBuilder();
    val vbody = PBVoteNodeIdx.newBuilder();
    vbase.setState(PBFTStage.PRE_PREPARE)
    vbase.setMType(0)
//    vbase.setMaxVid(value)
    vbase.setN(ViewState.nextN())
    vbase.setV(ViewState.curV());
    
    vbase.setFromAddr(NodeInstance.curnode.uri)
    vbase.setFromNodeName(NodeInstance.curnode.name)
    
    
//    vbase.addVoteContents(vbody);
    if(NodeInstance.curnode.node_bits.longValue()==0){
       NodeInstance.curnode.node_bits=NodeInstance.curnode.node_bits.setBit(NodeInstance.curnode.node_idx)
    }
    var bits = NodeInstance.curnode.node_bits;
    
    NodeInstance.curnode.pendingNodes.values.map(n =>
      if (bits.testBit(n.try_node_idx)) {
        log.debug("error in try_node_idx @n=" + n.name + ",try=" + n.try_node_idx + ",bits=" + bits);
      } else { //no pub keys
        bits = bits.setBit(n.try_node_idx);
        vbody.addNodes(toPMNode(n));
      })

    vbody.setNodeBitsEnc(BitMap.hexToMapping(bits))
    
    log.info("vote -- Nodes:" + vbody);
    NodeInstance.forwardMessage("VOTPZP", vbody.build());
    //vbody.setNodeBitsEnc(bits.toString(16));

    Thread.sleep((Math.random() * 10000).asInstanceOf[Int]);

    log.debug("LayerNodeTask:Run-----[END]"); //
  }
  //Scheduler.scheduleWithFixedDelay(new Runnable, initialDelay, delay, unit)
  def main(args: Array[String]): Unit = {
    URLHelper.init()
    //System.setProperty("java.protocol.handler.pkgs", "org.fc.brewchain.bcapi.url");
    println(new URL("tcp://127.0.0.1:5100").getHost);
  }
}