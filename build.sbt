name := "Xml2Json"
organization := "tech.navicore"

fork := true
javaOptions in test ++= Seq(
  "-Xms512M", "-Xmx2048M",
  "-XX:MaxPermSize=2048M",
  "-XX:+CMSClassUnloadingEnabled"
)

parallelExecution in test := false

version := "0.1.0"

val scala212 = "2.12.11"

scalaVersion := scala212

val main = Project(id = "Xml2Json", base = file("."))

libraryDependencies ++=
  Seq(

    "org.rogach" %% "scallop" % "3.4.0",
    "org.json4s" %% "json4s-xml" % "3.6.12",
    "org.json4s" %% "json4s-native" % "3.6.12",

    "org.scalatest" %% "scalatest" % "3.1.2" % "test"

  )

mainClass in assembly := Some("tech.navicore.text.xml2json.Cli")
assemblyJarName in assembly := "Xml2Json.jar"

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

