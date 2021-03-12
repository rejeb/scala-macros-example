package com.rbr.macros.example

import org.scalatest.{FlatSpec, Matchers}

import java.time.LocalDateTime
import scala.math.Ordering.Implicits.infixOrderingOps
class PersonTest extends FlatSpec with Matchers{

  it should "should return false on person" in {
    implicit val localDateTimeOrdering: Ordering[Person] = Ordering.fromLessThan(_.creationDate isBefore _.creationDate)

    val person1 = Person("test1","25",LocalDateTime.now().minusDays(20))
    val person2 = Person("test2","25",LocalDateTime.now().minusDays(10))

    val isBefore = (person1 < person2)
    isBefore shouldBe true
  }
}
