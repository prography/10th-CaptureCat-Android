package com.prography.data.mapper

import com.prography.database.model.ScreenshotEntity
import com.prography.domain.model.UiScreenshotModel

fun ScreenshotEntity.toDomain(): UiScreenshotModel = UiScreenshotModel(
    id = id,
    uri = uri,
    appName = appName,
    tags = tags.split(",").filter { it.isNotBlank() },
    isFavorite = isFavorite,
    dateStr = dateStr
)

fun UiScreenshotModel.toEntity(): ScreenshotEntity = ScreenshotEntity(
    id = id,
    uri = uri,
    appName = appName,
    tags = tags.joinToString(","),
    isFavorite = isFavorite,
    dateStr = dateStr
)
