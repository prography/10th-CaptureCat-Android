package com.prography.domain.model

data class TagModel(
    val id: String,
    val name: String
)

data class TagWithCount(
    val tag: String,
    val count: Int
)