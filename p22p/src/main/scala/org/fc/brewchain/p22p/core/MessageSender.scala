package org.fc.brewchain.p22p.core

import onight.tfw.otransio.api.PSender
import onight.tfw.otransio.api.IPacketSender
import scala.beans.BeanProperty
import onight.osgi.annotation.NActorProvider
import com.google.protobuf.Message
import onight.tfw.otransio.api.beans.FramePacket
import onight.tfw.otransio.api.PacketHelper
import onight.tfw.async.CallBack
import onight.tfw.ntrans.api.NActor
import onight.oapi.scala.traits.OLog
import org.fc.brewchain.p22p.node.PNode
import onight.tfw.otransio.api.PackHeader
import org.fc.brewchain.p22p.node.LinkNode

@NActorProvider
object MessageSender extends NActor with OLog {

  //http. socket . or.  mq  are ok
  @PSender
  @BeanProperty
  var sockSender: IPacketSender = null;

  def sendMessage(gcmd: String, body: Message, node: LinkNode,cb: CallBack[FramePacket]) {
    val pack = PacketHelper.buildFromBody(body, gcmd);
    pack.putHeader(PackHeader.PACK_TO, node.name);
    pack.putHeader(PackHeader.PACK_URI, node.uri);
    sockSender.asyncSend(pack, cb)
  }
}
