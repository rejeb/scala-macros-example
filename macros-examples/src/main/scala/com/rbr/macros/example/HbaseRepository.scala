package com.rbr.macros.example

import com.rbr.scala.RowKeyMappable
class HbaseRepository[T](implicit mappable: RowKeyMappable[T]) {

  def getData(data: Map[String, String]): T = {
    mappable.fromMap(data)
  }

  def saveData(t:T): Map[String, String] = {
    mappable.toMap(t)
  }
}
