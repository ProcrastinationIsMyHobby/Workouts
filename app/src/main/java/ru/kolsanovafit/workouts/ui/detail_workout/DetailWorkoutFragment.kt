package ru.kolsanovafit.workouts.ui.detail_workout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.kolsanovafit.workouts.R
import ru.kolsanovafit.workouts.databinding.FragmentDetailWorkoutBinding

class DetailWorkoutFragment : Fragment(R.layout.fragment_detail_workout) {

    private lateinit var binding: FragmentDetailWorkoutBinding
    private val viewModel: DetailWorkoutViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailWorkoutBinding.bind(view)
    }

}