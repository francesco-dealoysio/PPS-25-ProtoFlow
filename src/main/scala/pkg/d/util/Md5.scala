package pkg.d.util

import java.security.MessageDigest
import scala.util.Try

object Md5

  /** Compute MD5 hash of a given string and return it as a hex string */
  def md5(text: String): String =
    require(text != null, "Input text cannot be null")

    // Get MD5 digest instance
    val md = MessageDigest.getInstance("MD5")

    // Compute digest as byte array
    val digestBytes = md.digest(text.getBytes("UTF-8"))

    // Convert bytes to hex string
    digestBytes.map("%02x".format(_)).mkString

  @main def tryMd5: Unit =
    println(md5(""))
    println(md5("topolino"))