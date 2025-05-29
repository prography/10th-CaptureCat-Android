package com.prography.data.repository.remote.photo

import com.android.prography.data.api.PhotoService
import com.android.prography.data.repository.remote.photo.PhotoRemoteDataSource
import com.prography.data.entity.PhotoResponse
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