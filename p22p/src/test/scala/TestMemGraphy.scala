import scala.collection.mutable.Map
import java.util.ArrayList
import org.fc.brewchain.p22p.node.PNode
import onight.oapi.scala.traits.OLog
import scala.collection.mutable.ListBuffer
import java.math.BigInteger
import org.fc.brewchain.bcapi.crypto.BitMap
import org.fc.brewchain.p22p.pbgens.P22P.PVBase
import onight.tfw.outils.serialize.UUIDGenerator

object TestMemGraphy extends OLog {

  def main(args: Array[String]): Unit = {
    val nodeCount = 100;
    val nodes = new ListBuffer[PNode]();
    for (i <- 1 to nodeCount) {
      nodes.+=(new PNode("a" + i,i));
    }
    nodes.map { node =>
      nodes.map { f1 =>
        node.addDNode(f1)
      }
    }

    var start = System.currentTimeMillis();
    val sendcc = 1;
    val msg = PVBase.newBuilder().setMessageUid(UUIDGenerator.generate()).build()
    for (i <- 1 to sendcc) {
      val node = nodes((Math.random() * nodeCount % nodeCount).asInstanceOf[Int]);
      node.forwardMessage("aaa",msg, node.directNode.keys, node);
    }
    log.debug("cost=" + (System.currentTimeMillis() - start))
    val totalrecv = nodes.foldLeft(0L)(_ + _.counter.recv.get)
    val totalsend = nodes.foldLeft(0L)(_ + _.counter.send.get)
    nodes.filter { _.counter.recv.get != sendcc }.map { f => 
      println("recvError:Not Recving:" + f.name + ",recv=" + f.counter.recv.get + ",sendcc=" + sendcc)
    }
    println("totalSend:" + totalsend + ",totalrecv=" + totalrecv);

    var node_bits = new BigInteger("0")
    println(node_bits.toString() + "==>" + node_bits.testBit(2));
    
    val node_bits1 = node_bits.setBit(100).setBit(1).setBit(12).setBit(0);
    
    println(node_bits1.toString(16) + "==>" + node_bits1.testBit(100)+"==>"+BitMap.hexToMapping(node_bits1));
    println(node_bits1.bitCount()+","+node_bits1.bitLength());

  }
}