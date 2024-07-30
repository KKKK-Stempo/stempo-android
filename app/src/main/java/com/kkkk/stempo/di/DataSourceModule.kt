package com.kkkk.stempo.di

import com.kkkk.data.dataSource.AuthDataSource
import com.kkkk.data.dataSource.RecordDataSource
import com.kkkk.data.dataSource.RhythmDataSource
import com.kkkk.data.dataSource.StudyDataSource
import com.kkkk.data.dataSourceImpl.AuthDataSourceImpl
import com.kkkk.data.dataSourceImpl.RecordDataSourceImpl
import com.kkkk.data.dataSourceImpl.RhythmDataSourceImpl
import com.kkkk.data.dataSourceImpl.StudyDataSourceImpl
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

    @Provides
    @Singleton
    fun provideRhythmDataSource(rhythmDataSourceImpl: RhythmDataSourceImpl): RhythmDataSource =
        rhythmDataSourceImpl

    @Provides
    @Singleton
    fun provideStudyDataSource(studyDataSourceImpl: StudyDataSourceImpl): StudyDataSource =
        studyDataSourceImpl
}
