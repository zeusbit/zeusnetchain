package org.fc.brewchain.p22p.node

import onight.tfw.mservice.NodeHelper
import org.fc.brewchain.bcapi.crypto.KeyPair
import org.fc.brewchain.bcapi.crypto.EncHelper
import org.apache.commons.lang3.StringUtils
import org.spongycastle.util.encoders.Hex
import org.ethereum.crypto.HashUtil

object NodeInstance {
  val node_name = NodeHelper.getCurrNodeName

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

  val curnode = new LinkNode("tcp",node_name, NodeHelper.getCurrNodeListenOutAddr, NodeHelper.getCurrNodeListenOutPort,
    System.currentTimeMillis(), kp.pubkey);


}
