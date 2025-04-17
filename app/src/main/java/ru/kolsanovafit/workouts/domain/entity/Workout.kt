package ru.kolsanovafit.workouts.domain.entity

data class Workout(
    val id: Int,
    val title: String,
    val description: String,
    val type: WorkoutType,
    val duration: String
)