package com.rbr.scala


import shapeless.Annotation

import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe.Tree
import scala.tools.reflect.ToolBox
import scala.reflect.runtime.currentMirror

final case class RowKeyColumn(name: String, position: Int) extends StaticAnnotation

object RowKeyColumn {
  def apply(annotationTree:Tree)= {
  val tb = currentMirror.mkToolBox()
    tb.eval(tb.untypecheck(annotationTree)).asInstanceOf[RowKeyColumn]
  }


}
