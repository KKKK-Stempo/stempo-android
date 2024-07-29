package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface RhythmService {
    @POST("/api/v1/rhythm/{bpm}")
    suspend fun postToGetRhythmUrl(
        @Path("bpm") bpm: Int,
    ): BaseResponse<String>

    @GET
    suspend fun getRhythmWav(
        @Url url: String
    ): ResponseBody
}