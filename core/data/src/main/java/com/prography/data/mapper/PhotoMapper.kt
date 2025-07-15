package com.prography.data.mapper

import com.prography.domain.model.UiScreenshotModel
import com.prography.network.entity.PhotoResponse

fun PhotoResponse.toUiScreenshotModel(): UiScreenshotModel {
    return UiScreenshotModel(
        id = id.toString(),
        uri = url,
        appName = name,
        tags = tags.map { it.name },
        isFavorite = false,
        dateStr = captureDate
    )
}

fun List<PhotoResponse>.toUiScreenshotModels(): List<UiScreenshotModel> {
    return map { it.toUiScreenshotModel() }
}