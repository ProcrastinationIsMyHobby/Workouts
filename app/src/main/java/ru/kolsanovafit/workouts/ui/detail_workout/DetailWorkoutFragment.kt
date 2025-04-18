package ru.kolsanovafit.workouts.ui.detail_workout

import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.FragmentDetailWorkoutBinding
import ru.kolsanovafit.workouts.utils.fragmentLifecycleScope

@AndroidEntryPoint
class DetailWorkoutFragment : Fragment(R.layout.fragment_detail_workout) {

    private var _binding: FragmentDetailWorkoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailWorkoutViewModel by viewModels<DetailWorkoutViewModel>()
    private val args: DetailWorkoutFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailWorkoutBinding.bind(view)

        // Инициализируем плеер с прогресс-баром
        viewModel.initializePlayer(binding.progressBar)

        // Добавляем наблюдение за жизненным циклом
        lifecycle.addObserver(viewModel.mediaPlayer)

        binding.apply {
            tvTitle.text = args.title
            tvDescription.text = args.description
        }

        setupTextureListener()
        setupControls()
        observeViewModel()
    }

    private fun observeViewModel() {
        fragmentLifecycleScope(Lifecycle.State.STARTED) {
            launch {
                viewModel.playPauseIcon.collect { iconRes ->
                    binding.btnPlayPause.setImageResource(iconRes)
                }
            }

            launch {
                viewModel.showError.collect { error ->
                    error?.let { (what, extra) ->
                        Toast.makeText(
                            requireContext(),
                            "Error: $what, $extra",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
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
                viewModel.mediaPlayer.setVideoSurface(surface)
            }

            override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) = Unit

            override fun onSurfaceTextureUpdated(st: SurfaceTexture) = Unit

            override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                // Только отсоединяем поверхность, без освобождения плеера
                viewModel.mediaPlayer.detachSurface()
                return true
            }
        }
    }

    override fun onDestroyView() {
        // Отключаем наблюдатель жизненного цикла только если фрагмент действительно уничтожается
        if (requireActivity().isFinishing) {
            lifecycle.removeObserver(viewModel.mediaPlayer)
        }

        // Освобождаем binding
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        // Только если фрагмент действительно уничтожается, а не пересоздается
        if (!requireActivity().isChangingConfigurations) {
            lifecycle.removeObserver(viewModel.mediaPlayer)
        }
        super.onDestroy()
    }
}