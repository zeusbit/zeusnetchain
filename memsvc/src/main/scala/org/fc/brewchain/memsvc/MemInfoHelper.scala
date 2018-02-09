package org.fc.brewchain.memsvc

import scala.beans.BeanProperty

import org.fc.brewchain.bcapi.MemberSvcInfo
import org.fc.brewchain.memsvc.Memsvc.PMPeer

import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.commons.SessionModules
import onight.oapi.scala.traits.OLog
import onight.tfw.outils.serialize.JsonSerializer

trait MemInfoHelper extends PBUtils {
  def peer2NodeInfo(peer: PMPeer, token: String,auditstatus:String="init"): String = {
    val info = new MemberSvcInfo();
    info.setNodeid(peer.getNodeid);
    info.setOutaddr(peer.getPeeraddr);
    info.setOutport(peer.getPeerport);
    info.setHealthy("UP")
    info.setCoreconn(peer.getCoreconn);
    info.setMaxconn(peer.getMaxconn);
    info.setToken(token);
    info.setAuditstatus(auditstatus)
    info.setRole("peernode");
    JsonSerializer.formatToString(info);

  }
}
