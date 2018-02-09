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

@NActorProvider
@Slf4j
object MemCtrl extends PSM[PSMemReq] {
  override def service = MemCtrlService
}

// http://localhost:8001/svc/pblst.do?fh=VLSTSVC000000J00&bd={}&gcmd=LSTSVC
object MemCtrlService extends OLog with PBUtils with LService[PSMemReq] {
  val jsonse = SerializerFactory.getSerializer(SerializerFactory.SERIALIZER_JSON);

  class Factory(objs: ListBuffer[PMMember.Builder]) extends JsonPBUtil.BuilderFactory {
    def getBuilder: Message.Builder = {
      val nb = PMMember.newBuilder();
      objs += nb;
      nb
    }
  }

  override def onPBPacket(pack: FramePacket, pbo: PSMemReq, handler: CompleteHandler) = {
    try {
      var ret = PRMemRes.newBuilder();
      ThreadContext.setContext("iscluster", true);
      val health: String = Daos.oparam.getHealth;
      var members = new ListBuffer[PMMember.Builder]();
      JsonPBUtil.json2PBArrayS(health, new Factory(members));
      members.map { x => ret.addMembers(x.build()) }
      ret.setRetmsg("SUCCESS").setRetcode(0);
      handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()))
    } catch {
      case t: Throwable => { log.error("fato:", t); throw new RuntimeException(t); }
    }finally{
      ThreadContext.cleanContext();
    }
  }

  //  override def getCmds(): Array[String] = Array(PWCommand.LST.name())
  override def cmd: String = PWCommand.LST.name();

}