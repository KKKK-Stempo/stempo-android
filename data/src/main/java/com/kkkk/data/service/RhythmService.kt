package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.RecordRequestDto
import com.kkkk.data.dto.request.RhythmRequestDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface RhythmService {
    @POST("/api/v1/rhythm")
    suspend fun postToGetRhythmUrl(
        @Body request: RhythmRequestDto,
    ): BaseResponse<String>

    @GET
    suspend fun getRhythmWav(
        @Url url: String
    ): ResponseBody

    @POST("api/v1/records")
    suspend fun postRhythmRecord(
        @Body request: RecordRequestDto
    ): BaseResponse<String>
}