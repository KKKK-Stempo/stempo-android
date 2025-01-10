package com.kkkk.presentation.main.record

data class RecordState(
    val selectedMonth: Int = 3,
    val averageAccuracy: Int = 0,
    val isDialogVisible: Boolean = false,
    val countToday: Int = 0,
    val countWeek: Int = 0,
    val countConsecutive: Int = 0,
)