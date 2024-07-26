package com.kkkk.stempo.di

import com.kkkk.data.repositoryImpl.AuthRepositoryImpl
import com.kkkk.data.repositoryImpl.RecordRepositoryImpl
import com.kkkk.domain.repository.AuthRepository
import com.kkkk.domain.repository.RecordRepository
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
}
