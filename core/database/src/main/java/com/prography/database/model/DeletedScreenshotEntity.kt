package com.prography.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deleted_screenshots")
data class DeletedScreenshotEntity(
    @PrimaryKey
    val fileName: String,
    val deletedAt: Long = System.currentTimeMillis()
)