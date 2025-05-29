package com.prography.home

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

/**
 * 스크린샷만 선택할 수 있는 커스텀 ActivityResultContract
 */
class GetScreenshotContent : ActivityResultContract<Unit, Uri?>() {

    @CallSuper
    override fun createIntent(context: Context, input: Unit) =
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            .let { android.content.Intent(android.content.Intent.ACTION_PICK, it) }
            .apply {
            type = "image/*"
                // 스크린샷을 선택하기 쉽도록 타이틀 설정
                putExtra(android.content.Intent.EXTRA_TITLE, "스크린샷 선택")
            }

    override fun parseResult(resultCode: Int, intent: android.content.Intent?): Uri? {
        return if (resultCode == android.app.Activity.RESULT_OK) {
            intent?.data
        } else {
            null
        }
    }
}

/**
 * 스크린샷 관련 유틸리티 함수
 */
object ScreenshotUtils {

    /**
     * URI가 스크린샷인지 확인
     */
    fun isScreenshotUri(context: Context, uri: Uri): Boolean {
        if (uri.scheme != "content") return false

        val projection = arrayOf(MediaStore.Images.Media.DATA)

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val path = cursor.getString(columnIndex)

                return path.contains("screenshot", ignoreCase = true) ||
                        path.contains("screen_shot", ignoreCase = true) ||
                        isInScreenshotDir(path)
            }
        }

        return false
    }

    /**
     * 경로가 스크린샷 디렉토리에 있는지 확인
     */
    private fun isInScreenshotDir(path: String): Boolean {
        val screenshotDirs = listOf(
            "Screenshots",
            "Screenshot",
            "화면캡쳐",
            "화면 캡쳐",
            "스크린샷"
        )

        return screenshotDirs.any { path.contains(it, ignoreCase = true) }
    }

    /**
     * 직접 MediaStore API를 사용하여 스크린샷 목록 가져오기 (Android 10 이상용)
     */
    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    fun getScreenshotsForAndroid10Plus(context: Context): List<Uri> {
        val screenshots = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH
        )

        // RELATIVE_PATH를 사용해 스크린샷 필터링 (Android 10+)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        // 여러 스크린샷 경로 패턴 - 필요시 더 추가 가능
        val selectionArgs = arrayOf("%Screenshot%")

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            context.contentResolver.query(
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                        id
                    )
                    screenshots.add(contentUri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return screenshots
    }

    /**
     * 사용자 정의 스크린샷 선택기 활동을 위한 Intent 생성
     */
    fun createScreenshotPickerIntent(context: Context): android.content.Intent {
        // 스크린샷 목록을 가져옴
        val screenshotUris = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 이상에서는 RELATIVE_PATH 필터링 사용
            getScreenshotsForAndroid10Plus(context)
        } else {
            // 이전 버전에서는 일반적인 방법 사용
            getScreenshots(context)
        }

        // 목록이 비어 있지 않다면 첫 번째 스크린샷을 미리보기로 사용
        val initialUri = if (screenshotUris.isNotEmpty()) {
            screenshotUris.first()
        } else {
            null
        }

        // 사용자 정의 Intent 생성
        return android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT)
            .apply {
                type = "image/*"
                addCategory(android.content.Intent.CATEGORY_OPENABLE)
                putExtra(android.content.Intent.EXTRA_TITLE, "스크린샷 선택")
                putExtra(android.content.Intent.EXTRA_LOCAL_ONLY, true)

                // Android 11 이상에서는 초기 URI 설정 가능
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && initialUri != null) {
                    putExtra(android.content.Intent.EXTRA_STREAM, initialUri)
                }
            }
    }

    /**
     * 디바이스의 스크린샷 목록 가져오기
     */
    fun getScreenshots(context: Context): List<Uri> {
        val screenshots = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )

        // Android 10 (Q) 이상인 경우 RELATIVE_PATH 사용
        val selection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ? OR ${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?"
        } else {
            "${MediaStore.Images.Media.DATA} LIKE ? OR ${MediaStore.Images.Media.DATA} LIKE ?"
        }

        // 여러 스크린샷 경로 패턴 확인
        val selectionArgs = arrayOf(
            "%Screenshot%",
            "%screen%shot%"
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)

                // 추가 확인: 경로에 스크린샷 관련 키워드가 포함되어 있는지
                if (path.contains("screenshot", ignoreCase = true) ||
                    path.contains("screen_shot", ignoreCase = true) ||
                    isInScreenshotDir(path)
                ) {
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
                        id
                    )
                    screenshots.add(contentUri)
                }
            }
        }

        return screenshots
    }
}