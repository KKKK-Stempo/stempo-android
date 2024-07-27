package com.kkkk.domain.repository

import java.io.File

interface RhythmRepository {
    suspend fun postToGetRhythmUrl(
        bpm: Int
    ): Result<String>

    suspend fun getRhythmWav(
        url: String
    ): Result<File>
}
