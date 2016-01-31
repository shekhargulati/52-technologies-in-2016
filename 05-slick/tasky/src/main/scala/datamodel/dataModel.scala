package datamodel

import java.time.LocalDateTime

import datamodel.Priority.Priority
import datamodel.columnDataMappers._
import slick.driver.H2Driver.api._

object dataModel {

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

  lazy val Tasks = TableQuery[TaskTable]

  val createTaskTableAction = Tasks.schema.create

  def insertTaskAction(tasks: Task*) = Tasks ++= tasks.toSeq

  val listAllTasksAction = Tasks.result
}




