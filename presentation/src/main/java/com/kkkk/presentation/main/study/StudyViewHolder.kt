package com.kkkk.presentation.main.study

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kkkk.domain.entity.response.StudyModel
import kr.genti.presentation.R
import kr.genti.presentation.databinding.ItemStudyCheckStringBinding

class StudyViewHolder(
    private val binding: ItemStudyCheckStringBinding,
    private val isMe: Boolean
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: StudyModel.StudyItemModel) =
        with(binding) {
            ivCheckbox.setImageResource(
                if (data.completed) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
            )
            if (isMe) {
                ivDelete.visibility = View.GONE
                etString.isEnabled = false
            } else{
                ivDelete.visibility = View.VISIBLE
                etString.isEnabled = true
            }
            etString.setText(data.description)
        }
}
