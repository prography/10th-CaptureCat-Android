package com.android.prography.data.repository.remote.photo

import com.prography.data.entity.PhotoResponse
import com.prography.domain.util.NetworkState

interface PhotoRemoteDataSource {
    suspend fun getRandomPhotos(accessKey: String, countIdx : Int): NetworkState<List<PhotoResponse>>
}