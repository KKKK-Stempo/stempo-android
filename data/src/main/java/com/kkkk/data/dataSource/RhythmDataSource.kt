package com.kkkk.data.dataSource

import com.kkkk.data.dto.BaseResponse

interface RhythmDataSource {
    suspend fun postToGetRhythm(
        bpm: Int
    ): BaseResponse<String>
}