package com.kkkk.presentation.main.record

import androidx.lifecycle.ViewModel
import com.kkkk.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecordViewModel
@Inject
constructor(
    private val recordRepository: RecordRepository,
) : ViewModel() {
    private val _recordState = MutableStateFlow(RecordState())
    val recordState = _recordState.asStateFlow()
}