package com.kkkk.presentation.main.rhythm

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseBottomSheet
import com.kkkk.core.extension.setOnSingleClickListener
import kr.genti.presentation.R
import kr.genti.presentation.databinding.BottomSheetRhythmBinding

class RhythmBottomSheet :
    BaseBottomSheet<BottomSheetRhythmBinding>(R.layout.bottom_sheet_rhythm) {

    private val viewModel by activityViewModels<RhythmViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        initSubmitBtnListener()
    }

    private fun initSubmitBtnListener() {
        binding.btnSubmitLevel.setOnSingleClickListener {
            viewModel.setRhythmLevel()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.resetTempRhythmLevel()
    }
}