package tech.navicore.text.xml2json

import org.rogach.scallop._

class CliConf(arguments: Seq[String]) extends ScallopConf(arguments) {
  val pretty: ScallopOption[Boolean] = opt[Boolean](required = false)
  verify()
}

object Go {

  def apply(pp: Boolean): Unit = {
  }

}

object Cli extends App {

  val conf = new CliConf(args)
  val pp: Boolean = conf.pretty()

  Go(pp)

}
