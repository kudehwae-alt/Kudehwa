package com.example.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formatTzs(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        return "TZS ${formatter.format(amount.toLong())}"
    }
}
