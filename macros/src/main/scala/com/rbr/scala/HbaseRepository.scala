package com.rbr.scala


import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import scala.collection.JavaConverters._
import scala.math.Ordered.orderingToOrdered

class HbaseRepository[T](implicit hbaseMapper: HbaseMapper[T], rowMapper: RowMapper[T], ord: Ordering[T]) {


  val config: Configuration = HBaseConfiguration.create
//  config.set("hbase.zookeeper.quorum", "rbr.k8s.master")
//  config.set("hbase.zookeeper.property.clientPort", "2181")
  val connection: Connection = ConnectionFactory.createConnection(config)

  val columnFamily = "data".getBytes

  def fromEntity(row: Entity): T = {
    hbaseMapper.fromEntity(row)
  }

  def toEntity(pojo: T): Entity = {
    hbaseMapper.toEntity(pojo)
  }

  def saveToHbase(pojo: T, tableName: String): Unit = {
    val TABLE_NAME = TableName.valueOf(tableName)
    val table = connection.getTable(TABLE_NAME)
    val entity = toEntity(pojo)
    val query = new Put(entity.rowKey.getBytes)
    entity.columns.foreach(c => query.addColumn(columnFamily, c._1.getBytes, c._2.getBytes))
    table.put(query)
  }

  def makeQuery(pojo: T): Put = {
    val entity = toEntity(pojo)
    entity.columns.foldRight(new Put(entity.rowKey.getBytes))((c, put) => put.addImmutable(columnFamily, c._1.getBytes, c._2.getBytes))

  }

  def saveToHbase(pojos: Seq[T], tableName: String): Unit = {
    val TABLE_NAME = TableName.valueOf(tableName)
    val table = connection.getTable(TABLE_NAME)
    println("Start Building Query.")
    val start=System.currentTimeMillis()
    val query = pojos.map(makeQuery).asJava
    println(s"Query built in ${System.currentTimeMillis()-start}")
    println("Start put to hbase.")
    val startInsertHbase=System.currentTimeMillis()
    table.put(query)
    println(s"Put hbase done in ${System.currentTimeMillis()-startInsertHbase}")
  }

  def fromRow(row: Row): T = rowMapper.fromRow(row)

  def toRow(pojo: T, schema: StructType): Row = rowMapper.toRow(pojo, schema)

  def fromMap(map: Map[String, String]): T = rowMapper.fromMap(map)

  def findMax(data: Seq[T]): T = data.max

  def findMin(data: Seq[T]): T = data.min
}
