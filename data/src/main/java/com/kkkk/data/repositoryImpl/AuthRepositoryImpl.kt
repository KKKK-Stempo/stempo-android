package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.AuthDataSource
import com.kkkk.data.dto.request.TokenRequestDto.Companion.toDto
import com.kkkk.domain.entity.request.TokenRequestModel
import com.kkkk.domain.entity.response.AuthTokenModel
import com.kkkk.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
@Inject
constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun postReissueTokens(
        authorization: String,
        request: TokenRequestModel,
    ): Result<AuthTokenModel> =
        runCatching {
            authDataSource.postReissueTokens(
                authorization,
                request.toDto(),
            ).data.toModel()
        }
}
