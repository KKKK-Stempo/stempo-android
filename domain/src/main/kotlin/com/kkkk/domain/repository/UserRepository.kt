package com.kkkk.domain.repository

interface UserRepository {
    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun setTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun getDeviceToken(): String

    fun setDeviceToken(deviceToken: String)

    fun clearInfo()
}
