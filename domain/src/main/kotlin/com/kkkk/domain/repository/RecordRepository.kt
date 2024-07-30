package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.RecordModel

interface RecordRepository {
    suspend fun getRecordList(
        startDate: String,
        endDate: String
    ): Result<List<RecordModel>>
}
