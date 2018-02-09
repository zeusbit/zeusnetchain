package org.fc.brewchain.xdn

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import org.fc.brewchain.bcapi.crypto.EncHelper
import lombok.extern.slf4j.Slf4j
import onight.oapi.scala.commons.LService
import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.traits.OLog
import onight.osgi.annotation.NActorProvider
import onight.tfw.async.CompleteHandler
import onight.tfw.otransio.api.PacketHelper
import onight.tfw.otransio.api.beans.FramePacket
import org.fc.brewchain.bcapi.exception.FBSException
import org.apache.commons.lang3.StringUtils
import java.util.HashSet
import onight.tfw.outils.serialize.UUIDGenerator
import scala.collection.JavaConversions._
import org.apache.commons.codec.binary.Base64
import org.fc.brewchain.p22p.pbgens.P22P.PSJoin
import org.fc.brewchain.p22p.pbgens.P22P.PRetJoin
import org.fc.brewchain.p22p.PSMPZP
import org.fc.brewchain.p22p.pbgens.P22P.PCommand
import org.fc.brewchain.p22p.node.NodeInstance
import org.fc.brewchain.p22p.node.LinkNode
import java.net.URL
import org.fc.brewchain.p22p.pbgens.P22P.PMNodeInfo
import org.fc.brewchain.p22p.action.PMNodeHelper

@NActorProvider
@Slf4j
object PZPNodeJoin extends PSMPZP[PSJoin] {
  override def service = PZPNodeJoinService
}

//
// http://localhost:8000/fbs/xdn/pbget.do?bd=
object PZPNodeJoinService extends OLog with PBUtils with LService[PSJoin] with PMNodeHelper {
  override def onPBPacket(pack: FramePacket, pbo: PSJoin, handler: CompleteHandler) = {
    log.debug("onPBPacket::" + pbo)
    var ret = PRetJoin.newBuilder();
    try {
      //       pbo.getMyInfo.getNodeName
      val from = pbo.getMyInfo;
      if (pbo.getOp == PSJoin.Operation.MANU_DISCOVER) {
        val _urlcheck = new URL(from.getProtocol + "://" + from.getAddress + ":" + from.getPort)
        NodeInstance.curnode.addPendingNode(new LinkNode(from.getProtocol, from.getNodeName, from.getAddress, // 
          from.getPort))
          
      } else if (pbo.getOp == PSJoin.Operation.NODE_CONNECT) {
        NodeInstance.curnode.addPendingNode(new LinkNode(from.getProtocol, from.getNodeName, from.getAddress, // 
          from.getPort, from.getStartupTime, from.getPubKey, from.getTryNodeIdx, from.getNodeIdx))

        ret.addNodes(toPMNode(NodeInstance.curnode));
        
        NodeInstance.curnode.directNode.mapValues { _pn =>
          ret.addNodes(toPMNode(_pn));
        }
      }
    } catch {
      case e: FBSException => {
        ret.clear()
      }
      case t: Throwable => {
        log.error("error:", t);
        ret.clear()
      }
    } finally {
      handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()))
    }
  }
  //  override def getCmds(): Array[String] = Array(PWCommand.LST.name())
  override def cmd: String = PCommand.JIN.name();
}
