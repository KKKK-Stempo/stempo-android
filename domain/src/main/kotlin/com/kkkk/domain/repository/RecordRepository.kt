package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.RecordList
import com.kkkk.domain.entity.response.StatisticsModel

interface RecordRepository {
    suspend fun getRecordList(
        startDate: String,
        endDate: String,
    ): Result<RecordList>

    suspend fun getRecordStatistics(): Result<StatisticsModel>
}
