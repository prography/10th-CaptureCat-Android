package com.prography.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_photos")
data class PhotoLocalModel(
    @PrimaryKey val id: String,
    val imageUrl: String
)
