package com.prography.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prography.data.remote.entity.ImageUrls

@Entity(tableName = "bookmark_photos")
data class BookmarkPhoto(
    @PrimaryKey val id: String,
    val imageUrl: ImageUrls
)
