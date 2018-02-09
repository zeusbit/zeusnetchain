package org.fc.brewchain.bcapi.crypto

import java.security.SecureRandom
import org.ethereum.crypto.ECKey
import org.spongycastle.util.encoders.Hex
import lombok.extern.slf4j.Slf4j
import org.ethereum.crypto.HashUtil

@Slf4j
object TestC {

  def main(args: Array[String]): Unit = {
    val ran = new SecureRandom();
    //ran.generateSeed(System.currentTimeMillis().asInstanceOf[Int])
    val eckey = new ECKey(ran);
    println("new eckey:" + eckey);
    println("pub:" + Hex.toHexString(eckey.getPubKey));
    println("pri:" + Hex.toHexString(eckey.getPrivKeyBytes));
    println("nodeid:" + Hex.toHexString(eckey.getNodeId));
    println("address:" + Hex.toHexString(eckey.getAddress));
    println("ripedmd160:" + Hex.toHexString(HashUtil.ripemd160(eckey.getAddress)));
    val t = Array(10, "abc", "def").foldLeft("I")(_ + "," + _)
    println(t)

    val str = "V2=2=1=3232";
    println(str.split("=").length);

    val arr = str.split("=");
    val idx = arr(0).length() + arr(1).length() + arr(2).length() + 3;
    if (idx < str.length()) {
      val msgstr = str.substring(idx);
      println("msgstr="+msgstr);
    }
  }
}