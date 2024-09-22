package com.kkkk.presentation.main.study

import android.util.Log
import androidx.lifecycle.ViewModel
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val studyRepository: StudyRepository,
) : ViewModel() {
    // 추후 과제 리스트 관련으로 사용
    private val _studyList = MutableStateFlow<List<Int>>(emptyList())
    val studyList: StateFlow<List<Int>> = _studyList

    private val _typeIsMe = MutableStateFlow(true)
    val typeIsMe: StateFlow<Boolean> = _typeIsMe

    fun setTypeIsMe(isMe: Boolean) {
        _typeIsMe.value = isMe
    }

    fun addItems(items: List<Int>) {
        _studyList.value += items
    }
}
