package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.TokenRequestDto
import com.kkkk.data.dto.response.AuthTokenDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/reissue")
    suspend fun postReissueTokens(
        @Header("Authorization") authorization: String
    ): BaseResponse<AuthTokenDto>

    @POST("api/v1/login")
    suspend fun postLogin(
        @Header("Authorization") deviceTag: String,
    ): BaseResponse<AuthTokenDto>
}
