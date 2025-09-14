package com.prography.domain.model

data class TagModel(
    val id: Long? = null,
    val name: String
)

data class TagWithCount(
    val id: Int? = 0,
    val tag: String,
    val count: Int
)

data class AutocompleteTagModel(
    val id: Int,
    val name: String
)