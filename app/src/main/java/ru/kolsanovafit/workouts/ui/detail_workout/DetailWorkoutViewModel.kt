package ru.kolsanovafit.workouts.ui.detail_workout

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.kolsanovafit.workouts.domain.repo.Repository
import ru.kolsanovafit.workouts.ui.detail_workout.media_player.LifecycleMediaPlayer
import javax.inject.Inject

@HiltViewModel
class DetailWorkoutViewModel @Inject constructor(
    private val repo: Repository,
) : ViewModel() {

    companion object {
        private const val DEFAULT_VIDEO_URL = "https://ref.test.kolsa.ru/example-02.mp4"
    }

    val mediaPlayer = LifecycleMediaPlayer(
        DEFAULT_VIDEO_URL,
        null
    )

    private val _state = MutableStateFlow<DetailWorkoutUIState>(DetailWorkoutUIState.Loading)
    val state = _state.asStateFlow()

    fun initializePlayer(progressView: android.view.View) {
        mediaPlayer.setProgressView(progressView)
        mediaPlayer.initializePlayer()
    }

    override fun onCleared() {
        mediaPlayer.releasePlayer()
        super.onCleared()
    }
}