package ru.kolsanovafit.workouts.ui.list_workouts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.kolsanovafit.workouts.databinding.ItemWorkoutBinding
import ru.kolsanovafit.workouts.domain.entity.Workout

class WorkoutItemAdapter(
    private val onItemClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutItemViewHolder>(), Filterable {

    var fullWorkoutList: List<Workout> = emptyList()
        set(value) {
            field = value
            filter.filter(currentQuery)
        }

    private var workoutList: List<Workout> = emptyList()

    private var currentQuery: String = ""
    private var currentTypeFilter: Int? = null

    fun setTypeFilter(typeValue: Int?) {
        currentTypeFilter = typeValue
        filter.filter(currentQuery)
    }

    private val workoutFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            currentQuery = constraint?.toString()?.trim() ?: ""
            var filteredList = if (currentQuery.isEmpty()) {
                fullWorkoutList
            } else {
                fullWorkoutList.filter {
                    it.title.contains(currentQuery, ignoreCase = true)
                }
            }
            currentTypeFilter?.let { typeValue ->
                filteredList = filteredList.filter { it.type.value == typeValue }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val filtered = results?.values as? List<Workout> ?: emptyList()
            updateList(filtered)
        }
    }

    private fun updateList(newList: List<Workout>) {
        val diffResult = DiffUtil.calculateDiff(WorkoutDiffUtilCallback(workoutList, newList))
        workoutList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getFilter(): Filter = workoutFilter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWorkoutBinding.inflate(inflater, parent, false)
        return WorkoutItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: WorkoutItemViewHolder, position: Int) {
        holder.bind(workoutList[position])
    }

    override fun getItemCount(): Int = workoutList.size
}