package org.fc.brewchain.p22p.node

import onight.tfw.mservice.NodeHelper
import org.fc.brewchain.bcapi.crypto.KeyPair
import org.fc.brewchain.bcapi.crypto.EncHelper
import org.apache.commons.lang3.StringUtils
import org.spongycastle.util.encoders.Hex
import org.ethereum.crypto.HashUtil
import org.fc.brewchain.p22p.Daos
import java.math.BigInteger
import onight.oapi.scala.traits.OLog

object NodeInstance extends OLog{
  val node_name = NodeHelper.getCurrNodeName
  val NODE_ID_PROP = "org.bc.node.id"
  val kp = {
    val pubkey = NodeHelper.getPropInstance.get("zp.bc.node.pub", "");
    val prikey = NodeHelper.getPropInstance.get("zp.bc.node.pri", "");
    val address = NodeHelper.getPropInstance.get("zp.bc.node.addr", "");
    if (StringUtils.isBlank(pubkey)) {
      EncHelper.newKeyPair()
    } else {
      new KeyPair(
        pubkey,
        prikey,
        address,
        Hex.toHexString(HashUtil.ripemd160(Hex.decode(pubkey))));
    }

  }

  val curnode = new LinkNode("tcp", node_name, NodeHelper.getCurrNodeListenOutAddr, NodeHelper.getCurrNodeListenOutPort,
    System.currentTimeMillis(), kp.pubkey);

  def isReady(): Boolean = {
    log.debug("check Node Instace:Daos.odb="+Daos.odb)
    if (Daos.odb == null) return false;
    this.synchronized {
      if (curnode.node_idx.intValue() <= 0) {
        var v = Daos.odb.get(NODE_ID_PROP);
        val nodeidx = if (v == null || v.get() == null || !StringUtils.isNumeric(v.get.getValue)) {
          NodeHelper.getCurrNodeIdx();
        } else {
         log.debug("Load Node Instace Index from DB="+v.get.getValue)
          Integer.parseInt(v.get.getValue);
        }
        Daos.odb.put(NODE_ID_PROP, String.valueOf(nodeidx))
        curnode.try_node_idx = nodeidx;
        curnode.node_idx = nodeidx;
        
        log.debug("Init Node Instace  Index="+nodeidx)
      }
      return true;
    }

  }

}
