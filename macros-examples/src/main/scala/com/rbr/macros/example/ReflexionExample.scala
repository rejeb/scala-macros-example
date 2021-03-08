package com.rbr.macros.example

import com.rbr.scala.RowKeyMappableMacro._

import java.time.LocalDateTime

object ReflexionExample extends App {


  val purchaseMapper = new CaseClassReflexionMapper[Purchase]()(materializeRowKeyMappable[Purchase])
  val personMapper = new CaseClassReflexionMapper[Person]()(materializeRowKeyMappable[Person])
  val inputPerson: Seq[Map[String, String]] = (1 to 10000).map(genPersonMap)
  val inputPuchase = (1 to 10000).map(genPurchaseMap)
  println("start mapping fields")
  val startTime = System.currentTimeMillis()

  val persons = inputPuchase.map(purchaseMapper.map)

  val elapsed = System.currentTimeMillis() - startTime
  println(s"Elapsed time to Map ${persons.length} is $elapsed")

  def genPersonMap(i: Int): Map[String, String] = {
    Map("NAME" -> s"Jeff Lebowski$i", s"AGE" -> "25", "CREATION_DATE" -> LocalDateTime.now().toString)
  }

  def genPurchaseMap(i: Int): Map[String, String] = {
    (1 until 14)
      .flatMap(t => {
        Map(s"NAME${t}" -> "Jeff Lebowski", s"ORDER_NUMBER${t}" -> s"${i}23819", s"SHIPPED${t}" -> "false")
      })
      .toMap
  }

  def genPuchage(i: Int, acc: Map[String, String]): Map[String, String] = {
    if (i == 10000) {
      acc
    } else {
      genPuchage(i + 1, acc + (i.toString -> i.toString))
    }
  }

}

