import scala.collection.mutable.Map
import java.util.ArrayList
import org.fc.brewchain.p22p.node.PNode
import onight.oapi.scala.traits.OLog
import scala.collection.mutable.ListBuffer
import java.math.BigInteger
import org.fc.brewchain.bcapi.crypto.BitMap

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
    for (i <- 1 to sendcc) {
      val node = nodes((Math.random() * nodeCount % nodeCount).asInstanceOf[Int]);
      node.forwardMessage("test", node.directNode.keys, node.name);
    }
    log.debug("cost=" + (System.currentTimeMillis() - start))
    val totalrecv = nodes.foldLeft(0L)(_ + _.counter.recv.get)
    val totalsend = nodes.foldLeft(0L)(_ + _.counter.send.get)
    nodes.filter { _.counter.recv.get != sendcc }.map { f => 
      println("recvError:Not Recving:" + f.name + ",recv=" + f.counter.recv.get + ",sendcc=" + sendcc)
    }
    println("totalSend:" + totalsend + ",totalrecv=" + totalrecv);

    var node_bits = new BigInteger("10")
    println(node_bits.toString() + "==>" + node_bits.testBit(2));
    
    val node_bits1 = node_bits.setBit(100);
    
    println(node_bits1.toString(16) + "==>" + node_bits1.testBit(100)+"==>"+BitMap.hexToMapping(node_bits1));
    println(node_bits.toString() + "==>" + node_bits.testBit(100));

  }
}