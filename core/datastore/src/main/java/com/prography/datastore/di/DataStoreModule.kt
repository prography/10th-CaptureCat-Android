package com.prography.datastore.di

import android.content.Context
import com.prography.datastore.user.UserPreferenceDataStore
import com.prography.datastore.tag.TagDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserPreferenceDataStore(
        @ApplicationContext context: Context
    ): UserPreferenceDataStore = UserPreferenceDataStore(context)

    @Provides
    @Singleton
    fun provideTagDataStore(
        @ApplicationContext context: Context
    ): TagDataStore = TagDataStore(context)
}
