package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.VideoDto
import retrofit2.http.POST

interface StudyService {
    @POST("api/v1/videos")
    suspend fun getVideos(): BaseResponse<VideoDto>
}
