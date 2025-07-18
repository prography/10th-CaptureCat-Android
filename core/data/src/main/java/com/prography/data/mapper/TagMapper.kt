package com.prography.data.mapper

import com.prography.domain.model.TagModel
import com.prography.network.entity.TagResponse

fun TagResponse.toTagModel(): TagModel {
    return TagModel(
        id = this.id.toString(),
        name = this.name
    )
}

fun List<TagResponse>.toTagModels(): List<TagModel> {
    return this.map { it.toTagModel() }
}

fun TagModel.toTagResponse(): TagResponse {
    return TagResponse(
        id = this.id.toIntOrNull() ?: 0,
        name = this.name
    )
}

fun List<TagModel>.toTagResponses(): List<TagResponse> {
    return this.map { it.toTagResponse() }
}