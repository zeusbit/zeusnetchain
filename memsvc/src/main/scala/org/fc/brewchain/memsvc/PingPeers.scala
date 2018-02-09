package org.fc.brewchain.memsvc

import onight.osgi.annotation.NActorProvider
import onight.tfw.otransio.api.beans.FramePacket
import org.fc.brewchain.memsvc.Memsvc.PSMemReq
import onight.tfw.async.CompleteHandler
import org.fc.brewchain.memsvc.Memsvc.PRMemRes
import onight.tfw.otransio.api.PacketHelper
import org.fc.brewchain.memsvc.Memsvc.PWCommand
import onight.tfw.oparam.api.OParam
import onight.tfw.ojpa.api.annotations.StoreDAO
import lombok.extern.slf4j.Slf4j
import lombok.Setter
import lombok.Getter
import scala.beans.BeanProperty
import onight.tfw.mservice.ThreadContext
import onight.tfw.ojpa.api.DomainDaoSupport
import onight.tfw.outils.serialize.SerializerFactory
import org.fc.brewchain.memsvc.Memsvc.PMMember
import onight.oapi.scala.commons.PBUtils
import onight.tfw.outils.bean.JsonPBUtil
import com.google.protobuf.Message
import scala.collection.mutable.ListBuffer
import java.util.ArrayList
import onight.oapi.scala.traits.OLog
import onight.oapi.scala.commons.LService
import org.fc.brewchain.memsvc.Memsvc.PSPeerReq
import org.fc.brewchain.memsvc.Memsvc.PRPeerRes
import onight.tfw.otransio.api.PackHeader
import org.fc.brewchain.memsvc.Memsvc.PMPeer
import onight.tfw.outils.serialize.UUIDGenerator
import org.fc.brewchain.bcapi.PeerNodeInfo
import org.apache.commons.lang3.StringUtils
import onight.tfw.outils.serialize.JsonSerializer

@NActorProvider
@Slf4j
object PingPeersCtrl extends PSM[PSPeerReq] {
  override def service = PingPeersService
}

// http://localhost:8000/svc/pbppp.do?fh=VLSTSVC000000J00&bd={}&gcmd=LSTSVC
object PingPeersService extends OLog with PBUtils with LService[PSPeerReq] with MemInfoHelper {

  override def onPBPacket(pack: FramePacket, pbo: PSPeerReq, handler: CompleteHandler) = {
    try {
      var ret = PRPeerRes.newBuilder();
      ret.setRetmsg("SUCCESS").setRetcode(0);

      ThreadContext.setContext("iscluster", true);

      val bpack = PacketHelper.genASyncPBPack("INF", "PEE", PSMemReq.newBuilder().setParams("test").build());
      bpack.putHeader(PackHeader.TTL, "" + Daos.props.get("org.zippo.p2p.ttl", 0));
      bpack.putHeader(PackHeader.WALL, "1");
      Daos.pSender.post(bpack)

      handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()))
    } catch {
      case t: Throwable => { log.error("fato:", t); throw new RuntimeException(t); }
    }
  }

  //  override def getCmds(): Array[String] = Array(PWCommand.LST.name())
  override def cmd: String = PWCommand.PPP.name();

}