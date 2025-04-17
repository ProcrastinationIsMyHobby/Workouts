package ru.kolsanovafit.workouts.ui.detail_workout.media_player

sealed class PlayerState {
    data object Initial : PlayerState()
    data object Preparing : PlayerState()
    data object Buffering : PlayerState()
    data object Playing : PlayerState()
    data object Paused : PlayerState()
    data object Completed : PlayerState()
    data class Error(val what: Int, val extra: Int) : PlayerState()
}