package ru.kolsanovafit.workouts.ui.detail_workout.media_player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LifecycleMediaPlayer(
    private val context: Context,
    private val videoUri: Uri,
    private val progressView: View? = null
) : DefaultLifecycleObserver {

    private var mediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null

    private var resumePosition = 0
    private var wasPlaying = false

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Initial)
    val playerState: StateFlow<PlayerState> = _playerState

    fun initializePlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnErrorListener { _, what, extra ->
                _playerState.value = PlayerState.Error(what, extra)
                true
            }
            setOnInfoListener { _, what, _ ->
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    progressView?.visibility = View.VISIBLE
                    _playerState.value = PlayerState.Buffering
                } else {
                    progressView?.visibility = View.GONE
                }
                true
            }
            setOnPreparedListener {
                progressView?.visibility = View.GONE
                start()
                _playerState.value = PlayerState.Playing
            }
            setOnCompletionListener {
                _playerState.value = PlayerState.Completed
            }
            setDataSource(context, videoUri)
        }
    }

    fun setVideoSurface(surface: Surface) {
        this.surface = surface
        mediaPlayer?.setSurface(surface)
        _playerState.value = PlayerState.Preparing
        progressView?.visibility = View.VISIBLE
        mediaPlayer?.prepareAsync()
    }

    fun togglePlay() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playerState.value = PlayerState.Paused
            } else {
                if (it.currentPosition >= it.duration) it.seekTo(0)
                it.start()
                _playerState.value = PlayerState.Playing
            }
        }
    }

    fun restartVideo() {
        mediaPlayer?.apply {
            seekTo(0)
            start()
            _playerState.value = PlayerState.Playing
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        mediaPlayer?.takeIf { wasPlaying }?.apply {
            seekTo(resumePosition)
            start()
            _playerState.value = PlayerState.Playing
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        mediaPlayer?.let {
            wasPlaying = it.isPlaying
            if (wasPlaying) {
                resumePosition = it.currentPosition
                it.pause()
                _playerState.value = PlayerState.Paused
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        releasePlayer()
    }

    fun releasePlayer() {
        mediaPlayer?.run {
            reset()
            release()
        }
        mediaPlayer = null
        surface?.release()
        surface = null
    }
}