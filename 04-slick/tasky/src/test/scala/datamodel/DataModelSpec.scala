package datamodel

import java.time.LocalDateTime

import datamodel.DataModel.Task
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}
import slick.driver.H2Driver.api._

import scala.concurrent._
import scala.concurrent.duration._

class DataModelSpec extends FunSpec with Matchers with BeforeAndAfterEach {

  var db: Database = _

  override protected def beforeEach(): Unit = {
    db = Database.forConfig("taskydb")
    Await.result(db.run(DataModel.createTaskTableAction), 2 seconds)
  }

  override protected def afterEach(): Unit = Await.result(db.shutdown, 2 seconds)

  describe("DataModel Spec") {

    it("should insert single task into database") {
      val result = Await.result(db.run(DataModel.insertTaskAction(Task(title = "Learn Slick", dueBy = LocalDateTime.now().plusDays(1)))), 2 seconds)
      result should be(Some(1))
    }

    it("should insert multiple tasks into database") {
      val tasks = Seq(
        Task(title = "Learn Slick", dueBy = LocalDateTime.now().plusDays(1)),
        Task(title = "Write blog on Slick", dueBy = LocalDateTime.now().plusDays(2)),
        Task(title = "Build a simple application using Slick", dueBy = LocalDateTime.now().plusDays(3))
      )
      val result = Await.result(db.run(DataModel.insertTaskAction(tasks: _*)), 2 seconds)
      result should be(Some(3))
    }

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

  }


}
