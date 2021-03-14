package com.rbr.scala


import com.rbr.scala.annotations.{NzColumn, RowKeyColumn}

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import scala.tools.nsc.MainBench.theCompiler.abort

trait HbaseMapper[T] {

  def toEntity(pojo: T): Entity

  def fromEntity(entity: Entity): T
}

object HbaseMapper {
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

    val rowKeyForEntity = rowKeyFields.map(e => makeMapRowKeys(e._1, e._2))
    val columnFieldsForEntity = columnFields.map(e => makeMapColumns(e._1, e._2))

    val entityToPojo = (rowKeyFields ++ columnFields).map(e => makeMapToPojo(e._1, e._2))

    c.Expr[HbaseMapper[T]] {
      q"""
      final class $clsName extends _root_.com.rbr.scala.HbaseMapper[$tpe] {
        import _root_.com.rbr.scala.Converters._
        def toEntity(pojo: $tpe): _root_.com.rbr.scala.Entity = _root_.com.rbr.scala.Entity.from(Seq(..$rowKeyForEntity),Map(..$columnFieldsForEntity))
        def fromEntity(entity: _root_.com.rbr.scala.Entity): $tpe = $companion(..$entityToPojo)
      }
      new $clsName(): _root_.com.rbr.scala.HbaseMapper[$tpe]
    """
    }
  }

  private def makeMapRowKeys(fieldName: TermName, annTree: Tree) = annTree match {
    case q"RowKeyColumn($_, $position)" => {
      q"($position,pojo.${fieldName})"
    }
    case _ => abort(s"Annotation $annTree for field $fieldName does not match RowKeyColumn Type.")
  }

  private def makeMapColumns(fieldName: TermName, annTree: Tree) = annTree match {
    case q"NzColumn($value, $_)" => {
      q"$value->pojo.${fieldName}"
    }
    case _ => abort(s"Annotation $annTree for field $fieldName does not match RowKeyColumn Type.")
  }

  private def makeMapToPojo(fieldName: TermName, annTree: Tree) = annTree match {
    case q"RowKeyColumn($_, $position)" => {
      val value = q"entity.rowKeyFields.lift($position-1)"
      q"$fieldName = $value"
    }
    case q"NzColumn($value, $_)" => {
      q"$fieldName = entity.columns.get($value)"
    }
    case _ => abort(s"Annotation $annTree for field $fieldName does not match NzColumn Type.")
  }
}
