package com.prography.data.remote.datasource

import com.android.prography.data.api.PhotoService
import com.prography.data.remote.entity.PhotoResponse
import com.prography.domain.util.NetworkState
import javax.inject.Inject

class PhotoRemoteDataSourceImpl @Inject constructor(
    private val photoService: PhotoService
) : PhotoRemoteDataSource {
    override suspend fun getRandomPhotos(
        accessKey : String,
        count : Int
    ): NetworkState<List<PhotoResponse>> {
        return photoService.getRandomPhotos(accessKey, count)
    }
}