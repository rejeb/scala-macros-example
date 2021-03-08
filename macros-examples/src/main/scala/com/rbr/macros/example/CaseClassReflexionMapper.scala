package com.rbr.macros.example

import com.rbr.scala.RowKeyMappable

import scala.language.experimental.macros

class CaseClassReflexionMapper[T](implicit mappable: RowKeyMappable[T]) {
  def map(data: Map[String, String]): Option[T] = {
     Option(mappable.fromMap(data))
  }

}




