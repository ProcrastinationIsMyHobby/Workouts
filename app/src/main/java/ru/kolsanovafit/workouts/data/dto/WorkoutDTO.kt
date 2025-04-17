package ru.kolsanovafit.workouts.data.dto

import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.domain.entity.WorkoutType

data class WorkoutDTO(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Int,
    val duration: String
)

fun WorkoutDTO.toDomain(): Workout {
    return Workout(
        id = id,
        title = title,
        description = description ?: "",
        type = WorkoutType.fromValue(type)
            ?: WorkoutType.TRAINING,
        duration = duration
    )
}

fun List<WorkoutDTO>.toDomain(): List<Workout> = map { it.toDomain() }