package com.prography.data.di

import android.content.Context
import com.prography.data.util.SharedPreferenceUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideSharedPreferenceUtil(@ApplicationContext context: Context): SharedPreferenceUtil =
        SharedPreferenceUtil(context)
}