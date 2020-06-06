package tech.navicore.text.xml2json

import org.json4s.native.JsonMethods._
import org.json4s.Xml.toJson
import org.json4s._

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

import org.rogach.scallop._

class CliConf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val pretty: ScallopOption[Boolean] = opt[Boolean](required = false)
  val elemName: ScallopOption[String] =
    opt[String](required = true, descr = "Name of element to extract")
  val arrayNames: ScallopOption[String] = opt[String](
    required = false,
    descr = "Comma-separated element names of arrays that should flatten",
    default = Some(""))
  val numericElems: ScallopOption[String] = opt[String](
    required = false,
    descr = "Comma-separated element names of numeric values",
    default = Some(""))
  val boolElems: ScallopOption[String] = opt[String](
    required = false,
    descr = "Comma-separated element names of boolean values",
    default = Some(""))
  verify()
}

object Cli extends App {

  private def stringToJInt(string: String): JInt = {
    string.trim match {
      case "" => JInt(0)
      case s  => JInt(s.toInt)
    }
  }

  private def stringToJBool(string: String): JBool = {
    string.trim match {
      case "" => JBool(false)
      case s  => JBool(s.toBoolean)
    }
  }

  def transform(xmlElems: Elem,
                elemName: String,
                arrayNames: List[String],
                numericElems: List[String],
                boolElems: List[String]): JValue = {
    toJson(xmlElems \\ elemName) transformField {
      case (x, l) if arrayNames.contains(x) => (x, if (l.children.isEmpty) JNull else l.children.head)
      case (x, JString(s)) if numericElems.contains(x) => (x, stringToJInt(s))
      case (x, JString(s)) if boolElems.contains(x) => (x, stringToJBool(s))
    }
  }

  def writeJson(xmlStr: String,
                elemName: String,
                arrayNames: List[String],
                numericNames: List[String],
                boolNames: List[String]): Option[String] = {

    Try(XML.loadString(xmlStr)) match {

      case Success(xmlelems) =>
        val json =
          transform(xmlelems, elemName, arrayNames, numericNames, boolNames)
        val txnJson: Seq[String] = json \ elemName match {
          case jarray: JArray => jarray.children.map(t => compact(render(t)))
          case jobj: JObject =>
            List(compact(render(jobj)))
          case jval: JValue =>
            List(compact(render(jval)))
          case _ =>
            List()
        }
        Some(txnJson.head)

      case Failure(_) =>
        None

    }
  }

  def apply(pp: Boolean,
            elemName: String,
            arrayNames: List[String],
            numericElems: List[String],
            boolElems: List[String]): Unit = {

    Iterator
      .continually(scala.io.StdIn.readLine())
      .takeWhile(_ != null)
      .foreach(xml => {

        val result: Option[String] = Try(XML.loadString(xml)) match {

          case Success(xmlelems) =>
            val json =
              transform(xmlelems, elemName, arrayNames, numericElems, boolElems)
            val txnJson: Seq[String] = json \ elemName match {
              case jarray: JArray =>
                jarray.children.map(t => compact(render(t)))
              case jobj: JObject =>
                List(compact(render(jobj)))
              case jval: JValue =>
                List(compact(render(jval)))
              case _ =>
                List()
            }
            Some(txnJson.head)

          case Failure(_) =>
            None

        }

        val printMe = result.map(r => {
          if (pp && r.charAt(0) == '{') {
            val parsedJson: JValue = parse(r)
            pretty(render(parsedJson))
          } else {
            r
          }
        })
        println(printMe.getOrElse("*** ERROR ***"))

      })

  }

  val conf = new CliConf(args)
  val pp: Boolean = conf.pretty()
  val elemName: String = conf.elemName()
  val arrayNames: List[String] = conf.arrayNames().split(',').toList
  val numericElems: List[String] = conf.numericElems().split(',').toList
  val boolElems: List[String] = conf.boolElems().split(',').toList

  apply(pp, elemName, arrayNames, numericElems, boolElems)

}
