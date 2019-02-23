SBT: The Missing Tutorial [![TimeToRead](http://ttr.myapis.xyz/ttr.svg?pageUrl=https://github.com/shekhargulati/52-technologies-in-2016/blob/master/02-sbt/README.md)](http://ttr.myapis.xyz/)
---

Welcome to the second blog of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. From last year, I have started using Scala as my main programming language. One of the tools that you have to get used to while working with a programming language is a build tool. In my office projects, we use Gradle for all our projects be it Scala or Java. In most of my personal Scala projects, I have started using `sbt` as my preferred build tool. **`sbt` is a general purpose build tool written in Scala**. Most of the time we try to hack our way while using a build tool never learning it properly. As Scala will be the language that I will cover most in this series, I decided to thoroughly learn `sbt` this week. We (developers) often underestimate the importance of learning a build tool thoroughly and end up not using build tool in the most effective way. Good working knowledge of a build tool can make us more productive so we should take it seriously.

> **This blog is part of my year long blog series [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016)**

## Table of Contents

We will cover the following in this tutorial:

* [What is sbt?](#what-is-sbt)
* [Install sbt on your machine](#install-sbt-on-your-machine)
* [Getting started with sbt](#getting-started-with-sbt)
* [sbt modes](#sbt-modes)
* [Create an sbt Scala project](#create-a-sbt-scala-project)
  * [sbt says Hello](#sbt-says-hello)
  * [Set Scala version](#set-scala-version)
  * [Building Tasky application](#building-tasky-application)
    * [Add dependencies](#add-dependencies)
    * [Run tests](#run-tests)
    * [Rerun tests](#rerun-tests)
* [Writing your own tasks](#writing-your-own-tasks)
* [Using plugins](#using-plugins)
* [Tips](#tips)

## What is sbt?

`sbt` i.e. Simple Build Tool is a general purpose build tool written in Scala for JVM developers. It borrows good ideas from other successful build tools like Ant, Maven, and Gradle.

1. Default project layouts
2. Built-in tasks
3. Plugin architecture
4. Declarative Dependency management
5. Code over Configuration: A DSL for build tool

Apart from the feature set mentioned above `sbt` also provides the following additional features:

1. Interactive nature: It isn't just a build tool, it also provides an interactive environment to work in
2. Scala REPL integration

<img src="http://www.scala-sbt.org/assets/typesafe_sbt_svg.svg" height="100" width="100" align="middle">

## Install sbt on your machine

If you are on mac then, you can use package manager like `brew` to install `sbt` on your machine:

```bash
$ brew install sbt
```

For other systems, download the latest version of sbt from the [website](http://www.scala-sbt.org/download.html). Current production version is `0.13.16`. You can download by clicking [https://cocl.us/sbt01316zip](https://cocl.us/sbt01316zip).

You can refer to manual instructions from `sbt` website http://www.scala-sbt.org/0.13/tutorial/Manual-Installation.html. This blog is written using sbt version `0.13.9`.

Once you have successfully installed `sbt` on your machine, create an empty directory somewhere in your file system which we will use for this blog.

To view the basic information about `sbt`, we can use `about` task. The `sbt about` task and its output is shown below:

```
$ sbt about

[info] Set current project to code (in build file:/Users/shekhargulati/blogs/sbt-playground)
[info] This is sbt 0.13.9
[info] The current project is {file:/Users/shekhargulati/blogs/sbt-playground}code 0.1-SNAPSHOT
[info] The current project is built against Scala 2.10.5
[info] Available Plugins: sbt.plugins.IvyPlugin, sbt.plugins.JvmPlugin, sbt.plugins.CorePlugin, sbt.plugins.JUnitXmlReportPlugin
[info] sbt, sbt plugins, and build definitions are using Scala 2.10.5
```

As you can see in the second line of the output we are using sbt version 0.13.9.

> First time you run `sbt`, it will download some jars that are required by sbt to perform its job. The default sbt installation is very minimalistic and does not come bundled with everything.

## Getting started with sbt

`sbt` terminology consists of two terms -- **tasks** and **settings**. A task defines an action which you want to perform like compile. A setting is used to define a value for example name and version of the project.

With `sbt` whenever you want to perform any action you execute a task. Task is the unit of currency in `sbt`. A task can depend on another task to do its job. `sbt` creates a task dependency graph to determine which task should run first. If task `t1` depends on task `t2` then task `t2` will be executed first and then task `t1` will be executed. You can view all the tasks applicable to a project by running `sbt tasks` task:

```bash
$ sbt tasks
```

The above task will produce the following output:

```
[info] Set current project to code (in build file:/Users/shekhargulati/blogs/tasky/)

This is a list of tasks defined for the current project.
It does not list the scopes the tasks are defined in; use the 'inspect' task for that.
Tasks produce values.  Use the 'show' task to run the task and print the resulting value.

  clean            Deletes files produced by the build, such as generated sources, compiled classes, and task caches.
  compile          Compiles sources.
  console          Starts the Scala interpreter with the project classes on the classpath.
  consoleProject   Starts the Scala interpreter with the sbt and the build definition on the classpath and useful imports.
  consoleQuick     Starts the Scala interpreter with the project dependencies on the classpath.
  copyResources    Copies resources to the output directory.
  doc              Generates API documentation.
  package          Produces the main artifact, such as a binary jar.  This is typically an alias for the task that actually does the packaging.
  packageBin       Produces a main artifact, such as a binary jar.
  packageDoc       Produces a documentation artifact, such as a jar containing API documentation.
  packageSrc       Produces a source artifact, such as a jar containing sources and resources.
  publish          Publishes artifacts to a repository.
  publishLocal     Publishes artifacts to the local Ivy repository.
  publishM2        Publishes artifacts to the local Maven repository.
  run              Runs a main class, passing along arguments provided on the task line.
  runMain          Runs the main class selected by the first argument, passing the remaining arguments to the main method.
  test             Executes all tests.
  testOnly         Executes the tests provided as arguments or all tests if no arguments are provided.
  testQuick        Executes the tests that either failed before, were not run or whose transitive dependencies changed, among those provided as arguments.
  update           Resolves and optionally retrieves dependencies, producing a report.
```

> One thing that surprised me about `sbt` is that it runs all the tasks in parallel by default. If the tasks have dependencies, then `sbt` uses the task dependency graph to determine which tasks can run in parallel and which can run in a sequential manner. To make it clear, let's suppose we have three tasks `t1`, `t2`, and `t3`. `t1` and `t3` depends on `t2` then `t2` will run first and `t1` and `t3` will run in parallel.

## sbt modes

You can use `sbt` in two modes -- command-line mode and interactive mode. In the command-line mode, you run `sbt` task from your machine terminal. Once the task successfully finishes then `sbt` exits. For example, when you ran `sbt about` task, it printed `sbt` and build information on the console and then `sbt` exited and you were back to your terminal. In the interactive mode, you run `sbt` command and it launches a `sbt` shell. Inside the `sbt` shell session, you run `sbt` tasks.

## Create an sbt Scala project

In this tutorial, we will build a simple task management app -- `tasky`. A task management app will allow us to work with our daily to-do items. Create a new directory `tasky` at any convenient location on your filesystem. Once created, change directory to `tasky`:

```bash
$ mkdir tasky
$ cd tasky
```

> Code for demo application is on [GitHub](./tasky)

Inside the `tasky` directory, create a new file -- `build.sbt` to house the build script. `build.sbt` is the name of the sbt build script. The content of `build.sbt` is shown below:

```scala
name := "tasky"
version := "0.1.0"
```

`:=` is a function defined in the `sbt` library. It is used to define a setting that overwrites any previous value without referring to other settings. For example, `name := "tasky"` will overwrite any previous value set in the `name` variable.

Now, run the `sbt` command:

```
$ sbt
[info] Set current project to tasky (in build file:/Users/shekhargulati/blogs/tasky)
>
```

Once you are inside the `sbt` shell, you can run various `sbt` tasks. To view all the tasks available you can use `help` task:

```bash
> help
```

```
help                                    Displays this help message or prints detailed help on requested tasks (run 'help <task>').
completions                             Displays a list of completions for the given argument string (run 'completions <string>').
about                                   Displays basic information about sbt and the build.
tasks                                   Lists the tasks defined for the current project.
settings                                Lists the settings defined for the current project.
reload                                  (Re)loads the current project or changes to plugins project or returns from it.
projects                                Lists the names of available projects or temporarily adds/removes extra builds to the session.
project                                 Displays the current project or changes to the provided `project`.
set [every] <setting>                   Evaluates a Setting and applies it to the current project.
session                                 Manipulates session settings.  For details, run 'help session'.
inspect [uses|tree|definitions] <key>   Prints the value for 'key', the defining scope, delegates, related definitions, and dependencies.
<log-level>                             Sets the logging level to 'log-level'.  Valid levels: debug, info, warn, error
plugins                                 Lists currently available plugins.
; <task> (; <task>)*              Runs the provided semicolon-separated tasks.
~ <task>                             Executes the specified task whenever source files change.
last                                    Displays output from a previous task or the output from a specific task.
last-grep                               Shows lines from the last output for 'key' that match 'pattern'.
export <tasks>+                         Executes tasks and displays the equivalent task lines.
exit                                    Terminates the build.
--<task>                             Schedules a task to run before other tasks on startup.
show <key>                              Displays the result of evaluating the setting or task associated with 'key'.
all <task>+                             Executes all of the specified tasks concurrently.

More task help available using 'help <task>' for:
  !, +, ++, <, alias, append, apply, eval, iflast, onFailure, reboot, shell
```

By default, `sbt` follows Maven project layout i.e. Scala source files are placed inside `src/main/scala` and test source files are placed inside `src/test/scala`:

```bash
$ mkdir -p src/main/scala
$ mkdir -p src/test/scala
```

### sbt says Hello

Now, let's create a new Scala file `HelloSbt.scala` inside `src/main/scala` and place the following contents in it:

```scala
object HelloSbt extends App {
  println("Sbt says Hello!!")
}
```

Now you can run the code from inside the `sbt` shell by first compiling the code using `compile` task and then running it using the `run` task as shown below:

```
> compile
[info] Compiling 1 Scala source to /Users/shekhargulati/blogs/tasky/target/scala-2.10/classes...
[success] Total time: 2 s, completed 10 Jan, 2016 8:51:55 AM
```

```
> run
[info] Running HelloSbt
Sbt says Hello!!
[success] Total time: 0 s, completed 10 Jan, 2016 8:52:15 AM
```

### Set Scala version

In the build output shown above, you can see that `sbt` chose Scala version 2.10. You can specify a different Scala version using `scalaVersion` setting. Update the `build.sbt` with the `scalaVersion` setting.

```scala
name := "tasky"
version := "0.1.0"
scalaVersion := "2.11.6"
```

`sbt` will not pick any change in the `build.sbt` until you run the `reload` task. Execute the `reload` task to refresh the `sbt` shell with new build script:

```
> reload
[info] Set current project to tasky (in build file:/Users/shekhargulati/blogs/tasky/)
```

Now if you compile the project using `compile` task you will see that project is compiled using scala `2.11.6` version:

```
> compile
[info] Updating {file:/Users/shekhargulati/blogs/tasky/}tasky...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Compiling 1 Scala sources to /Users/shekhargulati/dev/blogs/tasky/target/scala-2.11/classes...
[info] 'compiler-interface' not yet compiled for Scala 2.11.6. Compiling...
[info]   Compilation completed in 7.316 s
[success] Total time: 8 s, completed 10 Jan, 2016 1:30:06 PM
```

> From the [sbt documentation](http://www.scala-sbt.org/0.13/docs/Howto-Scala.html): If the Scala version is not specified, the version sbt was built against is used. It is recommended to explicitly specify the version of Scala.  
> Please note that because `compile` is a dependency of `run`, you don’t have to run `compile` before each `run`; just type `sbt run`.

### Building Tasky application

Let's create a simple data model for our task management application. Create a new file `datamodels.scala` inside the `src/main/scala`. Fill the file with the following contents.

```scala
import java.time.LocalDate

case class Task(title: String, dueOn: LocalDate, tags: Seq[String] = Seq(), finished: Boolean = false)
```

> Please note we are using Java 8 Date-Time API. If you want to learn Java 8, then you can refer to my Java 8 tutorial  [https://github.com/shekhargulati/java8-the-missing-tutorial](https://github.com/shekhargulati/java8-the-missing-tutorial)

If you are inside the `sbt` shell, then you can compile the code using `compile` task. To experiment with your data model, you can use `sbt` in an interactive mode by executing the `console` task from within the `sbt` shell:

```
> console
[info] Updating {file:/Users/shekhargulati/blogs/tasky/}tasky...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] Compiling 3 Scala sources to /Users/shekhargulati/blogs/tasky/target/scala-2.11/classes...
[info] Starting scala interpreter...
[info]
Welcome to Scala version 2.11.6 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_60).
Type in expressions to have them evaluated.
Type :help for more information.

scala>
```

Now you can start using `Task` class. Let's create a task:

```scala
scala> import java.time.{LocalDate,Month}
import java.time.{LocalDate, Month}

scala> val t1 = Task("Write blog on SBT", LocalDate.of(2016,Month.JANUARY,10), Seq("blogging"))
t1: Task = Task(Write blog on SBT,2016-01-10,List(blogging),false)

scala> val t2 = Task("Write a factorial program", LocalDate.of(2016,Month.JANUARY,11), Seq("coding"))
t2: Task = Task(Write a factorial program,2016-01-11,List(coding),false)
```

We can find all the tasks which are due today by writing the following code:

```scala
scala> val tasks = Seq(t1,t2)
tasks: Seq[Task] = List(Task(Write blog on SBT,2016-01-10,List(blogging),false), Task(Write a factorial program,2016-01-11,List(coding),false))

scala> tasks.filter(t => LocalDate.now().isEqual(t.dueOn))
res3: Seq[Task] = List(Task(Write blog on SBT,2016-01-10,List(blogging),false))
```

This is the kind of experiment driven development that sbt promotes. Once you have a rough idea of what you want to do then, you can start using the TDD approach to get things done.

Create a new scala file `taskmanager.scala` inside `src/main/scala` and place the following content:

```scala
object TaskManager {

  def allTasksDueToday(tasks: List[Task]): List[Task] = Nil

}
```

In the code shown above, we have created a scala object `TaskManager` which defines a single method `allTasksDueToday`. Currently, we have not written any implementation as we will first write test case for this method. Let's start with writing a test case for `allTasksDueToday` method. To write a test case, we have to choose a scala library that we can use. For this tutorial, we will use `scalatest` library.

#### Add Dependencies

To add `scalatest` dependency to your Scala sbt project, add the following line to `build.sbt`. `sbt` uses Apache Ivy dependency manager to perform automatic dependency management:

```scala
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"
```

`sbt` exposes keys that are defined in [Keys.scala](http://www.scala-sbt.org/0.13/sxr/sbt/Keys.scala.html) to `build.sbt`. Keys are of three types: Setting Key, Task Key, and Input Key. From the [sbt documentation](http://www.scala-sbt.org/release/tutorial/Basic-Def.html),

* `SettingKey[T]`: a key for a value computed once (the value is computed when loading the project, and kept around).

* `TaskKey[T]`: a key for a value, called a task, that has to be recomputed each time, potentially with side effects.

* `InputKey[T]`: a key for a task that has task line arguments as input.

Syntax to add a library to build.sbt looks like as shown below:

```
libraryDependencies += groupID % artifactID % version % configuration
```

`configuration` is not required for all dependencies. For `scalatest` dependency we have used `test` configuration.

`libraryDependencies` is a SettingKey that stores all declared managed dependencies. This key is populated only once when a project is loaded and then it is reused. Whenever you add a dependency in `build.sbt` file then you have to call the reload task to update dependencies:

```
> reload
[info] Set current project to tasky (in build file:/Users/shekhargulati/blogs/tasky/)
```

> `reload` will not download the dependencies that you add in the `build.sbt` file. It will only refresh the project model so when you run task next time it will download all the required dependencies.

#### Run Tests

Let's write a test for `TaskManager` `allTasksDueToday` method. There are various testing styles that you can use with `scalatest`. In this tutorial, I am using `FlatSpec` style. You can refer to [scalatest documentation for more information](http://www.scalatest.org/user_guide/selecting_a_style).

Create `src/test/scala/TaskManagerSpec.scala` and add the following code to it:

```scala
import org.scalatest._

class TaskManagerSpec extends FlatSpec with Matchers {

  "An empty tasks list" should "have 0 tasks due today" in {
      val tasksDueToday = TaskManager.allTasksDueToday(List())
      tasksDueToday should have length 0
  }

}
```

To run the test you can execute the test task:

```
> test
[info] Updating {file:/Users/shekhargulati/blogs/tasky/}tasky...
[info] Resolving jline#jline;2.12.1 ...
[info] downloading https://jcenter.bintray.com/org/scalatest/scalatest_2.11/2.2.6/scalatest_2.11-2.2.6.jar ...
[info] 	[SUCCESSFUL ] org.scalatest#scalatest_2.11;2.2.6!scalatest_2.11.jar(bundle) (42568ms)
[info] Done updating.
[info] Compiling 1 Scala source to /Users/shekhargulati/blogs/tasky/target/scala-2.11/classes...
[info] Compiling 1 Scala source to  /Users/shekhargulati/blogs/tasky/target/scala-2.11/test-classes...
[info] TaskManagerSpec:
[info] An empty tasks list
[info] - should have 0 tasks due today
[info] Run completed in 215 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 1, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 2 s, completed 10 Jan, 2016 2:15:41 PM
```

> Please note first time you run the `test` task test dependencies will be downloaded from a central repository as can be seen from the output of test task shown above.

#### Rerun Tests

One of the coolest features of `sbt` is that it can rerun your tasks without manual intervention whenever any project source file changes. This is enabled using the `~` operator. If you prefix any sbt task with `~` then `sbt` will wait for changes in the source files. As soon as any file changes, it will rerun that task.

Type the `~test` command inside `sbt` shell:

```
> ~test
[info] TaskManagerSpec:
[info] An empty tasks list
[info] - should have 0 tasks due today
[info] Run completed in 167 milliseconds.
[info] Total number of tests run: 1
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 1, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 1 s, completed 10 Jan, 2016 2:43:45 PM
1. Waiting for source changes... (press enter to interrupt)
```

As you can see above, `~test` ran the our `TaskManagerSpec` and then entered into watch mode. If we add any new test or change the existing test then `test` task will run again.

Let's add a new test case for scenario when we have a non-empty task list.

```scala
import org.scalatest._
import java.time.LocalDate

class TaskManagerSpec extends FlatSpec with Matchers {

  "An empty tasks list" should "have 0 tasks due today" in {
      val tasksDueToday = TaskManager.allTasksDueToday(List())
      tasksDueToday should have length 0
  }

  "A task list with one task due today" should "have 1 task due today" in {
    val t1 = Task("Write blog on SBT", LocalDate.now(), Seq("blogging"))
    val t2 = Task("Write a factorial program", LocalDate.now().plusDays(1), Seq("coding"))
    val tasksDueToday = TaskManager.allTasksDueToday(List(t1, t2))
    tasksDueToday should have length 1
  }

}
```

As soon as you save the file, `sbt` will detect file content change and rerun all the tests. The newly added test will fail as we have not yet added the actual implementation in `allTasksDueToday` method. `sbt` console output is shown below.

```
1. Waiting for source changes... (press enter to interrupt)
[info] Compiling 1 Scala source to /Users/shekhargulati/blogs/tasky/target/scala-2.11/test-classes...
[info] TaskManagerSpec:
[info] An empty tasks list
[info] - should have 0 tasks due today
[info] A task list with one task due today
[info] - should have 1 task due today *** FAILED ***
[info]   List() had length 0 instead of expected length 1 (TaskManagerSpec.scala:15)
[info] Run completed in 172 milliseconds.
[info] Total number of tests run: 2
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 1, failed 1, canceled 0, ignored 0, pending 0
[info] *** 1 TEST FAILED ***
[error] Failed tests:
[error] 	TaskManagerSpec
[error] (test:test) sbt.TestsFailedException: Tests unsuccessful
[error] Total time: 3 s, completed 10 Jan, 2016 2:50:53 PM
2. Waiting for source changes... (press enter to interrupt)
```

Let's add the actual implementation to `allTasksDueToday` method:

```scala
import java.time.LocalDate

object TaskManager {

  def allTasksDueToday(tasks: List[Task]): List[Task] = tasks.filter(t => t.dueOn.isEqual(LocalDate.now))

}
```

Now, tests will pass and you will see the following output in the sbt console:

```
2. Waiting for source changes... (press enter to interrupt)
[info] Compiling 1 Scala source to /Users/shekhargulati/blogs/tasky/target/scala-2.11/classes...
[info] TaskManagerSpec:
[info] An empty tasks list
[info] - should have 0 tasks due today
[info] A task list with one task due today
[info] - should have 1 task due today
[info] Run completed in 143 milliseconds.
[info] Total number of tests run: 2
[info] Suites: completed 1, aborted 0
[info] Tests: succeeded 2, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 1 s, completed 10 Jan, 2016 2:55:37 PM
3. Waiting for source changes... (press enter to interrupt)
```

## Writing your own tasks

`sbt` makes it very easy to define your own tasks. Let's write a simple task that prints total number of commits on the current Git branch. Creating a custom task is a two step process:

1. You have to define a `TaskKey` for your task
2. You have to provide the task definition

To write our task we will first write `gitCommitCountTask` `taskKey` in the `build.sbt` file:

```scala
val gitCommitCountTask = taskKey[String]("Prints commit count of the current branch")
```

The type specified in the taskKey i.e. String in this case becomes the type of the task result.

The task definition of the `gitCommitCountTask` is shown below. It uses `git` command-line to get the relevant information:

```scala
gitCommitCountTask := {
  val branch = scala.sys.process.Process("git symbolic-ref -q HEAD").lines.head.replace("refs/heads/","")
  val commitCount = scala.sys.process.Process(s"git rev-list --count $branch").lines.head
  println(s"total number of commits on [$branch]: $commitCount")
  commitCount
}
```

You can run the `gitCommitCountTask` task as shown below. You can also execute the `gitCommitCountTask` from inside the sbt shell:

```
$ sbt gitCommitCountTask
[info] Set current project to tasky (in build file:/Users/shekhargulati/blogs/tasky/)
total number of commits on [master]: 10
[success] Total time: 0 s, completed 10 Jan, 2016 5:16:07 PM
```

To learn more about writing custom tasks refer to [sbt documentation](http://www.scala-sbt.org/0.13/tutorial/Custom-Settings.html).

## Using plugins

Plugin allows you to package your tasks so that you can distribute and reuse them easily. One of the plugins that I include in my Scala projects is Scalastyle sbt plugin. Scalastyle is a style checkers for the Scala programming language. From the [Scalastyle project website](http://www.scalastyle.org/),

> Scalastyle examines your Scala code and indicates potential problems with it. If you have come across Checkstyle for Java, then you’ll have a good idea what scalastyle is. Except that it’s for Scala obviously.

To include `scalastyle-sbt-plugin` in your build, you have to add `scalastyle-sbt-plugin` inside `project/plugins.sbt` file. Create a new file `project/plugins.sbt` and add the following content in it:

```scala
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")
```

> It is a naming convention to define plugins in the `plugins.sbt` file. You can name it anything else as well.

Once you have defined the plugin, you can use the plugin by executing the task it exposes. The `scalastyle-sbt-plugin` exposes `scalastyle` task. Let's check the quality of our code:

```
→ sbt scalastyle
[info] Loading project definition from /Users/shekhargulati/blogs/tasky/project
[info] Resolving org.fusesource.jansi#jansi;1.4 ...
[info] downloading https://repo1.maven.org/maven2/org/scalastyle/scalastyle-sbt-plugin_2.10_0.13/0.8.0/scalastyle-sbt-plugin-0.8.0.jar ...
[info] 	[SUCCESSFUL ] org.scalastyle#scalastyle-sbt-plugin;0.8.0!scalastyle-sbt-plugin.jar (3199ms)
[info] downloading https://jcenter.bintray.com/org/scalastyle/scalastyle_2.10/0.8.0/scalastyle_2.10-0.8.0.jar ...
[info] 	[SUCCESSFUL ] org.scalastyle#scalastyle_2.10;0.8.0!scalastyle_2.10.jar (20244ms)
[info] downloading https://jcenter.bintray.com/org/scalariform/scalariform_2.10/0.1.7/scalariform_2.10-0.1.7.jar ...
[info] 	[SUCCESSFUL ] org.scalariform#scalariform_2.10;0.1.7!scalariform_2.10.jar (46701ms)
[info] downloading https://jcenter.bintray.com/com/typesafe/config/1.2.0/config-1.2.0.jar ...
[info] 	[SUCCESSFUL ] com.typesafe#config;1.2.0!config.jar(bundle) (9354ms)
[info] Done updating.
[info] Set current project to tasky (in build file:/Users/shekhargulati/blogs/tasky/)
[info] scalastyle using config /Users/shekhargulati/blogs/tasky/scalastyle-config.xml
java.lang.RuntimeException: config does not exist: scalastyle-config.xml
	at scala.sys.package$.error(package.scala:27)
[error] (compile:scalastyle) config does not exist: scalastyle-config.xml
[error] Total time: 0 s, completed 10 Jan, 2016 6:01:23 PM
```

The task will fail because plugin could not find `scalastyle-config.xml`. You can generate the configuration file using the `scalastyleGenerateConfig` task:

```
→ sbt scalastyleGenerateConfig
[info] Loading project definition from /Users/shekhargulati/blogs/tasky/project
[info] Set current project to tasky (in build file:/Users/shekhargulati/blogs/tasky/)
[success] created: /Users/shekhargulati/blogs/tasky/scalastyle-config.xml
[success] Total time: 0 s, completed 10 Jan, 2016 6:04:04 PM
```

Now, re-run the `scalastyle` task to check the quality of your project. This time task will get executed successfully.

You can learn more about Scalastyle from its website [http://www.scalastyle.org/](http://www.scalastyle.org/).

## Tips

These are some of the quick tips that might help you when you use `sbt`.

### Tip 1: Getting the right Scala version with %%

As mentioned before Scala remain binary compatible only between minor versions. This results in various library versions for different scala versions. Few sections back, we used scalatest library. The dependency was defined as follows:

```scala
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"
```

The scala version was specified in the artifactID "scalatest_2.11". This means every time we update the Scala version we would have to update the dependency. We can implicitly get the Scala version using the `%%` operator:

```scala
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
```

### Tip 2: Cross-compilation for multiple Scala versions

One thing that we all Java developers take for granted is that Java remain binary compatible between releases. This means you can run code written using JDK 1.0 on JDK 1.8. This is not true for Scala. Scala only remains binary compatible between minor versions i.e. 2.10.1 will remain binary compatible with minor version 2.10.2 but not with major version 2.11.0. Let's suppose you have a library that you want to compile using different versions of Scala. In your `build.sbt` you can .

```scala
scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.9.1", "2.10.1")
```

Now, when you will use sbt to build the project by default, it will build the project against the Scala version 2.11.1 but, you have an option to use other Scala versions defined in your build script.


### Tip 3: Pass options to Scala compiler

You can pass options to `scalac` by defining a setting `scalacOptions` as shown below:

```scala
scalacOptions ++= Seq("-feature", "-language:_", "-unchecked", "-deprecation", "-encoding", "utf8")
```

### Tip 4: View compile classpath dependencies

To view compile classpath dependencies you can run the following task from inside the sbt shell. Task and its output is shown below:

```scala
> show compile:dependencyClasspath

[info] List(Attributed(/Users/shekhargulati/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.11.6.jar), Attributed(/Users/shekhargulati/.ivy2/cache/com.typesafe.slick/slick_2.11/bundles/slick_2.11-3.1.1.jar), Attributed(/Users/shekhargulati/.ivy2/cache/org.slf4j/slf4j-api/jars/slf4j-api-1.7.10.jar), Attributed(/Users/shekhargulati/.ivy2/cache/com.typesafe/config/bundles/config-1.2.1.jar), Attributed(/Users/shekhargulati/.ivy2/cache/org.reactivestreams/reactive-streams/jars/reactive-streams-1.0.0.jar), Attributed(/Users/shekhargulati/.ivy2/cache/ch.qos.logback/logback-classic/jars/logback-classic-1.1.3.jar), Attributed(/Users/shekhargulati/.ivy2/cache/ch.qos.logback/logback-core/jars/logback-core-1.1.3.jar))
```

Similarly, if you have to view test classpath then you can run `show test:dependencyClasspath` task.

### Tip 5: View dependency graph

If you are Maven or Gradle user then one command that you would like to use is to view the dependency graph. sbt does not have a inbuilt command to view the dependency graph. You can view the dependency graph by using [sbt-dependency-graph plugin](https://github.com/jrudolph/sbt-dependency-graph).

To use the plugin, first add the plugin to `project/plugins.sbt`:

```scala
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.1")
```

Once done, reload the build configuration using the `reload` task.

Now, you will be able to use tasks defined by sbt-dependency-graph plugin. You can refer to sbt-dependency-graph plugin [documentation](https://github.com/jrudolph/sbt-dependency-graph#main-tasks) to get an overview of all the defined tasks:

```
> dependencyTree
[info] Updating {file:/Users/shekhargulati/blogs/fitman/}fitman...
[info] Resolving jline#jline;2.12.1 ...
[info] Done updating.
[info] default:fitman_2.11:0.1.0 [S]
[info]   +-ch.qos.logback:logback-classic:1.1.3
[info]   | +-ch.qos.logback:logback-core:1.1.3
[info]   | +-org.slf4j:slf4j-api:1.7.10
[info]   | +-org.slf4j:slf4j-api:1.7.7 (evicted by: 1.7.10)
[info]   |
[info]   +-com.typesafe.slick:slick_2.11:3.1.1 [S]
[info]     +-com.typesafe:config:1.2.1
[info]     +-org.reactivestreams:reactive-streams:1.0.0
[info]     +-org.slf4j:slf4j-api:1.7.10
[info]
[success] Total time: 0 s, completed 17 Jan, 2016 3:00:51 PM
```

That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/2](https://github.com/shekhargulati/52-technologies-in-2016/issues/2).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/02-sbt)](https://github.com/igrigorik/ga-beacon)
