package com.kkkk.presentation.xmlmain.xmlrhythm

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseDialog
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.DialogRhythmSaveBinding

class XmlRhythmSaveDialog :
    BaseDialog<DialogRhythmSaveBinding>(R.layout.dialog_rhythm_save) {
    private val viewModel by activityViewModels<XmlRhythmViewModel>()

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
        binding.btnPause.setOnSingleClickListener {
            dismiss()
        }
    }

    private fun initSaveBtnListener() {
        binding.btnSave.setOnSingleClickListener {
            viewModel.postRhythmRecordToSave()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.watchAccuracy = 0.0
    }
}