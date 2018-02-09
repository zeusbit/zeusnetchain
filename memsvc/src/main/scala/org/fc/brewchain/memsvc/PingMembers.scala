package org.fc.brewchain.memsvc

import scala.beans.BeanProperty

import org.fc.brewchain.memsvc.Memsvc.PRPeerRes
import org.fc.brewchain.memsvc.Memsvc.PSMemReq
import org.fc.brewchain.memsvc.Memsvc.PSPeerReq
import org.fc.brewchain.memsvc.Memsvc.PWCommand

import onight.oapi.scala.commons.LService
import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.traits.OLog
import onight.tfw.async.CompleteHandler
import onight.tfw.mservice.ThreadContext
import onight.tfw.otransio.api.PackHeader
import onight.tfw.otransio.api.PacketHelper
import onight.tfw.otransio.api.beans.FramePacket
import onight.osgi.annotation.NActorProvider
import lombok.extern.slf4j.Slf4j

@NActorProvider
@Slf4j
object PingMembersCtrl extends PSM[PSPeerReq] {
  override def service = PingMembersService
}

// http://localhost:8000/svc/pbppp.do?fh=VLSTSVC000000J00&bd={}&gcmd=LSTSVC
object PingMembersService extends OLog with PBUtils with LService[PSPeerReq] with MemInfoHelper {

  override def onPBPacket(pack: FramePacket, pbo: PSPeerReq, handler: CompleteHandler) = {
    try {
      var ret = PRPeerRes.newBuilder();
      ret.setRetmsg("SUCCESS").setRetcode(0);

      ThreadContext.setContext("iscluster", true);

      val bpack = PacketHelper.genASyncPBPack("INF", "SVC", PSMemReq.newBuilder().setParams("test").build());
      bpack.putHeader(PackHeader.TTL, "" + Daos.props.get("org.zippo.p2p.ttl", 0));
      bpack.putHeader(PackHeader.WALL, "1");
      Daos.pSender.post(bpack)

      handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()))
    } catch {
      case t: Throwable => { log.error("fato:", t); throw new RuntimeException(t); }
    }
  }

  //  override def getCmds(): Array[String] = Array(PWCommand.LST.name())
  override def cmd: String = PWCommand.PPM.name();

}