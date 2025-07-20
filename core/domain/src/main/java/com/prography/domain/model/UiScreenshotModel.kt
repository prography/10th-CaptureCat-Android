package com.prography.domain.model

data class UiScreenshotModel(
    val id: String,
    val uri: String,
    val tags: List<TagModel>,
    val isBookmarked: Boolean,
    val dateStr: String
)