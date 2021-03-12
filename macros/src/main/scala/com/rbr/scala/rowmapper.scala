package com.rbr.scala

import com.rbr.scala.annotations.{NzColumn, RowKeyColumn}
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.StructType

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

trait RowMapper[T] {
  def toRow(pojo: T, schema: StructType): Row = {
    val orderedValuesBySchema = schema.map(_.name).map(toMap(pojo)).toArray[Any]
    new GenericRowWithSchema(orderedValuesBySchema, schema)
  }

  def fromRow(row: Row): T = fromMap(row.getValuesMap[String](row.schema.fieldNames))

  def toMap(pojo: T): Map[String, String]

  def fromMap(map: Map[String, String]): T
}

object RowMapper {
  implicit def materializeRowMapper[T]: RowMapper[T] = macro RowMapperMacro.materializeRowMapperImpl[T]
}

class RowMapperMacro(val c: whitebox.Context) extends AnnotationBasedMapping {

  import c.universe._

  def materializeRowMapperImpl[T: WeakTypeTag]: Expr[RowMapper[T]] = {

    val tpe: Type = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion
    val clsName = TypeName(c.freshName())

    val rowKeyFields = fieldsWithAnnotation(tpe, weakTypeOf[RowKeyColumn])
    val columnFields = fieldsWithAnnotation(tpe, weakTypeOf[NzColumn])
    val (toMapParams, fromMapParams) = (rowKeyFields ++ columnFields).map(e => makeMappingForMap(e._1, e._2)).unzip
    c.Expr[RowMapper[T]] {
      q"""
      final class $clsName extends _root_.com.rbr.scala.RowMapper[$tpe] {
        import _root_.com.rbr.scala.Converters._
        def toMap(pojo: $tpe) : Map[String,String] = Map(..$toMapParams)
        def fromMap(map : Map[String,String]) : $tpe = $companion(..$fromMapParams)
      }
      new $clsName(): _root_.com.rbr.scala.RowMapper[$tpe]
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
