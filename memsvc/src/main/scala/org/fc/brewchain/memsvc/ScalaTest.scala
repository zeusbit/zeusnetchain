package org.fc.brewchain.memsvc

import scala.collection.mutable.ListBuffer
import org.fc.brewchain.memsvc.Memsvc.PMMember

object ScalaTest {
  def main(args: Array[String]): Unit = {
    var list = new ListBuffer[PMMember.Builder]();

    list += PMMember.newBuilder();
    list +=  PMMember.newBuilder();
    
    println("list=="+list)
    list.map { x => 
      x.build()
      println("xx="+x)
    }
  }
}