package com.rbr.scala

import com.rbr.scala.parser.ValParser

import java.time.LocalDateTime
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

case class InternalHbaseFieldInfo(classFieldName: String, columnName: String, columnValue: Int, isOptional: Boolean, fieldParser: ValParser)

trait RowKeyMappable[T] {
  def toMap(t: T): Map[String, String]
  def fromMap(map: Map[String, String]): T
}


object RowKeyMappableMacro {
  implicit def materializeRowKeyMappable[T]: RowKeyMappable[T] = macro materializeRowKeyMappableImpl[T]

  // FIXME Most of the content of this method is cut-n-pasted from generic.scala
  def construct(c:Context)(tpe: c.universe.Type): List[c.universe.Tree] => c.universe.Tree = {
    import c.universe._
      args => q"${tpe.typeSymbol.companion}(..$args)"
  }


  def materializeRowKeyMappableImpl[T: c.WeakTypeTag](c: Context): c.Expr[RowKeyMappable[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion
    val mapCompanion = weakTypeOf[Map[String,String]].typeSymbol.companion

    val (toMapParams, fromMapParams) = materializeHbaseFieldMapping(c)(tpe)

   val mappable = c.Expr[RowKeyMappable[T]] { q"""
      new _root_.com.rbr.scala.RowKeyMappable[$tpe] {
      import _root_.com.rbr.scala.parser._
        def toMap(t: $tpe): Map[String, String] = Map(..$toMapParams)
        def fromMap(map: Map[String, String]): $tpe = $companion(..$fromMapParams)
      }
    """ }
    println(mappable)
    mappable
  }

//  def materialize[T: WeakTypeTag]: Tree = {
//    val tpe = weakTypeOf[T]
//    if (isReprType(tpe))
//      abort("No Generic instance available for HList or Coproduct")
//
//    if (isProduct(tpe))
//      mkProductGeneric(tpe)
//    else
//      abort("No Generic instance available for HList or Coproduct")
//  }

//  def mkProductGeneric(tpe: Type): Tree = {
//    val companion = tpe.typeSymbol.companion
//    val mapCompanion = weakTypeOf[Map[String,String]].typeSymbol.companion
//    val (toMapParams, fromMapParams) = materializeHbaseFieldMapping(tpe)
//
//
//    val clsName = TypeName(c.freshName("anon$"))
//
//    q"""
//      final class $clsName extends _root_.com.rbr.scala.RowKeyMapper[$tpe] {
//        def to(t: $tpe): Repr = ($mapCompanion(..$toMapParams)).asInstanceOf[Repr]
//        def from(r: Repr): $tpe = ($companion(..$fromMapParams)).asInstanceOf[$tpe]
//      }
//      new $clsName(): _root_.com.rbr.scala.RowKeyMapper.Aux[$tpe]
//    """
//
//  }


  private def materializeHbaseFieldMapping(c: Context)(tpe: c.universe.Type): (List[c.universe.Tree], List[c.universe.Tree]) = {

    println("startAnnotating fields")
    val annTreeOpts =
      tpe.decls
        .filter(s => s.isTerm && (s.asTerm.isVal || s.asTerm.isVar))
        .toList.map(_.asTerm)
        .flatMap(fieldTerm => {
          makeFieldMapping(c)(fieldTerm)
        }).unzip

    println(annTreeOpts)

    annTreeOpts

  }

  private def makeFieldMapping(c: Context)(fieldTerm: c.universe.Symbol) = {
    import c.universe._
    val annTpe = weakTypeOf[RowKeyColumn]

    val construct0 = construct(c)(annTpe)
    fieldTerm.annotations.collectFirst {
      case ann if ann.tree.tpe =:= annTpe => {
        val fieldName = fieldTerm.name.toTermName
        val fieldType = fieldTerm.info
        val optionType = isOptionType(c)(fieldType)
        val annotation = construct0(ann.tree.children.tail)
        val annotationVal = c.eval[RowKeyColumn](c.Expr(c.untypecheck(annotation.duplicate)))
        val columnName = annotationVal.name
        val returnType = fieldTerm.typeSignature
        val parser = parse(c)(fieldType)
        val extractedValue= q"map($columnName)"
        (q"$columnName -> null.asInstanceOf[String] ", q"${parser(extractedValue)}")
      }
    }

  }

  def parse(c: Context)(fieldType: c.universe.Type): (c.universe.Tree => c.universe.Tree) = {
    import c.universe._
    fieldType match {
      case tpe if (tpe =:= typeOf[String]) => value => q"_root_.com.rbr.scala.parser.StringParser($value).parse"
      case tpe if (tpe =:= typeOf[Int]) => value => q"_root_.com.rbr.scala.parser.IntParser($value).parse"
      case tpe if (tpe =:= typeOf[Long]) => value => q"_root_.com.rbr.scala.parser.LongParser($value).parse"
      case tpe if (tpe =:= typeOf[Boolean]) => value => q"_root_.com.rbr.scala.parser.BooleanParser($value).parse"
      case tpe if (tpe =:= typeOf[LocalDateTime]) => value => q"_root_.com.rbr.scala.parser.LocalDateTimeParser($value).parse"
    }
  }

  def isOptionType(c: Context)(fieldType: c.universe.Type): Boolean = {

    fieldType <:< c.universe.typeOf[Option[Any]]
  }


}
