package com.prography.home

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 스크린샷만 선택할 수 있는 전용 이미지 피커 Activity
 */
class ScreenshotPickerActivity : ComponentActivity() {

    private val screenshotUris = mutableStateOf<List<Uri>>(emptyList())
    private val selectedUris =
        mutableStateOf<SnapshotStateList<Uri>>(mutableListOf<Uri>().toMutableStateList())
    private val isMultiSelectMode = mutableStateOf(true)
    private val currentFilter = mutableStateOf("전체")
    private val screenshotGroups = mutableStateOf<Map<String, List<Uri>>>(emptyMap())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 인텐트에서 다중 선택 모드 설정 받아오기
        isMultiSelectMode.value = intent.getBooleanExtra("multi_select_mode", true)

        // 백그라운드에서 스크린샷 목록을 로드
        runBlocking {
            withContext(Dispatchers.IO) {
                // Android 버전에 따라 적절한 방법 사용
                screenshotUris.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    fetchScreenshotsUsingRelativePath()
                } else {
                    fetchScreenshotsUsingDataPath()
                }

                // 스크린샷을 앱별로 분류
                screenshotGroups.value = groupScreenshotsByApp(screenshotUris.value)
            }
        }

        setContent {
            MaterialTheme {
                MultiScreenshotPickerScreen(
                    screenshots = screenshotUris.value,
                    selectedScreenshots = selectedUris.value,
                    isMultiSelectMode = isMultiSelectMode.value,
                    currentFilter = currentFilter.value,
                    screenshotGroups = screenshotGroups.value,
                    onFilterChange = { filter ->
                        currentFilter.value = filter
                    },
                    onScreenshotToggle = { uri ->
                        // 이미 선택된 항목인지 확인
                        if (selectedUris.value.contains(uri)) {
                            selectedUris.value.remove(uri)
                        } else {
                            if (isMultiSelectMode.value) {
                                // 다중 선택 모드
                                selectedUris.value.add(uri)
                            } else {
                                // 단일 선택 모드
                                selectedUris.value.clear()
                                selectedUris.value.add(uri)
                            }
                        }
                    },
                    onModeChange = {
                        isMultiSelectMode.value = it
                        // 모드 변경 시 선택 초기화
                        selectedUris.value.clear()
                    },
                    onConfirm = {
                        // ArrayList로 변환하여 결과 전달
                        val uriArrayList = ArrayList<Uri>().apply {
                            addAll(selectedUris.value)
                        }

                        val resultIntent = Intent().apply {
                            putParcelableArrayListExtra("selected_screenshots", uriArrayList)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    onCancel = {
                        // 취소 시 결과 없이 활동 종료
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    // Android 10 이상에서 RELATIVE_PATH를 사용하여 스크린샷 가져오기 
    private fun fetchScreenshotsUsingRelativePath(): List<Uri> {
        val screenshots = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.RELATIVE_PATH,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        // 여러 가지 스크린샷 경로 패턴 시도
        val selectionParts = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        // RELATIVE_PATH에 'Screenshot' 포함
        selectionParts.add("${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?")
        selectionArgs.add("%Screenshot%")

        // 한글 경로도 확인
        selectionParts.add("${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?")
        selectionArgs.add("%스크린샷%")

        // 파일명에 'screenshot' 포함
        selectionParts.add("${MediaStore.Images.Media.DISPLAY_NAME} LIKE ?")
        selectionArgs.add("%screenshot%")

        val selection = selectionParts.joinToString(" OR ")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
            projection,
            selection,
            selectionArgs.toTypedArray(),
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

        return screenshots
    }

    // Android 9 이하에서 DATA 경로를 사용하여 스크린샷 가져오기
    private fun fetchScreenshotsUsingDataPath(): List<Uri> {
        val screenshots = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA
        )

        // 여러 가지 스크린샷 경로 패턴 시도
        val selectionParts = mutableListOf<String>()
        val selectionArgs = mutableListOf<String>()

        // DATA 경로에 'screenshot' 포함
        selectionParts.add("${MediaStore.Images.Media.DATA} LIKE ?")
        selectionArgs.add("%screenshot%")

        // DATA 경로에 '스크린샷' 포함 (한글)
        selectionParts.add("${MediaStore.Images.Media.DATA} LIKE ?")
        selectionArgs.add("%스크린샷%")

        val selection = selectionParts.joinToString(" OR ")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL),
            projection,
            selection,
            selectionArgs.toTypedArray(),
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

        return screenshots
    }

    // 스크린샷을 앱별로 분류하는 함수
    private fun groupScreenshotsByApp(screenshots: List<Uri>): Map<String, List<Uri>> {
        val groups = mutableMapOf<String, MutableList<Uri>>()
        groups["전체"] = screenshots.toMutableList()

        // 날짜별 그룹 추가 (최근 7일)
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)

        // 최근 7일을 위한 날짜 범위 생성
        val dateGroups = mutableMapOf<String, Pair<Long, Long>>() // 그룹명 -> (시작밀리초, 종료밀리초)

        // 오늘
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val todayEnd = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        dateGroups["오늘"] = Pair(todayStart, todayEnd)

        // 어제
        val yesterdayStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val yesterdayEnd = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        dateGroups["어제"] = Pair(yesterdayStart, yesterdayEnd)

        // 이번 주
        val weekStart = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        dateGroups["이번 주"] = Pair(weekStart, todayEnd)

        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        for (uri in screenshots) {
            try {
                contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        val fileName = cursor.getString(nameIndex)

                        // 파일 이름에서 카테고리 추출
                        val category = getCategoryFromFileName(fileName)

                        // 카테고리별 그룹에 추가
                        if (category.isNotEmpty()) {
                            if (!groups.containsKey(category)) {
                                groups[category] = mutableListOf()
                            }
                            groups[category]!!.add(uri)
                        }

                        // 날짜별 분류
                        try {
                            val dateAddedIndex =
                                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                            val dateAdded = cursor.getLong(dateAddedIndex) * 1000 // 초 -> 밀리초 변환

                            // 날짜 그룹에 추가
                            for ((groupName, dateRange) in dateGroups) {
                                if (dateAdded in dateRange.first..dateRange.second) {
                                    if (!groups.containsKey(groupName)) {
                                        groups[groupName] = mutableListOf()
                                    }
                                    groups[groupName]!!.add(uri)
                                    break
                                }
                            }
                        } catch (e: Exception) {
                            // DATE_ADDED 컬럼이 없는 경우 예외 처리
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return groups
    }

    // 파일명에서 앱 이름 추출 (다양한 패턴 지원)
    private fun getCategoryFromFileName(fileName: String): String {
        try {
            // 파일명이 너무 짧으면 기타 처리
            if (fileName.length < 5) {
                return "기타"
            }

            // 확장자 제거
            val nameWithoutExt = fileName.substringBeforeLast(".")

            // 패턴 1: "Screenshot_날짜_id_앱이름.jpg"
            if (fileName.startsWith("Screenshot_", ignoreCase = true)) {
                val parts = nameWithoutExt.split("_")

                // 패턴에 맞는지 확인 (최소 4개 파트: Screenshot, 날짜, id, 앱이름)
                if (parts.size >= 4) {
                    // 마지막 부분이 앱 이름
                    val appName = parts.last()

                    if (appName.isNotEmpty()) {
                        return formatAppName(appName)
                    }
                }
            }

            // 패턴 2: "앱이름_Screenshot_날짜.jpg"
            if (fileName.contains("_Screenshot_", ignoreCase = true)) {
                val pattern = "_Screenshot_"
                val lowerFileName = fileName.lowercase()
                val lowerPattern = pattern.lowercase()
                val index = lowerFileName.indexOf(lowerPattern)
                if (index > 0) {
                    val firstPart = fileName.substring(0, index)
                    if (firstPart.isNotEmpty()) {
                        return formatAppName(firstPart)
                    }
                }
            }

            // 패턴 3: "앱이름-Screenshot-날짜.jpg"
            if (fileName.contains("-Screenshot-", ignoreCase = true)) {
                val pattern = "-Screenshot-"
                val lowerFileName = fileName.lowercase()
                val lowerPattern = pattern.lowercase()
                val index = lowerFileName.indexOf(lowerPattern)
                if (index > 0) {
                    val firstPart = fileName.substring(0, index)
                    if (firstPart.isNotEmpty()) {
                        return formatAppName(firstPart)
                    }
                }
            }

            // 앱 이름 키워드 매핑 (일반적인 키워드 기반 분류도 지원)
            val appKeywords = mapOf(
                "kakao" to "카카오톡",
                "instagram" to "인스타그램",
                "youtube" to "유튜브",
                "facebook" to "페이스북",
                "twitter" to "트위터",
                "naver" to "네이버",
                "chrome" to "크롬",
                "samsung" to "삼성",
                "gallery" to "갤러리",
                "camera" to "카메라",
                "message" to "메시지"
            )

            // 키워드 기반 매칭
            for ((keyword, appName) in appKeywords) {
                if (fileName.contains(keyword, ignoreCase = true)) {
                    return appName
                }
            }

            return "기타"
        } catch (e: Exception) {
            e.printStackTrace()
            return "기타"
        }
    }

    // 앱 이름 포맷팅 (첫 글자 대문자, 특수문자 제거 등)
    private fun formatAppName(rawName: String): String {
        val trimmedName = rawName.trim()

        // 숫자로만 이루어진 경우 기타로 처리
        if (trimmedName.all { it.isDigit() }) {
            return "기타"
        }

        // 너무 짧거나 긴 이름 처리
        if (trimmedName.length < 2 || trimmedName.length > 20) {
            return "기타"
        }

        // 첫 글자 대문자 변환 및 앱 이름 매핑
        val formattedName = trimmedName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        // 유명 앱 이름 한글 변환
        return when (formattedName.lowercase()) {
            "kakaotalk" -> "카카오톡"
            "instagram" -> "인스타그램"
            "youtube" -> "유튜브"
            "facebook" -> "페이스북"
            "twitter" -> "트위터"
            "chrome" -> "크롬"
            "naver" -> "네이버"
            "settings" -> "설정"
            "gallery" -> "갤러리"
            "camera" -> "카메라"
            "message", "messages" -> "메시지"
            else -> formattedName
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultiScreenshotPickerScreen(
    screenshots: List<Uri>,
    selectedScreenshots: List<Uri>,
    isMultiSelectMode: Boolean,
    currentFilter: String,
    screenshotGroups: Map<String, List<Uri>>,
    onFilterChange: (String) -> Unit,
    onScreenshotToggle: (Uri) -> Unit,
    onModeChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val filteredScreenshots = remember(currentFilter, screenshotGroups) {
        derivedStateOf {
            screenshotGroups[currentFilter] ?: screenshots
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("스크린샷 선택")
                        if (selectedScreenshots.isNotEmpty()) {
                            Text(
                                "${selectedScreenshots.size}개 선택됨",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        // 뒤로 가기 아이콘
                        Text("←")
                    }
                },
                actions = {
                    // 선택 모드 전환 버튼
                    IconButton(onClick = { onModeChange(!isMultiSelectMode) }) {
                        Text(if (isMultiSelectMode) "단일" else "다중")
                    }
                    // 선택 완료 버튼
                    TextButton(
                        onClick = onConfirm,
                        enabled = selectedScreenshots.isNotEmpty()
                    ) {
                        Text("완료")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 앱별 필터 탭
            AppFilterTabs(
                filters = screenshotGroups.keys.toList(),
                currentFilter = currentFilter,
                onFilterSelected = onFilterChange
            )

            // 스크린샷 표시 영역
            if (filteredScreenshots.value.isEmpty()) {
                // 스크린샷이 없는 경우
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("스크린샷이 없습니다")
                }
            } else {
                // 스크린샷 그리드 표시 (선택 상태 표시)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredScreenshots.value) { uri ->
                        val isSelected = selectedScreenshots.contains(uri)

                        // 스크린샷 표시
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .clickable { onScreenshotToggle(uri) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Box {
                                GlideImage(
                                    model = uri,
                                    contentDescription = "Screenshot",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // 선택 표시
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        contentAlignment = Alignment.TopEnd
                                    ) {
                                        Surface(
                                            modifier = Modifier.size(24.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "✓",
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppFilterTabs(
    filters: List<String>,
    currentFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    // 필터 우선순위 설정
    val priorityFilters = listOf("전체", "오늘", "어제", "이번 주")

    // 필터 정렬
    val sortedFilters = filters.sortedWith(compareBy {
        // 우선순위 필터가 먼저 오도록
        val priority = priorityFilters.indexOf(it)
        if (priority >= 0) priority else Int.MAX_VALUE
    })

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            sortedFilters.forEach { filter ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (currentFilter == filter)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (currentFilter == filter)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { onFilterSelected(filter) }
                ) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (currentFilter == filter)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // 현재 필터 표시
        if (currentFilter != "전체") {
            Text(
                text = "카테고리: $currentFilter",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
            )
        }
    }
}