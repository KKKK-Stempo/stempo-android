package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.RecordModel
import com.kkkk.domain.entity.response.StatisticsModel

interface RecordRepository {
    suspend fun getRecordList(
        startDate: String,
        endDate: String
    ): Result<List<RecordModel>>

    suspend fun getRecordStatistics(): Result<StatisticsModel>
}
