package com.example.ezroom.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Generic result wrapper
sealed interface Try<out T> {
    data class Success<T>(val value: T) : Try<T>
    data class Failure(val error: AppError) : Try<Nothing>

    fun onSuccess(action: (T) -> Unit): Try<T> {
        if (this is Success) action(value)
        return this
    }

    fun onFailure(action: (AppError) -> Unit): Try<T> {
        if (this is Failure) action(error)
        return this
    }
}

// App error types
sealed interface AppError {
    data class Network(val message: String) : AppError
    data class Database(val message: String) : AppError
    data class Validation(val message: String) : AppError
    data object Unauthorized : AppError
    data object NotFound : AppError
    data class Unknown(val throwable: Throwable? = null) : AppError
}

fun <T> Flow<T>.asTry(): Flow<Try<T>> = map<T, Try<T>> { Try.Success(it) }
    .catch { emit(Try.Failure(AppError.Unknown(it))) }
