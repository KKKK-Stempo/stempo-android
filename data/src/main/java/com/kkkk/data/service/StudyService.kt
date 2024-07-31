package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.VideoDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StudyService {
    @GET("api/v1/videos")
    suspend fun getVideos(
        @Query("page")
        page: Int,
        @Query("size")
        size: Int,
        @Query("sortBy")
        sortBy: String,
        @Query("sortDirection")
        sortDirection: String,
    ): BaseResponse<VideoDto>
}
