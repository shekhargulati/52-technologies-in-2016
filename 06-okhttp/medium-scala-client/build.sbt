name := "medium-scala-client"

version := "1.0"

description := "Scala client for Medium.com REST API"

scalaVersion := "2.11.7"

libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.0.1"

libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

libraryDependencies += "com.squareup.okhttp3" % "mockwebserver" % "3.0.1" % "test"
