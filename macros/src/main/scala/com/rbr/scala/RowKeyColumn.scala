package com.rbr.scala


import scala.annotation.StaticAnnotation

final case class RowKeyColumn(name: String, position: Int) extends StaticAnnotation

