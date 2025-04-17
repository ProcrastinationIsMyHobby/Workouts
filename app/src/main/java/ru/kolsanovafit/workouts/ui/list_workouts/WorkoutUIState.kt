package ru.kolsanovafit.workouts.ui.list_workouts

import ru.kolsanovafit.workouts.domain.entity.Workout

sealed class WorkoutUIState {
    data object Loading : WorkoutUIState()
    data class Success(val workouts: List<Workout>) : WorkoutUIState()
    data class Error(val message: String) : WorkoutUIState()
    data object Empty : WorkoutUIState()
}