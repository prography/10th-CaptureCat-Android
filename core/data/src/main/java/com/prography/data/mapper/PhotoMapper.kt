package com.prography.data.mapper

import com.prography.domain.model.UiScreenshotModel
import com.prography.network.entity.PhotoResponse

fun PhotoResponse.toUiScreenshotModel(): UiScreenshotModel {
    return UiScreenshotModel(
        id = id.toString(),
        uri = url,
        tags = tags.toTagModels(),
        isFavorite = false,
        dateStr = captureDate
    )
}

fun List<PhotoResponse>.toUiScreenshotModels(): List<UiScreenshotModel> {
    return map { it.toUiScreenshotModel() }
}
