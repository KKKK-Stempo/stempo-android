package com.kkkk.presentation.main.study

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStudyBinding

@AndroidEntryPoint
class StudyFragment : BaseFragment<FragmentStudyBinding>(R.layout.fragment_study) {
    private val viewModel by activityViewModels<StudyViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor(R.color.gray_100)
        setToggleClickListener()
        viewModel.typeIsMe.flowWithLifecycle(lifecycle).distinctUntilChanged().onEach { isMe ->
            setToggleState(isMe)
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ResourceAsColor")
    private fun setToggleClickListener() {
        binding.itemToggle.tvStudent.setOnClickListener {
            viewModel.setTypeIsMe(true)
        }
        binding.itemToggle.tvTeacher.setOnClickListener {
            viewModel.setTypeIsMe(false)
        }
    }

    private fun setToggleState(isMe: Boolean) {
        when (isMe) {
            true -> {
                with(binding) {
                    with(itemToggle) {
                        tvStudent.setBackgroundResource(R.drawable.shape_toggle_selected)
                        tvStudent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.black
                            )
                        )
                        tvTeacher.background = null
                        tvTeacher.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }

                    layoutMyExercise.visibility = View.VISIBLE
                    layoutTeacherExercise.visibility = View.GONE

                    if (viewModel.studyList.value.isEmpty()) {
                        ivMyExerciseEmpty.visibility = View.VISIBLE
                        layoutMyExerciseValid.visibility = View.GONE
                    } else {
                        ivMyExerciseEmpty.visibility = View.GONE
                        layoutMyExerciseValid.visibility = View.VISIBLE
                    }
                }
            }

            false -> {
                with(binding) {
                    with(itemToggle) {
                        tvTeacher.setBackgroundResource(R.drawable.shape_toggle_selected)
                        tvTeacher.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.black
                            )
                        )
                        tvStudent.background = null
                        tvStudent.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.white
                            )
                        )
                    }

                    layoutMyExercise.visibility = View.GONE
                    layoutTeacherExercise.visibility = View.VISIBLE

                    if (viewModel.studyList.value.isEmpty()) {
                        ivTeacherExerciseEmpty.visibility = View.VISIBLE
                        rvTeacherExercise.visibility = View.GONE
                    } else {
                        ivTeacherExerciseEmpty.visibility = View.GONE
                        rvTeacherExercise.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
