package ru.kolsanovafit.workouts.ui.list_workouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kolsanovafit.workouts.domain.entity.LoadState
import ru.kolsanovafit.workouts.domain.repo.Repository
import javax.inject.Inject

@HiltViewModel
class ListWorkoutsViewModel @Inject constructor (
    private val repo: Repository,
) : ViewModel() {

    private val _state = MutableStateFlow<WorkoutUIState>(WorkoutUIState.Loading)
    val state = _state.asStateFlow()

    init{
        fetchWorkouts()
    }

    private fun fetchWorkouts() = viewModelScope.launch(Dispatchers.IO) {
        _state.value = WorkoutUIState.Loading
        when (val result = repo.getWorkouts()) {
            is LoadState.Success -> _state.value = WorkoutUIState.Success(result.data)
            is LoadState.Error -> _state.value = WorkoutUIState.Error(result.message)
            is LoadState.Empty -> _state.value = WorkoutUIState.Empty
            else -> {}
        }
    }
}