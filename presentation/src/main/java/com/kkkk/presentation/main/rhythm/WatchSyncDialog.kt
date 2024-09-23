package com.kkkk.presentation.main.rhythm

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import com.kkkk.core.base.BaseDialog
import com.kkkk.core.extension.setOnSingleClickListener
import com.kkkk.presentation.manager.PhoneDataManager
import dagger.hilt.android.AndroidEntryPoint
import kr.genti.presentation.R
import kr.genti.presentation.databinding.DialogWatchSyncBinding
import javax.inject.Inject

@AndroidEntryPoint
class WatchSyncDialog :
    BaseDialog<DialogWatchSyncBinding>(R.layout.dialog_watch_sync) {
    private val viewModel by activityViewModels<RhythmViewModel>()

    @Inject
    lateinit var phoneDataManager: PhoneDataManager

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

        initWatchSyncBtnListener()
    }

    private fun initWatchSyncBtnListener() {
        binding.btnWatchSync.setOnSingleClickListener {
            phoneDataManager.sendIntToWearable(
                PhoneDataManager.PATH_BPM,
                PhoneDataManager.KEY_BPM,
                viewModel.bpm
            )
            dismiss()
        }
    }
}