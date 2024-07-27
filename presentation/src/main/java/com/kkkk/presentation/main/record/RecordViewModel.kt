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

    fun setIsChangingMonth() {
        isChangingMonth.value = isChangingMonth.value?.not() ?: false
    }

    fun setReportMonth(month: Int) {
        reportMonth.value = month
        isChangingMonth.value = false
    }

    fun setGraphValue() {
        // TODO
        val startDate = "2024-04-27"
        val endDate = "2024-07-27"
        _chartEntry.value = UiState.Loading
        viewModelScope.launch {
            recordRepository.getRecordList(startDate, endDate)
                .onSuccess {
                    if (it.isEmpty()) {
                        _chartEntry.value = UiState.Empty
                        return@launch
                    }
                    dateList = it.map { record -> record.date }
                    _chartEntry.value = UiState.Success(
                        it.mapIndexed { index, record ->
                            Entry(index.toFloat(), record.accuracy.toFloat())
                        }.toMutableList()
                    )
                }
                .onFailure {
                    _chartEntry.value = UiState.Failure(it.message.toString())
                }
        }
    }
}