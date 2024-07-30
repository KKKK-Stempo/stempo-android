package com.kkkk.data.service

import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.TokenRequestDto
import com.kkkk.data.dto.response.AuthTokenDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    // TODO: 서버 나오면 수정
    @POST("api/users/reissue")
    suspend fun postReissueTokens(
        @Header("Authorization") authorization: String
    ): BaseResponse<AuthTokenDto>

    @POST("api/v1/login")
    suspend fun postLogin(
        @Header("") deviceTag: String,
    ): BaseResponse<AuthTokenDto>
}
