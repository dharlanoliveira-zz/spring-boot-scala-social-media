package dharlanoliveira.mysocialmedia.application.util

import java.security.MessageDigest
import java.math.BigInteger

object Util {
  def md5(s: Array[String]): String = {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(s.mkString.getBytes)
    val bigInt = new BigInteger(1,digest)
    val hashedString = bigInt.toString(16)
    hashedString
  }
}


