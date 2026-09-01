package pkg.c.data

import java.io.*
import java.util.Properties
import scala.io.Source
import scala.jdk.CollectionConverters.*

object Properties:

  def createPropsFile(filePath: String, comment: String): Unit =
    val props = new Properties()
    try
      props.setProperty("_this", filePath)
      props.setProperty("_comment", comment)
      props.store(new FileOutputStream(filePath), comment)
    catch
      case e: IOException =>
        println(s"Error in createPropsFile: ${e.getMessage}")
  
  def setPropsFileProperty(filePath: String, key: String, value: String): Unit =
    val props = new Properties()
    try
      props.load(new FileInputStream(filePath))
      props.setProperty(key, value)
      props.store(new FileOutputStream(filePath),"")
    catch
      case e: IOException =>
        println(s"Error in setPropsFileProperty: $filePath")

  def getPropsFileProperty(filePath: String, key: String): String =
    val props = new Properties()
    try
      props.load(new FileInputStream(filePath))
      props.getProperty(key, "<not set>")
    catch
      case e: IOException =>
        s"Error in getPropsFileProperty: ${e.getMessage}"
  
  def removePropsFileProperty(filePath: String, key: String): Unit =
    if (File(filePath).exists())
      val props = getPropsFileProperties(filePath)
      if (props.containsKey(key))
        props.remove(key)
        val comment = if props.containsKey("_comment") then props.getProperty("_comment") else ""
        try
          props.store(new FileOutputStream(filePath), comment)
        catch
          case e: IOException =>
            s"Error in removePropsFileProperty: ${e.getMessage}"
  
  def clearPropsFileProperties(filePath: String): Unit =
    try
      if (File(filePath).exists())
        val props = getPropsFileProperties(filePath)
        val comment = if props.containsKey("_comment") then props.getProperty("_comment") else ""
        props.clear()
        props.store(new FileOutputStream(filePath), comment)
    catch
      case e: IOException =>
        println(s"Error in clearPropsFileProperties: ${e.getMessage}")
  
  def getPropsFileProperties(filePath: String): Properties =
    val props = new Properties()
    val source = Source.fromFile(filePath)
    try
      props.load(source.bufferedReader())
    finally
      source.close()
    props
  
  def setPropsFileProperties(filePath: String, props: Properties): Unit =
    try
      if (File(filePath).exists())
        val comment = if props.containsKey("_comment") then props.getProperty("_comment") else ""
        props.store(new FileOutputStream(filePath), comment)
    catch
      case e: IOException =>
        println(s"Error in setPropsFileProperties: ${e.getMessage}")
  
  private def displayPropsFileProperties(filePath: String): Unit =
    try
      if (File(filePath).exists())
        val props = getPropsFileProperties(filePath)
        val propsMap: Map[String, String] = props.asScala.toMap
        propsMap.foreach { case (k, v) => println(s"$k = $v") }
    catch
      case e: IOException =>
        println(s"Error in displayPropsFileProperties: ${e.getMessage}")