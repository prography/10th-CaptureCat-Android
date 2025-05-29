package com.prography.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_photos")
data class BookmarkPhoto(
    @PrimaryKey val id: String,
    val imageUrl: ImageUrls
)
