package com.kkkk.domain.repository

interface UserRepository {
    fun getAccessToken(): String

    fun getRefreshToken(): String

    fun getDeviceToken(): String

    fun getBpm(): Int

    fun getBit(): Int

    fun setTokens(
        accessToken: String,
        refreshToken: String,
    )

    fun setDeviceToken(deviceToken: String)

    fun setBpm(bpm: Int)

    fun setBit(bit: Int)

    fun clearInfo()
}
