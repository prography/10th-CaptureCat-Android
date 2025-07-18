package com.prography.organize.model

import android.net.Uri
import com.prography.domain.model.TagModel

data class OrganizeScreenshotItem(
    val id: String,
    val uri: Uri,
    val fileName: String?,
    val isFavorite: Boolean = false,
    val tags: List<TagModel> = emptyList()
)
