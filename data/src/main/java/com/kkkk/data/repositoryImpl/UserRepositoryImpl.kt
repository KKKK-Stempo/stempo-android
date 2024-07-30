package com.kkkk.data.repositoryImpl

import com.kkkk.data.local.UserSharedPref
import com.kkkk.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl
@Inject
constructor(
    private val userSharedPref: UserSharedPref,
) : UserRepository {
    override fun getAccessToken(): String = userSharedPref.accessToken

    override fun getRefreshToken(): String = userSharedPref.refreshToken

    override fun getBpmLevel(): Int = userSharedPref.bpmLevel

    override fun getDeviceToken(): String = userSharedPref.deviceToken

    override fun setTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        userSharedPref.accessToken = accessToken
        userSharedPref.refreshToken = refreshToken
    }

    override fun setBpmLevel(bpmLevel: Int) {
        userSharedPref.bpmLevel = bpmLevel
    }

    override fun setDeviceToken(deviceToken: String) {
        userSharedPref.deviceToken = deviceToken
    }

    override fun clearInfo() {
        userSharedPref.clearInfo()
    }
}
