package datamodel

object Priority extends Enumeration {
  type Priority = Value
  val HIGH = Value(3)
  val MEDIUM = Value(2)
  val LOW = Value(1)
}