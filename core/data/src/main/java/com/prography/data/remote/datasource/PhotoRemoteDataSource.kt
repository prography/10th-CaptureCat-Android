package com.prography.data.remote.datasource

import com.prography.data.remote.entity.PhotoResponse
import com.prography.domain.util.NetworkState

interface PhotoRemoteDataSource {
    suspend fun getRandomPhotos(accessKey: String, countIdx : Int): NetworkState<List<PhotoResponse>>
}