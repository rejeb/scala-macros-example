package com.rbr.scala

import java.time.LocalDateTime

object parser {

  def stringParser(value:String):String=value

  def intParser(value:String):Int = value.toInt
  def longParser(value:String) : Long = value.toLong

  def booleanParser(value:String):Boolean =value.toBoolean

  def localDateTimeParser(value:String):LocalDateTime = LocalDateTime.parse(value)


}


