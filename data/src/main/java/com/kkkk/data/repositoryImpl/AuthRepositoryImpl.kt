package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.AuthDataSource
import com.kkkk.domain.entity.response.AuthTokenModel
import com.kkkk.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl
@Inject
constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun reissueTokens(
        authorization: String
    ): Result<AuthTokenModel> = runCatching {
        authDataSource.postReissueTokens(
            authorization,
        ).data.toModel()
    }

    override suspend fun login(
        deviceTag: String
    ): Result<AuthTokenModel> = runCatching {
        authDataSource.postLogin(deviceTag).data.toModel()
    }
}
