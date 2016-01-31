package datamodel

import java.sql.Timestamp
import java.time.LocalDateTime

import datamodel.Priority._
import slick.driver.H2Driver.api._

object columnDataMappers {

  implicit val localDateTimeColumnType: BaseColumnType[LocalDateTime] = MappedColumnType.base[LocalDateTime, Timestamp](
    ldt => Timestamp.valueOf(ldt),
    t => t.toLocalDateTime
  )

  implicit val setStringColumnType: BaseColumnType[Set[String]] = MappedColumnType.base[Set[String], String](
    tags => tags.mkString(","),
    tagsString => tagsString.split(",").toSet
  )


  implicit val priorityMapper = MappedColumnType.base[Priority, Int](
    p => p.id,
    v => Priority(v)
  )
}
