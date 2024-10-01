package com.kkkk.presentation.main.rhythm

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kkkk.core.base.BaseBottomSheet
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.stempo.presentation.R
import com.kkkk.stempo.presentation.databinding.BottomSheetRhythmBinding

class RhythmBottomSheet :
    BaseBottomSheet<BottomSheetRhythmBinding>(R.layout.bottom_sheet_rhythm) {

    private val viewModel by activityViewModels<RhythmViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            window?.setBackgroundDrawableResource(R.color.transparent)
            findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let {
                it.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                        BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                        it.requestLayout()
                    }
                })
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        viewModel.setRhythmToTemp()
        initSubmitBtnListener()
    }

    private fun initSubmitBtnListener() {
        binding.btnSubmitLevel.setOnSingleClickListener {
            viewModel.setTempToRhythm()
            dismiss()
        }
    }
}