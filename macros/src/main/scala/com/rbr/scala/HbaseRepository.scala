package com.rbr.scala


import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import scala.math.Ordered.orderingToOrdered

class HbaseRepository[T](implicit mappable: RowMapper[T], ord: Ordering[T]) {

  def getData(row: Row): T = {
    mappable.fromRow(row)
  }

  def saveData(pojo: T, schema: StructType): Row = mappable.toRow(pojo, schema)

  def findMax(data: Seq[T]): T = data.sortWith((t1,t2)=> t1 >= t2).head

  def findMin(data: Seq[T]): T = data.sortWith((t1,t2)=> t1 <= t2).head
}
