package org.fc.brewchain.peer.fbs

import org.fc.brewchain.peer.Daos
import org.fc.brewchain.peer.persist.FBOAddress
import org.fc.brewchain.bcapi.crypto.KeyPair
import com.google.protobuf.Message
import org.apache.commons.codec.binary.Base64
import onight.tfw.outils.serialize.SerializerFactory
import onight.tfw.outils.serialize.ProtobufSerializer
import java.net.URLEncoder
import java.net.URLDecoder
import com.google.protobuf.Message.Builder
import org.fc.brewchain.bcapi.exception.FBSException
import org.fc.brewchain.fbs.pbgens.Fbs.PSRegistry
import com.google.protobuf.MessageOrBuilder
import org.fc.brewchain.peer.persist.FBOBlock
import onight.tfw.outils.serialize.JsonSerializer

object DBHelper {

  def checkAndPut(path: String, objput: String): Boolean = {

    var vtrade = Daos.oparam.compareAndSwap(path, objput, null);
    
    return vtrade != null && vtrade.get != null
  }
  
  def checkAndUpdate(path: String, objput: String,lastObj:String): Boolean = {

    var vtrade = Daos.oparam.compareAndSwap(path, objput, lastObj);
    
    return vtrade != null && vtrade.get != null
  }

  val pbsio = SerializerFactory.getSerializer(SerializerFactory.SERIALIZER_PROTOBUF);
  val jssio = SerializerFactory.getSerializer(SerializerFactory.SERIALIZER_JSON);

  def toBString(message: MessageOrBuilder): String = {
    toArrayBString("", "", Array(message))
  }

  //  def toLString(title: String, info: String, objectv: Object): String = {
  //    "V2=" + URLEncoder.encode(title, "UTF-8") + "=" + URLEncoder.encode(info, "UTF-8") + "=" +
  //      Base64.encodeBase64URLSafeString(jssio.serialize(objectv).asInstanceOf[Array[Byte]]);
  //  }
  //  

  def toLString(blk: FBOBlock): String = {
    blk.toLString();
    //     val bkkl=JsonSerializer.formatToString(blk);
    //    "V2=" + URLEncoder.encode("BLK", "UTF-8") + "="+blk.BLOCK_HEIGHT+","+blk.TXN_COUNT+"=" +
    //      Base64.encodeBase64URLSafeString(bkkl.getBytes("UTF-8"));
  }

  def fromLString[T](bstr: String, clazz: Class[T]) = {
    FBOBlock.fromLString(bstr);
  }

  def toBString(title: String, message: MessageOrBuilder): String = {
    toArrayBString(title, "", Array(message))
  }

  def toBString(title: String, info: String, message: MessageOrBuilder): String = {
    toArrayBString(title, info, Array(message))
  }

  def toArrayBString(title: String, info: String, messages: Array[MessageOrBuilder]): String = {
    "V2=" + URLEncoder.encode(title, "UTF-8") + "=" + URLEncoder.encode(info, "UTF-8")+"="+System.currentTimeMillis() + "=" + messages.map { x => Base64.encodeBase64URLSafeString(pbsio.serialize(x).asInstanceOf[Array[Byte]])
    }.mkString("=");
  }

  def fromBString(bstr: String, builder: Builder): (String, String, Builder) = {
    //    val arr = bstr.split("=");
    //    if (arr.length < 2) {
    //      throw new FBSException("-10", "WRONG MESSAGETYPE");
    //    } else {
    //      var msg: Message = null;
    //      val idx = arr(0).length() + arr(1).length() + arr(2).length() + 3;
    //      if (idx < bstr.length()) {
    //        val msgstr = bstr.substring(idx);
    //        builder.mergeFrom(Base64.decodeBase64(msgstr));
    //      } else {
    //        throw new FBSException("-11", "NOBODY FOUND MESSAGETYPE");
    //      }
    //      //sio.deserialize(arg0, arg1)
    //      (URLDecoder.decode(arr(1), "UTF-8"), URLDecoder.decode(arr(2), "UTF-8"), builder)
    //    }
    val arr = fromBString(bstr, Array(builder));
    (arr._1, arr._2, arr._3(0))
    //"V=" + URLEncoder.encode(title, "UTF-8") + "=" + URLEncoder.encode(info, "UTF-8") + "=" + Base64.encodeBase64String(sio.serialize(message).asInstanceOf[Array[Byte]]);
  }

  def fromBString(bstr: String, builders: Array[Builder]): (String, String, Array[Builder]) = {
    val arr = bstr.split("=");
    if (arr.length < 2) {
      throw new FBSException("-10", "WRONG MESSAGETYPE");
    } else {
      var msg: Message = null;
      //val idx = arr(0).length() + arr(1).length() + arr(2).length() + 3;
      var idx = 0;
      var arrs = arr.filter { x => idx = idx + 1; idx >= 5 }

      idx = 0;
      arrs.map { x =>
        if (idx < builders.length && builders(idx) != null) {
          builders(idx).mergeFrom(Base64.decodeBase64(x));
        }
        idx += 1;
      }
      //sio.deserialize(arg0, arg1)
      (URLDecoder.decode(arr(1), "UTF-8"), URLDecoder.decode(arr(2), "UTF-8"), builders)
    }
    //"V=" + URLEncoder.encode(title, "UTF-8") + "=" + URLEncoder.encode(info, "UTF-8") + "=" + Base64.encodeBase64String(sio.serialize(message).asInstanceOf[Array[Byte]]);
  }

  def main(args: Array[String]): Unit = {
    val str = "V2==1=3232";
    println(str.split("="));
  }
}
