package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.AuthRequestDto
import com.kkkk.data.dto.response.AuthTokenDto

interface AuthDataSource {
    suspend fun postReissueTokens(
        authorization: String,
    ): BaseResponse<AuthTokenDto>

    suspend fun postLogin(
        auth: AuthRequestDto,
    ): BaseResponse<AuthTokenDto>

    suspend fun postSignUp(
        auth: AuthRequestDto,
    ): BaseResponse<AuthTokenDto>
}
