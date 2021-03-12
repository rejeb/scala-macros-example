package com.rbr.scala

import com.rbr.scala.annotations.NzColumn

import scala.reflect.macros.whitebox
import scala.language.experimental.macros

object OrderedPojo {
  implicit def orderedPojo[T]: Ordering[T] = macro OrderedPojoMacro.materializeOrderedPojoImpl[T]
}

class OrderedPojoMacro(val c: whitebox.Context) extends AnnotationBasedMapping {

  import c.universe._

  def findCompareField(columnFields: List[(TermName, Tree)]): Option[TermName] = {
    columnFields.collectFirst {
      case (fieldName, annTree) if c.eval[Boolean](c.Expr(c.untypecheck(isUpdateCriteria(annTree).duplicate))) => {
        fieldName
      }
    }
  }

  def isUpdateCriteria(annTree: Tree): Tree = annTree match {
    case q"NzColumn($_, $updateCriteria)" => q"$updateCriteria"
    case _ => q"false"
  }

  def materializeOrderedPojoImpl[T: WeakTypeTag]: Expr[Ordering[T]] = {
    val tpe = weakTypeOf[T]
    val columnFields = fieldsWithAnnotation(tpe, weakTypeOf[NzColumn])
    val compareField = findCompareField(columnFields)
    val compareExpr = if (compareField.isDefined) q"x.${compareField.get}.compareTo(y.${compareField.get})" else q"0"
    c.Expr[Ordering[T]] {
      q"""
      new Ordering[$tpe] {
      override def compare(x: $tpe, y: $tpe): Int = $compareExpr
    }
    """
    }
  }
}
