package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.RecordDto

interface RecordDataSource {
    suspend fun getRecordList(
        startDate: String,
        endDate: String
    ): BaseResponse<List<RecordDto>>
}