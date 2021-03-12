package com.rbr.macros.example


import com.rbr.scala.annotations.{NzColumn, RowKeyColumn}

import java.time.LocalDateTime
import scala.annotation.meta.field

case class Person(@(RowKeyColumn@field)("NAME", 1)
                  var name: String,
                  @(RowKeyColumn@field)("AGE", 2)
                  var age: String,
                  @(NzColumn@field)(value = "CREATION_DATE", updateCriteria = false)
                  var creationDate: LocalDateTime = null) {
  def this() = this(null, null)
}
