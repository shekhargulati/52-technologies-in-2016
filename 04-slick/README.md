Slick 3: Functional Relational Mapping for Mere Mortals Part 1
----

Welcome to the fourth blog of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. Today, we will get started with Slick. Slick(Scala Language-Integrated Connection Kit) is a powerful Scala library to work with relational databases. **Slick is not an ORM library**. It bases its implementation on **functional programming** and does not hide database behind an ORM layer giving you full control over when a database access should happen. It allows you to work with database just like you are working with Scala collections. Slick API is asynchronous in nature making it suitable for building reactive applications. Although Slick itself is asynchronous in nature, internally it uses JDBC which is a synchronous API. Slick is a big topic so today we will only cover the basics. I will write couple more parts to this blog.

The core idea behind Slick is that as a developer you don't have to write SQL queries. Instead, library will create SQL for you if you build the query using the constructs provided by the library.

<img src="http://slick.typesafe.com/resources/images/slick-logo.png">

Benefits of using slick:

1. Type safety and compile time checking
2. Generate query for any database
3. Composable
4. Back-pressure built-in
5. Streaming support via reactive streams
6. You can use SQL as well

From the [Slick docs](http://slick.typesafe.com/doc/3.1.1/introduction.html#functional-relational-mapping):

> **The language integrated query model in Slick’s FRM is inspired by the LINQ project at Microsoft and leverages concepts tracing all the way back to the early work of Mnesia at Ericsson.**

Slick supports most of the relational databases in the market. You can view full list [here](http://slick.typesafe.com/doc/3.1.1/supported-databases.html). You can work with all open source databases like MySQL, PostgreSQL for free. Databases like Oracle, SQL Server, and DB2 are available as closed extensions that you can use only after buying subscription.


> **This blog is part of my year long blog series [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016)**

## Github repository

The code for today’s demo application is available on github: [tasky](./tasky).

## Getting Started

Create a new directory `tasky` on your filesystem. Inside the `tasky` directory create a sbt build file `build.sbt` with the following contents.

```scala
name := "tasky"

description := "A simple task manager for humans"

version := "0.1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.1.1"
libraryDependencies += "com.h2database" % "h2" % "1.4.191"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
```

> **In this tutorial, we will Slick version 3.1.1**

In the `build.sbt` file shown above, we have first defined basic information about the project like name, version, and description. We have also specified that we are going to use Scala version `2.11.7`.  After that we have declared few dependencies. The only required dependency is of `Slick`. `logback` is used for logging and `scalatest` will be used for writing test cases. In this tutorial, we will use `h2` database so we have declared its dependency as well. `h2` is an in-memory SQL database implementation written in `Java`. It runs in the same process as your application and is useful for testing and getting started purposes. For real apps, you should use databases like MySQL or PostgreSQL.

Create the following directory structure inside the `tasky` directory.

```bash
$ mkdir -p src/main/scala
$ mkdir -p src/test/scala
```

Now, we have a basic Scala SBT project setup for Slick application development.

Next, import the project in your favorite IDE.

## Define Database Tables

Tables represent mapping between Scala datatypes and database tables. Create a new package `datamodel` inside the `src/main/scala` directory.

Inside the `datamodel` package, create a scala object `DataModel.scala`.

```scala
package datamodel

import slick.driver.H2Driver.api._

object DataModel {

}
```

The import `slick.driver.H2Driver.api._` is required to tell which Slick database API we will use in our application. As shown above, we are using H2 for our application.

Let's create a new Scala datatype for our task management application. To keep things simple and easy to understand, we will start with only one domain object i.e. Task. `Task` case class is shown below.

```scala
import java.time.LocalDateTime

object DataModel {

  case class Task(
                   title: String,
                   description: String = "",
                   createdAt: LocalDateTime = LocalDateTime.now(),
                   dueBy: LocalDateTime,
                   tags: Set[String] = Set(),
                   id: Long = 0L)
}
```

The case class represent a `Task` datatype with six fields. This will map to a task table that will store a list of tasks that a user has to perform. As you can see, we have used different datatypes like String, Java 8 LocalDateTime, Set, and Long. LocalDateTime is part of Java 8 Date Time API. We have also given default values to some of these fields. This will allow us to not pass these value when we are constructing task objects. So, we can create a task by just providing `title` and `dueBy` values.

> **Please refer to [my Java 8 tutorial](https://github.com/shekhargulati/java8-the-missing-tutorial/blob/master/08-date-time-api.md) if you are new to Java 8**

Now let's create a table mapping for our Task case class.

```scala
object DataModel {

  case class Task(
                   title: String,
                   description: String = "",
                   createdAt: LocalDateTime = LocalDateTime.now(),
                   dueBy: LocalDateTime,
                   tags: Set[String] = Set(),
                   id: Long = 0L)

  class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
    def title = column[String]("title")

    def description = column[String]("description")

    def createdAt = column[LocalDateTime]("createdAt")

    def dueBy = column[LocalDateTime]("dueBy")

    def tags = column[Set[String]]("tags")

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    override def * = (title, description, createdAt, dueBy, tags, id) <>(Task.tupled, Task.unapply)
  }
}
```

Let's understand the `TaksTable` class code shown above.

1. Every table needs to extend `Table` abstract class. Table class needs a type parameter that tells what we will store in our table. Here, we are storing `Task` in the TaskTable. TaskTable constructors needs two mandatory fields - tag and table name. As shown above, we have used `tasks` as the name of our table. `tag` is something internal to slick that you have to pass to the `Table` constructor. This is used by slick to determine shape of a single table row. There is not much mentioned about `tag` in the slick documentation so I might not be 100% correct.

2. Next, we defined definitions of each of the columns. These map one-to-one to our domain class `Task`.

3. `id` is our primary key. In the column definition, we have said slick to make id an auto incrementing primary key. This will make sure database allocate id to each row in auto increment manner.

4. The `*` method is the default projection of our table. You have to define this method in your `Table` class. The type of the `*` projection has to be the same as type specified in the `Table` type parameter. In our case, both have to be `Task`. The `<>` method is used to convert between a tuple `(title, description, createdAt, dueBy, tags, id)` and `Task` data type. The `<>` needs two functions - first takes a tuple and convert it to an object and second a function that converts an object to a tuple.

> It is not required to use a case class you could have also used a regular Scala class as well. If you do use a regular class, then you have to provide two extra functions corresponding to `tupled` and `unapply`. The advantage that we get by using a case class is that it provides `tupled` and `unapply` methods. In the code shown below, we have created a Task object and defined two methods `toTask` and `fromTask`. These methods will serve the purpose of `tupled` and `unapply` methods.

```scala
class Task(
            val title: String,
            val description: String = "",
            val createdAt: LocalDateTime = LocalDateTime.now(),
            val dueBy: LocalDateTime,
            val tags: Set[String] = Set[String](),
            val id: Long = 0L)

object Task {

  def apply(title: String,
            description: String = "",
            createdAt: LocalDateTime = LocalDateTime.now(),
            dueBy: LocalDateTime,
            tags: Set[String] = Set[String](),
            id: Long = 0L): Task = new Task(title, description, createdAt, dueBy, tags, id)

  def toTask(t: (String, String, LocalDateTime, LocalDateTime, Set[String], Long)): Task = new Task(t._1, t._2, t._3, t._4, t._5, t._6)

  def fromTask(task: Task): Option[(String, String, LocalDateTime, LocalDateTime, Set[String], Long)] = Some((task.title, task.description, task.createdAt, task.dueBy, task.tags, task.id))
}
```

## Create TableQuery object

Once we have defined our table definition `TaskTable`, we have to define a value of type `TableQuery` which represents an actual database table. It provides a query DSL that you can use to interact with the table.

```scala
lazy val Tasks = TableQuery[TaskTable]
```

## Define Custom Mapping

If you try to compile the code that we have written so far it will not compile. The reason for that is slick does not support Java 8 `LocalDateTime` and `Set[String]` datatypes for column definition. However, we can write our custom mappers that will convert our types to the type `Slick` understands. Create a new object `ColumnDataMapper` in the same file `DataModel.scala` as shown below.

```scala
object ColumnDataMapper {

  implicit val localDateTimeColumnType = MappedColumnType.base[LocalDateTime, Timestamp](
    ldt => Timestamp.valueOf(ldt),
    t => t.toLocalDateTime
  )

  implicit val setStringColumnType = MappedColumnType.base[Set[String], String](
    tags => tags.mkString(","),
    tagsString => tagsString.split(",").toSet
  )

}
```

In the code shown above, we have defined two mapper -- a) converts between `LocalDateTime` to `java.sql.Timestamp` and vice-versa b) converts between `Set[String]` to `String` and vice-versa.

Now, add the import for your custom data mappings. You have to explicitly add the custom mappers to the column definition.

```scala
import datamodel.ColumnDataMapper.{localDateTimeColumnType, setStringColumnType}

class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
  def title = column[String]("title")

  def description = column[String]("description")

  def createdAt = column[LocalDateTime]("createdAt")(localDateTimeColumnType)

  def dueBy = column[LocalDateTime]("dueBy")(localDateTimeColumnType)

  def tags = column[Set[String]]("tags")(setStringColumnType)

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  override def * = (title, description, createdAt, dueBy, tags, id) <>(Task.tupled, Task.unapply)
}
```

Now, code will compile successfully. You can use `sbt compile` task to compile the application.

## Define Schema Create Action

Action represents commands that we want to run against database. Let's write our first action that will create the database schema.

```scala
lazy val Tasks = TableQuery[TaskTable]

val createTaskTableAction = Tasks.schema.create
```

The `createTaskTableAction` action will create the schema when it is executed against database. **Defining an action does not execute it**.

## Execute Schema Create Action

Actions are executed against a database. Slick provides a `Database` type that allows our code to interact with the database. It is a handle to a specific database. To get the handle to a database, you use the following code.

```scala
val db = Database.forConfig("taskydb")
```

The `taskydb` is a reference to a configuration object defined using typesafe config project.

Let's write our first test case that will use the database object to create the schema. In the `src/test/scala`, create a new package `datamodel`. Create a new Scala class `CreateDatabaseSpec` as shown below.

```scala
package datamodel

import org.scalatest.{FunSpec, Matchers}

import slick.driver.H2Driver.api._

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class CreateDatabaseSpec extends FunSpec with Matchers {

  describe("DataModel Spec") {

    it("should create database") {
      val db = Database.forConfig("taskydb")
      val result = Await.result(db.run(DataModel.createTaskTableAction), 2 seconds)
      println(result)
    }

  }
}
```
In the code shown above:

1. The `scala.concurrent` set of imports are required to tell slick that we will use ExecutionContext defined by the import to execute slick code. We have to do this because slick API is fully asynchronous and executes database calls in a separate thread pool.

2. Then we created our database object using the `taskydb` configuration.  This gives us the handle to interact with database.

3. The db object has a method called `run` that executes an action and returns a `Future`. As slick is async in nature, we have wrapped the future in a `Await.result` call to make it easy to test.

You will have to create a file called `application.conf` in the `src/test/resources` directory. Populate it with content shown below.

```
taskydb = {
  connectionPool      = disabled
  url                 = "jdbc:h2:mem:taskydb"
  driver              = "org.h2.Driver"
  keepAliveConnection = true
}
```

When you will run this code, you will see in the logs that it has create a database schema.

```
18:50:34.955 [ScalaTest-run-running-CreateDatabaseSpec] DEBUG s.backend.DatabaseComponent.action - #1: schema.create [create table "tasks" ("title" VARCHAR NOT NULL,"description" VARCHAR NOT NULL,"createdAt" TIMESTAMP NOT NULL,"dueBy" TIMESTAMP NOT NULL,"tags" VARCHAR NOT NULL,"id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY)]

18:50:35.012 [taskydb-1] DEBUG slick.jdbc.JdbcBackend.statement - Preparing statement: create table "tasks" ("title" VARCHAR NOT NULL,"description" VARCHAR NOT NULL,"createdAt" TIMESTAMP NOT NULL,"dueBy" TIMESTAMP NOT NULL,"tags" VARCHAR NOT NULL,"id" BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) NOT NULL PRIMARY KEY)
18:50:35.268 [taskydb-1] DEBUG slick.jdbc.JdbcBackend.benchmark - Execution of prepared statement took 23ms
```

On every run of our test case, we will create a new database.

## Insert tasks into Task table

Let's now write a test case that will insert some records into the `Task` table.

```scala
it("should insert single task into database") {
  val db = Database.forConfig("taskydb")
  val result = Await.result(db.run(DataModel.insertTaskAction(Task(title = "Learn Slick", dueBy = LocalDateTime.now().plusDays(1)))), 2 seconds)
  result should be(Some(1))
}
```

The test case shown above calls the `insertTaskAction` passing it a `Task`. The result of `insertTaskAction` is the number of rows affected by the action. As we are only passing one task so we should expect one as result.

Now, let's look at the `insertTaskAction` definition in the `DataModel` object.

```
def insertTaskAction(tasks: Task*) = Tasks ++= tasks.toSeq
```

The insertTaskAction takes a `varargs` of tasks allowing user to pass one or more tasks. To insert tasks, we used `++=` method.  According to [slick documentation](http://slick.typesafe.com/doc/3.0.0/queries.html#inserting),

> **`++=` gives you an accumulated count in an Option (which can be None if the database system does not provide counts for all rows)**


## Query table

Let's query the database to select all the tasks in the database.

```scala
it("should list all tasks in the database") {
  val tasks = Seq(
    Task(title = "Learn Slick", dueBy = LocalDateTime.now().plusDays(1)),
    Task(title = "Write blog on Slick", dueBy = LocalDateTime.now().plusDays(2)),
    Task(title = "Build a simple application using Slick", dueBy = LocalDateTime.now().plusDays(3))
  )
  Await.result(db.run(DataModel.insertTaskAction(tasks: _*)), 2 seconds)
  val result = Await.result(db.run(DataModel.listTasksAction), 2 seconds)
  result should have length 3
}
```

The test case shown above queries the database using `listTasksAction` shown below.

```scala
val listTasksAction = Tasks.result
```

The `listTasksAction` makes a `select "title", "description", "createdAt", "dueBy", "tags", "id" from "tasks"` sql query using the default `*` projection.


## Conclusion

Slick is a powerful library to interact with relational databases. Today, we have just scratched the surface of this feature rich library. You leant how to define table definition, insert data, perform `select *` query. I will write couple more blogs on Slick to cover it in more details. So stay tuned!

That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/6](https://github.com/shekhargulati/52-technologies-in-2016/issues/6).


[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/04-slick)](https://github.com/igrigorik/ga-beacon)
