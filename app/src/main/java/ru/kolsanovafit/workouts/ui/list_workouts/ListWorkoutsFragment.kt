package ru.kolsanovafit.workouts.ui.list_workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.FragmentListWorkoutsBinding
import ru.kolsanovafit.workouts.domain.entity.Workout
import ru.kolsanovafit.workouts.domain.entity.WorkoutType
import ru.kolsanovafit.workouts.extentions.formatErrorMessage
import ru.kolsanovafit.workouts.ui.list_workouts.adapter.WorkoutItemAdapter

@AndroidEntryPoint
class ListWorkoutsFragment : Fragment(R.layout.fragment_list_workouts) {

    private var _binding: FragmentListWorkoutsBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun obverseState() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.state.collect { state ->
                when (state) {
                    WorkoutListLoadState.Loading -> {
                        showLoading()
                    }

                    is WorkoutListLoadState.Success -> {
                        showWorkouts(state.workouts)
                    }

                    WorkoutListLoadState.Empty -> {
                        showEmptyTextView()
                    }

                    is WorkoutListLoadState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            formatErrorMessage(state.error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}