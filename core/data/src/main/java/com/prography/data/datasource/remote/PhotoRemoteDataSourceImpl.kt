package com.prography.data.datasource.remote

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.prography.data.mapper.toUiScreenshotModels
import com.prography.domain.model.UiScreenshotModel
import com.prography.network.api.PhotoService
import com.prography.network.entity.UploadItem
import com.prography.network.util.getDataOrNull
import com.prography.network.util.isSuccess
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import androidx.core.net.toUri

class PhotoRemoteDataSourceImpl @Inject constructor(
    private val photoService: PhotoService,
    @ApplicationContext private val context: Context
) : PhotoRemoteDataSource {
    override suspend fun getScreenshots(
        page: Int,
        size: Int
    ): Result<List<UiScreenshotModel>> {
        return runCatching {
            val response = photoService.getScreenshots(page, size)
            response.getDataOrNull()?.toUiScreenshotModels() ?: emptyList()
        }
    }

    override suspend fun uploadScreenshots(screenshots: List<UiScreenshotModel>): Result<Unit> {
        return runCatching {
            Timber.d("Starting to upload ${screenshots.size} screenshots")

            // uploadItems JSON 생성
            val uploadItems = screenshots.map { screenshot ->
                val fileName = getFileNameFromUri(screenshot.uri)
                val captureDate = formatDateForUpload(screenshot.dateStr)
                Timber.d("Processing screenshot: uri=${screenshot.uri}, fileName=$fileName, captureDate=$captureDate, tags=${screenshot.tags}")
                UploadItem(
                    fileName = fileName,
                    captureDate = captureDate,
                    tagNames = screenshot.tags
                )
            }

            val uploadItemsJson = Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(UploadItem.serializer()),
                uploadItems
            )
            Timber.d("UploadItems JSON: $uploadItemsJson")

            val uploadItemsRequestBody =
                uploadItemsJson.toRequestBody("application/json".toMediaType())

            // 파일 파트 생성
            val fileParts = mutableListOf<MultipartBody.Part>()

            screenshots.forEach { screenshot ->
                val uri = Uri.parse(screenshot.uri)
                val fileName = getFileNameFromUri(screenshot.uri)

                Timber.d("Creating file part for: $fileName from URI: ${screenshot.uri}")

                try {
                    // URI에서 바이트 배열 읽기
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                        ?: throw Exception("Failed to read bytes from URI: $uri")
                    inputStream?.close()

                    Timber.d("Read ${bytes.size} bytes for file: $fileName")

                    val requestFile = bytes.toRequestBody("image/jpeg".toMediaType())
                    val filePart = MultipartBody.Part.createFormData("files", fileName, requestFile)
                    fileParts.add(filePart)

                    Timber.d("Added file part for: $fileName")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to create file part for: $fileName")
                    throw e
                }
            }

            Timber.d("Created ${fileParts.size} file parts, calling API...")

            // API 호출
            val response = photoService.uploadScreenshots(
                uploadItems = uploadItemsRequestBody,
                files = fileParts
            )

            if (response.isSuccess()) {
                Timber.d("Upload successful: ${response.result}")
            } else {
                val errorMessage = response.error?.message ?: "Unknown error"
                Timber.e("Upload failed: $errorMessage")
                throw Exception("Upload failed: $errorMessage")
            }
        }
    }

    private fun getFileNameFromUri(uriString: String): String {
        val uri = Uri.parse(uriString)

        // ContentResolver로 실제 파일명 조회 시도
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        val fileName = cursor.getString(displayNameIndex)
                        if (!fileName.isNullOrBlank()) {
                            Timber.d("Found filename from ContentResolver: $fileName")
                            return fileName
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to get filename from ContentResolver")
        }

        // URI path에서 파일명 추출 시도
        val path = uri.path
        if (!path.isNullOrBlank()) {
            val fileName = path.substringAfterLast("/")
            if (fileName.isNotBlank()) {
                Timber.d("Found filename from URI path: $fileName")
                return if (fileName.contains(".")) {
                    fileName
                } else {
                    "$fileName.jpg"
                }
            }
        }

        // URI의 마지막 경로 세그먼트 사용
        val lastPathSegment = uri.lastPathSegment
        if (!lastPathSegment.isNullOrBlank()) {
            Timber.d("Found filename from lastPathSegment: $lastPathSegment")
            return if (lastPathSegment.contains(".")) {
                lastPathSegment
            } else {
                "$lastPathSegment.jpg"
            }
        }

        // 모든 방법이 실패한 경우 기본값 사용
        val defaultFileName = "screenshot_${System.currentTimeMillis()}.jpg"
        Timber.d("Using default filename: $defaultFileName")
        return defaultFileName
    }

    private fun formatDateForUpload(dateStr: String): String {
        return try {
            // dateStr이 "2023년 12월 15일" 형태라면 "2023-12-15"로 변환
            if (dateStr.contains("년") && dateStr.contains("월") && dateStr.contains("일")) {
                val parts = dateStr.replace("년", "-").replace("월", "-").replace("일", "").split("-")
                if (parts.size >= 3) {
                    val year = parts[0].trim()
                    val month = parts[1].trim().padStart(2, '0')
                    val day = parts[2].trim().padStart(2, '0')
                    "$year-$month-$day"
                } else {
                    getCurrentDate()
                }
            } else if (dateStr.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                // 이미 올바른 형태
                dateStr
            } else {
                getCurrentDate()
            }
        } catch (e: Exception) {
            Timber.w(e, "Failed to format date: $dateStr")
            getCurrentDate()
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}