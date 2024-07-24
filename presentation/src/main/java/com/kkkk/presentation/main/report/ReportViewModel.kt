package com.kkkk.presentation.main.report

import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject
constructor(
    // private val authRepository: AuthRepository,
) : ViewModel() {
    var chartEntry = arrayListOf<Entry>()
    val mockList = listOf(
        Pair(10f, "2024-02-28"),
        Pair(20f, "2024-03-28"),
        Pair(20f, "2024-04-02"),
        Pair(30f, "2024-04-29"),
        Pair(100f, "2024-05-23"),
        Pair(90f, "2024-06-13")
    )

    fun setGraphValue() {
        chartEntry = arrayListOf<Entry>()
        mockList.forEachIndexed { index, (value, _) ->
            chartEntry.add(Entry(index.toFloat(), value))
        }
    }
}