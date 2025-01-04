package com.kkkk.presentation.xmlmain.xmlprofile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.jakewharton.processphoenix.ProcessPhoenix
import com.kkkk.core.base.BaseFragment
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.core.extension.setStatusBarColor
import com.kkkk.core.extension.toast
import com.kkkk.stempo.presentation.BuildConfig
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class XmlProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    private val viewModel by activityViewModels<XmlProfileViewModel>()
    private var profileWithDrawDialog: XmlProfileWithDrawDialog? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        observeRebirthEvent()
        initWebBtnListener()
        initReportBtnListener()
        initVersionListener()
        setStatusBarColor(R.color.gray_100)
    }

    private fun observeRebirthEvent() {
        viewModel.rebirth.onEach {
            ProcessPhoenix.triggerRebirth(context)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
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
            btnSuggest.setOnSingleClickListener { navigateToWeb(URL_SUGGEST) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initVersionListener() {
        with(binding) {
            tvVersionCode.text = "v ${BuildConfig.VERSION_NAME}"
            btnWithdraw.setOnSingleClickListener {
                profileWithDrawDialog = XmlProfileWithDrawDialog()
                profileWithDrawDialog?.show(
                    parentFragmentManager,
                    XmlProfileWithDrawDialog::class.java.simpleName
                )
            }
        }
    }

    private fun navigateToWeb(url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            startActivity(this)
        }
    }

    companion object {
        private const val URL_ANNOUNCE =
            "https://field-colt-189.notion.site/Stempo-e8252a5094eb4351819809dc3abd6623?pvs=4"
        private const val URL_FAQ =
            "https://field-colt-189.notion.site/FAQ-3e01b4b00b0c4b2c84aaacd79b2b6045?pvs=4"
        private const val URL_SUGGEST =
            "https://docs.google.com/forms/d/e/1FAIpQLSfTyRUJzIURmSvIJOgJlqqLCRECVPtTHWj8xCNsBrIIuzwBRA/viewform"
    }
}