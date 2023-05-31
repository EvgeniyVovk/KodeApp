package com.example.kodeapp.utils

import java.time.LocalDate

object Constants {
    const val BASE_URL = "https://stoplight.io/"
    const val ARG_POS = "ARG_POS"
    const val ARG_USER = "ARG_USER"
    val CURRENT_YEAR = LocalDate.now().year.toString()
    val NEXT_YEAR = LocalDate.now().year.plus(1).toString()

    val tabNames: Array<String> = arrayOf(
        "Все",
        "Android",
        "iOS",
        "Дизайн",
        "Менеджмент",
        "QA",
        "Бэк-офис",
        "Frontend",
        "HR",
        "PR",
        "Backend",
        "Техподдержка",
        "Аналитика")

    val departments: Map<Int, String> = mapOf(
        1 to "android",
        2 to "ios",
        3 to "design",
        4 to "management",
        5 to "qa",
        6 to "back_office",
        7 to "frontend",
        8 to "hr",
        9 to "pr",
        10 to "backend",
        11 to "support",
        12 to "analytics")
}