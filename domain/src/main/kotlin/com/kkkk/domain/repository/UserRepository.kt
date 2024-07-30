package com.kkkk.domain.repository

interface UserRepository {
    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun getBpmLevel(): Int

    fun setTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun setBpmLevel(bpmLevel: Int)

    fun getDeviceToken(): String

    fun setDeviceToken(deviceToken: String)

    fun clearInfo()
}
