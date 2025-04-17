package ru.kolsanovafit.workouts.ui.detail_workout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kolsanovafit.workouts.domain.repo.Repository
import javax.inject.Inject

@HiltViewModel
class DetailWorkoutViewModel @Inject constructor (
    private val repo: Repository,
) : ViewModel() {

}