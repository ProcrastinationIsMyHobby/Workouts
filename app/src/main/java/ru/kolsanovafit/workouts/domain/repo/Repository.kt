package ru.kolsanovafit.workouts.domain.repo

import ru.kolsanovafit.workouts.domain.entity.LoadResult
import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.domain.entity.WorkoutVideo

interface Repository {
    suspend fun getWorkouts(): LoadResult<List<Workout>>
    suspend fun getVideoById(id: Int): LoadResult<WorkoutVideo>
}