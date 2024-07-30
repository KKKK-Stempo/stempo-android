package com.kkkk.domain.repository

import com.kkkk.domain.entity.response.AuthTokenModel

interface AuthRepository {
    suspend fun postReissueTokens(
        authorization: String
    ): Result<AuthTokenModel>
}
