package com.rbr.scala

import shapeless.labelled.{FieldType, field}
import shapeless.{::, HList, HNil, LabelledGeneric, Lazy, Witness}

object FieldParser {
  trait ValParser[A] {
    def parse(str:String): A
  }

  trait FieldParser[A] {
    def parse: A
  }

  type FT[A, B] = FieldType[A, B]
  type ListField[K <: Symbol, A] = FieldType[K, List[A]]
  type FP[A] = FieldParser[A]
  type Args = (List[String],Map[String, Int])
  type Named[K <: Symbol] = Witness.Aux[K]

  private def create[A](thunk: A): FP[A] = {
    new FP[A] {
      def parse: A = thunk
    }
  }

  def apply[A](implicit st: Lazy[FP[A]]): FP[A] = st.value

  implicit def genericParser[A, R <: HList](implicit generic: LabelledGeneric.Aux[A, R], parser: Lazy[FP[R]], args:Args): FP[A] = {
    create(generic.from(parser.value.parse))
  }

  implicit def hlistParser[K <: Symbol, H, T <: HList](implicit hParser: Lazy[FP[FT[K, H]]], tParser: FP[T]): FP[FT[K, H] :: T] = {
    create {
      val hv = hParser.value.parse
      val tv = tParser.parse
      hv :: tv
    }
  }

  implicit def standardTypeParser[K <: Symbol, V:ValParser](implicit named: Named[K], args:Args): FP[FieldType[K, V]] = {
    create(field[K](implicitly[ValParser[V]].parse(findArg)))
  }

  implicit def optionParser[V](implicit valParser:ValParser[V]): ValParser[Option[V]] = new ValParser[Option[V]]{
    def parse(str:String):Option[V] = {
      str.isEmpty match {
        case true => None
        case false => Some(valParser.parse(str))
      }
    }
  }

  implicit def listParser[V](implicit valParser:ValParser[V]): ValParser[List[V]] = new ValParser[List[V]]{
    def parse(str:String):List[V] = {
      str.isEmpty match {
        case true => Nil
        case false => str.split(",").map(valParser.parse).toList
      }
    }
  }

  implicit def doubleParser: ValParser[Double] = new ValParser[Double]{
    def parse(str:String):Double = str.toDouble
  }

  implicit def intParser: ValParser[Int] = new ValParser[Int]{
    def parse(str:String):Int = str.toInt
  }

  implicit def strParser: ValParser[String] = new ValParser[String]{
    def parse(str:String):String = str
  }

  implicit def boolParser: ValParser[Boolean] = new ValParser[Boolean]{
    def parse(str:String):Boolean = str.toBoolean
  }

  implicit val hnilParser: FP[HNil] = create(HNil)

  private def findArg[K <: Symbol](implicit args:Args, named: Named[K]): String = {
    val name = named.value.name
    val index = args._2(name)
    args._1(index)
  }


}
