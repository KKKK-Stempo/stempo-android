package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.StudyDataSource
import com.kkkk.domain.entity.response.StudyModel
import com.kkkk.domain.repository.StudyRepository
import javax.inject.Inject

class StudyRepositoryImpl
@Inject
constructor(
    private val studyDataSource: StudyDataSource,
) : StudyRepository {
    override suspend fun getVideos(
        page: Int,
        size: Int,
    ): Result<StudyModel> = runCatching {
        studyDataSource.getVideos(
            page = page,
            size = size
        ).data.toModel()
    }

    override suspend fun getArticles(page: Int, size: Int): Result<StudyModel> = runCatching {
        studyDataSource.getArticles(
            page = page,
            size = size
        ).data.toModel()
    }
}
