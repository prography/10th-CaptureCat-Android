package com.prography.network.entity

import kotlinx.serialization.Serializable

/**
 * 단일 응답 객체 형식
 */
@Serializable
data class ApiResponse<T>(
    val result: String,
    val data: T? = null,
    val error: ApiError? = null
)

/**
 * 리스트 응답 객체 형식
 */
@Serializable
data class ApiListResponse<T>(
    val result: String,
    val data: ApiListData<T>? = null,
    val error: ApiError? = null
)

/**
 * 리스트 데이터 형식
 */
@Serializable
data class ApiListData<T>(
    val hasNext: Boolean,
    val lastCursor: Int?,
    val items: List<T>
)

/**
 * 에러 객체 형식
 */
@Serializable
data class ApiError(
    val code: String,
    val message: String
)