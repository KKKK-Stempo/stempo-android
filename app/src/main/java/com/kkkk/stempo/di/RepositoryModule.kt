package com.kkkk.stempo.di

import com.kkkk.data.repositoryImpl.AuthRepositoryImpl
import com.kkkk.data.repositoryImpl.RecordRepositoryImpl
import com.kkkk.data.repositoryImpl.RhythmRepositoryImpl
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.RecordRepository
import com.kkkk.domain.repository.RhythmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository =
        authRepositoryImpl

    @Provides
    @Singleton
    fun provideRecordRepository(recordRepositoryImpl: RecordRepositoryImpl): RecordRepository =
        recordRepositoryImpl

    @Provides
    @Singleton
    fun provideRhythmRepository(rhythmRepositoryImpl: RhythmRepositoryImpl): RhythmRepository =
        rhythmRepositoryImpl
}
