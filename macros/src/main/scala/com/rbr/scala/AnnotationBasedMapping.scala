package com.rbr.scala

import scala.reflect.macros.whitebox

trait AnnotationBasedMapping {
  val c:whitebox.Context
  import c.universe._
  def fieldsWithAnnotation(tpe: Type, annTpe: Type): List[(TermName, Tree)] = {
    tpe.decls
      .toList
      .filter(s => s.isTerm && (s.asTerm.isVal || s.asTerm.isVar))
      .flatMap(fieldTerm => {
        fieldWithAnnotation(fieldTerm, annTpe)
      })
  }

  def fieldWithAnnotation(fieldTerm: Symbol, annTpe: Type): Option[(TermName, Tree)] = {
    val construct0: (List[Tree] => Tree) = args => q"${annTpe.typeSymbol.companion}(..$args)"
    val fieldName: TermName = TermName(fieldTerm.name.toTermName.toString.trim)
    val annotation = fieldTerm.annotations.collectFirst {
      case ann if ann.tree.tpe =:= annTpe => {
        construct0(ann.tree.children.tail)
      }
    }
    annotation.map((fieldName, _))
  }
}
