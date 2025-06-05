package com.prography.network.di

import com.android.prography.data.api.PhotoService
import com.prography.network.datasource.PhotoRemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun providePhotoRemoteDataSourceImpl(photoService: PhotoService) =
        PhotoRemoteDataSourceImpl(photoService)
}