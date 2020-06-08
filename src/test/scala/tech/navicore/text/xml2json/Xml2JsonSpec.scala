package tech.navicore.text.xml2json

import java.io.InputStream

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.io.Source

class Xml2JsonSpec extends AnyFlatSpec with Matchers {

  val stream: InputStream = getClass.getResourceAsStream("/test.xml")
  val xmlString: String = Source.fromInputStream(stream).mkString

  "An Xml Rec" should "parse" in {

    val json = Xml2Json(xmlString, "location")

    json should be (defined)
    json.get.contains("{") should be (true)
    json.get.contains("locationNumber\":\"1\"") should be (true)

  }

  "A Numeric Xml Rec" should "parse" in {

    val json = Xml2Json(xmlString, "location", numericNames = List("locationNumber"))

    json should be (defined)
    json.get.contains("{") should be (true)
    json.get.contains("locationNumber\":1") should be (true)
    json.get.contains("important\":\"F\"") should be (true)

  }

  "A Bool Xml Rec" should "parse" in {

    val json = Xml2Json(xmlString, "location", numericNames = List("locationNumber"), boolNames = List("important"))

    json should be (defined)
    json.get.contains("{") should be (true)
    json.get.contains("important\":false") should be (true)

  }
}