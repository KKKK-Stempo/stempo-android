package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.RecordRequestDto
import com.kkkk.data.dto.request.RhythmRequestDto
import okhttp3.ResponseBody

interface RhythmDataSource {
    suspend fun postToGetRhythmUrl(
        request: RhythmRequestDto
    ): BaseResponse<String>

    suspend fun getRhythmWav(
        url: String
    ): ResponseBody

    suspend fun postRhythmRecord(
        request: RecordRequestDto
    ): BaseResponse<String>
}