package com.kkkk.presentation.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.colorOf
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.setStatusBarColor
import com.kkkk.core.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.FragmentProfileBinding

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initWebBtnListener()
        initReportBtnListener()
        setStatusBarColor(R.color.gray_100)
    }

    private fun initWebBtnListener() {
        with(binding) {
            btnAnnounce.setOnSingleClickListener { navigateToWeb(URL_ANNOUNCE) }
            btnFaq.setOnSingleClickListener { navigateToWeb(URL_FAQ) }
        }
    }

    private fun initReportBtnListener() {
        with(binding) {
            btnVoice.setOnSingleClickListener { toast("다음 업데이트 때 사용 가능해요!") }
            btnSuggest.setOnSingleClickListener { toast("다음 업데이트 때 사용 가능해요!") }
        }
    }

    private fun navigateToWeb(url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            startActivity(this)
        }
    }

    companion object {
        private const val URL_ANNOUNCE ="https://field-colt-189.notion.site/Stempo-e8252a5094eb4351819809dc3abd6623?pvs=4"
        private const val URL_FAQ = "https://field-colt-189.notion.site/FAQ-3e01b4b00b0c4b2c84aaacd79b2b6045?pvs=4"
    }
}