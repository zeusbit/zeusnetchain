package org.fc.brewchain.ssc

import scala.collection.mutable.ListBuffer

trait Slicer {
  def splitArr(bcArr: List[List[String]]): List[String];
  val LINE_SEP = ","
  val BALL_SEP = "\\|"
  val Zt3 = for { i <- 0 to 999 } yield "%03d".format(i)
  val Zt2 = for { i <- 0 to 99 } yield "%02d".format(i)

  def split(bc: String): List[String] = {
    splitArr(toLists(bc))
  }

  def toLists(bc: String): List[List[String]] = {
    val lines = bc.split(LINE_SEP);
    val line5s = for { i <- 0 to 5 } yield if (i < lines.length) lines(i) else "-";
    line5s.map { x =>
      //      println("line:"+x+"::"+x.split("\\|").toList.size)
      x.split(BALL_SEP).toList
    }.toList
  }

  def isDuplStr(str1: String, str2: String): Boolean = {
    str1.split("").map { x =>
      if (x.length() > 0 && str2.trim().contains(x)) return true;
    }
    return false;
  }
  def isNotDuplStr(str1: String, str2: String): Boolean = {
    if (isDuplStr(str1, str2)) return false;
    return true;
  }
}
object SL_Duplex extends Slicer { 
  def splitArr(bcarr: List[List[String]]): List[String] = {
    for {
      i0 <- bcarr(0)
      i1 <- bcarr(1)
      i2 <- bcarr(2)
      i3 <- bcarr(3)
      i4 <- bcarr(4)
      i5 <- bcarr(5)
      i6 <- bcarr(6)
      i7 <- bcarr(7)
      i8 <- bcarr(8)
      i9 <- bcarr(9)
    } yield i0 + i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8 + i9
  }
}
object SL_Combine extends Slicer { 
  def splitArr(bcarr: List[List[String]]): List[String] = {
    SL_fushi.splitArr(bcarr).map { x =>
      for {
        len <- 0 to x.length - 1
      } yield x.substring(len)
    }.flatten
  }
}

object SL_Select extends Slicer { 
  ...
}


object BestSplitter {

  def split(bc: String) = {
....
  }

  def P(lst: List[String]) {
    println("::size=" + lst.size + ":" + lst)
  }

  def main(args: Array[String]): Unit = {
    val buff = ListBuffer[String]();
        P(SL_zuhe.split("1,2,3,4,5|2"))

  }
}
