package ru.kolsanovafit.workouts.ui.detail_workout

import ru.kolsanovafit.workouts.domain.entity.NetworkError
import ru.kolsanovafit.workouts.domain.entity.Workout

sealed class DetailWorkoutUIState {
    data object Loading : DetailWorkoutUIState()
    data class Success(val workouts: List<Workout>) : DetailWorkoutUIState()
    data class Error(val error: NetworkError) : DetailWorkoutUIState()
    data object Empty : DetailWorkoutUIState()
}