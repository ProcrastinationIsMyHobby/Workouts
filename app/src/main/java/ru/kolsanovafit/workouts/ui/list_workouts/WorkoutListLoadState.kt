package ru.kolsanovafit.workouts.ui.list_workouts

import ru.kolsanovafit.workouts.domain.entity.NetworkError
import ru.kolsanovafit.workouts.domain.entity.Workout

sealed class WorkoutListLoadState {
    data object Loading : WorkoutListLoadState()
    data class Success(val workouts: List<Workout>) : WorkoutListLoadState()
    data class Error(val error: NetworkError) : WorkoutListLoadState()
    data object Empty : WorkoutListLoadState()
}