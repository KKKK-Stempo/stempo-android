package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.StudyDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyService {
    @GET("api/v1/homeworks")
    suspend fun getHomeworks(
        @Query("page")
        page: Int,
        @Query("size")
        size: Int,
    ): BaseResponse<StudyDto>

    @DELETE("api/v1/homeworks/{homeworkId}")
    suspend fun deleteHomework(
        @Path("homeworkId") homeworkId: Int
    ): BaseResponse<Int>
}
