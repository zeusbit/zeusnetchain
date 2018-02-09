package org.fc.brewchain.peer.persist

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext
import scala.reflect.classTag
import org.fc.brewchain.peer.mysql.commons.SimpleDAO
import onight.tfw.outils.serialize.UUIDGenerator
import java.math.BigDecimal
import scala.collection.mutable.MutableList
import org.apache.commons.codec.binary.Base64
import java.lang.Long
import onight.oapi.scala.commons.PBUtils
import org.fc.brewchain.peer.fbs.DBHelper

object FBSDAOs {
  implicit val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))
}

object FBSP {
  val CH = "#"
  val CC = "~"
}

case class FBOBlock(
    val BVERSION: String = "FBK",
    val BLOCK_HEIGHT: Long = 0L,
    val TXN_COUNT: Int = 0,
    val HASH_BLOCK: String = null,
    val HASH_MERKLE_ROOT: String = null,
    val HASH_PREV_BLOCK: String = null,
    val HASH_PREV_MERKLE_ROOT: String = null, //-->trade_no
    val EXT_COMMENTS: String = null,
    val BLOCK_STATUS: String = "0",
    val CREATE_TIME: Long = System.currentTimeMillis(),
    val UPDATE_TIME: Long = 0L,
    val CONFIRM_TIME: Long = 0L,

    val IDX_COUNT_START: Long = 0L,
    val IDX_COUNT_END: Long = 0L,
    val LOG_UUID: String = UUIDGenerator.generate(),
    val SUB_CHAIN_ID:String = "0") {

  def toLString(): String = Array(BVERSION, BLOCK_HEIGHT, BLOCK_STATUS, TXN_COUNT,
    HASH_BLOCK, HASH_MERKLE_ROOT, HASH_PREV_BLOCK, HASH_PREV_MERKLE_ROOT, IDX_COUNT_START, IDX_COUNT_END,CREATE_TIME,UPDATE_TIME,CONFIRM_TIME,SUB_CHAIN_ID).mkString(FBSP.CH)

  def nextBabyBase = FBOBlock(BLOCK_HEIGHT = BLOCK_HEIGHT + 1, HASH_PREV_BLOCK = HASH_BLOCK, HASH_PREV_MERKLE_ROOT = HASH_MERKLE_ROOT)

  def blkPath = "/zippo/bc/block/" + BLOCK_HEIGHT + "/base";
  def blkTxDir = "/zippo/bc/block/" + BLOCK_HEIGHT + "/txs";
  def parentPath = "/zippo/bc/block/" + BLOCK_HEIGHT + "";

}
object FBOBlock {
  def genisBlock = new FBOBlock()
  def fromLString(str: String): FBOBlock = {
    val arr = str.split(FBSP.CH)
    if (arr.length < 14)
      null
    else
      try { 
        FBOBlock(BVERSION = arr(0), BLOCK_HEIGHT = Long.parseLong(arr(1)), BLOCK_STATUS = arr(2), TXN_COUNT = Integer.parseInt(arr(3)),
          HASH_BLOCK = arr(4), HASH_MERKLE_ROOT = arr(5), HASH_PREV_BLOCK = arr(6), HASH_PREV_MERKLE_ROOT = arr(7),
          IDX_COUNT_START = Long.parseLong(arr(8)), IDX_COUNT_END = Long.parseLong(arr(9)),
          CREATE_TIME= Long.parseLong(arr(10)),UPDATE_TIME= Long.parseLong(arr(11)),CONFIRM_TIME= Long.parseLong(arr(12))
          ,SUB_CHAIN_ID=arr(13));
      } catch {
        case _: Throwable => null;
      }
  }
}
...


