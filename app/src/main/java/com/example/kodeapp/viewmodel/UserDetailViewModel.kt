package com.example.kodeapp.viewmodel

import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

class UserDetailViewModel : ViewModel() {

    fun formatDate(dateString: String): String {
        val inputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))
        val date = LocalDate.parse(dateString, inputDateFormat)
        return outputDateFormat.format(date)
    }

    fun calculateAge(birthDateString: String): Int {
        val birthDate = LocalDate.parse(birthDateString)
        val currentDate = LocalDate.now()
        return Period.between(birthDate, currentDate).years
    }

    fun getAgeSuffix(age: Int): String {
        return when {
            age % 10 == 1 && age % 100 != 11 -> "год"
            age % 10 in 2..4 && age % 100 !in 12..14 -> "года"
            else -> "лет"
        }
    }

    fun convertString(str1: String, str2: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(str1)
        stringBuilder.append(" ")
        stringBuilder.append(str2)
        return stringBuilder.toString()
    }
}