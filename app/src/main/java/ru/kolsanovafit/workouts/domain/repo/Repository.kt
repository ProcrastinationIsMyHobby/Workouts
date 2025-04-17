package ru.kolsanovafit.workouts.domain.repo

import ru.kolsanovafit.workouts.domain.entity.LoadState
import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.domain.entity.WorkoutVideo

interface Repository {
    suspend fun getWorkouts(): LoadState<List<Workout>>
    suspend fun getVideoById(id: Int): LoadState<WorkoutVideo>
}