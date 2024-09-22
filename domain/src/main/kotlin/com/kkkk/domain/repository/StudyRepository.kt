package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.StudyModel

interface StudyRepository {
    suspend fun getHomeworks(
        page: Int,
        size: Int
    ): Result<StudyModel>

    suspend fun deleteHomework(
        homeworkId: Int
    ): Result<Int>
}
