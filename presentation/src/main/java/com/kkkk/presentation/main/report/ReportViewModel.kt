package com.kkkk.presentation.main.report

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject
constructor(
    // private val authRepository: AuthRepository,
) : ViewModel() {

    val isChangingMonth = MutableLiveData<Boolean>(false)
    val reportMonth = MutableLiveData<Int>(3)

    val mockList = listOf(
        Pair(10f, "2024-02-28"),
        Pair(20f, "2024-03-28"),
        Pair(20f, "2024-04-02"),
        Pair(30f, "2024-04-29"),
        Pair(10f, "2024-05-08"),
        Pair(20f, "2024-05-18"),
        Pair(20f, "2024-05-22"),
        Pair(30f, "2024-06-09"),
        Pair(100f, "2024-06-13"),
        Pair(90f, "2024-06-23")
    )
    var nowList = listOf<Pair<Float, String>>()

    private val _chartEntry = MutableStateFlow<MutableList<Entry>>(mutableListOf())
    val chartEntry: StateFlow<MutableList<Entry>> = _chartEntry

    init {
        setGraphValue()
    }

    fun setIsChangingMonth() {
        isChangingMonth.value = isChangingMonth.value?.not() ?: false
    }

    fun setReportMonth(month: Int) {
        reportMonth.value = month
        isChangingMonth.value = false
    }

    fun setGraphValue() {
        // TODO 서버 붙이고 수정
        nowList = when (reportMonth.value) {
            1 -> mockList.subList(5, 10)
            3 -> mockList.subList(2, 10)
            6 -> mockList.subList(0, 10)
            else -> return
        }
        val tempList = arrayListOf<Entry>()
        nowList.forEachIndexed { index, (value, _) ->
            tempList.add(Entry(index.toFloat(), value))
        }
        _chartEntry.value = tempList
    }
}