package com.prography.network.di

import com.prography.network.api.AuthService
import com.prography.network.api.PhotoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun providePhotoService(retrofit: Retrofit): PhotoService =
        retrofit.create(PhotoService::class.java)

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    @Named("TokenRefreshAuthService")
    fun provideTokenRefreshAuthService(
        @Named("TokenRefreshRetrofit") retrofit: Retrofit
    ): AuthService =
        retrofit.create(AuthService::class.java)
}