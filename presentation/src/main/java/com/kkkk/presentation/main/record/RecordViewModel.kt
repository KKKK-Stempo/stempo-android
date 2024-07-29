package com.kkkk.presentation.main.record

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.kkkk.core.state.UiState
import com.kkkk.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecordViewModel
@Inject
constructor(
    private val recordRepository: RecordRepository
) : ViewModel() {

    val isChangingMonth = MutableLiveData<Boolean>(false)
    val reportMonth = MutableLiveData<Int>(3)

    private val _chartEntry = MutableStateFlow<UiState<MutableList<Entry>>>(UiState.Empty)
    val chartEntry: StateFlow<UiState<MutableList<Entry>>> = _chartEntry

    var dateList = listOf<String>()

    var startDate = ""
    var endDate = ""

    init {
        setGraphWithDate()
    }

    fun setIsChangingMonth() {
        isChangingMonth.value = isChangingMonth.value?.not() ?: false
    }

    fun setReportMonth(month: Int) {
        reportMonth.value = month
        isChangingMonth.value = false
        setGraphWithDate()
    }

    private fun setGraphWithDate() {
        endDate = DATE_FORMAT.format(Date())
        DATE_FORMAT.parse(endDate)?.let { date ->
            val postCalendar = Calendar.getInstance().apply {
                time = date
                add(Calendar.MONTH, -(reportMonth.value ?: 3))
            }
            startDate = DATE_FORMAT.format(postCalendar.time)
        }
        setGraphValue()
    }

    private fun setGraphValue() {
        _chartEntry.value = UiState.Loading
        viewModelScope.launch {
            recordRepository.getRecordList(startDate, endDate)
                .onSuccess { recordList ->
                    if (recordList.isEmpty()) {
                        _chartEntry.value = UiState.Empty
                        return@launch
                    }
                    dateList = recordList.map { record ->
                        DATE_FORMAT.parse(record.date)?.let { DISPLAY_DATE_FORMAT.format(it) } ?: ""
                    }
                    _chartEntry.value = UiState.Success(
                        recordList.mapIndexed { index, record ->
                            Entry(index.toFloat(), record.accuracy.toFloat())
                        }.toMutableList()
                    )
                }
                .onFailure {
                    _chartEntry.value = UiState.Failure(it.message.toString())
                }
        }
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        private val DISPLAY_DATE_FORMAT = SimpleDateFormat("MM/dd", Locale.KOREA)
    }
}