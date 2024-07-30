package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import okhttp3.ResponseBody
import java.io.File

interface RhythmDataSource {
    suspend fun postToGetRhythmUrl(
        bpm: Int
    ): BaseResponse<String>

    suspend fun getRhythmWav(
        url: String
    ): ResponseBody
}