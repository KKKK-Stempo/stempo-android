package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.StudyModel

interface StudyRepository {
    suspend fun getHomeworks(
        page: Int,
        size: Int,
    ): Result<StudyModel>

    suspend fun addHomework(
        description: String,
    ): Result<Int>

    suspend fun deleteHomework(
        homeworkId: Int,
    ): Result<Int>

    suspend fun updateHomework(
        homeworkId: Int,
        description: String,
        completed: Boolean,
    ): Result<Int>
}
