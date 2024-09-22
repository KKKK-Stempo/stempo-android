package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.RhythmDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.dto.request.RecordRequestDto
import com.kkkk.data.dto.request.RhythmRequestDto
import com.kkkk.data.service.RhythmService
import okhttp3.ResponseBody
import javax.inject.Inject

data class RhythmDataSourceImpl
@Inject
constructor(
    private val rhythmService: RhythmService
) : RhythmDataSource {

    override suspend fun postToGetRhythmUrl(request: RhythmRequestDto): BaseResponse<String> =
        rhythmService.postToGetRhythmUrl(request)

    override suspend fun getRhythmWav(url: String): ResponseBody =
        rhythmService.getRhythmWav(url)

    override suspend fun postRhythmRecord(request: RecordRequestDto): BaseResponse<String> =
        rhythmService.postRhythmRecord(request)
}
