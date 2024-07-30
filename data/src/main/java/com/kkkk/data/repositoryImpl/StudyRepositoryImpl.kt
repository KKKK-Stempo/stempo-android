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
    override suspend fun getVideos(): Result<VideoModel> = runCatching {
        studyDataSource.getVideos().data.toModel()
    }
}
