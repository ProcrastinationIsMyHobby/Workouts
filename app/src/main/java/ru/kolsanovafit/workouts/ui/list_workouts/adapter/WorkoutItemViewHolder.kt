package ru.kolsanovafit.workouts.ui.list_workouts.adapter

import androidx.recyclerview.widget.RecyclerView
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
            textDuration.text = workout.duration
            textDescription.text = workout.description

            root.setOnClickListener {
                onItemClick.invoke(workout)
            }
        }
    }
}