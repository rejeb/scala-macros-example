package com.rbr.macros.example

import com.rbr.scala.Mappable
import com.rbr.scala.MappableMacro._
import scala.language.experimental.macros

class CaseClassReflexionMapper[T](implicit mappable: Mappable[T]) {

  def map(data: Map[String, String]): Option[T] = {
   // val values = buildArgs(hbaseFieldMapping.apply().asInstanceOf[HList],data,HNil)


    Option(mappable.fromMap(data))
  }

}




