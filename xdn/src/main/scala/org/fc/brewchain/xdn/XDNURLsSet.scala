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
import org.fc.brewchain.xdn.pbgens.Xdn.PSURLUpdate
import org.fc.brewchain.xdn.pbgens.Xdn.PRetURLUpdate
import org.fc.brewchain.xdn.pbgens.Xdn.PSURLHashCallback
import org.fc.brewchain.xdn.pbgens.Xdn.PRetURLHashCallback
import org.fc.brewchain.peerxdn.BCConsensus

@NActorProvider
@Slf4j
object XDNUrlSet extends PSMXDN[PSURLHashCallback] {
  override def service = XDNUrlSetService
}

//
// http://localhost:8000/fbs/xdn/pbget.do?bd=
object XDNUrlSetService extends OLog with PBUtils with LService[PSURLHashCallback] {
  val exec = new ThreadPoolExecutor(10, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
  override def onPBPacket(pack: FramePacket, pbo: PSURLHashCallback, handler: CompleteHandler) = {
    log.debug("onPBPacket::" + pbo)
    var ret = PRetURLHashCallback.newBuilder();
    try {
      ret.setSuccess(true).setMessage("ok");
      pbo.getUrlList.map { x =>
        val url = Base64.decodeBase64(x.getUrlBase64); //先不入链
        log.debug("fetch return url:" + new String(url) + ",Hash=" + x.getHash + ",calc_time=" + x.getCalcTime + ",content_hash=" + x.getContentHash + ",retcode=" + x.getRetCode);
        val hash = if (StringUtils.isBlank(x.getHash)) x.getContentHash else x.getHash
        if (!BCConsensus.compareAndMake(x.getUrlBase64, hash, x.getCalcTime, x.getCbRefs, x.getRetCode)) {
          throw new FBSException("set node value failed");
        }

      }

    } catch {
      case e: FBSException => {
        ret.clear()
        ret.setSuccess(false).setMessage(e.getRet_code + ":" + e.getRet_message)
      }
      case t: Throwable => {
        log.error("error:", t);
        ret.clear()
        ret.setSuccess(false).setMessage("error:" + t.getMessage)
      }
    } finally {
      handler.onFinished(PacketHelper.toPBReturn(pack, ret.build()))
    }
  }
  //  override def getCmds(): Array[String] = Array(PWCommand.LST.name())
  override def cmd: String = PCommand.SET.name();
}
