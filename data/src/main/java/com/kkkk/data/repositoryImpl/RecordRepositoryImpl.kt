package com.kkkk.data.repositoryImpl

import com.kkkk.data.dataSource.RecordDataSource
import com.kkkk.domain.entity.response.RecordModel
import com.kkkk.domain.repository.RecordRepository
import javax.inject.Inject

class RecordRepositoryImpl
@Inject
constructor(
    private val recordDataSource: RecordDataSource,
) : RecordRepository {

    override suspend fun getRecordList(
        startDate: String,
        endDate: String
    ): Result<List<RecordModel>> =
        runCatching {
            recordDataSource.getRecordList(startDate, endDate).data.map { it.toModel() }
        }
}
