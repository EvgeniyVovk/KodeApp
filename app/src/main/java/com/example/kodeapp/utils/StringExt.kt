package com.example.kodeapp.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun String.toLocalDate(): LocalDate? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        LocalDate.parse(this, formatter)
    }catch (e: Exception){
        null
    }
}