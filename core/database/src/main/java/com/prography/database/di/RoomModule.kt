package com.prography.database.di

import android.content.Context
import androidx.room.Room
import com.prography.database.db.AppDatabase
import com.prography.database.dao.ScreenshotDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideScreenshotDao(database: AppDatabase): ScreenshotDao {
        return database.screenshotDao()
    }
}
