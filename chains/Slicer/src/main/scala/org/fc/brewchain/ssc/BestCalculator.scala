package org.fc.brewchain.ssc

import scala.collection.mutable.ListBuffer

trait Calc {
  def calcArr(bcArr: List[List[String]])(implicit winno: String): List[String] = { List() }
  val LINE_SEP = ","
  val BALL_SEP = "\\|"
  val Zt3 = for { i <- 0 to 999 } yield "%03d".format(i)
  val Zt2 = for { i <- 0 to 99 } yield "%02d".format(i)

  def calc(bc: String)(implicit winno: String): List[String] = {
    calcArr(toLists(bc))
  }

  def toLists(bc: String): List[List[String]] = {
    val lines = bc.split(LINE_SEP);
    val line5s = for { i <- 0 to 5 } yield if (i < lines.length) lines(i) else "-";
    line5s.map { x =>
      //      println("line:"+x+"::"+x.calc("\\|").toList.size)
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
  def calc_Same1(bc: String)(implicit winno: String): List[(String, Int)] = {
    val bcarr = toLists(bc)
    val r = for {
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

    } yield (i0 + i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8 + i9, 0)
    //        println("r=" + r)
    r.filter { x => x._1.equals(winno) }
  }

  def calc_Dim2(bc: String)(implicit winno: String): List[(String, Int)] = {
    val bcarr = toLists(bc)
    val line0 = bcarr(0).map { x => x.toList.filter { winno.contains(_) }.map { x => (x + "-" * 4, 0) } }.flatten
    val line1 = bcarr(1).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 1 + x + "-" * 9, 0) } }.flatten
    val line2 = bcarr(2).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 2 + x + "-" * 8, 0) } }.flatten
    val line3 = bcarr(3).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 3 + x + "-" * 7, 0) } }.flatten
    val line4 = bcarr(4).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 4 + x + "-" * 6, 0) } }.flatten
    val line5 = bcarr(5).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 5 + x + "-" * 5, 0) } }.flatten
    val line6 = bcarr(6).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 6 + x + "-" * 4, 0) } }.flatten
    val line7 = bcarr(7).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 7 + x + "-" * 3, 0) } }.flatten
    val line8 = bcarr(8).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 8 + x + "-" * 2, 0) } }.flatten
    val line9 = bcarr(9).map { x => x.toList.filter { winno.contains(_) }.map { x => ("-" * 9 + x + "-" * 1, 0) } }.flatten

    List(line0, line1, line2, line3, line4, line5, line6, line7, line8, line9).flatten
  }


  def calc_Sum(bc: String)(implicit winno: String): List[(String, Int)] = {
    val bcarr = toLists(bc)
    val totalnum = winno.toList.foldLeft(0)((B, A) => B + Integer.parseInt(A + "")) + ""
    //    println("totalnum=" + totalnum)
    bcarr(0).filter { x =>
      x.equals(totalnum)
    }.map { x => (x, 0) }
  }

  def calc_SumString(bc: String)(implicit winno: String): List[(String, Int)] = {
    
    val c0 = existCount(winno, winno.charAt(0))
    val c1 = existCount(winno, winno.charAt(1))
    val c2 = existCount(winno, winno.charAt(2))
    val winlevel = if (c0 == c1 && c1 == c2 && c0 == 1) 0 else if (c0 == 2 || c1 == 2 || c2 == 2) 1 else -1
    if (winlevel == -1) {
      return List();
    }
    val bcarr = toLists(bc)
    val totalnum = winno.toList.foldLeft(0)((B, A) => B + Integer.parseInt(A + "")) + ""
    //    println("totalnum=" + totalnum)
    bcarr(0).filter { x =>
      x.equals(totalnum)
    }.map { x => (x, winlevel) }
  }
  def calc_SumThree(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SumString(bc)(winno.substring(0, 3))
  }
  def calc_SumMid(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SumString(bc)(winno.substring(1, 4))
  }
  def calc_SumLastThree(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SumString(bc)(winno.substring(2, 5))
  }

  def calc_Direct(bc: String)(implicit winno: String): List[(String, Int)] = {
    val bcarr = toLists(bc)
    val totalnum = winno.toList.foldLeft(0)((B, A) => B + Integer.parseInt(A + "")) + ""
    bcarr(0).filter { x =>
      x.equals(totalnum)
    }.map { x => (x, 0) }
  }
  

  def calc_Combiles(bc: String)(implicit winno: String): List[(String, Int)] = {
    val bcarr = toLists(bc)
    val r = for {
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

    } yield List(
      (i5 + i6 + i7 + i8 + i9, 0), (i6 + i7 + i8 + i9, 1), (i7 + i8 + i9, 2), (i7 + i8, 3), (i9, 3),
      (i0 + i1 + i2 + i3 + i4, 0), (i1 + i2 + i3 + i4, 1), (i2 + i3 + i4, 2), (i3 + i4, 3), (i4, 3)
      )
    r.flatten.filter { x => winno.endsWith(x._1) }.map(x =>
      ("-" * (5 - x._1.length()) + x._1, x._2))
  }


  def existCount(winno: String, com: Char): Int = {
    var cc = 0
    for (i <- 0 to winno.length() - 1) if (winno(i).equals(com)) cc = cc + 1
    return cc;
  }

  def existCount(winno: String, com: String, compareV: Int): Boolean = {
    val v = com.toList.map { x =>
      existCount(winno, x)
    }.filter { x => x == compareV }

    v.length == com.length()
  }

  def calc_DuplicateChain(bc: String, picnum0: Int, picnum1: Int, comnum0: Int, comnum1: Int)(implicit winno: String): List[(String, Int)] = {
    val bcarr = toLists(bc)
    val v = for {
      c <- SC_zuxuan.calcZuxuan(bcarr, 0, picnum0)
      d <- SC_zuxuan.calcZuxuan(bcarr, 1, picnum1)
    } yield List(c, d)
    v.filter(x =>
      {
        isNotDuplStr(x(0), x(1)) && existCount(winno, x(0), comnum0) && existCount(winno, x(1), comnum1)
      }).map(x => (x(0) + "*" + comnum0 + "/" + x(1) + "*" + comnum1, 0));
  }

  def calc_SpecialChar(bc: String, count: Int)(implicit winno: String): List[(String, Int)] = {
    val bcarr = toLists(bc)
    bcarr(0).filter { x =>
      //      println("winno=" + winno + ",x=" + x+"::idx="+winno.indexOf(x))
      existCount(winno, x(0)) >= count
    }.map { f =>
//      println("ff="+f)
      val fstr = winno.toList.map { fx => if (fx.equals(f(0))) fx else "*" }
      (fstr.mkString(""), 0)
    }
  }
  def calc_One(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 1)
  }

  def calc_Two(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 2)
  }
  def calc_Three(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 3)
  }
  def calc_Four(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 4)
  }
  def calc_5(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 5)
  }
  def calc_6(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 6)
  }
  def calc_7(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 7)
  }
  def calc_8(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 8)
  }
  def calc_9(bc: String)(implicit winno: String): List[(String, Int)] = {
    calc_SpecialChar(bc, 9)
  }
}


object BestCalc extends Calc {

  val genText = UUID.next();
  def calc(bc: String) = {
    calc_9(bc)(genText);
  }

  def P(lst: List[(String, Int)]) {
    println("::size=" + lst.size + ":" + lst)
  }

  def main(args: Array[String]): Unit = {
    val buff = ListBuffer[String]();
    

    //    println("==================")
    //    P(SC_fushi.calc("1|2,2,3,4,5"))
    //    P(SC_zuhe.calc("1,2,3,4,5|2"))
    //    P(SC_zuxuan.calc("1|2|3|4|5|6|0"))
  }
}
