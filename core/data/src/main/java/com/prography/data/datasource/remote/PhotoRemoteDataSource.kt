package com.prography.data.datasource.remote

import com.prography.domain.model.UiScreenshotModel

interface PhotoRemoteDataSource {
    suspend fun getScreenshots(page: Int = 0, size: Int = 20): Result<List<UiScreenshotModel>>
}