package com.rbr.scala

import java.time.LocalDateTime
import scala.annotation.meta.field


case class Person(@(RowKeyColumn@field)("NAME", 1)
                  name: String,
                  @(RowKeyColumn@field)("AGE", 2)
                  age: Int,
                  @(RowKeyColumn@field)("CREATION_DATE", 3)
                  creationDate: LocalDateTime)
