package com.rbr.macros.example

import com.rbr.scala.RowKeyMappable

class HbaseRepository[T](implicit mappable: RowKeyMappable[T]) {
  val rowKeyMapper = new CaseClassReflexionMapper[T]()

  def getData(data: Map[String, String]): T = {
    rowKeyMapper.map(data)
  }

  def saveData(t:T): Map[String, String] = {
    rowKeyMapper.unmap(t)
  }
}
