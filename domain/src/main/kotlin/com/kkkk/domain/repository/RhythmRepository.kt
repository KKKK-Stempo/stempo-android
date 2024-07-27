package com.kkkk.domain.repository

interface RhythmRepository {
    suspend fun postToGetRhythm(
        bpm: Int
    ): Result<String>
}
