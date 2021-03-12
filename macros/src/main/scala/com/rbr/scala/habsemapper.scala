package com.rbr.scala


import com.rbr.scala.annotations.{NzColumn, RowKeyColumn}

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait HbaseMapper[T] {
  def toEntity(pojo: T): Entity
  def fromEntity(entity: Entity): T
}

object HbaseMapper{
  implicit def materializeHbaseMapper[T]: HbaseMapper[T] = macro HbaseMapperMacro.materializeHbaseMapperImpl[T]
}

class HbaseMapperMacro(val c: Context) extends AnnotationBasedMapping {
  import c.universe._

  def materializeHbaseMapperImpl[T: WeakTypeTag]: Expr[HbaseMapper[T]] = {

    val tpe = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion
    val clsName = TypeName(c.freshName())

    val rowKeyFields = fieldsWithAnnotation(tpe, weakTypeOf[RowKeyColumn])
    val columnFields = fieldsWithAnnotation(tpe, weakTypeOf[NzColumn])
    val (toMapParams, fromMapParams) = (rowKeyFields ++ columnFields).map(e => makeMappingForMap(e._1, e._2)).unzip
    c.Expr[HbaseMapper[T]] {
      q"""
      final class $clsName extends _root_.com.rbr.scala.HbaseMapper[$tpe] {
        import _root_.com.rbr.scala.Converters._
        def toEntity(pojo: $tpe): Entity = ???
        def fromEntity(entity: Entity): $tpe = ???
      }
      new $clsName(): _root_.com.rbr.scala.HbaseMapper[$tpe]
    """
    }
  }

  private def makeMappingForMap(fieldName: TermName, annTree: Tree) = annTree match {
    case q"RowKeyColumn($name, $_)" => {
      (q"$name->pojo.${fieldName}", q"$fieldName = map.get($name)")
    }
    case q"NzColumn($value, $_)" => {
      (q"$value->pojo.${fieldName}", q"$fieldName = map.get($value)")
    }
  }
}
