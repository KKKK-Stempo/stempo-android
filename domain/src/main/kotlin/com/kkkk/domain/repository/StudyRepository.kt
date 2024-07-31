package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.StudyModel

interface StudyRepository {
    suspend fun getVideos(
        page: Int,
        size: Int
    ): Result<StudyModel>

    suspend fun getArticles(
        page: Int,
        size: Int
    ): Result<StudyModel>
}
