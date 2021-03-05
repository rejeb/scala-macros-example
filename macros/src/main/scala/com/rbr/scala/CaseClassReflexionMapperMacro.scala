package com.rbr.scala

import shapeless.CaseClassMacros

import scala.reflect.macros.whitebox.Context

class CaseClassReflexionMapperMacro(val c: Context) extends CaseClassMacros {


  import c.universe._

  def someTpe = typeOf[Some[_]].typeConstructor

  def noneTpe = typeOf[None.type]

  // FIXME Most of the content of this method is cut-n-pasted from generic.scala
  def construct(tpe: Type): List[Tree] => Tree = {
    // FIXME Cut-n-pasted from generic.scala
    val sym = tpe.typeSymbol
    val isCaseClass = sym.asClass.isCaseClass

    def hasNonGenericCompanionMember(name: String): Boolean = {
      val mSym = sym.companion.typeSignature.member(TermName(name))
      mSym != NoSymbol && !isNonGeneric(mSym)
    }

    if (isCaseClass || hasNonGenericCompanionMember("apply"))
      args => q"${companionRef(tpe)}(..$args)"
    else
      args => q"new $tpe(..$args)"
  }

  def buildFieldMapping[P]: Tree = {

    val tpe = weakTypeOf[P]

    val annTreeOpts = tpe.decls
      .filter(s => s.isTerm && s.asTerm.isVal)
      .toList.map(_.asTerm)
      .map(fieldTerm => {
        val (fieldName, annotation, optionType, fieldType) = hbaseFieldInfo(fieldTerm)
        q"_root_.com.rbr.scala.HbaseFieldInfo[$fieldType]($fieldName, $annotation,$optionType,$fieldType)"
      })

    println(annTreeOpts)

    val result = annTreeOpts.foldRight(q"_root_.scala.collection.immutable.Nil": Tree) {
      case (bound, acc) => {
        q"$bound::$acc"
      }
    }
    println(result)
    result
  }

  def extractFieldInfo(fieldTerm: c.Expr[TermSymbol]): c.Expr[Tree] = {
    reify {
      val (fieldName, annotation, optionType, fieldType) = hbaseFieldInfo(fieldTerm.splice)
      q"_root_.com.rbr.scala.InternalHbaseFieldInfo[$fieldType]($fieldName, $annotation,$optionType,$fieldType)"
    }
  }


  def hbaseFieldInfo(fieldTerm: TermSymbol): (String, Tree, Boolean, Type) = {
    val annTpe = weakTypeOf[RowKeyColumn]

    val construct0 = construct(annTpe)
    val fieldName = fieldTerm.name.toString.trim
    val fieldType = fieldTerm.info
    val annotationAndOptionType = fieldTerm.annotations.collectFirst {
      case ann if ann.tree.tpe =:= annTpe => {
        (construct0(ann.tree.children.tail), isOptionType(fieldType))
      }
    }
    if (annotationAndOptionType.isDefined) {

      val annotation = annotationAndOptionType.get._1
      val optionType = annotationAndOptionType.get._2
      (fieldName, annotation, optionType, fieldType)
    } else {
      abort(s"$fieldTerm is not annotated with $annTpe")
    }

  }

  def isOptionType(fieldType: Type): Boolean = {
    fieldType <:< typeOf[Option[Any]]
  }

  case class HbaseFieldInfo[T](classFieldName: String, annotationInfo: RowKeyColumn, isOptional: Boolean) extends HbaseFieldInfoTrait {
    type FieldType = T

//    def parse: (Option[String] => T) = FieldType match {
//      case tpe if (tpe =:= typeOf[String]) => value => value.orNull.asInstanceOf[T]
//      case tpe if (tpe =:= typeOf[Int]) => value => value.map(_.toInt).getOrElse(-1).asInstanceOf[T]
//      case tpe if (tpe =:= typeOf[Long]) => value => value.map(_.toLong).getOrElse(-1).asInstanceOf[T]
//      case tpe if (tpe =:= typeOf[Boolean]) => value => value.map(_.toBoolean).getOrElse(false).asInstanceOf[T]
//      case tpe if (tpe =:= typeOf[LocalDateTime]) => value => value.map(LocalDateTime.parse).orNull.asInstanceOf[T]
//
//    }
  }

}

trait HbaseFieldInfoTrait {
  type FieldType
  val classFieldName: String
  val annotationInfo: RowKeyColumn
  val isOptional: Boolean

//  def parse: (Option[String] => Any)
}

