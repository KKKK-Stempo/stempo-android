package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.HomeworkDto
import com.kkkk.data.dto.response.StudyDto

interface StudyDataSource {
    suspend fun getHomeworks(page: Int, size: Int): BaseResponse<StudyDto>

    suspend fun addHomework(description: String): BaseResponse<Int>

    suspend fun deleteHomework(homeworkId: Int): BaseResponse<Int>

    suspend fun updateHomework(
        homeworkId: Int,
        homeworkDto: HomeworkDto,
    ): BaseResponse<Int>
}
