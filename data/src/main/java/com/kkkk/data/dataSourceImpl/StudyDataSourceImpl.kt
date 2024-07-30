package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.StudyDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.VideoDto
import com.kkkk.data.service.StudyService
import javax.inject.Inject

data class StudyDataSourceImpl @Inject constructor(
    private val studyService: StudyService,
) : StudyDataSource {
    override suspend fun getVideos(): BaseResponse<VideoDto> = studyService.getVideos()
}
