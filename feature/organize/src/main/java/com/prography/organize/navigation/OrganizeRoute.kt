package com.prography.organize.navigation

import android.content.ContentUris
import android.provider.MediaStore
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import com.prography.navigation.NavigationHelper
import com.prography.organize.model.OrganizeScreenshotItem
import com.prography.organize.ui.OrganizeScreen

@Composable
fun OrganizeRoute(
    navigationHelper: NavigationHelper,
    screenshotIds: List<String> = emptyList()
) {
    val context = LocalContext.current

    // 전달받은 ID로부터 실제 스크린샷 데이터를 로드
    val actualScreenshots = remember(screenshotIds) {
        if (screenshotIds.isNotEmpty()) {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
            )
            val selection =
                "${MediaStore.Images.Media._ID} IN (${screenshotIds.joinToString(",") { "?" }})"
            val selectionArgs = screenshotIds.toTypedArray()

            val loadedScreenshots = mutableListOf<OrganizeScreenshotItem>()

            context.contentResolver.query(uri, projection, selection, selectionArgs, null)
                ?.use { cursor ->
                    val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idCol)
                        val fileName = cursor.getString(nameCol)
                        val imageUri = ContentUris.withAppendedId(uri, id)

                        loadedScreenshots.add(
                            OrganizeScreenshotItem(
                                id = id.toString(),
                                uri = imageUri,
                                fileName = fileName
                            )
                        )
                    }
                }

            loadedScreenshots
        } else {
            emptyList()
        }
    }

    OrganizeScreen(
        screenshots = actualScreenshots,
        currentIndex = 0,
        onNavigateUp = {
            navigationHelper.navigate(NavigationEvent.Up)
        },
        onComplete = {
            // 메인 화면으로 돌아가기 (전체 스택 정리)
            navigationHelper.navigate(NavigationEvent.To(AppRoute.Main, popUpTo = true))
        }
    )
}
