package com.android.prography.data.api
import com.prography.network.entity.PhotoResponse
import com.prography.domain.util.NetworkState
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotoService {
    @GET("photos/random/")
    suspend fun getRandomPhotos(
        @Query("client_id") clientId: String,
        @Query("count") countIdx : Int
    ): NetworkState<List<PhotoResponse>>
}
