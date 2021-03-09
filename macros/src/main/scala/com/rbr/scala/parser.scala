package com.rbr.scala

import java.time.LocalDateTime

object parser {

  def stringParser(value: Option[String]): String = stringOptionParser(value).orNull

  def stringOptionParser(value: Option[String]): Option[String] = value

  def intParser(value: Option[String]): Int = intOptionParser(value).getOrElse(Int.MinValue)

  def intOptionParser(value: Option[String]): Option[Int] = value.map(_.toInt)

  def longParser(value: Option[String]): Long = longOptionParser(value).getOrElse(Long.MinValue)

  def longOptionParser(value: Option[String]): Option[Long] = value.map(_.toLong)

  def booleanParser(value: Option[String]): Boolean = booleanOptionParser(value).getOrElse(false)

  def booleanOptionParser(value: Option[String]): Option[Boolean] = value.map(_.toBoolean)

  def localDateTimeParser(value: Option[String]): LocalDateTime = localDateTimeOptionParser(value).orNull

  def localDateTimeOptionParser(value: Option[String]): Option[LocalDateTime] = value.map(LocalDateTime.parse)


}


