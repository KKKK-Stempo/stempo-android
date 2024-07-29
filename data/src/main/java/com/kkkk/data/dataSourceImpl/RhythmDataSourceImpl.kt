package com.kkkk.data.dataSourceImpl

import com.kkkk.data.dataSource.RhythmDataSource
import com.kkkk.data.dto.BaseResponse
import com.kkkk.data.service.RhythmService
import okhttp3.ResponseBody
import java.io.File
import javax.inject.Inject

data class RhythmDataSourceImpl
@Inject
constructor(
    private val rhythmService: RhythmService
) : RhythmDataSource {

    override suspend fun postToGetRhythmUrl(bpm: Int): BaseResponse<String> =
        rhythmService.postToGetRhythmUrl(bpm)

    override suspend fun getRhythmWav(url: String): BaseResponse<ResponseBody> =
        rhythmService.getRhythmWav(url)
}
