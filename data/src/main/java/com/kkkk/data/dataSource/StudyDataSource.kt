package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.VideoDto

interface StudyDataSource {
    suspend fun getVideos(
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: String,
    ): BaseResponse<VideoDto>
}
