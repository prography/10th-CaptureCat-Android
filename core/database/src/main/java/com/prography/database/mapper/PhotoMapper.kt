package com.prography.database.mapper

import com.prography.data.local.entity.PhotoLocalEntity
import com.prography.database.model.PhotoLocalModel

object PhotoMapper {
    fun PhotoLocalModel.toEntity(): PhotoLocalEntity {
        return PhotoLocalEntity(
            id = this.id,
            imageUrl = this.imageUrl
        )
    }

    fun PhotoLocalEntity.toModel(): PhotoLocalModel {
        return PhotoLocalModel(
            id = this.id,
            imageUrl = this.imageUrl
        )
    }
}