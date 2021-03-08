package com.rbr.scala

import com.rbr.scala.parser.ValParser
import shapeless.{CaseClassMacros, DepFn0, HList}

import java.time.LocalDateTime
import scala.language.experimental.macros
import scala.reflect.macros.whitebox




//
//trait HbaseFieldMapping[P] extends DepFn0 with Serializable {
//  type Out <: HList
//}
//
//object HbaseFieldMapping {
//  def apply[P](implicit hbaseFieldMapping: HbaseFieldMapping[P]): Aux[P, hbaseFieldMapping.Out] = {
//    hbaseFieldMapping
//  }
//
//  type Aux[P, Out0 <: HList] = HbaseFieldMapping[P] {type Out = Out0}
//
//  def mkHbaseFieldMapping[P, Out0 <: HList](hbaseFieldMapping: => Out0): Aux[P, Out0] =
//    new HbaseFieldMapping[P] {
//      type Out = Out0
//
//      def apply() = hbaseFieldMapping
//    }
//
//  implicit def materialize[P, Out <: HList]: Aux[P, Out] = macro HbaseFieldMappingMacros.materializeHbaseFieldMapping[P, Out]
//}
//
//class HbaseFieldMappingMacros(val c: whitebox.Context) extends CaseClassMacros {
//
//  import c.universe._
//
//  def someTpe = typeOf[Some[_]].typeConstructor
//
//  def noneTpe = typeOf[None.type]
//
//  // FIXME Most of the content of this method is cut-n-pasted from generic.scala
//  def construct(tpe: Type): List[Tree] => Tree = {
//    // FIXME Cut-n-pasted from generic.scala
//    val sym = tpe.typeSymbol
//    val isCaseClass = sym.asClass.isCaseClass
//
//    def hasNonGenericCompanionMember(name: String): Boolean = {
//      val mSym = sym.companion.typeSignature.member(TermName(name))
//      mSym != NoSymbol && !isNonGeneric(mSym)
//    }
//
//    if (isCaseClass || hasNonGenericCompanionMember("apply"))
//      args => q"${companionRef(tpe)}(..$args)"
//    else
//      args => q"new $tpe(..$args)"
//  }
//
//
//  def materializeHbaseFieldMapping[P: WeakTypeTag, Out: WeakTypeTag]: Tree = {
//    val annTpe = weakTypeOf[RowKeyColumn]
//    val internalFieldInfoTpe = weakTypeOf[InternalHbaseFieldInfo]
//    if (!isProduct(annTpe))
//      abort(s"$annTpe is not a case class-like type")
//
//    val construct0 = construct(annTpe)
//
//    val tpe = weakTypeOf[P]
//
//    val annTreeOpts =
//      if (isProduct(tpe)) {
//        tpe.decls
//          .filter(s => s.isTerm && s.asTerm.isVal)
//          .toList.map(_.asTerm)
//          .map(fieldTerm => {
//            fieldTerm.annotations.collectFirst {
//              case ann if ann.tree.tpe =:= annTpe => {
//                val fieldName = fieldTerm.name.toString.trim
//                val fieldType = fieldTerm.info
//                val optionType = isOptionType(fieldType)
//                val annotation = construct0(ann.tree.children.tail)
//                val fieldParser = parse(fieldType)
//                q"_root_.com.rbr.scala.InternalHbaseFieldInfo($fieldName, $annotation,$optionType,$fieldParser)"
//              }
//            }
//          })
//      }
//      else {
//        abort(s"$tpe is not case class like or the root of a sealed family of types")
//      }
//
//
//    println(annTreeOpts)
//
//    val wrapTpeTrees = annTreeOpts.map {
//      case Some(internalFieldInfoTree) => appliedType(someTpe, internalFieldInfoTpe) ->
//        q"""
//             _root_.scala.Some($internalFieldInfoTree)
//         """
//      case None => noneTpe ->
//        q"""
//             _root_.scala.None
//         """
//    }
//
//    val outTpe = mkHListTpe(wrapTpeTrees.map { case (aTpe, _) => aTpe })
//    val outTree = wrapTpeTrees.foldRight(q"_root_.shapeless.HNil": Tree) {
//      case ((_, bound), acc) => {
//        pq"_root_.shapeless.::($bound, $acc)"
//      }
//    }
//
//    q"_root_.com.rbr.scala.HbaseFieldMapping.mkHbaseFieldMapping[$tpe, $outTpe]($outTree)"
//  }
//
//  def parse(fieldType: Type): Tree = fieldType match {
//    case tpe if (tpe =:= typeOf[String]) => q"_root_.com.rbr.scala.parser.StringParser"
//    case tpe if (tpe =:= typeOf[Int]) => q"_root_.com.rbr.scala.parser.IntParser"
//    case tpe if (tpe =:= typeOf[Long]) => q"_root_.com.rbr.scala.parser.LongParser"
//    case tpe if (tpe =:= typeOf[Boolean]) => q"_root_.com.rbr.scala.parser.BooleanParser"
//    case tpe if (tpe =:= typeOf[LocalDateTime]) => q"_root_.com.rbr.scala.parser.LocalDateTimeParser"
//  }
//
//  def isOptionType(fieldType: Type): Boolean = {
//    fieldType <:< typeOf[Option[Any]]
//  }
//
//}
