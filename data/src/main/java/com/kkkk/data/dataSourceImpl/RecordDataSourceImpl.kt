package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.RecordDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.RecordDto
import com.kkkk.data.dto.response.StatisticsDto
import com.kkkk.data.service.RecordService
import javax.inject.Inject

data class RecordDataSourceImpl
@Inject
constructor(
    private val recordService: RecordService,
) : RecordDataSource {

    override suspend fun getRecordList(
        startDate: String,
        endDate: String,
    ): BaseResponse<List<RecordDto>> = recordService.getRecordList(startDate, endDate)

    override suspend fun getRecordStatistics(): BaseResponse<StatisticsDto> =
        recordService.getRecordStatistics()
}
