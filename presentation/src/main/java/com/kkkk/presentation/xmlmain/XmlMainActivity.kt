package com.kkkk.presentation.xmlmain

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.kkkk.core.base.BaseActivity
import com.kkkk.presentation.xmlmain.xmlprofile.XmlProfileFragment
import com.kkkk.presentation.xmlmain.xmlrecord.XmlRecordFragment
import com.kkkk.presentation.xmlmain.xmlrhythm.XmlRhythmFragment
import com.kkkk.presentation.xmlmain.xmlrhythm.XmlRhythmViewModel
import com.kkkk.presentation.xmlmain.xmlrhythm.XmlStretchFragment
import com.kkkk.presentation.xmlmain.xmlstudy.StudyFragment
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class XmlMainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var rhythmViewModel: XmlRhythmViewModel

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
        supportFragmentManager.findFragmentById(R.id.fcv_main) ?: navigateTo<XmlRhythmFragment>()

        binding.bnvMain.setOnItemSelectedListener { menu ->
            if (binding.bnvMain.selectedItemId == menu.itemId) {
                return@setOnItemSelectedListener false
            }
            when (menu.itemId) {
                R.id.menu_rhythm -> navigateTo<XmlRhythmFragment>()

                R.id.menu_report -> navigateTo<XmlRecordFragment>()

                R.id.menu_study -> navigateTo<StudyFragment>()

                R.id.menu_profile -> navigateTo<XmlProfileFragment>()

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
        rhythmViewModel = ViewModelProvider(this)[XmlRhythmViewModel::class.java]
    }

    private fun observeStretchViewNavigate() {
        rhythmViewModel.isStretchView.flowWithLifecycle(lifecycle).onEach { isStretch ->
            if (isStretch) {
                navigateTo<XmlStretchFragment>()
            } else {
                navigateTo<XmlRhythmFragment>()
            }
        }.launchIn(lifecycleScope)
    }
}
