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

  describe("Task Data Model Query Spec") {

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

    it("should select all task titles") {
      val taskTitles = performAction(selectAllTaskTitleQuery.result)
      taskTitles should have length 7
      taskTitles should be(List(t1.title, t2.title, t3.title, t4.title, t5.title, t6.title, t7.title))
    }

    it("should select task title, priority, and creation date for all tasks") {
      val taskTitles = performAction(selectMultipleColumnsQuery.result)
      taskTitles should have length 7
      taskTitles should be(List(
        (t1.title, t1.priority, t1.createdAt),
        (t2.title, t2.priority, t2.createdAt),
        (t3.title, t3.priority, t3.createdAt),
        (t4.title, t4.priority, t4.createdAt),
        (t5.title, t5.priority, t5.createdAt),
        (t6.title, t6.priority, t6.createdAt),
        (t7.title, t7.priority, t7.createdAt))
      )
    }

    it("should select all the high priority task titles"){
      val highPriorityTasks = performAction(selectHighPriorityTasksQuery.result)
      highPriorityTasks should have length 4
      highPriorityTasks should be(List(t1.title, t3.title, t5.title, t7.title))
    }

    it("should skip first 2 records and then limit result to 3") {
      val tasks = performAction(findAllTasksPageQuery(2, 3).result)
      tasks should have length 3
      tasks.head should have(
        'title (t3.title),
        'description (t3.description),
        'createdAt (t3.createdAt),
        'dueBy (t3.dueBy),
        'tags (t3.tags)
      )
    }

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

    it("should find all due tasks") {
      val dueTasks = performAction(findAllDueTasks.result)
      dueTasks should have length 5
      dueTasks.map(_.title) should be(List(t3.title, t4.title, t5.title, t6.title, t7.title))
    }

    it("should find all tasks due today") {
      val dueTasks = performAction(selectAllTaskTitlesDueToday.result)
      dueTasks should have length 1
      dueTasks should be(List(t3.title))
    }


    it("select tasks between today and same date next month "){
      val tasks = performAction(selectTasksBetweenTodayAndSameDateNextMonthQuery.result)
      tasks should have length 1
    }

    it("check if any high priority task exists today"){
      val exists = performAction(checkIfAnyHighPriorityTaskExistsToday.result)
      exists should be(true)
    }


  }


}