package ru.kolsanovafit.workouts.ui.list_workouts.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.ItemWorkoutBinding
import ru.kolsanovafit.workouts.domain.entity.Workout

class WorkoutItemViewHolder(
    val binding: ItemWorkoutBinding,
    private val onItemClick: (Workout) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(workout: Workout) {
        binding.apply {
            textTitle.text = workout.title
            textType.text = workout.type.toString()
            // Потому что в json вместо минут иногда лежит это значение
            textDuration.text = if (workout.duration == "workout") {
                workout.duration
            } else {
                binding.root.context.getString(R.string.minutes, workout.duration)
            }
            textDescription.text = workout.description

            root.setOnClickListener {
                onItemClick.invoke(workout)
            }
        }
    }
}