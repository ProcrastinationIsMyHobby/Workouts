package ru.kolsanovafit.workouts.data.dto

import ru.kolsanovafit.workouts.domain.entity.WorkoutVideo

data class VideoWorkoutDTO(
    val id: Int,
    val duration: String,
    val link: String
)

fun VideoWorkoutDTO.toDomain(baseUrl: String): WorkoutVideo {
    return WorkoutVideo(
        id = id,
        duration = duration,
        link = baseUrl + link
    )
}