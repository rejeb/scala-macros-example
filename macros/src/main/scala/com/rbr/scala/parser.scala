package com.rbr.scala

import java.time.LocalDateTime

object parser {

  trait ValParser {
    type ValType
    val value: String
    def parse: ValType
  }

  case class StringParser(val value: String) extends ValParser {
    override type ValType = String

    override def parse: String = value
  }

  case class  IntParser(val value: String) extends ValParser {
    override type ValType = Int

    override def parse: Int = value.toInt
  }

  case class LongParser(val value: String) extends ValParser {
    override type ValType = Long

    override def parse: Long = value.toLong
  }

  case class BooleanParser(val value: String) extends ValParser {
    override type ValType = Boolean

    override def parse: Boolean = value.toBoolean
  }

  case class LocalDateTimeParser(val value: String) extends ValParser {
    override type ValType = LocalDateTime

    override def parse: LocalDateTime = LocalDateTime.parse(value)
  }

}


