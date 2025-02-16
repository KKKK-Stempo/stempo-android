package com.kkkk.presentation.onboarding.terms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.kkkk.core.base.BaseActivity
import com.kkkk.core.extension.navigateToScreenClear
import com.kkkk.presentation.manager.AmplitudeManager
import com.kkkk.presentation.onboarding.onbarding.OnboardingActivity
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.ActivityTermsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsActivity : BaseActivity<ActivityTermsBinding>(R.layout.activity_terms) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AmplitudeManager.trackEvent("view_terms")

        binding.tvTerms.setOnClickListener {
            navigateToWeb(URL_TERMS)
        }

        binding.tvPersonalInformation.setOnClickListener {
            navigateToWeb(URL_PERSONAL_INFORMATION)
        }

        binding.btnTermsAgree.setOnClickListener {
            AmplitudeManager.updateBooleanProperties("agreed_to_terms", true)
            navigateToScreenClear<OnboardingActivity>()
        }
    }

    private fun navigateToWeb(url: String) {
        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            startActivity(this)
        }
    }

    companion object {
        private const val URL_TERMS =
            "https://field-colt-189.notion.site/Stempo-1658e42622af4c7ab343db3f81d6006b?pvs=4"
        private const val URL_PERSONAL_INFORMATION =
            "https://field-colt-189.notion.site/Stempo-a3dfaebc01a744d19a6ddcd4a75917f6?pvs=4"
    }
}
