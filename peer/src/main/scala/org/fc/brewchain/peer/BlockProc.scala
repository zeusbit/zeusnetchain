package org.fc.brewchain.peer

import onight.oapi.scala.commons.PBUtils
import onight.oapi.scala.traits.OLog
import onight.tfw.mservice.NodeHelper
import org.slf4j.MDC
import onight.tfw.mservice.ThreadContext
import onight.tfw.oparam.api.OTreeValue
import onight.tfw.async.CallBack
import scala.collection.JavaConversions._
import org.apache.commons.lang3.StringUtils
import org.fc.brewchain.bcapi.crypto.EncHelper
import scala.collection.mutable.MutableList
import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.IllegalArgumentException
import onight.tfw.outils.serialize.UUIDGenerator
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicBoolean
import org.fc.brewchain.peer.persist.FBOBlock
import org.fc.brewchain.peer.fbs.DBHelper
import org.fc.brewchain.peer.persist.FBOTransaction
import org.fc.brewchain.fbs.pbgens.Fbs.PMBlockInfo
import org.fc.brewchain.fbs.pbgens.Fbs.PMAssetInfo
import org.fc.brewchain.fbs.pbgens.Fbs.PSAssetCreate
import org.fc.brewchain.fbs.pbgens.Fbs.PMFundInfo

// http://localhost:8001/pee/pbinf.do?fh=VINFSVC000000J00&bd={}&gcmd=INFSVC
object BlockProc extends OLog with PBUtils {

 .....
}