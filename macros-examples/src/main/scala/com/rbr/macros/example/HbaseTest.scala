package com.rbr.macros.example

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Put}
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}

object HbaseTest extends App {

  val config: Configuration = HBaseConfiguration.create
  //
  //  config.set("hbase.zookeeper.quorum", "rbr.k8s.master")
  //  config.set("hbase.zookeeper.property.clientPort", "2181")
  val connection: Connection = ConnectionFactory.createConnection(config)

  val tableName = TableName.valueOf("PERSON")
  val table = connection.getTable(tableName)

  val query = new Put("PERSON_NAME4".getBytes)
  query.addColumn("data".getBytes, "AGE".getBytes, "25".getBytes)
  query.addColumn("data".getBytes, "CREATION_DATE".getBytes, "2032-10-02T18:00:01.200Z".getBytes)

  table.put(query)
}
