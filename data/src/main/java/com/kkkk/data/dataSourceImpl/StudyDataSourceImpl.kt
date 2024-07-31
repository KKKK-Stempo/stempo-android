package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.StudyDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.StudyDto
import com.kkkk.data.service.StudyService
import javax.inject.Inject

data class StudyDataSourceImpl @Inject constructor(
    private val studyService: StudyService,
) : StudyDataSource {
    override suspend fun getVideos(
        page: Int,
        size: Int,
    ): BaseResponse<StudyDto> = studyService.getVideos(
        page = page,
        size = size
    )

    override suspend fun getArticles(page: Int, size: Int): BaseResponse<StudyDto> =
        studyService.getArticles(
            page = page,
            size = size
        )
}
