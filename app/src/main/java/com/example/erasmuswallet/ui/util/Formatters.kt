package com.example.erasmuswallet.ui.util

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val moneyFormat = NumberFormat.getCurrencyInstance(Locale.ITALY).apply {
    minimumFractionDigits = 2
    maximumFractionDigits = 2
}

val italianDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun Double.toEuro(): String = moneyFormat.format(this)

fun LocalDate.toItalianDate(): String = format(italianDateFormatter)

fun parseItalianDate(value: String): LocalDate? = runCatching {
    LocalDate.parse(value, italianDateFormatter)
}.getOrNull()
