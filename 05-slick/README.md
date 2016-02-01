Slick 3: Functional Relational Mapping for Mere Mortals Part 2: Querying data
----

Last week we learnt the [basics of Slick](https://github.com/shekhargulati/52-technologies-in-2016/tree/master/04-slick) library. We started with a general introduction of Slick, then covered how to define a table definition, custom mappers, and perform insert queries. Today, we will learn how to perform `select` queries with Slick. Slick allows you to work with database tables in the same way as you work with Scala collections. This means that you can use methods like `map`, `filter`, `sort`, etc. to process data in your table.

> **In case you are new to Slick, please first read [part 1 of Slick tutorial](https://github.com/shekhargulati/52-technologies-in-2016/tree/master/04-slick). This blog is part of my year long blog series [52 Technologies in 2016](https://github.com/shekhargulati/52-technologies-in-2016)**

## Github repository

The code for today’s demo application is available on github: [tasky](./tasky).

## Let's (again) look at the data model

Before we start with querying data, let's again look at the data model. I have added one more field to the `TaskTable`. The field that we have added is an enum to store priority of the task. Enums are useful when a variable can have one of the small set of possible values. In our example application, `Priority` is an enum that can be either `HIGH`, `LOW`, or `MEDIUM`. To create a new enum, create an object that extends `scala.Enumeration` as shown below. We have created `Priority` enum in a new file `Priority.scala` inside the `datamodel` package.

```scala
package datamodel

object Priority extends Enumeration {
  type Priority = Value
  val HIGH = Value(3)
  val MEDIUM = Value(2)
  val LOW = Value(1)
}
```

As you can see above, we have provided int values to each enum constant.

After creating our new enum, we have to add its declaration in our `Task` case class as well as `TaskTable`.

```scala
import datamodel.columnDataMappers._
case class Task(
                 title: String,
                 description: String = "",
                 createdAt: LocalDateTime = LocalDateTime.now(),
                 dueBy: LocalDateTime,
                 tags: Set[String] = Set[String](),
                 priority: Priority = Priority.LOW,
                 id: Long = 0L)


class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
  def title = column[String]("title")

  def description = column[String]("description")

  def createdAt = column[LocalDateTime]("createdAt")

  def dueBy = column[LocalDateTime]("dueBy")

  def tags = column[Set[String]]("tags")

  def priority = column[Priority]("priority")

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  override def * = (title, description, createdAt, dueBy, tags, priority, id) <>(Task.tupled, Task.unapply)
}
```

If we try to compile code now, it will not compile. We have to add column mapping to convert between `Priority` enum to `Int`. This is shown below.

```scala
implicit val priorityMapper = MappedColumnType.base[Priority, Int](
  p => p.id,
  v => Priority(v)
)
```

Compile and run the test cases using `sbt test` and everything should work fine.

## Select all the tasks in the database

Let's start with the simplest select query i.e. `select * from tasks`. We want to list all the tasks in our database. As discussed last week, we have to create an instance of `TableQuery` that will give us the handle to Slick Query DSL API. We already have instance of `TableQuery` created inside the `dataModels.scala`.

```scala
lazy val Tasks = TableQuery[TaskTable]
```

Create a new Scala object `queries` inside the `queries` package. This object will house all the queries.

```scala
package queries

import datamodel.columnDataMappers._
import datamodel.dataModel.Tasks
import slick.driver.H2Driver.api._

object queries {

}
```

As shown above, we have created a new Scala object `queries` and added the required imports.

1. `import datamodel.columnDataMappers._` is required so that Slick knows how to handle our custom data types like `LocalDateTime`, `Set[String]`, and `Priority`.

2. `import datamodel.dataModel.Tasks` is required so that we can work with the `Tasks` `TableQuery` object.

3. `import slick.driver.H2Driver.api._` is required to tell which Slick database API we will use in our application.

Before we will write query for listing all the tasks in the database let's write a test case. Create a new test specification `QueriesSpec` and populate it with following contents.

```scala
package queries

import java.time.LocalDateTime

import datamodel.dataModel.Task
import datamodel.{Priority, dataModel}
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}
import queries._
import slick.driver.H2Driver.api._

import scala.concurrent._
import scala.concurrent.duration._

class QueriesSpec extends FunSpec with Matchers with BeforeAndAfterAll {

  var db: Database = _
  var t1: Task = _
  var t2: Task = _
  var t3: Task = _
  var t4: Task = _
  var t5: Task = _
  var t6: Task = _
  var t7: Task = _

  override protected def beforeAll(): Unit = {
    db = Database.forConfig("taskydb")
    Await.result(db.run(dataModel.createTaskTableAction), 2 seconds)
    t1 = Task(title = "Write part 1 blog on Slick", dueBy = LocalDateTime.now().minusDays(7), tags = Set("blogging", "scala", "slick"), priority = Priority.HIGH)
    t2 = Task(title = "Give a Java 8 training", dueBy = LocalDateTime.now().minusDays(3), tags = Set("java", "training", "travel"), priority = Priority.LOW)
    t3 = Task(title = "Write part 2 blog on Slick queries", dueBy = LocalDateTime.now(), tags = Set("blogging", "scala", "slick"), priority = Priority.HIGH)
    t4 = Task(title = "Read Good to Great book", dueBy = LocalDateTime.now().plusDays(15), tags = Set("reading", "books", "startup"), priority = Priority.MEDIUM)
    t5 = Task(title = "Read Programming Scala book", dueBy = LocalDateTime.now().plusDays(30), tags = Set("reading", "books", "scala"), priority = Priority.HIGH)
    t6 = Task(title = "Go to Goa for holiday", dueBy = LocalDateTime.now().plusDays(60), tags = Set("travel"), priority = Priority.LOW)
    t7 = Task(title = "Build my dream application using Play framework and Slick", dueBy = LocalDateTime.now().plusMonths(3), tags = Set("application", "play", "startup"), priority = Priority.HIGH)
    val tasks = Seq(t1, t2, t3, t4, t5, t6, t7)
    performAction(dataModel.insertTaskAction(tasks: _*))
  }

  private def performAction[T](action: DBIO[T]): T = {
    Await.result(db.run(action), 2 seconds)
  }
}
```

In the code shown above, we have done the following:

1. We imported all the required classes and traits that are required by our test case.

2. We provided implementation of `beforeAll` method. This allows us to perform one time setup for this test case. We inserted seven tasks in the database using the `insertTaskAction` we discussed last week. In the task list shown above, there are two tasks that were due in past and 5 tasks which are due in future.

3. `performAction` is a method that will help us avoid writing boilerplate code of wrapping the future in an `Await`. We will just pass an action to `performAction` and it will take care of the rest. We will use this method in all our test cases.

Now, that we have setup our test data. We can write our first test case that will select all the tasks in the `tasks` table.

```scala
import queries._

it("should select all the tasks stored in the database") {
  val tasks = performAction(selectAllTasksQuery.result)
  tasks should have length 7
  tasks.head should have(
    'title (t1.title),
    'description (t1.description),
    'createdAt (t1.createdAt),
    'dueBy (t1.dueBy),
    'tags (t1.tags)
  )
}
```

In the code shown above, only thing that is of interest to us is the `selectAllTasksQuery`. This is imported from the `queries` object. `performAction` method discussed above needs an action. You can convert a query to an action by calling the `result` method on it. If you try to run the test case now, it will not work as we have not yet defined `selectAllTasksQuery`.

In the `queries` object, define `selectAllTasksQuery` as shown below.

```scala
object queries {
  val selectAllTasksQuery: Query[TaskTable, Task, Seq] = Tasks
}
```

Let's try to decipher one line of code that we have written above. In the code shown above, we have a defined a value `selectAllTasksQuery` that returns `Tasks` object. `Tasks` is an instance of `TableQuery` object we defined in `dataModels.scala`. `Tasks` i.e. `TableQuery` object is the gateway to the Slick query DSL API. When you return `Tasks` object then Slick uses the default `*` projection that we defined in the `TaskTable`.

The other interesting bit is the type of `selectAllTasksQuery`. You are not required to define the type here as Scala can infer the type. By understanding the type `Query[TaskTable, Task, Seq]`, you will understand how Slick determine what value should be returned by the query. `Query` takes three type parameters. The first type parameter is called the packed type i.e. the type of values you work against in the query DSL. The second type is called the unpacked type i.e. the type of values you get back when you run the query. The third type is the container type that collects the result.

Run the test case and it should pass.  You can look at the logs to confirm that Slick executed `select *` query.

```sql
select "title", "description", "createdAt", "dueBy", "tags", "priority", "id" from "tasks"
```

## Select all task titles

The first query that we saw above fetches all the columns of `tasks` table. Most of the time we only want to select few columns. Let's write our test case for this use case.

```scala
it("should select all task titles") {
  val taskTitles = performAction(selectAllTaskTitleQuery.result)
  taskTitles should have length 7
  taskTitles should be(List(t1.title, t2.title, t3.title, t4.title, t5.title, t6.title, t7.title))
}
```

As you can see above, we are executing `selectAllTaskTitleQuery`. This query is defined in `queries` object as shown below.

```scala
val selectAllTaskTitleQuery: Query[Rep[String], String, Seq] = Tasks.map(taskTable => taskTable.title)
```

In the code shown above, we have used map function on the `Tasks` table query object. `map` is a transformation function that take a lambda. The lambda function tells Slick that we only want to select title column. One thing to note here is that in the `map` function we are working on the `TaskTable` object. As `map` function only returns title so the type of `selectAllTaskTitleQuery` is `Query[Rep[String], String, Seq]`.

You can also use the shorthand `_` in the lambda as shown below.

```scala
val selectAllTaskTitleQuery: Query[Rep[String], String, Seq] = Tasks.map(_.title)
```

You can also select more than one columns in the map function as shown below.

```scala
val selectMultipleColumnsQuery: Query[(Rep[String], Rep[Priority], Rep[LocalDateTime]), (String, Priority, LocalDateTime), Seq] = Tasks.map(t => (t.title, t.priority, t.createdAt))
```

The query executed by Slick can be seen in the logs.

```sql
select "title", "priority", "createdAt" from "tasks"
```

## Select all the high priority task titles

So far we have selected all the data in our tasks table. There are times when we have to filter data as we have to do it this usecase. We have filter out all the high priority tasks and then select only title field. Let's write the test case first.

```scala
it("should select all the high priority task titles"){
  val highPriorityTasks = performAction(selectHighPriorityTasksQuery.result)
  highPriorityTasks should have length 4
  highPriorityTasks should be(List(t1.title, t3.title, t5.title, t7.title))
}
```

In the dataset that we created in `beforeAll` method, we have four high priority tasks.

The `selectHighPriorityTasksQuery` will use the `filter` and the `map` operation to get the job done. `filter` allows us to specify the `where` clauses.

```scala
val selectHighPriorityTasksQuery: Query[Rep[String], String, Seq] = Tasks.filter(_.priority === Priority.HIGH).map(_.title)
```

In the code shown above, we first filtered out all the high priority tasks and then selected only title column.

You can view the SQL query generated by Slick in the logs.

```sql
select "title" from "tasks" where "priority" = 3
```

## Paginate results

Slick allows you to paginate our the result by using the `drop` and `limit` methods of `TableQuery`. To skip first 3 elements and then limit the result to 2 records, you can write following Slick code.

```scala
Tasks.drop(3).take(2)
```

You can view the SQL query generated by Slick in the logs.

```sql
select "title", "description", "createdAt", "dueBy", "tags", "priority", "id" from "tasks" limit 3 offset 2
```

## Sort tasks in descending order of due date

A lot of times we have to work with data in some sorting order. Let's suppose, we want to work on the task that is due last. One way to sort would be to sort the data in your application code. You could also ask your database to return the data in sorted order by passing the `order by clause`. Let's write a test case to test this scenario.

```scala
it("should sort tasks in descending order of due date") {
  val tasks = performAction(selectTasksSortedByDueDateDescQuery.result)
  tasks.head should have(
    'title (t7.title),
    'description (t7.description),
    'createdAt (t7.createdAt),
    'dueBy (t7.dueBy),
    'tags (t7.tags)
  )
}
```

We have to define `selectTasksSortedByDueDateDescQuery` in the `queries` object as shown below.

```scala
val selectTasksSortedByDueDateDescQuery = Tasks.sortBy(_.dueBy.desc)
```

The reason `desc` is available on the `dueBy` is because for Slick it is a `Timestamp`. All the operations that work on `Timestamp` are available on the `dueBy` as well.

You can view the SQL query generated by Slick in the logs.

```sql
select "title", "description", "createdAt", "dueBy", "tags", "priority", "id" from "tasks" order by "dueBy" desc
```

## Select all tasks due today

To select all the tasks due today we can use `filter` operator as shown below. We are using `LocalDate` `asStartOfDay` method to define the time range of our where clause.

```scala
val selectAllTasksDueToday = Tasks
  .filter(t => t.dueBy > LocalDate.now().atStartOfDay() && t.dueBy < LocalDate.now().atStartOfDay().plusDays(1))
  .map(_.title)
```

You could have also used two filters instead of one as shown below.

```scala
val selectAllTasksDueToday = Tasks
  .filter(_.dueBy > LocalDate.now().atStartOfDay())
  .filter(_.dueBy < LocalDate.now().atStartOfDay().plusDays(1))
  .map(_.title)
```

You can view the SQL query generated by Slick in the logs.

```sql
select "title" from "tasks" where ("dueBy" > {ts '2016-01-31 00:00:00.0'}) and ("dueBy" < {ts '2016-02-01 00:00:00.0'})
```

## Select data with in a range

We can use the SQL `BETWEEN` operator to select data between two dates as shown below.

```scala
val selectTasksBetweenTodayAndSameDateNextMonthQuery = Tasks.filter(t => t.dueBy.between(LocalDateTime.now(), LocalDateTime.now().plusMonths(1)))
```

You can view the SQL query generated by Slick in the logs.

```sql
select "title", "description", "createdAt", "dueBy", "tags", "priority", "id" from "tasks" where "dueBy" between {ts '2016-01-31 21:44:40.643'} and {ts '2016-02-29 21:44:40.643'}
```

## Check if any high priority task is pending today

You can use SQL `exists` operator as shown below.

```scala
val selectAllTasksDueToday = Tasks
  .filter(_.dueBy > LocalDate.now().atStartOfDay())
  .filter(_.dueBy < LocalDate.now().atStartOfDay().plusDays(1))

val checkIfAnyHighPriorityTaskExistsToday = selectAllTasksDueToday.filter(_.priority === Priority.HIGH).exists
```

You can view the SQL query generated by Slick in the logs.

```sql
select exists(select "description", "createdAt", "priority", "tags", "dueBy", "id", "title" from "tasks" where (("dueBy" > {ts '2016-01-31 00:00:00.0'}) and ("dueBy" < {ts '2016-02-01 00:00:00.0'})) and ("priority" = 3))
```

There are many more aggregate functions like `max`, `min`, `average` that you can use.

## Conclusion

Today, we looked at how we can use the Slick library to query our data. If you have used Scala collections or Java 8 Streams you should feel home. We still haven't covered many other important Slick topics like joins, profiles, working with real databases like MySQL or PostgreSQL, etc.  I will write at least one more post about Slick so that we have good understanding of it.

That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/7](https://github.com/shekhargulati/52-technologies-in-2016/issues/7).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/05-slick)](https://github.com/igrigorik/ga-beacon)
