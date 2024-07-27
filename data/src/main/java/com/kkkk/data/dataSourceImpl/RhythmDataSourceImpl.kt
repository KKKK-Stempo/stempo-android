package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.RhythmDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.service.RhythmService
import javax.inject.Inject

data class RhythmDataSourceImpl
@Inject
constructor(
    private val rhythmService: RhythmService
) : RhythmDataSource {

    override suspend fun postToGetRhythm(bpm: Int): BaseResponse<String> =
        rhythmService.postToGetRhythm(bpm)
}
