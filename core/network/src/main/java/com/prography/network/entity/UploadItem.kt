package com.prography.network.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadItem(
    @SerialName("fileName")
    val fileName: String,
    @SerialName("captureDate")
    val captureDate: String,
    @SerialName("tagNames")
    val tagNames: List<String>
)