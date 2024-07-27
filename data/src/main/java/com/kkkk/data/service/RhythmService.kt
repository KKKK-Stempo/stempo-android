package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.io.File

interface RhythmService {
    @POST("/api/v1/rhythm/{bpm}")
    suspend fun postToGetRhythmUrl(
        @Path("bpm") bpm: Int,
    ): BaseResponse<String>

    @GET("{url}")
    suspend fun getRhythmWav(
        @Path("url") url: String
    ): BaseResponse<File>
}