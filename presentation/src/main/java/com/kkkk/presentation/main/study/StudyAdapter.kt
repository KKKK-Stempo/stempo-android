package com.kkkk.presentation.main.study

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.kkkk.core.util.ItemDiffCallback
import com.kkkk.domain.entity.response.StudyModel
import kr.genti.presentation.databinding.ItemStudyCheckStringBinding

class StudyAdapter(context: Context, private val isMe: Boolean) :
    ListAdapter<StudyModel.StudyItemModel, StudyViewHolder>(
        StudyDiffCallback,
    ) {
    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyViewHolder {
        val binding = ItemStudyCheckStringBinding.inflate(inflater, parent, false)
        return StudyViewHolder(binding, isMe = isMe)
    }

    override fun onBindViewHolder(holder: StudyViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    override fun getItemCount() = currentList.size

    companion object {
        private val StudyDiffCallback =
            ItemDiffCallback<StudyModel.StudyItemModel>(
                onItemsTheSame = { old, new -> old.id == new.id },
                onContentsTheSame = { old, new -> old == new },
            )
    }
}