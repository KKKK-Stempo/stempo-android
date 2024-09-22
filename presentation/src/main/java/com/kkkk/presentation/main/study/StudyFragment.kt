package com.kkkk.presentation.main.study

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentStudyBinding

@AndroidEntryPoint
class StudyFragment : BaseFragment<FragmentStudyBinding>(R.layout.fragment_study) {
    private val tabTextList = listOf("재활운동", "정보")

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor(R.color.gray_100)
        setToggleClickListener()
    }

    @SuppressLint("ResourceAsColor")
    private fun setToggleClickListener() {
        binding.itemToggle.tvTeacher.setOnClickListener {
            with(binding.itemToggle) {
                tvTeacher.setBackgroundResource(R.drawable.shape_toggle_selected)
                tvTeacher.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                tvStudent.background = null
                tvStudent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
        binding.itemToggle.tvStudent.setOnClickListener {
            with(binding.itemToggle) {
                tvStudent.setBackgroundResource(R.drawable.shape_toggle_selected)
                tvStudent.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                tvTeacher.background = null
                tvTeacher.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
    }
}
