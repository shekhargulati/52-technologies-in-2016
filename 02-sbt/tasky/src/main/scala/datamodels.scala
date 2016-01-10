import java.time.LocalDate

case class Task(title: String, dueOn: LocalDate, tags: Seq[String] = Seq(), finished: Boolean = false)
