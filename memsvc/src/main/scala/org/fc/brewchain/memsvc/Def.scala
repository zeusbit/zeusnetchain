package org.fc.brewchain.memsvc

import onight.oapi.scala.commons.SessionModules
import com.google.protobuf.Message
import org.fc.brewchain.memsvc.Memsvc.PWModule
import onight.oapi.scala.traits.OLog
import onight.oapi.scala.commons.PBUtils
import onight.osgi.annotation.NActorProvider
import lombok.extern.slf4j.Slf4j
import org.fc.brewchain.memsvc.Memsvc.PSMemReq
import onight.tfw.ojpa.api.annotations.StoreDAO
import scala.beans.BeanProperty
import onight.tfw.oparam.api.OParam
import onight.tfw.ojpa.api.DomainDaoSupport
import org.codehaus.jackson.map.SerializerFactory
import onight.tfw.otransio.api.PSender
import onight.tfw.otransio.api.IPacketSender

abstract class PSM[T <: Message] extends SessionModules[T] with PBUtils with OLog {
  override def getModule: String = PWModule.SVC.name()
}


@NActorProvider
@Slf4j
object Daos extends PSM[Message] {
  @StoreDAO(target = "etcd", daoClass = classOf[OParam])
  @BeanProperty
  var oparam: OParam = null
  def setOparam(daoparam: DomainDaoSupport) {
    if (daoparam != null && daoparam.isInstanceOf[OParam]) {
      oparam = daoparam.asInstanceOf[OParam];
    } else {
      log.warn("cannot set OParam from:" + daoparam);
    }
  }
  
  @BeanProperty
  @PSender
  var pSender:IPacketSender = null;
  
}


