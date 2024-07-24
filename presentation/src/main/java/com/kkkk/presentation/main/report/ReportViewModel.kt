package com.kkkk.presentation.main.report

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject
constructor(
    // private val authRepository: AuthRepository,
) : ViewModel() {

    val isChangingMonth = MutableLiveData<Boolean>(false)
    val reportMonth = MutableLiveData<Int>(1)

    var chartEntry = arrayListOf<Entry>()
    val mockList = listOf(
        Pair(10f, "2024-02-28"),
        Pair(20f, "2024-03-28"),
        Pair(20f, "2024-04-02"),
        Pair(30f, "2024-04-29"),
        Pair(100f, "2024-05-23"),
        Pair(90f, "2024-06-13")
    )

    fun setIsChangingMonth() {
        isChangingMonth.value = isChangingMonth.value?.not() ?: false
    }

    fun setReportMonth(month: Int) {
        reportMonth.value = month
        isChangingMonth.value = false
    }

    fun setGraphValue() {
        chartEntry = arrayListOf<Entry>()
        mockList.forEachIndexed { index, (value, _) ->
            chartEntry.add(Entry(index.toFloat(), value))
        }
    }
}