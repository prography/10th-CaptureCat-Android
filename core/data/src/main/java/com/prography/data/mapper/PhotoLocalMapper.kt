package com.prography.data.mapper

import com.prography.database.model.PhotoLocalModel
import com.prography.domain.model.UiPhotoHomeModel

object PhotoLocalMapper {
    fun PhotoLocalModel.toDomain(): UiPhotoHomeModel {
        return UiPhotoHomeModel(
            id = this.id,
            imageUrl = this.imageUrl
        )
    }

    fun UiPhotoHomeModel.toEntity(): PhotoLocalModel {
        return PhotoLocalModel(
            id = this.id,
            imageUrl = this.imageUrl
        )
    }
}