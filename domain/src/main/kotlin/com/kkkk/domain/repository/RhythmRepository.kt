package com.kkkk.domain.repository

import com.kkkk.domain.entity.request.RecordRequestModel

interface RhythmRepository {
    suspend fun postToGetRhythmUrl(
        bpm: Int
    ): Result<String>

    suspend fun getRhythmWav(
        url: String
    ): Result<ByteArray>

    suspend fun postRhythmRecord(
        request: RecordRequestModel
    ): Result<String>
}
