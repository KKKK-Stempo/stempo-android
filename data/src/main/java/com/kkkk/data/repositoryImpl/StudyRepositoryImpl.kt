package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.StudyDataSource
import com.kkkk.domain.entity.response.VideoModel
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
        sortBy: String,
        sortDirection: String
    ): Result<VideoModel> = runCatching {
        studyDataSource.getVideos(
            page = page,
            size = size,
            sortBy = sortBy,
            sortDirection = sortDirection
        ).data.toModel()
    }
}
