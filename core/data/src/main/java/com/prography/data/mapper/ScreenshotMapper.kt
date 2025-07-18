package com.prography.data.mapper

import com.prography.database.model.ScreenshotEntity
import com.prography.domain.model.TagModel
import com.prography.domain.model.UiScreenshotModel

fun ScreenshotEntity.toDomain(): UiScreenshotModel {
    return UiScreenshotModel(
        id = id,
        uri = uri,
        tags = tags.split(",")
            .filter { it.isNotBlank() }
            .mapIndexed { index, tagName ->
                TagModel(
                    id = "${id}_tag_$index", // 로컬에서는 임시 ID 생성
                    name = tagName.trim()
                )
            },
        isFavorite = isFavorite,
        dateStr = dateStr
    )
}

fun UiScreenshotModel.toEntity(): ScreenshotEntity {
    return ScreenshotEntity(
        id = id,
        uri = uri,
        tags = tags.joinToString(",") { it.name },
        isFavorite = isFavorite,
        dateStr = dateStr
    )
}

fun List<ScreenshotEntity>.toDomainList(): List<UiScreenshotModel> {
    return map { it.toDomain() }
}
