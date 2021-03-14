package com.rbr.macros.example


import com.rbr.scala.annotations.{NzColumn, RowKeyColumn}

import java.time.LocalDateTime
import scala.annotation.meta.field

case class Person(@(RowKeyColumn@field)("USER_NAME", 1)
                  var userName: String,
                  @(RowKeyColumn@field)("AGE", 2)
                  var age: Int,
                  @(NzColumn@field)(value = "CREATION_DATE", updateCriteria = true)
                  var creationDate: LocalDateTime = null) {
  def this() = this(null, -1)
}
