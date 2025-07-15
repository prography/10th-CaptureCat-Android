package com.prography.network.util

import com.prography.network.entity.ApiListResponse
import com.prography.network.entity.ApiResponse

/**
 * API 응답이 성공인지 확인
 */
fun <T> ApiResponse<T>.isSuccess(): Boolean = result == "SUCCESS"

/**
 * API 응답이 에러인지 확인
 */
fun <T> ApiResponse<T>.isError(): Boolean = result == "ERROR"

/**
 * 성공 데이터 가져오기 (null 안전)
 */
fun <T> ApiResponse<T>.getDataOrNull(): T? = if (isSuccess()) data else null

/**
 * 에러 메시지 가져오기
 */
fun <T> ApiResponse<T>.getErrorMessage(): String? = error?.message

/**
 * 에러 코드 가져오기
 */
fun <T> ApiResponse<T>.getErrorCode(): String? = error?.code

/**
 * 리스트 API 응답이 성공인지 확인
 */
fun <T> ApiListResponse<T>.isSuccess(): Boolean = result == "SUCCESS"

/**
 * 리스트 API 응답이 에러인지 확인
 */
fun <T> ApiListResponse<T>.isError(): Boolean = result == "ERROR"

/**
 * 리스트 성공 데이터 가져오기 (null 안전)
 */
fun <T> ApiListResponse<T>.getDataOrNull(): List<T>? = if (isSuccess()) data?.items else null

/**
 * 다음 페이지 존재 여부 확인
 */
fun <T> ApiListResponse<T>.hasNext(): Boolean = data?.hasNext ?: false

/**
 * 마지막 커서 값 가져오기
 */
fun <T> ApiListResponse<T>.getLastCursor(): Int? = data?.lastCursor

/**
 * 리스트 에러 메시지 가져오기
 */
fun <T> ApiListResponse<T>.getErrorMessage(): String? = error?.message

/**
 * 리스트 에러 코드 가져오기
 */
fun <T> ApiListResponse<T>.getErrorCode(): String? = error?.code