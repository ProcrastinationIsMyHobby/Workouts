package ru.kolsanovafit.workouts.domain.entity

sealed class LoadState<out T> {
    data class Success<out T>(val data: T) : LoadState<T>()
    data class Error(val message: String) : LoadState<Nothing>()
    data object Empty : LoadState<Nothing>()
}