package com.prography.data.repository.remote.photo

import com.prography.data.mapper.toUiPhotoResponseModel
import com.prography.data.remote.datasource.PhotoRemoteDataSourceImpl
import com.prography.domain.model.UiPhotoModel
import com.prography.domain.repository.PhotoRepository
import com.prography.domain.util.NetworkState
import timber.log.Timber
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(private val photoRemoteDataSourceImpl: PhotoRemoteDataSourceImpl) :
    PhotoRepository {
    override suspend fun getRandomPhotos(accessKey: String, countIdx : Int): Result<UiPhotoModel> {
        when (val data = photoRemoteDataSourceImpl.getRandomPhotos(accessKey, countIdx)) {
            is NetworkState.Success -> return Result.success(data.body.toUiPhotoResponseModel())
            is NetworkState.Failure -> return Result.failure(
                com.prography.util.RetrofitFailureStateException(data.error, data.code)
            )

            is NetworkState.NetworkError -> return Result.failure(IllegalStateException("NetworkError"))
            is NetworkState.UnknownError -> {
                Timber.e(data.t?.message)
                return Result.failure(IllegalStateException("unKnownError"))
            }
        }
    }
}
