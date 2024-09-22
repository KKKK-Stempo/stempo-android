package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.StudyDataSource
import com.kkkk.data.dto.request.HomeworkDto
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.domain.repository.StudyRepository
import javax.inject.Inject

class StudyRepositoryImpl @Inject constructor(
    private val studyDataSource: StudyDataSource,
) : StudyRepository {
    override suspend fun getHomeworks(page: Int, size: Int): Result<StudyModel> = runCatching {
        studyDataSource.getHomeworks(page, size).data.toModel()
    }

    override suspend fun deleteHomework(homeworkId: Int): Result<Int> = runCatching {
        studyDataSource.deleteHomework(homeworkId).data
    }

    override suspend fun updateHomework(
        homeworkId: Int,
        description: String,
        completed: Boolean,
    ): Result<Int> = runCatching {
        studyDataSource.updateHomework(homeworkId, HomeworkDto(description, completed)).data
    }
}
