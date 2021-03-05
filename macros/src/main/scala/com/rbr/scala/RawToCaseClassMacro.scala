package com.rbr.scala

import shapeless.{CaseClassMacros, _}

import java.time.LocalDateTime
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

object RawToCaseClassMacro {
  type RawType = Map[String, String]

  class CaseClassGeneric[P] extends LabelledGeneric[P] {


    type Repr = RawType

    def from(r: Repr): P = ???

    def to(t: P): Repr = ???
  }

  class MapCaseClassGenericMacro(val c: Context) extends CaseClassMacros {

    import c.universe._
    def construct(tpe: Type): List[Tree] => Tree = {
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

//    def from[RawType: WeakTypeTag, P: WeakTypeTag]: Tree = {
//      val annTpe = weakTypeOf[RowKeyColumn]
//      val internalFieldInfoTpe = weakTypeOf[InternalHabseFieldInfoTrait]
//      val tpe = weakTypeOf[P]
//      val ctorDtor = CtorDtor(tpe)
//      val (p, ts) = ctorDtor.binding
//      val construct0 = construct(annTpe)
//      val annTreeOpts =
//        if (isProduct(tpe)) {
//          tpe.decls
//            .filter(s => s.isTerm && s.asTerm.isVal)
//            .toList.map(_.asTerm)
//            .map(fieldTerm => {
//              fieldTerm.annotations.collectFirst {
//                case ann if ann.tree.tpe =:= annTpe => {
//                  val fieldName = fieldTerm.name.toString.trim
//                  val fieldType = fieldTerm.info
//                  val optionType = isOptionType(fieldType)
//                  val annotation = construct0(ann.tree.children.tail)
//                  annotation match {
//                    case q"new $_($name,$_)" => println(name) //50
//                  }
//                  q"""
//                 _root_.com.rbr.scala.InternalHabseFieldInfo($fieldName, $annotation,$optionType)
//                """
//                }
//              }
//            })
//        }
//        else {
//          abort(s"$tpe is not case class like or the root of a sealed family of types")
//        }
//
//    }

    def isOptionType(fieldType: Type): Boolean = {
      fieldType <:< typeOf[Option[Any]]
    }

    def convertFunction(fieldType: Type): (Option[String] => Any) = fieldType match {
      case tpe if (tpe =:= typeOf[String]) => value => value.orNull
      case tpe if (tpe =:= typeOf[Int]) => value => value.map(_.toInt).getOrElse(-1)
      case tpe if (tpe =:= typeOf[Long]) => value => value.map(_.toLong).getOrElse(-1)
      case tpe if (tpe =:= typeOf[Boolean]) => value => value.map(_.toBoolean).getOrElse(false)
      case tpe if (tpe =:= typeOf[LocalDateTime]) => value => value.map(LocalDateTime.parse).orNull

    }
  }


}

