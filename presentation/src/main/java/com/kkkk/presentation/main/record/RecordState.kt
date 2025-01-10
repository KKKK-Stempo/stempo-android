package com.kkkk.presentation.main.record

import com.github.mikephil.charting.data.Entry

data class RecordState(
    val selectedMonth: Int = 3,
    val averageAccuracy: Int = 0,
    val isDialogVisible: Boolean = false,
    val dateList: List<String> = emptyList(),
    val entriesList: List<Entry> = emptyList(),
    val isRecordEmpty: Boolean = true,
    val countToday: Int = 0,
    val countWeek: Int = 0,
    val countConsecutive: Int = 0,
)