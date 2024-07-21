package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.AuthDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.TokenRequestDto
import com.kkkk.data.dto.response.AuthTokenDto
import com.kkkk.data.service.AuthService
import javax.inject.Inject

data class AuthDataSourceImpl
@Inject
constructor(
    private val authService: AuthService,
) : AuthDataSource {
    override suspend fun postReissueTokens(
        authorization: String,
        request: TokenRequestDto,
    ): BaseResponse<AuthTokenDto> = authService.postReissueTokens(authorization, request)
}
