name := "tasky"

description := "A simple task manager for humans"

version := "0.1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.1.1"
libraryDependencies += "com.h2database" % "h2" % "1.4.191"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"