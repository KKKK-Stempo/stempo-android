package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.AuthDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.AuthRequestDto
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
    ): BaseResponse<AuthTokenDto> = authService.postReissueTokens(authorization)

    override suspend fun postLogin(
        auth: AuthRequestDto,
    ): BaseResponse<AuthTokenDto> = authService.postLogin(auth)

    override suspend fun postSignUp(auth: AuthRequestDto): BaseResponse<AuthTokenDto> =
        authService.postSignUp(auth)

    override suspend fun deleteUnregister(
        authorization: String,
    ): BaseResponse<String> = authService.deleteUnregister(authorization)
}
