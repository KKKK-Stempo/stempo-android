package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.VideoModel

interface StudyRepository {
    suspend fun getVideos(
        page: Int,
        size: Int,
        sortBy: String,
        sortDirection: String
    ): Result<VideoModel>
}
