package com.kkkk.presentation.main.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.kkkk.domain.entity.response.RecordModel
import com.kkkk.domain.repository.RecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val recordRepository: RecordRepository,
) : ViewModel() {
    private val _recordState = MutableStateFlow(RecordState())
    val recordState = _recordState.asStateFlow()

    private val _recordSideEffect = MutableSharedFlow<RecordSideEffect>()
    val recordSideEffect = _recordSideEffect.asSharedFlow()

    init {
        getStatisticBadges()
    }

    fun updateSelectedMonth(month: Int) {
        _recordState.update { it.copy(selectedMonth = month) }
    }

    private fun getStatisticBadges() {
        viewModelScope.launch {
            recordRepository.getRecordStatistics()
                .onSuccess { statistics ->
                    _recordState.update {
                        it.copy(
                            countToday = statistics.todayWalkTrainingCount,
                            countWeek = statistics.weeklyWalkTrainingCount,
                            countConsecutive = statistics.consecutiveWalkTrainingDays
                        )
                    }
                }
                .onFailure {
                    _recordSideEffect.emit(RecordSideEffect.ErrorToast)
                }
        }
    }

    fun setGraphWithDate() {
        val endDate = DATE_FORMAT.format(Date())
        val startDate = runCatching {
            Calendar.getInstance().apply {
                time = DATE_FORMAT.parse(endDate) as Date
                add(Calendar.MONTH, -recordState.value.selectedMonth)
            }.time
        }.getOrNull()?.let { DATE_FORMAT.format(it) } ?: return
        setGraphValue(startDate, endDate)
    }

    private fun setGraphValue(startDate: String, endDate: String) {
        viewModelScope.launch {
            recordRepository.getRecordList(startDate, endDate)
                .onSuccess { recordList ->
                    if (recordList.records.size <= 1) {
                        _recordState.update { it.copy(isRecordEmpty = true) }
                    } else {
                        _recordState.update {
                            it.copy(
                                isRecordEmpty = false,
                                averageAccuracy = recordList.accuracyAverage,
                                dateList = recordList.records.toDateList(),
                                entriesList = recordList.records.toEntryList(),
                            )
                        }
                    }
                }
                .onFailure {
                    _recordSideEffect.emit(RecordSideEffect.ErrorToast)
                }
        }
    }

    private fun List<RecordModel>.toDateList(): List<String> =
        map { record ->
            DATE_FORMAT.parse(record.date)?.let { DISPLAY_DATE_FORMAT.format(it) }.orEmpty()
        }

    private fun List<RecordModel>.toEntryList(): MutableList<Entry> =
        mapIndexed { index, record ->
            Entry(index.toFloat(), record.accuracy.toFloat())
        }.toMutableList()

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        private val DISPLAY_DATE_FORMAT = SimpleDateFormat("MM/dd", Locale.KOREA)
    }
}