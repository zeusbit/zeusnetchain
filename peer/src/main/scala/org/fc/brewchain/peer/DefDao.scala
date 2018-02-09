package org.fc.brewchain.peer

import scala.beans.BeanProperty


import com.google.protobuf.Message

import lombok.extern.slf4j.Slf4j
import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.commons.SessionModules
import onight.oapi.scala.traits.OLog
import onight.osgi.annotation.NActorProvider
import onight.tfw.ojpa.api.DomainDaoSupport
import onight.tfw.ojpa.api.annotations.StoreDAO
import onight.tfw.oparam.api.OParam
import onight.tfw.otransio.api.IPacketSender
import onight.tfw.otransio.api.PSender
import org.fc.brewchain.peer.pbgens.Peer.PModule
import org.fc.brewchain.poc.pbgens.Cebpoc.PPOCModule
import org.fc.brewchain.baas.pbgens.Baas.PBModule
import org.fc.brewchain.fbs.pbgens.Fbs.PFBSModule

abstract class PSM[T <: Message] extends SessionModules[T] with PBUtils with OLog {
  override def getModule: String = PModule.PEE.name()
}

abstract class PSMPOC[T <: Message] extends SessionModules[T] with PBUtils with OLog {
  override def getModule: String = PPOCModule.POC.name()
}

abstract class PSMBaas[T <: Message] extends SessionModules[T] with PBUtils with OLog {
  override def getModule: String = PBModule.BOS.name()
}

abstract class PSMFBS[T <: Message] extends SessionModules[T] with PBUtils with OLog {
  override def getModule: String = PFBSModule.FBS.name()
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


