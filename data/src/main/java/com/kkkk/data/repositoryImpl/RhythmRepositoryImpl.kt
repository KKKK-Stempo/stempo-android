package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.RhythmDataSource
import com.kkkk.data.dto.request.RecordRequestDto.Companion.toDto
import com.kkkk.domain.entity.request.RecordRequestModel
import com.kkkk.domain.repository.RhythmRepository
import javax.inject.Inject

class RhythmRepositoryImpl
@Inject
constructor(
    private val rhythmDataSource: RhythmDataSource,
) : RhythmRepository {

    override suspend fun postToGetRhythmUrl(bpm: Int): Result<String> =
        runCatching {
            rhythmDataSource.postToGetRhythmUrl(bpm).data
        }

    override suspend fun getRhythmWav(url: String): Result<ByteArray> =
        runCatching {
            rhythmDataSource.getRhythmWav(url).bytes()
        }

    override suspend fun postRhythmRecord(request: RecordRequestModel): Result<String> =
        runCatching {
            rhythmDataSource.postRhythmRecord(request.toDto()).data
        }
}