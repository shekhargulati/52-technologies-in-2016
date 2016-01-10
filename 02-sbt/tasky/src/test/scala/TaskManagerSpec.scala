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
