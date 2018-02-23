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
import org.fc.brewchain.p22p.Daos
import java.net.URL
import org.fc.brewchain.bcapi.URLHelper
import org.osgi.service.url.URLStreamHandlerService
import onight.tfw.otransio.api.PacketHelper
import scala.collection.mutable.ArrayBuffer
import java.util.HashMap

//投票决定当前的节点
object JoinNetwork extends Runnable with OLog {
  val sameNodes = new HashMap[Integer, LinkNode]();
  val joinedNodes = new HashMap[Integer, LinkNode]();
  def run() = {
    Thread.currentThread().setName("JoinNetwork");
    try {
      val namedNodes = Daos.props.get("org.bc.networks", "tcp://localhost:5100").split(",").map { x =>
        log.debug("x=" + x)
        LinkNode.fromURL(x);
      }.filter { x => !sameNodes.containsKey(x.uri.hashCode()) && !joinedNodes.containsKey(x.uri.hashCode()) };
      if (namedNodes.size > 0) {
        val n = namedNodes(0);
        log.debug("JoinNetwork :Run----Try to Join :MainNet=" + n.uri + ",cur=" + NodeInstance.curnode.uri);
        if (!NodeInstance.curnode.equals(n)) {
          val joinbody = PSJoin.newBuilder().setOp(PSJoin.Operation.MANU_DISCOVER).setMyInfo(LayerNodeTask.currPMNodeInfo);
          log.debug("JoinNetwork :Start to Connect---:" + n.uri);
          MessageSender.sendMessage("JINPZP", joinbody.build(), n, new CallBack[FramePacket] {
            def onSuccess(fp: FramePacket) = {
              log.debug("send JINPZP success:to " + n.uri + ",body=" + fp.getBody)
              val retjoin = PRetJoin.newBuilder().mergeFrom(fp.getBody);
              if (retjoin.getRetCode() == -1) { //same message
                log.debug("get Same Node:" + n);
                sameNodes.put(n.uri.hashCode(), n);
                MessageSender.dropNode(n)
              } else if (retjoin.getRetCode() == 0) {
                joinedNodes.put(n.uri.hashCode(), n);
              }
              log.debug("get nodes:" + retjoin);
            }
            def onFailed(e: java.lang.Exception, fp: FramePacket) {
              log.debug("send JINPZP ERROR " + n.uri + ",e=" + e.getMessage, e)
            }
          });
        } else {
          log.debug("JoinNetwork :Finished ---- Current node is MainNode");
        }
      }
    } catch {
      case e: Throwable =>
        log.debug("JoinNetwork :Error", e);
    } finally {
      log.debug("JoinNetwork :[END]")
    }
  }
  //Scheduler.scheduleWithFixedDelay(new Runnable, initialDelay, delay, unit)
}