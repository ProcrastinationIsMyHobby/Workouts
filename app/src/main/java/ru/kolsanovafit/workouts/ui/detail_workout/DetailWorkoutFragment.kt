package ru.kolsanovafit.workouts.ui.detail_workout

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.FragmentDetailWorkoutBinding
import ru.kolsanovafit.workouts.ui.detail_workout.media_player.PlayerState
import ru.kolsanovafit.workouts.utils.fragmentLifecycleScope

@AndroidEntryPoint
class DetailWorkoutFragment : Fragment(R.layout.fragment_detail_workout) {

    private var _binding: FragmentDetailWorkoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailWorkoutViewModel by viewModels<DetailWorkoutViewModel>()
    private val args: DetailWorkoutFragmentArgs by navArgs()

    private var pendingLink: String? = null
    private var pendingSurface: Surface? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailWorkoutBinding.bind(view)
        viewLifecycleOwner.lifecycle.addObserver(viewModel.mediaPlayer)
        viewModel.loadVideoLink(args.id)

        binding.apply {
            tvTitle.text = args.title
            tvDescription.text = args.description
        }

        setupTextureListener()
        setupControls()
        observeViewModel()
    }

    private fun observeViewModel() {
        fragmentLifecycleScope(Lifecycle.State.RESUMED) {
            launch {
                viewModel.mediaPlayer.playerState.collect { playerState ->
                    updateUI(playerState)
                }
            }
            launch {
                viewModel.state.collect { state ->
                    when (state) {
                        is DetailWorkoutLoadState.Success ->{
                            val link = state.workout.link
                            pendingLink = link
                            pendingSurface?.let { surface ->
                                viewModel.initializePlayer(link)
                                viewModel.mediaPlayer.setVideoSurface(surface)
                                pendingLink = null
                            }
                        }
                        else-> Unit

                    }

                }
            }
        }
    }

    private fun updateUI(state: PlayerState) = binding.run {
        val isError = state is PlayerState.Error
        val isActive = state is PlayerState.Playing
                || state is PlayerState.Paused
                || state is PlayerState.Completed
        val isBuffering = state is PlayerState.Buffering
                || state is PlayerState.Preparing

        flVideoContainer.isVisible = !isError
        tvError.isVisible = isError
        btnPlayPause.isVisible = isActive
        btnReplay.isVisible = isActive
        progressBar.isVisible = isBuffering

        when (state) {
            is PlayerState.Error ->
                tvError.text = getString(R.string.playback_error, state.what.toString())

            is PlayerState.Playing ->
                btnPlayPause.setImageResource(R.drawable.ic_pause)

            is PlayerState.Paused,
            is PlayerState.Completed ->
                btnPlayPause.setImageResource(R.drawable.ic_play)

            else -> Unit
        }
    }


    private fun setupControls() {
        binding.apply {
            btnPlayPause.setOnClickListener { viewModel.mediaPlayer.togglePlay() }
            btnReplay.setOnClickListener { viewModel.mediaPlayer.restartVideo() }
        }
    }

    private fun setupTextureListener() {
        binding.textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(st: SurfaceTexture, width: Int, height: Int) {
                val surface = Surface(st)
                pendingSurface = surface
                pendingLink?.let { link ->
                    viewModel.initializePlayer(link)
                    viewModel.mediaPlayer.setVideoSurface(surface)
                    pendingLink = null
                }
            }

            override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) = Unit

            override fun onSurfaceTextureUpdated(st: SurfaceTexture) = Unit

            override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                viewModel.mediaPlayer.detachSurface()
                return true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}