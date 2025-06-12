package com.prography.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.prography.database.dao.BookmarkPhotoDao
import com.prography.database.dao.DeletedScreenshotDao
import com.prography.database.model.DeletedScreenshotEntity
import com.prography.database.model.PhotoLocalModel
import com.prography.database.util.Converters

@Database(
    entities = [
        PhotoLocalModel::class,
        DeletedScreenshotEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkPhotoDao(): BookmarkPhotoDao
    abstract fun deletedScreenshotDao(): DeletedScreenshotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // 개발 중이므로 간단히 처리
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
