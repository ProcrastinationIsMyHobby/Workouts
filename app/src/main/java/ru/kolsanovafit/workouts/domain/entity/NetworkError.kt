package ru.kolsanovafit.workouts.domain.entity

sealed class NetworkError {
    data class ServerError(val code: Int, val serverMessage: String) : NetworkError()
    data object ConnectionError : NetworkError()
    data class UnknownError(val exception: Throwable) : NetworkError()
}