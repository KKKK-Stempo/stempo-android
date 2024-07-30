package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.response.AuthTokenDto

interface AuthDataSource {
    suspend fun postReissueTokens(
        authorization: String,
    ): BaseResponse<AuthTokenDto>

    suspend fun postLogin(
        deviceTag: String,
    ): BaseResponse<AuthTokenDto>
}
