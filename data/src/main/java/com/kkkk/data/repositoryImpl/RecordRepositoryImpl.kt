package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.RecordDataSource
import com.kkkk.domain.entity.response.RecordList
import com.kkkk.domain.entity.response.StatisticsModel
import com.kkkk.domain.repository.RecordRepository
import javax.inject.Inject

class RecordRepositoryImpl
@Inject
constructor(
    private val recordDataSource: RecordDataSource,
) : RecordRepository {

    override suspend fun getRecordList(
        startDate: String,
        endDate: String,
    ): Result<RecordList> =
        runCatching {
            recordDataSource.getRecordList(startDate, endDate).data.toModel()
        }

    override suspend fun getRecordStatistics(): Result<StatisticsModel> =
        runCatching {
            recordDataSource.getRecordStatistics().data.toModel()
        }
}
