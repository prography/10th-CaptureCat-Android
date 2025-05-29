package com.android.prography.presentation.module

import com.android.prography.data.api.PhotoService
import com.prography.data.repository.remote.photo.PhotoRemoteDataSourceImpl
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
    fun providePhotoRemoteDataSourceImpl(photoService: PhotoService) = PhotoRemoteDataSourceImpl(photoService)

}