package pkg.c.data

import java.io.*
import java.nio.file.{Path, Paths}
import java.util.Properties
//import scala.util.Properties
import scala.io.Source
import scala.jdk.CollectionConverters.*

object Properties

  /* Crea un file di Properties vuoto */
  def createPropsFile(filePath: String, comment: String): Unit =
    val props = new Properties()
    try
      props.setProperty("_this", filePath)
      props.setProperty("_comment", comment)
      props.store(new FileOutputStream(filePath), comment)
    catch
      case e: IOException =>
        println(s"Error in createPropsFile: ${e.getMessage}")
  
  /* Inserisce la coppia (key, value) nel file di properties */
  def setPropsFileProperty(filePath: String, key: String, value: String): Unit =
    val props = new Properties()
    try
      props.load(new FileInputStream(filePath))
      props.setProperty(key, value)
      props.store(new FileOutputStream(filePath),"")
    catch
      case e: IOException =>
        println(s"Error in setPropsFileProperty: ${e.getMessage}")
  
  /* Restituisce il valore corrispondente alla chiave key o "<not set>"S se la chiave non esiste */
  def getPropsFileProperty(filePath: String, key: String): String =
    val props = new Properties()
    try
      props.load(new FileInputStream(filePath))
      props.getProperty(key, "<not set>")
    catch
      case e: IOException =>
        s"Error in getPropsFileProperty: ${e.getMessage}"
  
  /* Rimuove il valore corrispondente alla chiave key o "<not set>"S se la chiave non esiste */
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
  
  /* Elimina tutte le proprietà dal file di properties */
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
  
  /* Restituisce un oggetto Properties contenente le coppia (key, value) lette dal file di properties */
  def getPropsFileProperties(filePath: String): Properties =
    val props = new Properties()
    val source = Source.fromFile(filePath)
    try
      props.load(source.bufferedReader())
    finally
      source.close()
    props
  
  /* Imposta le proprietà del file di properties in base alle properties passate come argomento */
  def setPropsFileProperties(filePath: String, props: Properties): Unit =
    try
      if (File(filePath).exists())
        val comment = if props.containsKey("_comment") then props.getProperty("_comment") else ""
        props.store(new FileOutputStream(filePath), comment)
    catch
      case e: IOException =>
        println(s"Error in setPropsFileProperties: ${e.getMessage}")
  
  /* Display di tutte le proprietà contenute nel file di properties */
  def displayPropsFileProperties(filePath: String): Unit =
    try
      if (File(filePath).exists())
        val props = getPropsFileProperties(filePath)
        val propsMap: Map[String, String] = props.asScala.toMap
        propsMap.foreach { case (k, v) => println(s"$k = $v") }
    catch
      case e: IOException =>
        println(s"Error in displayPropsFileProperties: ${e.getMessage}")
  
  /* Display di tutte le proprietà contenute nel file di properties */
  def displayProperties(props: Properties): Unit =
    try
      val propsMap: Map[String, String] = props.asScala.toMap
      propsMap.foreach { case (k, v) => println(s"$k = $v") }
      //props.asScala.toMap.foreach { case (k, v) => println(s"$k = $v") }
      //props.asScala.toList.foreach { case (k, v) => println(s"$k = $v") }
      /*
          // Option 1: Iterate over keys
          for (key <- props.stringPropertyNames().asScala) {
            println(s"$key = ${props.getProperty(key)}")
          }
  
          // Option 2: Iterate over entries
          for ((key, value) <- props.asScala) {
            println(s"$key = $value")
          }
      */
    catch
      case e: IOException =>
        println(s"Error in displayProperties: ${e.getMessage}")
  
  @main def tryHandleProperties: Unit =
  /* test in PropertiesTest.scala */
  
  /*
    val filePath = "protoflow.properties"
  
    // test displayPropsFileProperties
    displayPropsFileProperties(filePath)
  
    // test displayProperties
    displayProperties(getPropsFileProperties(filePath))
  */