package com.prography.data.datasource.remote

import com.prography.data.mapper.toUiScreenshotModels
import com.prography.domain.model.UiScreenshotModel
import com.prography.network.api.PhotoService
import com.prography.network.util.getDataOrNull
import com.prography.network.util.isSuccess
import javax.inject.Inject

class PhotoRemoteDataSourceImpl @Inject constructor(
    private val photoService: PhotoService
) : PhotoRemoteDataSource {
    override suspend fun getScreenshots(
        page: Int,
        size: Int
    ): Result<List<UiScreenshotModel>> {
        return runCatching {
            val response = photoService.getScreenshots(page, size)
            response.getDataOrNull()?.toUiScreenshotModels() ?: emptyList()
        }
    }
}