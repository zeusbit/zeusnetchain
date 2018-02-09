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
import org.fc.brewchain.peerxdn.Daos
import org.fc.brewchain.xdn.pbgens.Xdn.PMFetch
import org.fc.brewchain.xdn.pbgens.Xdn.PRetURLHashCallback
import java.net.URL
import org.fc.brewchain.peerxdn.Scheduler

@NActorProvider
@Slf4j
object XDNUrlUpdate extends PSMXDN[PSURLUpdate] {
  override def service = XDNUrlUpdateService
  //  val url_post: String = Daos.props.get("xdn.agent.url", "http://localhost:8765/push_task")
  val url_post: String = Daos.props.get("xdn.agent.url", "http://192.168.99.100:8765/push_task")
  //  val callback_url: String = Daos.props.get("xdn.agent.callback", "http://localhost:8000/fbs/xdn/pbset.do")
  val callback_url: String = Daos.props.get("xdn.agent.callback", "http://192.168.99.1:8000/fbs/xdn/pbset.do")
}

//
// http://localhost:8000/fbs/xdn/pbget.do?bd=
object XDNUrlUpdateService extends OLog with PBUtils with LService[PSURLUpdate] {
  val exec = new ThreadPoolExecutor(10, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
  override def onPBPacket(pack: FramePacket, pbo: PSURLUpdate, handler: CompleteHandler) = {
    log.debug("onPBPacket::" + pbo)
    var ret = PRetURLUpdate.newBuilder();
    try {
      //check url
      ret.setCode("0000").setMsg("SUCCESS");
      val groups = Base64.encodeBase64String(pbo.getGroup().getBytes) + "-" + Base64.encodeBase64String(pbo.getUserid().getBytes) +
        "-" + Base64.encodeBase64String(pbo.getAppkey().getBytes);
      if (pbo.getUrlBase64Count == 0 && pbo.getRecalcTimesec < 0) {
        //remove all groups
        Scheduler.stopGroupRunners(groups)

      } else
        pbo.getUrlBase64List().filter { x =>
          val url = Base64.decodeBase64(x.getBytes); //先不入链
          new URL(new String(url)) != null
        }.map { x =>
          val url = Base64.decodeBase64(x.getBytes); //先不入链
          log.debug("fetch url:" + new String(url) + "==>" + x);
          val sc = PMFetch.newBuilder().setCallbackBase64(Base64.encodeBase64String(XDNUrlUpdate.callback_url.getBytes))
            .setCbRefs(groups).setMaxBytesSize(pbo.getMaxBytesSize);
          sc.addUrlBase64(x);
          //
          //        Daos.httpsender.post(arg0)
          //{"callback_base64": "aHR0cDovLzE5Mi4xNjguOTkuMTo4MDAwL2Zicy94ZG4vcGJzZXQuZG8=","url_base64" :["aHR0cDovL3d3dy5iYWlkdS5jb20=","aHR0cDovL3d3dy5zaW5hLmNvbQ=="]}
          ret.addUrl(
            PMURLPair.newBuilder().setUrlBase64(
              Base64.encodeBase64String(url)))

          val delay = if (pbo.getRecalcTimesec == 0) 60 else if (pbo.getRecalcTimesec > 0) pbo.getRecalcTimesec else -1;

          Scheduler.updateRunner(sc.getCbRefs, x, new Runnable {
            def run() = {
              try {
                val fp = PacketHelper.buildUrlFromPB(sc.build(), "POST", XDNUrlUpdate.url_post);
                fp.getFixHead.setEnctype('J');
                val result = Daos.httpsender.send(fp, 10 * 1000)
                log.debug("post to agent:" + new String(result.getBody));
                if (new String(result.getBody).indexOf(""""success":true""") > 0) {
                  log.debug("post to agent success:" + sc);
                } else {
                  log.debug("post to agent failed:" + sc);
                }
              } catch {
                case t: Throwable =>
                  log.error("run error in Scheduler:" + url + ",org=" + pbo.getGroup + ",usrid=" + pbo.getUserid+",appkey="+pbo.getAppkey, t);
              }
            }
          }, delay);
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
  override def cmd: String = PCommand.UPD.name();
}
