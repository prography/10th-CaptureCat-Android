package com.prography.data.datasource.remote

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.prography.data.mapper.toUiScreenshotModel
import com.prography.data.mapper.toUiScreenshotModels
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.model.TagWithCount
import com.prography.network.api.PhotoService
import com.prography.network.entity.AddTagsRequest
import com.prography.network.entity.UploadItem
import com.prography.network.util.NetworkState
import com.prography.network.util.getDataOrNull
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
import com.prography.domain.model.TagModel

class PhotoRemoteDataSourceImpl @Inject constructor(
    private val photoService: PhotoService,
    @ApplicationContext private val context: Context
) : PhotoRemoteDataSource {
    override suspend fun getScreenshots(
        hasTags: Boolean?,
        page: Int,
        size: Int
    ): Result<List<UiScreenshotModel>> {
        Timber.d("Calling photoService.getScreenshots(hasTags=$hasTags, page=$page, size=$size)")

        return when (val networkState = photoService.getScreenshots(hasTags, page, size)) {
            is NetworkState.Success -> {
                Timber.d("API Response: ${networkState.body}")
                val screenshots =
                    networkState.body.getDataOrNull()?.toUiScreenshotModels() ?: emptyList()
                Timber.d("Converted screenshots: ${screenshots.size} items")
                Result.success(screenshots)
            }

            is NetworkState.Failure -> {
                Timber.e("API Failure: ${networkState.error}")
                Result.failure(Exception("API 호출 실패: ${networkState.error}"))
            }

            is NetworkState.NetworkError -> {
                Timber.e("Network Error: ${networkState.error}")
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Timber.e("Unknown Error: ${networkState.errorState}")
                Result.failure(networkState.t ?: Exception(networkState.errorState))
            }
        }
    }

    override suspend fun uploadScreenshots(screenshots: List<UiScreenshotModel>): Result<Unit> {
        return runCatching {
            // 1. uniqueFileName 리스트 먼저 생성
            val fileNames = screenshots.mapIndexed { index, screenshot ->
                val originalFileName = getFileNameFromUri(screenshot.uri)
                val safeFileName = sanitizeFileName(originalFileName) // 
                "${System.currentTimeMillis()}_${index}_$safeFileName"
            }

// 2. uploadItems 생성
            val uploadItems = screenshots.mapIndexed { index, screenshot ->
                val captureDate = formatDateForUpload(screenshot.dateStr)
                Timber.d("Processing screenshot: uri=${screenshot.uri}, fileName=${fileNames[index]}, captureDate=$captureDate, tags=${screenshot.tags}")
                UploadItem(
                    fileName = fileNames[index],
                    captureDate = captureDate,
                    tagNames = screenshot.tags.map { it.name },
                    isBookmarked = screenshot.isBookmarked
                )
            }

// 3. JSON 파트 생성
            val uploadItemsJson = Json.encodeToString(
                kotlinx.serialization.builtins.ListSerializer(UploadItem.serializer()),
                uploadItems
            )
            val uploadItemsPart = MultipartBody.Part.createFormData(
                "uploadItems", "uploadItems.json", uploadItemsJson.toRequestBody("application/json".toMediaType())
            )


// 4. 파일 파트 생성
            val fileParts = mutableListOf<MultipartBody.Part>()

            screenshots.forEachIndexed { index, screenshot ->
                val uri = Uri.parse(screenshot.uri)
                val originalFileName = getFileNameFromUri(screenshot.uri)
                val finalFileName = fileNames[index] // 

                Timber.d("Creating file part for: $finalFileName from URI: ${screenshot.uri}")

                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                        ?: throw Exception("Failed to read bytes from URI: $uri")
                    inputStream.close()

                    if (bytes.isEmpty()) throw Exception("File is empty: $uri")
                    if (bytes.size > 10 * 1024 * 1024) throw Exception("File too large: ${bytes.size} bytes")

                    Timber.d("Read ${bytes.size} bytes for file: $finalFileName")

                    val contentType = when {
                        originalFileName.lowercase().endsWith(".png") -> "image/png"
                        originalFileName.lowercase().endsWith(".jpg") -> "image/jpg"
                        originalFileName.lowercase().endsWith(".jpeg") -> "image/jpeg"
                        originalFileName.lowercase().endsWith(".webp") -> "image/webp"
                        else -> "image/jpeg"
                    }

                    val requestFile = bytes.toRequestBody(contentType.toMediaType())
                    val filePart = MultipartBody.Part.createFormData("files", finalFileName, requestFile)
                    fileParts.add(filePart)

                    Timber.d("Added file part for: $finalFileName")
                } catch (e: Exception) {
                    Timber.e(e, "Failed to create file part for: $finalFileName")
                    throw e
                }
            }

            Timber.d("Created ${fileParts.size} file parts, calling API...")

            // API 
            val networkState = photoService.uploadScreenshots(
                uploadItems = uploadItemsPart,
                files = fileParts
            )

            when (networkState) {
                is NetworkState.Success -> {
                    Timber.d("Upload successful: ${networkState.body}")
                }

                is NetworkState.Failure -> {
                    val errorMessage = networkState.error ?: "Unknown error"
                    Timber.e("Upload failed: $errorMessage")
                    throw Exception("Upload failed: $errorMessage")
                }

                is NetworkState.NetworkError -> {
                    Timber.e("Upload network error: ${networkState.error}")
                    throw networkState.error
                }

                is NetworkState.UnknownError -> {
                    Timber.e("Upload unknown error: ${networkState.errorState}")
                    throw networkState.t ?: Exception(networkState.errorState)
                }
            }
        }
    }

    override suspend fun deleteTag(imageId: String, tagName: String): Result<Unit> {
        return runCatching {
            val networkState = photoService.deleteTag(imageId, tagName)

            when (networkState) {
                is NetworkState.Success -> {
                    Timber.d("Tag deletion successful: imageId=$imageId, tagName=$tagName")
                }

                is NetworkState.Failure -> {
                    val errorMessage = networkState.error ?: "Unknown error"
                    Timber.e("Tag deletion failed: $errorMessage")
                    throw Exception("Tag deletion failed: $errorMessage")
                }

                is NetworkState.NetworkError -> {
                    Timber.e("Tag deletion network error: ${networkState.error}")
                    throw networkState.error
                }

                is NetworkState.UnknownError -> {
                    Timber.e("Tag deletion unknown error: ${networkState.errorState}")
                    throw networkState.t ?: Exception(networkState.errorState)
                }
            }
        }
    }

    override suspend fun addTagsToScreenshot(
        screenshotId: String,
        tagNames: List<String>
    ): Result<List<TagModel>> {
        val networkState = photoService.addTagsToScreenshot(
            screenshotId,
            AddTagsRequest(tagNames)
        )

        return when (networkState) {
            is NetworkState.Success -> {
                val tagModels = networkState.body.getDataOrNull()?.map { response ->
                    TagModel(id = response.id.toString(), name = response.name)
                } ?: emptyList()
                Result.success(tagModels)
            }
            is NetworkState.Failure -> {
                Result.failure(Exception(networkState.error ?: "태그 추가 실패"))
            }

            is NetworkState.NetworkError -> {
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Result.failure(networkState.t ?: Exception("알 수 없는 오류"))
            }
        }
    }

    override suspend fun getScreenshotById(screenshotId: String): Result<UiScreenshotModel> {
        return when (val networkState = photoService.getScreenshotById(screenshotId)) {
            is NetworkState.Success -> {
                Timber.d("Single screenshot API Response: ${networkState.body}")
                val screenshot = networkState.body.getDataOrNull()?.toUiScreenshotModel()
                if (screenshot != null) {
                    Timber.d("Converted screenshot: $screenshot")
                    Result.success(screenshot)
                } else {
                    Timber.e("Screenshot not found in response for id $screenshotId")
                    Result.failure(Exception("스크린샷을 찾을 수 없습니다."))
                }
            }

            is NetworkState.Failure -> {
                Timber.e("Single screenshot API Failure: ${networkState.error}")
                Result.failure(Exception("API : ${networkState.error}"))
            }

            is NetworkState.NetworkError -> {
                Timber.e("Single screenshot Network Error: ${networkState.error}")
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Timber.e("Single screenshot Unknown Error: ${networkState.errorState}")
                Result.failure(networkState.t ?: Exception(networkState.errorState))
            }
        }
    }

    override suspend fun deleteScreenshot(screenshotId: String): Result<Unit> {
        return runCatching {
            val networkState = photoService.deleteScreenshot(screenshotId)

            when (networkState) {
                is NetworkState.Success -> {
                    Timber.d("Screenshot deletion successful: screenshotId=$screenshotId")
                }

                is NetworkState.Failure -> {
                    val errorMessage = networkState.error ?: "Unknown error"
                    Timber.e("Screenshot deletion failed: $errorMessage")
                    throw Exception("Screenshot deletion failed: $errorMessage")
                }

                is NetworkState.NetworkError -> {
                    Timber.e("Screenshot deletion network error: ${networkState.error}")
                    throw networkState.error
                }

                is NetworkState.UnknownError -> {
                    Timber.e("Screenshot deletion unknown error: ${networkState.errorState}")
                    throw networkState.t ?: Exception(networkState.errorState)
                }
            }
        }
    }

    override suspend fun addBookmark(imageId: String): Result<Unit> {
        return runCatching {
            val networkState = photoService.addBookmark(imageId)

            when (networkState) {
                is NetworkState.Success -> {
                    Timber.d("Bookmark add successful: imageId=$imageId")
                }

                is NetworkState.Failure -> {
                    val errorMessage = networkState.error ?: "Unknown error"
                    Timber.e("Bookmark add failed: $errorMessage")
                    throw Exception("Bookmark add failed: $errorMessage")
                }

                is NetworkState.NetworkError -> {
                    Timber.e("Bookmark add network error: ${networkState.error}")
                    throw networkState.error
                }

                is NetworkState.UnknownError -> {
                    Timber.e("Bookmark add unknown error: ${networkState.errorState}")
                    throw networkState.t ?: Exception(networkState.errorState)
                }
            }
        }
    }

    override suspend fun removeBookmark(imageId: String): Result<Unit> {
        return runCatching {
            val networkState = photoService.removeBookmark(imageId)

            when (networkState) {
                is NetworkState.Success -> {
                    Timber.d("Bookmark remove successful: imageId=$imageId")
                }

                is NetworkState.Failure -> {
                    val errorMessage = networkState.error ?: "Unknown error"
                    Timber.e("Bookmark remove failed: $errorMessage")
                    throw Exception("Bookmark remove failed: $errorMessage")
                }

                is NetworkState.NetworkError -> {
                    Timber.e("Bookmark remove network error: ${networkState.error}")
                    throw networkState.error
                }

                is NetworkState.UnknownError -> {
                    Timber.e("Bookmark remove unknown error: ${networkState.errorState}")
                    throw networkState.t ?: Exception(networkState.errorState)
                }
            }
        }
    }

    override suspend fun getFavoriteImages(page: Int, size: Int): Result<List<UiScreenshotModel>> {
        Timber.d("Calling photoService.getFavoriteImages(page=$page, size=$size)")

        return when (val networkState = photoService.getFavoriteImages(page, size)) {
            is NetworkState.Success -> {
                Timber.d("Favorite images API Response: ${networkState.body}")
                val favoriteImages =
                    networkState.body.getDataOrNull()?.toUiScreenshotModels() ?: emptyList()
                Timber.d("Converted favorite images: ${favoriteImages.size} items")
                Result.success(favoriteImages)
            }

            is NetworkState.Failure -> {
                Timber.e("Favorite images API Failure: ${networkState.error}")
                Result.failure(Exception("API : ${networkState.error}"))
            }

            is NetworkState.NetworkError -> {
                Timber.e("Favorite images Network Error: ${networkState.error}")
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Timber.e("Favorite images Unknown Error: ${networkState.errorState}")
                Result.failure(networkState.t ?: Exception(networkState.errorState))
            }
        }
    }

    override suspend fun getMostUsedTags(size: Int): Result<List<TagWithCount>> {
        Timber.d("Calling photoService.getMostUsedTags(size=$size)")

        return when (val networkState = photoService.getMostUsedTags(size = size)) {
            is NetworkState.Success -> {
                Timber.d("Most used tags API Response: ${networkState.body}")
                val tags = networkState.body.data?.items?.map { tagResponse ->
                    TagWithCount(
                        tag = tagResponse.name,
                        count = 0 // 
                    )
                } ?: emptyList()
                Timber.d("Converted most used tags: ${tags.size} items")
                Result.success(tags)
            }

            is NetworkState.Failure -> {
                Timber.e("Most used tags API Failure: ${networkState.error}")
                Result.failure(Exception("API 호출 실패: ${networkState.error}"))
            }

            is NetworkState.NetworkError -> {
                Timber.e("Most used tags Network Error: ${networkState.error}")
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Timber.e("Most used tags Unknown Error: ${networkState.errorState}")
                Result.failure(networkState.t ?: Exception(networkState.errorState))
            }
        }
    }

    override suspend fun completeTutorial(): Result<Unit> {
        Timber.d("Calling photoService.completeTutorial()")

        return when (val networkState = photoService.completeTutorial()) {
            is NetworkState.Success -> {
                Timber.d("Tutorial complete API Response: ${networkState.body}")
                Result.success(Unit)
            }

            is NetworkState.Failure -> {
                Timber.e("Tutorial complete API Failure: ${networkState.error}")
                Result.failure(Exception("튜토리얼 완료 API 호출 실패: ${networkState.error}"))
            }

            is NetworkState.NetworkError -> {
                Timber.e("Tutorial complete Network Error: ${networkState.error}")
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Timber.e("Tutorial complete Unknown Error: ${networkState.errorState}")
                Result.failure(networkState.t ?: Exception(networkState.errorState))
            }
        }
    }

    override suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): Result<List<UiScreenshotModel>> {
        Timber.d("Calling photoService.searchImagesByTags(tagNames=$tagNames, page=$page, size=$size)")

        return when (val networkState = photoService.searchImagesByTags(tagNames, page, size)) {
            is NetworkState.Success -> {
                Timber.d("Search images by tags API Response: ${networkState.body}")
                val screenshots =
                    networkState.body.data?.items?.map { it.toUiScreenshotModel() } ?: emptyList()
                Timber.d("Converted search results: ${screenshots.size} items")
                Result.success(screenshots)
            }

            is NetworkState.Failure -> {
                Timber.e("Search images by tags API Failure: ${networkState.error}")
                Result.failure(Exception("API 호출 실패: ${networkState.error}"))
            }

            is NetworkState.NetworkError -> {
                Timber.e("Search images by tags Network Error: ${networkState.error}")
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Timber.e("Search images by tags Unknown Error: ${networkState.errorState}")
                Result.failure(networkState.t ?: Exception(networkState.errorState))
            }
        }
    }

    override suspend fun getRelatedTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): Result<List<String>> {
        Timber.d("Calling photoService.getRelatedTags(tagNames=$tagNames, page=$page, size=$size)")

        return when (val networkState = photoService.getRelatedTags(tagNames, page, size)) {
            is NetworkState.Success -> {
                Timber.d("Get related tags API Response: ${networkState.body}")
                val relatedTags = networkState.body.data?.items?.map { it.name } ?: emptyList()
                Timber.d("Converted related tags: ${relatedTags.size} items")
                Result.success(relatedTags)
            }

            is NetworkState.Failure -> {
                Timber.e("Get related tags API Failure: ${networkState.error}")
                Result.failure(Exception("API 호출 실패: ${networkState.error}"))
            }

            is NetworkState.NetworkError -> {
                Timber.e("Get related tags Network Error: ${networkState.error}")
                Result.failure(networkState.error)
            }

            is NetworkState.UnknownError -> {
                Timber.e("Get related tags Unknown Error: ${networkState.errorState}")
                Result.failure(networkState.t ?: Exception(networkState.errorState))
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
            // dateStr이 "2023년 12월 15일" 또는 "2023년 2월 5일" 형태라면 "2023-12-15"로 변환
            if (dateStr.contains("년") && dateStr.contains("월") && dateStr.contains("일")) {
                val yearPart = dateStr.substringBefore("년").trim()
                val monthPart = dateStr.substringAfter("년").substringBefore("월").trim()
                val dayPart = dateStr.substringAfter("월").substringBefore("일").trim()

                val year = yearPart
                val month = monthPart.padStart(2, '0')
                val day = dayPart.padStart(2, '0')

                "$year-$month-$day"
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
    private fun sanitizeFileName(fileName: String): String {
        return fileName
            .replace(" ", "_") // 공백 제거
            .replace(Regex("[^A-Za-z0-9_.-]"), "") // 영문, 숫자, _, ., - 제외 전부 제거
    }
    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}