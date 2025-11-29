package com.example.kiram.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Patterns

/**
 * Extension functions for common operations
 */

// Date formatting
fun Long.toFormattedDate(format: String = Constants.DATE_FORMAT_LONG): String {
    return try {
        val sdf = SimpleDateFormat(format, Locale("tr", "TR"))
        sdf.format(Date(this))
    } catch (e: Exception) {
        ""
    }
}

// Email validation
fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

// Phone validation
fun String.isValidPhone(): Boolean {
    val digitsOnly = this.replace(Regex("[^0-9]"), "")
    return digitsOnly.length == Constants.PHONE_NUMBER_LENGTH
}

// Password validation
fun String.isValidPassword(): Boolean {
    return this.length >= Constants.MIN_PASSWORD_LENGTH
}

// Full name validation
fun String.isValidFullName(): Boolean {
    return this.trim().split(" ").size >= 2 && this.trim().length >= 3
}

// Turkish Lira formatting
fun Double.toTurkishLira(): String {
    return "₺${String.format(Locale("tr", "TR"), "%,.2f", this)}"
}

// Calculate average rating
fun Map<String, Float>.calculateAverage(): Float {
    return if (this.isEmpty()) 0f else this.values.average().toFloat()
}
