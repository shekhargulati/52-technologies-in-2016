name := "tasky"
version := "0.1.0"
scalaVersion := "2.11.6"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"

val gitCommitCountTask = taskKey[String]("Prints commit count of the current branch")

gitCommitCountTask := {
  val branch = Process("git symbolic-ref -q HEAD").lines.head.replace("refs/heads/","")
  val commitCount = Process(s"git rev-list --count $branch").lines.head
  println(s"total number of commits on [$branch]: $commitCount")
  commitCount
}
