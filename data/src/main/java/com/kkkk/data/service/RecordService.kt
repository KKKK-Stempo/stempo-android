package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.RecordDto
import com.kkkk.data.dto.response.RecordListDto
import com.kkkk.data.dto.response.StatisticsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RecordService {
    @GET("/api/v1/records")
    suspend fun getRecordList(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): BaseResponse<RecordListDto>

    @GET("/api/v1/records/statistics")
    suspend fun getRecordStatistics(): BaseResponse<StatisticsDto>
}
