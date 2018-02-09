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
import org.fc.brewchain.memsvc.Memsvc.PSPeerQuery
import scala.collection.JavaConversions._
import org.fc.brewchain.bcapi.MemberSvcInfo
import org.fc.brewchain.memsvc.Memsvc.PRetPeerQuery

@NActorProvider
@Slf4j
object PeerList extends PSM[PSPeerQuery] {
  override def service = PeerListService
}

//http://localhost:8000/svc/pbpls.do?fh=VPLSSVC000000J00&bd={}&gcmd=LSTSVC
object PeerListService extends OLog with PBUtils with LService[PSPeerQuery] with MemInfoHelper {
  val jsonse = SerializerFactory.getSerializer(SerializerFactory.SERIALIZER_JSON);

  override def onPBPacket(pack: FramePacket, pbo: PSPeerQuery, handler: CompleteHandler) = {
    try {
      var ret = PRetPeerQuery.newBuilder();
      ret.setRetmsg("SUCCESS").setRetcode(0);
      val inresult = Daos.oparam.get("/zippo/peers/");
      if (inresult == null || inresult.get == null || inresult.get.getNodes == null) {
        ret.setRetmsg("Failed:Member Tree Not FOUND:").setRetcode(-1);
      } else {
        inresult.get.getNodes.map { x =>
          try {
            val m = JsonSerializer.getInstance().deserialize(x.getValue(), classOf[MemberSvcInfo]);
            m
          } catch {
            case t: Throwable => { null }
          }
        }.filter { _ != null }.filter { m =>
          (StringUtils.isBlank(pbo.getAddr) || m.getOutaddr.matches(pbo.getAddr)) &&
            (StringUtils.isBlank(pbo.getNodeid) || m.getNodeid.matches(pbo.getNodeid)) &&
            (StringUtils.isBlank(pbo.getPort) || (m.getOutport+"").matches(pbo.getPort)) && 
            (StringUtils.isBlank(pbo.getOrg) || m.getOrg!=null&&m.getOrg.matches(pbo.getNodeid)) 
        }
          .map { m =>
            PMPeer.newBuilder().setNodeid(m.getNodeid).setMaxconn(m.getMaxconn).setPeeraddr(m.getOutaddr).setPeerport(m.getOutport)
              .setRole(m.getRole).setPeerid(m.getNodeid).setPeertoken(m.getToken).setAuditstatus(m.getAuditstatus).build();
          }.map {
            ret.addPeer(_);
          }
      }

      handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()))
    } catch {
      case t: Throwable => { log.error("fato:", t); throw new RuntimeException(t); }
    }
  }

  //  override def getCmds(): Array[String] = Array(PWCommand.LST.name())
  override def cmd: String = PWCommand.PLS.name();

}