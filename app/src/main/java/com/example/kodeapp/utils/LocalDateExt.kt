package com.example.kodeapp.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun LocalDate.toDayMonthString(): String? {
    val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))
    return LocalDate.of(this.year, this.month, this.dayOfMonth).format(formatter)
}