package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.TokenRequestDto
import com.kkkk.data.dto.response.AuthTokenDto

interface AuthDataSource {
    suspend fun postReissueTokens(
        authorization: String,
        request: TokenRequestDto,
    ): BaseResponse<AuthTokenDto>
}
