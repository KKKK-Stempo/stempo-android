package com.kkkk.data.local

interface UserSharedPref {
    var accessToken: String
    var refreshToken: String
    var deviceToken: String
    var userId: Long

    fun clearInfo()
}
