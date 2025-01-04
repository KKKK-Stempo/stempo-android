package com.kkkk.presentation.xmlmain.xmlprofile

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.kkkk.core.base.BaseDialog
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.DialogProfileWithDrawBinding

class XmlProfileWithDrawDialog :
    BaseDialog<DialogProfileWithDrawBinding>(R.layout.dialog_profile_with_draw) {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[XmlProfileViewModel::class.java] }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initPauseBtnListener()
        initSaveBtnListener()
    }

    private fun initPauseBtnListener() {
        binding.btnCancel.setOnSingleClickListener {
            dismiss()
        }
    }

    private fun initSaveBtnListener() {
        binding.btnDelete.setOnSingleClickListener {
            viewModel.withdraw()
            dismiss()
        }
    }
}
