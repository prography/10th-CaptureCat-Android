package com.prography.network.di

import com.android.prography.data.api.PhotoService
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
    fun providePhotoService(retrofit: Retrofit): PhotoService = retrofit.create(PhotoService::class.java)
}