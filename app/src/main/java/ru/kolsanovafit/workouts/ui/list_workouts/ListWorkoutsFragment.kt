package ru.kolsanovafit.workouts.ui.list_workouts

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.FragmentListWorkoutsBinding
import ru.kolsanovafit.workouts.domain.entity.NetworkError
import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.domain.entity.WorkoutType
import ru.kolsanovafit.workouts.ui.list_workouts.adapter.WorkoutItemAdapter
import ru.kolsanovafit.workouts.utils.fragmentLifecycleScope


@AndroidEntryPoint
class ListWorkoutsFragment : Fragment(R.layout.fragment_list_workouts) {

    private lateinit var binding: FragmentListWorkoutsBinding
    private val viewModel: ListWorkoutsViewModel by viewModels<ListWorkoutsViewModel>()

    private val workoutItemAdapter = WorkoutItemAdapter() { workout ->
        findNavController().navigate(
            ListWorkoutsFragmentDirections.actionListWorkoutsFragmentToWorkoutFragment(
                workout.id,
                workout.title,
                workout.description
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListWorkoutsBinding.bind(view)
        setupRecyclerView()
        setupSearchView()
        setupFilterButton()
        obverseState()
    }


    private fun setupFilterButton() {
        binding.buttonFilter.setOnClickListener { view ->
            showFilterPopup(view)
        }
    }

    private fun showFilterPopup(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter_all -> workoutItemAdapter.setTypeFilter(null)
                R.id.filter_training -> workoutItemAdapter.setTypeFilter(WorkoutType.TRAINING.value)
                R.id.filter_live -> workoutItemAdapter.setTypeFilter(WorkoutType.LIVE.value)
                R.id.filter_complex -> workoutItemAdapter.setTypeFilter(WorkoutType.COMPLEX.value)
            }
            true
        }
        popup.show()
    }

    private fun obverseState() = fragmentLifecycleScope(Lifecycle.State.RESUMED) {
        viewModel.state.collect { state ->
            when (state) {
                WorkoutUIState.Loading -> {
                    showLoading()
                }

                is WorkoutUIState.Success -> {
                    showWorkouts(state.workouts)
                }

                WorkoutUIState.Empty -> {
                    showEmptyTextView()
                }

                is WorkoutUIState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        formatErrorMessage(state.error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showEmptyTextView() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.VISIBLE
        }
    }

    private fun formatErrorMessage(error: NetworkError): String {
        return when (error) {
            is NetworkError.ServerError ->
                getString(R.string.server_error, error.code, error.serverMessage)

            is NetworkError.ConnectionError ->
                getString(R.string.connection_error)

            is NetworkError.UnknownError ->
                getString(R.string.unknown_error)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            emptyTextView.visibility = View.GONE
        }
    }

    private fun showWorkouts(workouts: List<Workout>) {
        binding.apply {
            workoutItemAdapter.fullWorkoutList = workouts
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            emptyTextView.visibility = View.GONE

        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            recyclerView.apply {
                adapter = workoutItemAdapter
                layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = LinearLayoutManager.VERTICAL
                }
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            searchView.clearFocus()
                        }
                    }
                })
            }
        }
    }

    private fun setupSearchView() {
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    workoutItemAdapter.filter.filter(newText)
                    return true
                }
            })
        }
    }
}