package com.prography.domain.model

data class UiScreenshotModel(
    val id: String,
    val uri: String,
    val appName: String,
    val tags: List<String>,
    val isFavorite: Boolean
)