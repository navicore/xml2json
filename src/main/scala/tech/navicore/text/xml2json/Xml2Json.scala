package tech.navicore.text.xml2json

import org.json4s.native.JsonMethods._
import org.json4s.Xml.toJson
import org.json4s._

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

object Xml2Json {

  private def stringToJInt(string: String): JInt = {
    string.trim match {
      case "" => JInt(0)
      case s  => JInt(s.toInt)
    }
  }

  private def stringToJBool(string: String): JBool = {
    string.trim match {
      case ""  => JBool(false)
      case "Y" => JBool(true)
      case "y" => JBool(true)
      case "N" => JBool(false)
      case "n" => JBool(false)
      case "1" => JBool(true)
      case "0" => JBool(false)
      case "t" => JBool(true)
      case "T" => JBool(true)
      case "f" => JBool(false)
      case "F" => JBool(false)
      case s   => JBool(s.toBoolean)
    }
  }

  def transform(xmlElems: Elem,
                elemName: String,
                arrayNames: List[String],
                numericElems: List[String],
                boolElems: List[String]): JValue = {
    toJson(xmlElems \\ elemName) transformField {
      case (x, l) if arrayNames.contains(x) =>
        (x, if (l.children.isEmpty) JNull else l.children.head)
      case (x, JString(s)) if numericElems.contains(x) => (x, stringToJInt(s))
      case (x, JString(s)) if boolElems.contains(x)    => (x, stringToJBool(s))
    }
  }

  def apply(xmlStr: String,
            elemName: String,
            arrayNames: List[String] = List(),
            numericNames: List[String] = List(),
            boolNames: List[String] = List()): Option[String] = {

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
        txnJson.headOption

      case Failure(_) =>
        None

    }
  }

}
