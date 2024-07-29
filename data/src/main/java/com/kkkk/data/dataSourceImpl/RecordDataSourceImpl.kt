package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.AuthDataSource
import com.kkkk.data.dataSource.RecordDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.TokenRequestDto
import com.kkkk.data.dto.response.AuthTokenDto
import com.kkkk.data.dto.response.RecordDto
import com.kkkk.data.service.AuthService
import com.kkkk.data.service.RecordService
import javax.inject.Inject

data class RecordDataSourceImpl
@Inject
constructor(
    private val recordService: RecordService,
) : RecordDataSource {

    override suspend fun getRecordList(
        startDate: String,
        endDate: String
    ): BaseResponse<List<RecordDto>> = recordService.getRecordList(startDate, endDate)
}
