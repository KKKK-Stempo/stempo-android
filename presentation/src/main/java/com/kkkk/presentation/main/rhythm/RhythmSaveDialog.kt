package com.kkkk.presentation.main.rhythm

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseDialog
import com.kkkk.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogRhythmSaveBinding

class RhythmSaveDialog :
    BaseDialog<DialogRhythmSaveBinding>(R.layout.dialog_rhythm_save) {
    private val viewModel by activityViewModels<RhythmViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
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
        binding.btnPause.setOnSingleClickListener { dismiss() }
    }

    private fun initSaveBtnListener() {
        binding.btnSave.setOnSingleClickListener {
            viewModel.posRhythmRecordToSave()
            dismiss()
        }
    }
}