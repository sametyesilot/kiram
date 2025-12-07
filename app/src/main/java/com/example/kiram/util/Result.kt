package com.example.kiram.util

/**
 * Sealed class for handling async operation results
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    object Idle : Result<Nothing>()
}
