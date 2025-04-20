package ru.kolsanovafit.workouts.ui.detail_workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.kolsanovafit.workouts.domain.entity.LoadResult
import ru.kolsanovafit.workouts.domain.repo.Repository
import ru.kolsanovafit.workouts.ui.detail_workout.media_player.LifecycleMediaPlayer
import javax.inject.Inject

@HiltViewModel
class DetailWorkoutViewModel @Inject constructor(
    private val repo: Repository,
) : ViewModel() {

    companion object {
        const val DEFAULT_VIDEO_URL = "https://ref.test.kolsa.ru/example-02.mp4"
    }

    val mediaPlayer = LifecycleMediaPlayer()

    private val _state = MutableStateFlow<DetailWorkoutLoadState>(DetailWorkoutLoadState.Loading)
    val state = _state.asStateFlow()

    fun loadVideoLink(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        _state.value = DetailWorkoutLoadState.Loading
        when (val result = repo.getVideoById(id)) {
            is LoadResult.Success -> _state.value = DetailWorkoutLoadState.Success(result.data)
            is LoadResult.Error -> _state.value = DetailWorkoutLoadState.Error(result.error)
            is LoadResult.Empty -> _state.value = DetailWorkoutLoadState.Empty
        }
    }

    fun initializePlayer(link: String) {
        mediaPlayer.initializePlayer(link)
    }

    override fun onCleared() {
        mediaPlayer.releasePlayer()
        super.onCleared()
    }
}