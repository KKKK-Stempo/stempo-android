package com.kkkk.stempo.di

import javax.inject.Qualifier

object RetrofitQualifier {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class JWT

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NOTOKEN
}
