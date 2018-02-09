package org.fc.brewchain.xdn

import org.apache.commons.codec.binary.Base64

object TestMain {
  def main(args: Array[String]): Unit = {
    val bb="aHR0cDovL3d3dy5iYWlkdS5jb20="
    
    println(new String(Base64.decodeBase64("aHR0cDovL3d3dy5zZmx5bWUuY29tL3Jlcy9pbWcvd2VjaGF0LmpwZw==")));
  }
}