package com.kkkk.data.local

interface UserSharedPref {
    var accessToken: String
    var refreshToken: String
    var deviceToken: String
    var bpm: Int
    var bit : Int

    fun clearInfo()
}
