package com.rbr.macros.example

import com.rbr.scala.RowKeyMappableMacro._
import shapeless.Generic

import java.time.LocalDateTime

object ReflexionExample extends App {


  val purchaseMapper = new HbaseRepository[Purchase]()

  val inputPurchase = (1 to 1000).map(genPurchaseMap)

  println("start map to purchase")

  val startTime = System.currentTimeMillis()
  val purchases = inputPurchase.map(purchaseMapper.getData)
  val elapsed = System.currentTimeMillis() - startTime

  println(s"Elapsed time from Map ${purchases.length} is $elapsed")

  println("start map to Map")

  val startTimeToMap = System.currentTimeMillis()
  val purchaseAsMap = purchases.map(purchaseMapper.saveData)
  val elapsedToMap = System.currentTimeMillis() - startTimeToMap

  println(s"Elapsed time to Map ${purchases.length} is $elapsedToMap")

  val genPurchase = Generic[Purchase]
  val reprPurchase = purchases.map(genPurchase.to)
  println("start map using shapeless")

  val startTimeShapeless = System.currentTimeMillis()
  val purchaseShapeless = reprPurchase.map(genPurchase.from)
  val elapsedShapeless = System.currentTimeMillis() - startTimeShapeless

  println(s"Elapsed time to shapeless ${purchases.length} is $elapsedShapeless")


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

