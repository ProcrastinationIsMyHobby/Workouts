package ru.kolsanovafit.workouts.domain.entity

enum class WorkoutType(val value: Int) {
    TRAINING(1),
    LIVE(2),
    COMPLEX(3);

    companion object {
        fun fromValue(value: Int): WorkoutType? = entries.find { it.value == value }
    }
}