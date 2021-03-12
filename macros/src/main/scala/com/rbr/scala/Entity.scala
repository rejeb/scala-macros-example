package com.rbr.scala

case class Entity(rowKey: String, columns: Map[String, String]){
  val rowKeys = rowKey.split("\u0000")
}
