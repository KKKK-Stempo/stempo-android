package com.kkkk.stempo.di

import com.kkkk.data.service.AuthService
import com.kkkk.data.service.RecordService
import com.kkkk.data.service.RhythmService
import com.kkkk.data.service.StudyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(
        @RetrofitQualifier.NOTOKEN retrofit: Retrofit,
    ): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideRecordService(
        @RetrofitQualifier.JWT retrofit: Retrofit,
    ): RecordService = retrofit.create(RecordService::class.java)

    @Provides
    @Singleton
    fun provideRhythmService(
        @RetrofitQualifier.JWT retrofit: Retrofit,
    ): RhythmService = retrofit.create(RhythmService::class.java)

    @Provides
    @Singleton
    fun provideStudyService(
        @RetrofitQualifier.JWT retrofit: Retrofit,
    ): StudyService = retrofit.create(StudyService::class.java)
}
