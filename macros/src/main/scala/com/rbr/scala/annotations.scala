package com.rbr.scala

import scala.annotation.StaticAnnotation

object annotations {

  final case class NzColumn(value: String, updateCriteria: Boolean = false) extends StaticAnnotation
  final case class RowKeyColumn(name: String, position: Int) extends StaticAnnotation

  case class Chronic(c:String)
}
