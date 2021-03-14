package com.rbr.macros.example

import com.rbr.scala.HbaseMapper.materializeHbaseMapper
import com.rbr.scala.OrderedPojo.orderedPojo
import com.rbr.scala.RowMapper.materializeRowMapper
import com.rbr.scala.{Entity, HbaseRepository}
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.{StringType, StructField, StructType}

import java.time.LocalDateTime

object PersonExample extends App {

  val schema: StructType = StructType(
    List(
      StructField("NAME", StringType, nullable = true),
      StructField("AGE", StringType, nullable = true),
      StructField("CREATION_DATE", StringType, nullable = true)
    )
  )
  val creationDate = LocalDateTime.now()
  val personsMapper = new HbaseRepository[Person]()

  val inputPersons = (0 to 100000).map(genEntity(_, creationDate))

  println("start map to purchase")

  val startTime = System.currentTimeMillis()
  val persons = inputPersons.map(personsMapper.fromEntity)
  val elapsed = System.currentTimeMillis() - startTime

  println(s"Elapsed time from Map ${persons.length} is $elapsed")
  val maxValue = personsMapper.findMax(persons)
  val minValue = personsMapper.findMin(persons)
  println(s"Max person $maxValue.")
  println(s"Min person $minValue")
  println("start save to Hbase")

  val startTimeToMap = System.currentTimeMillis()
  personsMapper.saveToHbase(persons, "PERSON")
  val elapsedToMap = System.currentTimeMillis() - startTimeToMap

  println(s"Elapsed time to save in hbase ${persons.length} is $elapsedToMap")

  def genRow(i: Int, schema: StructType): Row = {
    new GenericRowWithSchema(Array(s"Jeff Lebowski$i", "25", LocalDateTime.now().minusDays(i).toString), schema)
  }

  def genEntity(i: Int, creationDate: LocalDateTime): Entity = {
    Entity.from(Seq((1, s"Jeff Lebowski$i"), (2, "25")), Map("CREATION_DATE" -> creationDate.minusDays(i).toString))
  }

}

