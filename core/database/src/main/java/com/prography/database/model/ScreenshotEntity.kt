package com.prography.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshots")
data class ScreenshotEntity(
    @PrimaryKey val id: String,
    val uri: String,
    val appName: String,
    val tags: String, // "tag1,tag2"
    val isFavorite: Boolean
)
