package org.fc.brewchain.test

object P22P {
   
  
  def getDiv(n: Int): (Int, Int) = {
    val d: Int = Math.sqrt(n.asInstanceOf[Double]).asInstanceOf[Int];
    var i = d;
    for (i <- d until 1 by -1) {
      if (n % i == 0) {
        println("d=" + d + "==>" + (i, n / i));
        return (i, n / i);
      }
    }
    println("d.1=" + d + "==>" + (1, n));
    (1, n);
  }
  def calcTime(n: Int): Int = {
    if (n > 2) {
      val div = getDiv(n);
      if (div._1 > 2) {
        return div._1 + calcTime(div._2);
      } else {
        return div._1 + div._2;
      }
    }
    n;
  }
  def main(args: Array[String]): Unit = {
    //    println(getDiv(100));
    println("times:" + calcTime(999));

  }
}