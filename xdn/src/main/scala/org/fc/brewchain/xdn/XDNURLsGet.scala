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
import org.fc.brewchain.peerxdn.fbs.DBHelper
import org.fc.brewchain.peerxdn.PSMXDN
import org.fc.brewchain.xdn.pbgens.Xdn.PSURLHashGet
import org.fc.brewchain.xdn.pbgens.Xdn.PRetURLHashGet
import org.fc.brewchain.xdn.pbgens.Xdn.PCommand
import scala.collection.JavaConversions._
import org.apache.commons.codec.binary.Base64
import org.fc.brewchain.xdn.pbgens.Xdn.PMURLPair
import org.fc.brewchain.peerxdn.BCConsensus

@NActorProvider
@Slf4j
object XDNUrlGet extends PSMXDN[PSURLHashGet] {
  override def service = XDNUrlGetService
}

//
// http://localhost:8000/fbs/xdn/pbget.do?bd=
object XDNUrlGetService extends OLog with PBUtils with LService[PSURLHashGet] {
  val exec = new ThreadPoolExecutor(10, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
  override def onPBPacket(pack: FramePacket, pbo: PSURLHashGet, handler: CompleteHandler) = {
    log.debug("onPBPacket::" + pbo)
    var ret = PRetURLHashGet.newBuilder();
    try {
      ret.setCode("0000").setMsg("SUCCESS");
      pbo.getUrlBase64List().map { x =>
        val url = Base64.decodeBase64(x.getBytes); //先不入链
        log.debug("fetch url:" + new String(url));
        val bcret = BCConsensus.safeGet(x,Base64.encodeBase64String(pbo.getGroup().getBytes)+"-"+Base64.encodeBase64String(pbo.getUserid().getBytes)+"-"+Base64.encodeBase64String(pbo.getAppkey().getBytes));
        if (bcret != null) {
          ret.addUrl(
            PMURLPair.newBuilder().setUrlBase64(
              Base64.encodeBase64String(url)).setCalcTime(bcret._2).setContentHash(bcret._1).setRetCode(bcret._3))
        }
      }
    } catch {
      case e: FBSException => {
        ret.clear()
        ret.setCode(e.getRet_code).setMsg(e.getRet_message)
      }
      case t: Throwable => {
        log.error("error:", t);
        ret.clear()
        ret.setCode("1").setMsg("获取失败～");
      }
    } finally {
      handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()))
    }
  }
  //  override def getCmds(): Array[String] = Array(PWCommand.LST.name())
  override def cmd: String = PCommand.GET.name();
}
