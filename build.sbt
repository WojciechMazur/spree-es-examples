scalaVersion := "2.13.4"

name := "hello-world"
organization := "ch.epfl.scala"
version := "1.0"

val elastic4sVersion = "7.10.2"
libraryDependencies ++= Seq(
  // recommended client for beginners
  "com.sksamuel.elastic4s" %% "elastic4s-client-esjava"  % elastic4sVersion,
  "com.sksamuel.elastic4s" % "elastic4s-json-circe_2.13" % elastic4sVersion
)
