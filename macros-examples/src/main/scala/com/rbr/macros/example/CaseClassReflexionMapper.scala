package com.rbr.macros.example

import com.rbr.scala.{CaseClassReflexionMapperMacro, HbaseFieldInfoTrait, RowKeyColumn}

import java.time.LocalDateTime
import scala.collection.mutable.ListBuffer
import scala.language.experimental.macros
import scala.reflect.runtime.universe.{Symbol, Type, TypeTag}
import scala.reflect.runtime.{universe => ru}

class CaseClassReflexionMapper[P](implicit typeTag: TypeTag[P]) {


  val constructor = {
    val m = ru.runtimeMirror(getClass.getClassLoader)
    val clazz = ru.typeOf[P].typeSymbol.asClass
    val cm = m.reflectClass(clazz)
    val ctor = ru.typeOf[P].decl(ru.termNames.CONSTRUCTOR).asMethod
    cm.reflectConstructor(ctor)
  }

  def fieldsInfo: Seq[HbaseFieldInfoTrait] = macro CaseClassReflexionMapperMacro.buildFieldMapping[P]

  def map(data: Map[String, String]): P = {
    var values: ListBuffer[Any] = ListBuffer()
    val infos = fieldsInfo
//    for (fieldInfo: HbaseFieldInfoTrait <- infos) {
//      values += fieldInfo.parse(data.get(fieldInfo.annotationInfo.name))
//    }

    constructor.apply(values: _*).asInstanceOf[P]
  }


  def isOptionType(fieldType: Type): Boolean = {
    fieldType <:< ru.typeOf[Option[Any]]
  }

  def findFieldsWithAnnotations(memberScope: ru.MemberScope): Iterable[Symbol] = {
    memberScope
      .filter(s => s.isTerm && s.asTerm.isVal)
  }

  def isAnnotationType(annotationType: scala.reflect.runtime.universe.Annotation): Boolean = {
    val b = annotationType.tree.tpe =:= ru.typeOf[RowKeyColumn]
    b
  }


  def convertFunction(fieldType: Type): (Option[String] => Any) = {

    fieldType match {

      case tpe if (tpe =:= ru.typeOf[String]) => value => value.orNull
      case tpe if (tpe =:= ru.typeOf[Int]) => value => value.map(_.toInt).getOrElse(-1)
      case tpe if (tpe =:= ru.typeOf[Long]) => value => value.map(_.toLong).getOrElse(-1)
      case tpe if (tpe =:= ru.typeOf[Boolean]) => value => value.map(_.toBoolean).getOrElse(false)
      case tpe if (tpe =:= ru.typeOf[LocalDateTime]) => value => value.map(LocalDateTime.parse).orNull

    }
  }


}




