package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.AuthRequestDto
import com.kkkk.data.dto.request.TokenRequestDto
import com.kkkk.data.dto.response.AuthTokenDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/auth/reissue")
    suspend fun postReissueTokens(
        @Header("Authorization") authorization: String
    ): BaseResponse<AuthTokenDto>

    @POST("api/v1/auth/login")
    suspend fun postLogin(
        @Body auth: AuthRequestDto,
    ): BaseResponse<AuthTokenDto>

    @POST("api/v1/auth/register")
    suspend fun postSignUp(
        @Body auth: AuthRequestDto,
    ): BaseResponse<AuthTokenDto>
}
