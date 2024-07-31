package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.StudyDto

interface StudyDataSource {
    suspend fun getVideos(
        page: Int,
        size: Int
    ): BaseResponse<StudyDto>
}
