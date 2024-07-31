package com.kkkk.presentation.main.study

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.kkkk.core.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStudyExerciseBinding

@AndroidEntryPoint
class StudyExerciseFragment: BaseFragment<FragmentStudyExerciseBinding>(R.layout.fragment_study_exercise) {
    private val viewModel by viewModels<StudyViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // viewModel.getVideos()
    }
}
