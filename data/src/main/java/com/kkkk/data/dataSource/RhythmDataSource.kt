package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse
import java.io.File

interface RhythmDataSource {
    suspend fun postToGetRhythmUrl(
        bpm: Int
    ): BaseResponse<String>

    suspend fun getRhythmWav(
        url: String
    ): BaseResponse<File>
}