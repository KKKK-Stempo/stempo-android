package com.kkkk.presentation.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.presentation.main.profile.ProfileFragment
import com.kkkk.presentation.main.record.RecordFragment
import com.kkkk.presentation.main.rhythm.RhythmFragment
import com.kkkk.presentation.main.rhythm.RhythmViewModel
import com.kkkk.presentation.main.rhythm.StretchFragment
import com.kkkk.presentation.main.study.StudyFragment
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var rhythmViewModel: RhythmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBnvItemIconTintList()
        initBnvItemSelectedListener()
        initViewModelProvider()
        observeStretchViewNavigate()
    }

    private fun initBnvItemIconTintList() {
        with(binding.bnvMain) {
            itemIconTintList = null
            selectedItemId = R.id.menu_rhythm
        }
    }

    private fun initBnvItemSelectedListener() {
        supportFragmentManager.findFragmentById(R.id.fcv_main) ?: navigateTo<RhythmFragment>()

        binding.bnvMain.setOnItemSelectedListener { menu ->
            if (binding.bnvMain.selectedItemId == menu.itemId) {
                return@setOnItemSelectedListener false
            }
            when (menu.itemId) {
                R.id.menu_rhythm -> navigateTo<RhythmFragment>()

                R.id.menu_report -> navigateTo<RecordFragment>()

                R.id.menu_study -> navigateTo<StudyFragment>()

                R.id.menu_profile -> navigateTo<ProfileFragment>()

                else -> return@setOnItemSelectedListener false
            }
            true
        }
    }

    private inline fun <reified T : Fragment> navigateTo() {
        supportFragmentManager.commit {
            replace<T>(R.id.fcv_main, T::class.java.canonicalName)
        }
    }

    private fun initViewModelProvider() {
        rhythmViewModel = ViewModelProvider(this)[RhythmViewModel::class.java]
    }

    private fun observeStretchViewNavigate() {
        rhythmViewModel.isStretchView.flowWithLifecycle(lifecycle).onEach { isStretch ->
            if (isStretch) {
                navigateTo<StretchFragment>()
            } else {
                navigateTo<RhythmFragment>()
            }
        }.launchIn(lifecycleScope)
    }
}
