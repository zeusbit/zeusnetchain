package org.fc.brewchain.peer

import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.traits.OLog
import org.fc.brewchain.peer.persist.FBSDAOs
import org.fc.brewchain.peer.persist.FBOBlock
import onight.tfw.mservice.NodeHelper
import org.slf4j.MDC
import onight.tfw.mservice.ThreadContext
import onight.tfw.oparam.api.OTreeValue
import onight.tfw.async.CallBack
import scala.collection.JavaConversions._
import org.apache.commons.lang3.StringUtils
import org.fc.brewchain.peer.persist.FBOTransaction
import org.fc.brewchain.peer.persist.FBOAddress
import org.fc.brewchain.peer.persist.FBOGlobalProps
import org.fc.brewchain.bcapi.crypto.EncHelper
import org.fc.brewchain.peer.persist.FBOTransaction
import org.fc.brewchain.peer.fbs.DBHelper
import org.fc.brewchain.fbs.pbgens.Fbs.PMAssetInfo
import org.fc.brewchain.fbs.pbgens.Fbs.PMAssetInfoOrBuilder
import org.fc.brewchain.fbs.pbgens.Fbs.PMFundInfo
import org.fc.brewchain.fbs.pbgens.Fbs.PMSignAddress
import org.fc.brewchain.peer.fbs.FBSAssetProc
import org.fc.brewchain.fbs.pbgens.Fbs.PSAssetCreate

// http://localhost:8001/pee/pbinf.do?fh=VINFSVC000000J00&bd={}&gcmd=INFSVC
object TransactionProc extends OLog with PBUtils {
  def calcTxMPT(tx: FBOTransaction): String = {
    tx.mapComments().map {
      _ match {
        case Some(addr: FBOAddress) =>
          { //校验地址和hash！！！
            Some(EncHelper.MTHash(addr.HEX_ADDRESS, addr.PREV_TXID));
          }
        case Some(addr: FBOGlobalProps) =>
          {
            Some(EncHelper.MTHash(addr.PROP_KEY, addr.HASH_VALUE, addr.PRE_HASH_VALUE));
          }
        case None => {
          None
        }
        case Some(sign: String) => {
          Some(sign)
        }
        case _ => {
          None
        }
      }

    }.filter { x => x != None && x.get != null }.foldLeft("") { (b, a) =>
      EncHelper.MTHash(b, a.get)
    }
  }
  def validateTx(tv: OTreeValue): (PSAssetCreate.Builder,PMFundInfo.Builder,PMAssetInfo.Builder) = {

    if (tv.getValue == null) return null
    //check mkl
    FBSAssetProc.getAssetInfo(tv.getValue);
  }
}