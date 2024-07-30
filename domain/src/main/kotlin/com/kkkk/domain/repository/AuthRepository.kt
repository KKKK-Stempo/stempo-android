package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.AuthTokenModel

interface AuthRepository {
    suspend fun reissueTokens(
        authorization: String
    ): Result<AuthTokenModel>

    suspend fun login(
        deviceTag: String
    ): Result<AuthTokenModel>
}
