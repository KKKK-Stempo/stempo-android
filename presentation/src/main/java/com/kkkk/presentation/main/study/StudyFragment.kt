package com.kkkk.presentation.main.study

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.kkkk.core.base.BaseFragment
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

        setTabLayout()
        setViewPager()
    }

    private fun setTabLayout() {
        binding.tabStudy.apply {
            for (tabName in tabTextList) {
                val tab = this.newTab()
                tab.text = tabName
                this.addTab(tab)
            }
        }
    }

    private fun setViewPager() {
        binding.vpStudy.adapter = StudyViewPagerAdapter(requireActivity())
        binding.vpStudy.isUserInputEnabled = false
        TabLayoutMediator(binding.tabStudy, binding.vpStudy) { tab, pos ->
            tab.text = tabTextList[pos]
        }.attach()
    }
}
