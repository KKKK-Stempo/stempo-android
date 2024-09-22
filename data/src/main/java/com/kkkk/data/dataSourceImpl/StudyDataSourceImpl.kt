package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.StudyDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.StudyDto
import com.kkkk.data.service.StudyService
import javax.inject.Inject

data class StudyDataSourceImpl @Inject constructor(
    private val studyService: StudyService,
) : StudyDataSource {
    override suspend fun getHomeworks(page: Int, size: Int): BaseResponse<StudyDto> =
        studyService.getHomeworks(page, size)

    override suspend fun deleteHomework(homeworkId: Int): BaseResponse<Int> =
        studyService.deleteHomework(homeworkId)
}
