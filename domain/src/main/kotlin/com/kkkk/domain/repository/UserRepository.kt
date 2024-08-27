package com.kkkk.domain.repository

interface UserRepository {
    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun getBpm(): Int

    fun setTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun setBpm(bpm: Int)

    fun getDeviceToken(): String

    fun setDeviceToken(deviceToken: String)

    fun clearInfo()
}
