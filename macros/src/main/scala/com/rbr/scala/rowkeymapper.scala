package com.rbr.scala


import java.time.LocalDateTime
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait RowKeyMappable[T] {
  def toMap(t: T): Map[String, String]

  def fromMap(map: Map[String, String]): T
}


object RowKeyMappableMacro {
  implicit def materializeRowKeyMappable[T]: RowKeyMappable[T] = macro materializeRowKeyMappableImpl[T]

  def materializeRowKeyMappableImpl[T: c.WeakTypeTag](c: Context): c.Expr[RowKeyMappable[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion
    val clsName = TypeName(c.freshName("anon$"))


    val (toMapParams, fromMapParams) = materializeHbaseFieldMapping(c)(tpe)

    c.Expr[RowKeyMappable[T]] {
      q"""
      final class $clsName extends _root_.com.rbr.scala.RowKeyMappable[$tpe] {
      import _root_.com.rbr.scala.parser._
        def toMap(t: $tpe): Map[String, String] = Map(..$toMapParams)
        def fromMap(map: Map[String, String]): $tpe = $companion(..$fromMapParams)
      }
      new $clsName(): _root_.com.rbr.scala.RowKeyMappable[$tpe]
    """
    }
  }

  private def materializeHbaseFieldMapping(c: Context)(tpe: c.universe.Type): (List[c.universe.Tree], List[c.universe.Tree]) = {
    tpe.decls
      .toList
      .filter(s => s.isTerm && (s.asTerm.isVal || s.asTerm.isVar))
      .flatMap(fieldTerm => {
        makeFieldMapping(c)(fieldTerm)
      }).unzip
  }

  private def makeFieldMapping(c: Context)(fieldTerm: c.universe.Symbol) = {
    import c.universe._
    val annTpe = weakTypeOf[RowKeyColumn]

    val construct0: (List[Tree] => Tree) = args => q"${annTpe.typeSymbol.companion}(..$args)"

    fieldTerm.annotations.collectFirst {
      case ann if ann.tree.tpe =:= annTpe => {
        val fieldName = TermName(fieldTerm.name.toTermName.toString.trim)
        val fieldType = fieldTerm.info
        val annotation = construct0(ann.tree.children.tail)
        val columnName = q"$annotation.name"
        val parser = parse(c)(fieldType)
        val extractedValue = q"map($columnName)"
        (q"$columnName->t.${fieldName}.toString", q"${parser(extractedValue)}")
      }
    }

  }

  def parse(c: Context)(fieldType: c.universe.Type): (c.universe.Tree => c.universe.Tree) = {
    import c.universe._
    fieldType match {
      case tpe if (tpe =:= typeOf[String]) => value => q"_root_.com.rbr.scala.parser.stringParser($value)"
      case tpe if (tpe =:= typeOf[Int]) => value => q"_root_.com.rbr.scala.parser.intParser($value)"
      case tpe if (tpe =:= typeOf[Long]) => value => q"_root_.com.rbr.scala.parser.longParser($value)"
      case tpe if (tpe =:= typeOf[Boolean]) => value => q"_root_.com.rbr.scala.parser.booleanParser($value)"
      case tpe if (tpe =:= typeOf[LocalDateTime]) => value => q"_root_.com.rbr.scala.parser.localDateTimeParser($value)"
    }
  }
}
