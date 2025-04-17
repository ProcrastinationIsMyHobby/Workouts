package ru.kolsanovafit.workouts.domain.entity

sealed class LoadResult<out T> {
    data class Success<out T>(val data: T) : LoadResult<T>()
    data class Error(val message: String) : LoadResult<Nothing>()
    data object Empty : LoadResult<Nothing>()
}