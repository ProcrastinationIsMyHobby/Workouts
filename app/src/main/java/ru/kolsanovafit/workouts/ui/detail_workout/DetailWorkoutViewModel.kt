package ru.kolsanovafit.workouts.ui.detail_workout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.domain.repo.Repository
import ru.kolsanovafit.workouts.ui.detail_workout.media_player.LifecycleMediaPlayer
import ru.kolsanovafit.workouts.ui.detail_workout.media_player.PlayerState
import javax.inject.Inject

@HiltViewModel
class DetailWorkoutViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val repo: Repository,
) : ViewModel() {

    companion object {
        private const val DEFAULT_VIDEO_URL = "https://ref.test.kolsa.ru/example-02.mp4"
    }

    // Создаем mediaPlayer сразу при инициализации ViewModel
    val mediaPlayer = LifecycleMediaPlayer(
        context,
        DEFAULT_VIDEO_URL,
        null  // Прогресс-бар установим позже
    )


    private val _playPauseIcon = MutableStateFlow(R.drawable.ic_play)
    val playPauseIcon: StateFlow<Int> = _playPauseIcon

    private val _showError = MutableStateFlow<Pair<Int, Int>?>(null)
    val showError: StateFlow<Pair<Int, Int>?> = _showError

    // Модифицируем функцию для установки progress view
    fun initializePlayer(progressView: android.view.View) {
        mediaPlayer.setProgressView(progressView)
        mediaPlayer.initializePlayer()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch(Dispatchers.IO) {
            mediaPlayer.playerState.collect { state ->
                when (state) {
                    is PlayerState.Playing -> _playPauseIcon.value = R.drawable.ic_pause
                    is PlayerState.Paused, is PlayerState.Completed -> _playPauseIcon.value =
                        R.drawable.ic_play
                    is PlayerState.Error -> _showError.value = state.what to state.extra
                    else -> Unit
                }
            }
        }
    }

    override fun onCleared() {
        mediaPlayer.releasePlayer()
        super.onCleared()
    }
}