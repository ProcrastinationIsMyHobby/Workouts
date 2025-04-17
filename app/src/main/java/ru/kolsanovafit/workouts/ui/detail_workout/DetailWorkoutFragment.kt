package ru.kolsanovafit.workouts.ui.detail_workout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.FragmentDetailWorkoutBinding

class DetailWorkoutFragment : Fragment(R.layout.fragment_detail_workout) {

    private var _binding: FragmentDetailWorkoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailWorkoutViewModel by viewModels<DetailWorkoutViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentDetailWorkoutBinding.bind(view)
    }
}
