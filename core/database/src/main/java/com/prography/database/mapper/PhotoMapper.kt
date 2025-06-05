package com.prography.database.mapper

import com.prography.database.model.PhotoLocalModel
import com.prography.domain.model.UiPhotoHomeModel

object PhotoMapper {
    fun PhotoLocalModel.toEntity(): UiPhotoHomeModel {
        return UiPhotoHomeModel(
            id = this.id,
            imageUrl = this.imageUrl
        )
    }

    fun UiPhotoHomeModel.toModel(): PhotoLocalModel {
        return PhotoLocalModel(
            id = this.id,
            imageUrl = this.imageUrl
        )
    }
}