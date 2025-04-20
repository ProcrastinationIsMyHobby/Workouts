package ru.kolsanovafit.workouts.ui.detail_workout

import ru.kolsanovafit.workouts.domain.entity.NetworkError
import ru.kolsanovafit.workouts.domain.entity.WorkoutVideo

sealed class DetailWorkoutLoadState {
    data object Loading : DetailWorkoutLoadState()
    data class Success(val workout: WorkoutVideo) : DetailWorkoutLoadState()
    data class Error(val error: NetworkError) : DetailWorkoutLoadState()
    data object Empty : DetailWorkoutLoadState()
}