package tech.navicore.text.xml2json

import java.io.InputStream

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class Xml2JsonArraySpec extends AnyFlatSpec with Matchers {

  val stream: InputStream = getClass.getResourceAsStream("/test2.xml")
  val xmlString: String = Source.fromInputStream(stream).mkString

  "An Xml Rec" should "parse" in {
    val json = Xml2Json(xmlString, "location", arrayNames = List("nicknames"))
    json should be (defined)
    json.get.contains("{") should be (true)
    json.get.contains("locationNumber\":\"1\"") should be (true)
  }

  "An Array Rec" should "extract" in {
    val json = Xml2Json(xmlString, "nicknames")
    json should be (defined)
    json.get.contains("{") should be (true)
    json.get.contains("""{"nickname":["King","Kong"]""") should be (true)
  }

  "An Object Array Rec" should "extract" in {
    val json = Xml2Json(xmlString, "addresses")
    json should be (defined)
    json.get.contains("{") should be (true)
    json.get.contains("""{"addr":[{"street":"High Street",""") should be (true)
  }

}