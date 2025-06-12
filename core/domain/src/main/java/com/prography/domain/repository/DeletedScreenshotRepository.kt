package com.prography.domain.repository

interface DeletedScreenshotRepository {
    suspend fun getDeletedFileNames(): List<String>
    suspend fun addDeletedScreenshots(fileNames: List<String>)
    suspend fun removeDeletedScreenshots(fileNames: List<String>)
    suspend fun clearAllDeletedScreenshots()
}