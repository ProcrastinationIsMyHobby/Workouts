package ru.kolsanovafit.workouts.ui.list_workouts

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.FragmentListWorkoutsBinding
import ru.kolsanovafit.workouts.domain.entity.NetworkError
import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.ui.list_workouts.adapter.WorkoutItemAdapter
import ru.kolsanovafit.workouts.utils.fragmentLifecycleScope


@AndroidEntryPoint
class ListWorkoutsFragment : Fragment(R.layout.fragment_list_workouts) {

    private lateinit var binding: FragmentListWorkoutsBinding
    private val viewModel: ListWorkoutsViewModel by viewModels<ListWorkoutsViewModel>()

    private val workoutItemAdapter = WorkoutItemAdapter() { workout ->
        //навигация на второй фрагмент
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListWorkoutsBinding.bind(view)

        setupRecyclerView()
        setupSearchView()

        fragmentLifecycleScope(Lifecycle.State.RESUMED) {
            viewModel.state.collect { state ->
                when (state) {
                    WorkoutUIState.Loading -> {
                        showLoading()
                    }

                    is WorkoutUIState.Success -> {
                        showWorkouts(state.workouts)
                    }

                    WorkoutUIState.Empty -> {}
                    is WorkoutUIState.Error -> {
                        Toast.makeText(requireContext(), formatErrorMessage(state.error), Toast.LENGTH_LONG).show()
                    }
                }
            }
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
            //errorLayout.visibility = View.GONE
            //emptyLayout.visibility = View.GONE
        }
    }

    private fun showWorkouts(workouts: List<Workout>) {
        binding.apply {
            workoutItemAdapter.fullWorkoutList = workouts
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            //errorLayout.visibility = View.GONE
            //emptyLayout.visibility = View.GONE

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