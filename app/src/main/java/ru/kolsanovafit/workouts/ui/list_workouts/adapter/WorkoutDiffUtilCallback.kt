package ru.kolsanovafit.workouts.ui.list_workouts.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.kolsanovafit.workouts.domain.entity.Workout

class WorkoutDiffUtilCallback(
    private val oldList: List<Workout>,
    private val newList: List<Workout>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}