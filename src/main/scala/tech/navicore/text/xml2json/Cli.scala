package tech.navicore.text.xml2json

import org.json4s._
import org.json4s.native.JsonMethods._
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

  def apply(pp: Boolean,
            elemName: String,
            arrayNames: List[String],
            numericElems: List[String],
            boolElems: List[String]): Unit = {

    Iterator
      .continually(scala.io.StdIn.readLine())
      .takeWhile(_ != null)
      .foreach(xml => {

        val result: Option[String] =
          Xml2Json(xml, elemName, arrayNames, numericElems, boolElems)

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
