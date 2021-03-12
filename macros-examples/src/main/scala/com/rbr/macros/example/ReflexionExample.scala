package com.rbr.macros.example

import com.rbr.scala.HbaseRepository
import com.rbr.scala.OrderedPojo.orderedPojo
import com.rbr.scala.RowMapper.materializeRowMapper
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import shapeless.Generic

import java.time.LocalDateTime

object ReflexionExample extends App {

  val schema: StructType = StructType(
    List(
      StructField("NAME", StringType, nullable = true),
      StructField("AGE", StringType, nullable = true),
      StructField("CREATION_DATE", StringType, nullable = true)
    )
  )
  val purchaseMapper = new HbaseRepository[Person]()(materializeRowMapper[Person], orderedPojo[Person])

  val inputPurchase = (1 to 10000).map(genPersonMap(_, schema))

  println("start map to purchase")

  val startTime = System.currentTimeMillis()
  val purchases = inputPurchase.map(purchaseMapper.getData)
  val elapsed = System.currentTimeMillis() - startTime

  println(s"Elapsed time from Map ${purchases.length} is $elapsed")
  val maxValue = purchaseMapper.findMax(purchases)
  val minValue = purchaseMapper.findMin(purchases)
  println(s"Max person $maxValue.")
  println(s"Min person $minValue")
  println("start map to Row")

  val startTimeToMap = System.currentTimeMillis()
  val purchaseAsMap = purchases.map(purchaseMapper.saveData(_, schema))
  val elapsedToMap = System.currentTimeMillis() - startTimeToMap

  println(s"Elapsed time to Row ${purchases.length} is $elapsedToMap")

  val genPurchase = Generic[Person]
  val reprPurchase = purchases.map(genPurchase.to)
  println("start map using shapeless")

  val startTimeShapeless = System.currentTimeMillis()
  val purchaseShapeless = reprPurchase.map(genPurchase.from)
  val elapsedShapeless = System.currentTimeMillis() - startTimeShapeless

  println(s"Elapsed time to shapeless ${purchases.length} is $elapsedShapeless")


  def genPersonMap(i: Int, schema: StructType): Row = {

    new GenericRowWithSchema(Array(s"Jeff Lebowski$i", "25", LocalDateTime.now().minusDays(i).toString), schema)
    //Map("NAME" -> s"Jeff Lebowski$i", s"AGE" -> "25", "CREATION_DATE" -> LocalDateTime.now().toString)
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

