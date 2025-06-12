package com.prography.database.di

import android.content.Context
import androidx.room.Room
import com.prography.database.db.AppDatabase
import com.prography.database.dao.BookmarkPhotoDao
import com.prography.database.dao.DeletedScreenshotDao
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
    @Singleton
    fun provideBookmarkPhotoDao(database: AppDatabase): BookmarkPhotoDao {
        return database.bookmarkPhotoDao()
    }

    @Provides
    @Singleton
    fun provideDeletedScreenshotDao(database: AppDatabase): DeletedScreenshotDao {
        return database.deletedScreenshotDao()
    }
}
