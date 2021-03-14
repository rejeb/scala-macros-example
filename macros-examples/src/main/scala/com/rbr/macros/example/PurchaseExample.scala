package com.rbr.macros.example

import com.rbr.scala.HbaseMapper.materializeHbaseMapper
import com.rbr.scala.HbaseRepository
import com.rbr.scala.OrderedPojo.orderedPojo
import com.rbr.scala.RowMapper.materializeRowMapper

import java.time.{LocalDate, LocalDateTime}

object PurchaseExample extends App {

  val creationDate = LocalDateTime.now()
  val purchaseMapper = new HbaseRepository[Purchase]()

  val inputPurchase = (0 to 100000).map(genPurchaseMap)

  println("start map to purchase")
  val startTime = System.currentTimeMillis()
  val purchases = inputPurchase.map(purchaseMapper.fromMap)
  val elapsed = System.currentTimeMillis() - startTime
  println(s"Elapsed time from Map ${purchases.length} is $elapsed")

  val maxValue = purchaseMapper.findMax(purchases)
  println(s"Max person $maxValue.")

  val minValue = purchaseMapper.findMin(purchases)
  println(s"Min person $minValue")

  println("start save to Hbase")
  val startTimeToMap = System.currentTimeMillis()
  purchaseMapper.saveToHbase(purchases, "PURCHASE")
  val elapsedToMap = System.currentTimeMillis() - startTimeToMap
  println(s"Elapsed time to save in hbase ${purchases.length} is $elapsedToMap")


  def genPurchaseMap(i: Int): Map[String, String] = {
    (1 to 13)
      .flatMap(t => {
        Map(s"NAME${t}" -> s"Jeff Lebowski${i}", s"ORDER_NUMBER${t}" -> LocalDateTime.now().minusHours(i).toString, s"SHIPPED${t}" -> LocalDate.now().toString)
      })
      .toMap
  }

}

