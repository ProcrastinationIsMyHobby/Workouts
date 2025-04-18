package ru.kolsanovafit.workouts.ui.detail_workout.media_player

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.Surface
import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LifecycleMediaPlayer(
    private val context: Context,
    private val videoUri: String,
    private var progressView: View? = null
) : DefaultLifecycleObserver {

    private var mediaPlayer: MediaPlayer? = null
    private var surface: Surface? = null
    private var isPrepared = false

    private var resumePosition = 0
    private var wasPlaying = false

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Initial)
    val playerState: StateFlow<PlayerState> = _playerState

    fun setProgressView(view: View) {
        progressView = view
    }

    fun initializePlayer() {
        if (mediaPlayer == null) {
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
                    isPrepared = true
                    progressView?.visibility = View.GONE
                    start()
                    _playerState.value = PlayerState.Playing
                }
                setOnCompletionListener {
                    _playerState.value = PlayerState.Completed
                }
                try {
                    setDataSource(videoUri.toString())
                } catch (e: Exception) {
                    _playerState.value = PlayerState.Error(-1, 0)
                    e.printStackTrace()
                }
            }
        }
    }

    fun setVideoSurface(surface: Surface) {
        this.surface = surface
        mediaPlayer?.setSurface(surface)

        if (!isPrepared) {
            _playerState.value = PlayerState.Preparing
            progressView?.visibility = View.VISIBLE
            mediaPlayer?.prepareAsync()
        } else {
            mediaPlayer?.seekTo(resumePosition)
            if (wasPlaying) {
                mediaPlayer?.start()
                _playerState.value = PlayerState.Playing
            }
        }
    }

    fun togglePlay() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playerState.value = PlayerState.Paused
            } else {
                if (isPrepared && it.currentPosition >= it.duration) it.seekTo(0)
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
        if (isPrepared && wasPlaying) {
            mediaPlayer?.apply {
                seekTo(resumePosition)
                start()
                _playerState.value = PlayerState.Playing
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        mediaPlayer?.let {
            if (isPrepared) {
                wasPlaying = it.isPlaying
                if (wasPlaying) {
                    resumePosition = it.currentPosition
                    it.pause()
                    _playerState.value = PlayerState.Paused
                }
            }
        }
    }

    // Безопасное отсоединение поверхности без полного освобождения плеера
    fun detachSurface() {
        if (mediaPlayer?.isPlaying == true) {
            wasPlaying = true
            resumePosition = mediaPlayer?.currentPosition ?: 0
            mediaPlayer?.pause()
        }
        mediaPlayer?.setSurface(null)
        surface?.release()
        surface = null
    }

    fun releasePlayer() {
        mediaPlayer?.run {
            try {
                if (isPlaying) stop()
                reset()
                release()
            } catch (e: Exception) {

            }
        }
        mediaPlayer = null
        isPrepared = false
        surface?.release()
        surface = null
    }
}