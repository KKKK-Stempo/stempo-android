package com.kkkk.domain.repository

import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.entity.request.RhythmRequestModel

interface RhythmRepository {
    suspend fun postToGetRhythmUrl(
        request: RhythmRequestModel
    ): Result<String>

    suspend fun getRhythmWav(
        url: String
    ): Result<ByteArray>

    suspend fun postRhythmRecord(
        request: RecordRequestModel
    ): Result<String>
}
