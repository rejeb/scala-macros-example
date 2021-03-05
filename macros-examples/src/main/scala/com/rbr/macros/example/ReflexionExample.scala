package com.rbr.macros.example

import com.rbr.scala.{HbaseFieldMapping, InternalHbaseFieldInfo, Person, Purchase}
import shapeless.{::, HList, HNil}

import java.time.LocalDateTime

object ReflexionExample extends App {


  val annotations = HbaseFieldMapping[Person].apply().asInstanceOf[HList]
  val fieldAsMap = Map(s"NAME" -> "Jeff Lebowski", s"AGE" -> s"25", s"CREATION_DATE" -> LocalDateTime.now().toString)
  loopOverHList(annotations)

  def loopOverHList(h: HList): Unit = h match {
    case HNil => println("This Is all no OtherValues to be Treated :).")
    case x :: y => x match {
      case hbaseFieldInfo: Some[InternalHbaseFieldInfo] =>
        val fieldName = hbaseFieldInfo.get.classFieldName
        val fieldValue = hbaseFieldInfo.get.fieldParser.parse(fieldAsMap(hbaseFieldInfo.get.annotationInfo.name))
        println(s"Read Field ${fieldName} parsed Value is ${fieldValue}")
        loopOverHList(y)
      case _ => println("Value does not match InternalHbaseFieldInfo")
    }
  }

  val purchaseMapper = new CaseClassReflexionMapper[Purchase]()
  val input = (1 to 10000).map(genPurchaseMap)
  val startTime = System.currentTimeMillis()

  //val purchases = input.map(purchaseMapper.map)

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

}

