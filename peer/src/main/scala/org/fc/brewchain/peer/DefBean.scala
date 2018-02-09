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


case class Block(
    val LOGIN_ID: String = null
    
);

