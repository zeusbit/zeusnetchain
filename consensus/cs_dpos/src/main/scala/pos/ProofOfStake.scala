package org.byzantine.blockchain.pos

import com.roundeights.hasher.Implicits._
import org.byzantine.blockchain._

/**
  * PoS representation
  */
// We are assuming that these can't be forged (e.g. they're cryptographically signed by the validator)
case class ProofOfStake(timestamp: Long, stakeModifier: Hash, validator: Address)

object PoSGenesisBlock extends GenesisBlock(Const.GenesisProofOfStake)


/**
  * Proof-of-Stake auxiliary structure
  *
  * @param chain blockchain to restore the stake from
  */
// TODO: Make this object purely functional!
case class POSHelper(chain: Blockchain[ProofOfStake]) {
  private val blocks = chain.blocks
  private val chainState = State(List(blocks.head))
  private var currentBlockHeight = 0

  private def chainTop = blocks(currentBlockHeight)

  // When constructed, verify POS conditions for all blocks
  require(chainTop == PoSGenesisBlock, "First block must be the genesis block.")

  // TODO: what is this for?
  while (processNextBlock()) {}

  def stake(stakeholder: Address): Int = chainState.balance(stakeholder)

  def stakeModifier: Hash = chainTop.hash

  private def timestamp(): Long = chainTop.timestamp

  private def validatorAcceptance(timestamp: Long, candidate: Address): Boolean = {
    val amount = stake(candidate)
    val kernel: String = timestamp.toString + stakeModifier.toString + candidate.toString

    // Create a positive BigInt from the kernel's hash
    val kernelHash = new BigInt(new java.math.BigInteger(1, kernel.sha1.bytes))

    // Interpret the HashTarget (which is a string) as a hex number
    val targetHash = new BigInt(new java.math.BigInteger(Const.HashTarget, 16))

    kernelHash <= (targetHash * amount)
  }

  def validate(pos: ProofOfStake): Boolean = {
    pos.stakeModifier == stakeModifier &&
        math.abs(pos.timestamp - timestamp) <= Const.MaxAcceptedTimestampDiff &&
        validatorAcceptance(pos.timestamp, pos.validator)
  }

  private def processNextBlock(): Boolean = {
    val nextBlockHeight = currentBlockHeight + 1

    // If there's something left to process
    if (nextBlockHeight < blocks.length) {
      val nextBlock = blocks(nextBlockHeight)
      require(validate(nextBlock.proof), "Blocks must satisfy the POS conditions.")

      chainState.processBlock(blocks(nextBlockHeight))
      currentBlockHeight = nextBlockHeight
      true
    }
    else {
      false
    }
  }
}