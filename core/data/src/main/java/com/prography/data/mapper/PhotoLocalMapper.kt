package com.prography.data.mapper

import com.prography.data.local.entity.PhotoLocalEntity
import com.prography.domain.model.BookmarkPhoto

object PhotoLocalMapper {
    fun PhotoLocalEntity.toDomain(): BookmarkPhoto {
        return BookmarkPhoto(
            id = this.id,
            imageUrl = this.imageUrl,
            description = ""
        )
    }

    fun BookmarkPhoto.toEntity(): PhotoLocalEntity {
        return PhotoLocalEntity(
            id = this.id,
            imageUrl = this.imageUrl
        )
    }
}