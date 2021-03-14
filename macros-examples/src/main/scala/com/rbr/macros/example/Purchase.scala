package com.rbr.macros.example


import com.rbr.scala.annotations.{NzColumn, RowKeyColumn}

import java.time.{LocalDate, LocalDateTime}
import scala.annotation.meta.field

case class Purchase(@(RowKeyColumn@field)(name = "NAME1", position = 1)
                    name1: Option[String],
                    @(RowKeyColumn@field)(name = "ORDER_NUMBER1", position = 2)
                    orderNumber1: Option[LocalDateTime],
                    @(RowKeyColumn@field)(name = "SHIPPED1", position = 3)
                    shipped1: Option[LocalDate],
                    @(NzColumn@field)(value = "NAME2")
                    name2: String,
                    @(NzColumn@field)(value = "ORDER_NUMBER2", updateCriteria = true)
                    orderNumber2: LocalDateTime,
                    @(NzColumn@field)(value = "SHIPPED2")
                    shipped2: LocalDate,
                    @(NzColumn@field)(value = "NAME3")
                    name3: String,
                    @(NzColumn@field)(value = "ORDER_NUMBER3")
                    orderNumber3: LocalDateTime,
                    @(NzColumn@field)(value = "SHIPPED3")
                    shipped3: LocalDate,
                    @(NzColumn@field)(value = "NAME4")
                    name4: String,
                    @(NzColumn@field)(value = "ORDER_NUMBER4")
                    orderNumber4: LocalDateTime,
                    @(NzColumn@field)(value = "SHIPPED4")
                    shipped4: LocalDate,
                    @(NzColumn@field)(value = "NAME5")
                    name5: String,
                    @(NzColumn@field)(value = "ORDER_NUMBER5")
                    orderNumber5: LocalDateTime,
                    @(NzColumn@field)(value = "SHIPPED5")
                    shipped5: LocalDate,
                    @(NzColumn@field)(value = "NAME6")
                    name6: String,
                    @(NzColumn@field)(value = "ORDER_NUMBER6")
                    orderNumber6: LocalDateTime,
                    @(NzColumn@field)(value = "SHIPPED6")
                    shipped6: LocalDate)
