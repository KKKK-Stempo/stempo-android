package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import retrofit2.http.POST
import retrofit2.http.Path

interface RhythmService {
    @POST("/api/v1/rhythm/{bpm}")
    suspend fun postToGetRhythm(
        @Path("bpm") bpm: Int,
    ): BaseResponse<String>
}