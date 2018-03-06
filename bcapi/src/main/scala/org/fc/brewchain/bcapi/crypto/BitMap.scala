  package org.fc.brewchain.bcapi.crypto

import java.math.BigInteger
import org.apache.commons.lang3.StringUtils
import java.security.SecureRandom
import org.ethereum.crypto.ECKey
import org.spongycastle.util.encoders.Hex

object BitMap {

  val StrMapping = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM0123456789".toCharArray();
    val radix = StrMapping.length;
  val modx = new BigInteger("" + radix);

  def hexToInt(ch: Char): Int = {
    if (ch >= '0' && ch <= '9') ch - '0';
    else if (ch >= 'A' && ch <= 'F') ch - 'A' + 10;
    else if (ch >= 'a' && ch <= 'f') ch - 'a' + 10;
    else 0;
  }

  def int2Str(vi: Int): String = {
    var v = vi;
    val sb = new StringBuffer();
    while (v > 0) {
      sb.append(StrMapping.charAt(v % radix));
      v /= radix;
    }
    sb.toString();
  }

  def hexToMapping(lbi: BigInteger) = {
    var v = lbi;
    val sb = new StringBuffer();
//    println("modx="+modx)

    while (v.bitCount() > 0) {
//      println("v="+v.mod(modx))
      sb.append(StrMapping.charAt(v.mod(modx).intValue()));
      v = v.divide(modx);
    }
    sb.reverse().toString();
  }

  def mapToHex(str: String): BigInteger = {
    var v = new BigInteger("0");
    str.map { ch =>

      v = v.mod(modx);
    }
    v;
  }
  def newKeyPair(): KeyPair = {

    val ran = new SecureRandom();
    //ran.generateSeed(System.currentTimeMillis().asInstanceOf[Int])
    val eckey = new ECKey(ran);
    return new KeyPair(
      Hex.toHexString(eckey.getPubKey),
      Hex.toHexString(eckey.getPrivKeyBytes),
      Hex.toHexString(eckey.getAddress),
      Hex.toHexString(eckey.getAddress));

  }

  def mapToBigInt(strl: String):BigInteger = {
    var bi: BigInteger = new BigInteger("0");
    strl.map { x =>
      bi = bi.multiply(modx).add(new BigInteger("" + StrMapping.indexOf(x), 10))
//      println("x=" + x + "==>" + new BigInteger("" + StrMapping.indexOf(x), 10)+"...bi="+bi.toString(16))
    }
    bi
  }

  def main(args: Array[String]): Unit = {

     val hexstr = "6647dccf7908a611dd50fa74548afd94164be77dcb9a7e455e8543c500ed7258";
//    val hexstr = "100A";

    var bi = new BigInteger("0");
    bi=bi.setBit(215);
    println("bi=" + bi.toString(16));
    println("biequal::" + StringUtils.equalsIgnoreCase(bi.toString(16), hexstr) + ":len=" + hexstr.length() + "==>" + bi.bitCount())
    val bix = hexToMapping(bi);
    println("bix::" + bix);
    val bistr = mapToBigInt("aeXcre1pX4uk1Dab1rA8WQfOETRrPe");
    println("bistr=" + bistr.toString(16));
    println("biequal::" + StringUtils.equalsIgnoreCase(bistr.toString(16), (hexstr)) + ":len=" + hexstr.length())

  }
}