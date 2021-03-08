package com.rbr.macros.example

import shapeless.Generic
import com.rbr.scala.MappableMacro._
import java.time.LocalDateTime

object ReflexionExample extends App {

  val fieldAsMap = Map(s"NAME" -> "Jeff Lebowski", s"AGE" -> s"25", s"CREATION_DATE" -> LocalDateTime.now().toString)
  Map.empty
  val purchaseMapper = new CaseClassReflexionMapper[Purchase]
  val input = (1 to 10000).map(genPurchaseMap)
  val startTime = System.currentTimeMillis()

  val purchases = input.map(purchaseMapper.map)

  val elapsed = System.currentTimeMillis() - startTime
  val startTimeElapsed = System.currentTimeMillis()
  //  println(s"Elapsed time to Map ${purchases.length} is $elapsed")


  val elapsedToPrint = System.currentTimeMillis() - startTimeElapsed

  println(s"Elapsed time to print is $elapsedToPrint")

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

