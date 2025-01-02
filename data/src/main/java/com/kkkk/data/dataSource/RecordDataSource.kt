package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.RecordDto
import com.kkkk.data.dto.response.RecordListDto
import com.kkkk.data.dto.response.StatisticsDto
import com.kkkk.domain.entity.response.RecordList

interface RecordDataSource {
    suspend fun getRecordList(
        startDate: String,
        endDate: String
    ): BaseResponse<RecordListDto>

    suspend fun getRecordStatistics(): BaseResponse<StatisticsDto>
}