package com.rbr.scala

case class Entity(rowKey: String, columns: Map[String, String]){
  val rowKeyFields: Array[String] = rowKey.split("\u0000")
}

object Entity{
  val rowKeySeparator = "\u0000"
  def from(rowKeyFields:Seq[(Int,String)],columnFields:Map[String,String]):Entity={
    val rowKey = rowKeyFields.sortBy(_._1).map(_._2).mkString(rowKeySeparator)
    Entity(rowKey,columnFields)
  }
}
