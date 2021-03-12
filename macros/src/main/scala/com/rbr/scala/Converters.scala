package com.rbr.scala

import com.rbr.scala.annotations.Chronic

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Converters {
  private val localDateTimePattern = DateTimeFormatter.ISO_LOCAL_DATE_TIME
  implicit def stringToInt(value: Option[String]): Int = stringToIntOptionParser(value).getOrElse(Int.MinValue)

  implicit def stringToIntOptionParser(value: Option[String]): Option[Int] = value.map(_.toInt)

  implicit def stringToLong(value: Option[String]): Long = stringToLongOption(value).getOrElse(Long.MinValue)

  implicit def stringToLongOption(value: Option[String]): Option[Long] = value.map(_.toLong)

  implicit def stringToBoolean(value: Option[String]): Boolean = stringToBooleanOption(value).getOrElse(false)

  implicit def stringToBooleanOption(value: Option[String]): Option[Boolean] = value.map(_.toBoolean)

  implicit  def stringToLocalDateTime(value: Option[String]): LocalDateTime = stringToLocalDateTimeOptionParser(value).orNull

  implicit def stringToLocalDateTimeOptionParser(value: Option[String]): Option[LocalDateTime] = value.map(LocalDateTime.parse(_,localDateTimePattern))

  implicit def stringToChronic(value:Option[String]) : Chronic = Chronic(value.getOrElse(""))

 implicit def stringOptionToString(value:Option[String]):String = value.getOrElse("")
 implicit def intToString(value: Int): String = value.toString

 implicit def intOptionToString(value: Option[Int]): String = value.map(_.toString).getOrElse("")

 implicit def longToString(value: Long): String = value.toString

 implicit def longOptionToString(value: Option[Long]): String = value.map(_.toString).getOrElse("")

 implicit def booleanToString(value: Boolean): String = value.toString

 implicit def booleanOptionToString(value: Option[Boolean]): String = value.map(_.toString).getOrElse("")

 implicit  def localDateTimeToString(value: LocalDateTime): String = localDateTimeOptionToString(Option(value))

 implicit def localDateTimeOptionToString(value: Option[LocalDateTime]): String = value.map(_.format(localDateTimePattern)).getOrElse("")

  implicit def chronicToString(value:Chronic) : String = value.c
}


