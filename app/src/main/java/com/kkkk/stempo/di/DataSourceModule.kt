package com.kkkk.stempo.di

import com.kkkk.data.dataSource.AuthDataSource
import com.kkkk.data.dataSource.RecordDataSource
import com.kkkk.data.dataSourceImpl.AuthDataSourceImpl
import com.kkkk.data.dataSourceImpl.RecordDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideAuthDataSource(authDataSourceImpl: AuthDataSourceImpl): AuthDataSource =
        authDataSourceImpl

    @Provides
    @Singleton
    fun provideRecordDataSource(recordDataSourceImpl: RecordDataSourceImpl): RecordDataSource =
        recordDataSourceImpl
}
