package com.prography.network.datasource

import com.prography.network.entity.PhotoResponse
import com.prography.network.util.NetworkState

interface PhotoRemoteDataSource {
    suspend fun getRandomPhotos(accessKey: String, countIdx : Int): NetworkState<List<PhotoResponse>>
}