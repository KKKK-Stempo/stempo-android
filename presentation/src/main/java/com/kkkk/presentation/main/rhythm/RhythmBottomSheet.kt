package com.kkkk.presentation.main.rhythm

import android.os.Bundle
import android.view.View
import com.kkkk.core.base.BaseBottomSheet
import kr.genti.presentation.R
import kr.genti.presentation.databinding.BottomSheetRhythmBinding

class RhythmBottomSheet :
    BaseBottomSheet<BottomSheetRhythmBinding>(R.layout.bottom_sheet_rhythm) {

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

    }
}