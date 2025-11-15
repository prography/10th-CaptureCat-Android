package com.prography.data.datasource.local

import com.prography.data.mapper.toDomain
import com.prography.data.mapper.toEntity
import com.prography.database.dao.ScreenshotDao
import com.prography.domain.model.TagWithCount
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.model.TagModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import javax.inject.Inject
import java.util.UUID
import kotlin.Result

class ScreenshotLocalDataSourceImpl @Inject constructor(
    private val dao: ScreenshotDao
) : ScreenshotLocalDataSource {

    override suspend fun getScreenshots(hasTags: Boolean?): Flow<List<UiScreenshotModel>> {
        return dao.getAll().map { list ->
            val screenshots = list.map { it.toDomain() }
            when (hasTags) {
                true -> screenshots.filter { it.tags.isNotEmpty() }
                false -> screenshots.filter { it.tags.isEmpty() }
                null -> screenshots
            }
        }
    }

    override suspend fun insert(screenshot: UiScreenshotModel) {
        dao.insert(screenshot.toEntity())
    }

    override suspend fun update(screenshot: UiScreenshotModel) {
        dao.update(screenshot.toEntity())
    }

    override suspend fun delete(screenshot: UiScreenshotModel) {
        dao.delete(screenshot.toEntity())
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun deleteById(screenshotId: String) {
        dao.deleteById(screenshotId)
    }

    override suspend fun deleteTag(imageId: String, tagName: String) {
        val screenshotEntity = dao.getById(imageId)
        if (screenshotEntity != null) {
            val screenshot = screenshotEntity.toDomain()
            val updatedTags = screenshot.tags.filter { it.name != tagName }
            val updatedScreenshot = screenshot.copy(tags = updatedTags)
            dao.update(updatedScreenshot.toEntity())
            Timber.d("Deleted tag $tagName from screenshot $imageId")
        } else {
            Timber.w("deleteTag: Screenshot with id $imageId not found.")
        }
    }

    override suspend fun getById(screenshotId: String): UiScreenshotModel? {
        return dao.getById(screenshotId)?.toDomain()
    }

    override suspend fun addTagsToScreenshot(screenshotId: String, tagNames: List<String>) {
        val screenshotEntity = dao.getById(screenshotId)
        if (screenshotEntity != null) {
            val screenshot = screenshotEntity.toDomain()
            // Convert tag names to TagModel objects
            val newTags = tagNames.map { tagName ->
                TagModel(
                    id = System.currentTimeMillis(),
                    name = tagName
                )
            }
            // Ensure there are no duplicate tag names
            val currentTags = screenshot.tags
            val allTags = (currentTags + newTags).distinctBy { it.name }
            val updatedScreenshot = screenshot.copy(tags = allTags)
            dao.update(updatedScreenshot.toEntity())
            Timber.d("Added tags $tagNames to screenshot $screenshotId")
        } else {
            Timber.w("addTagsToScreenshot: Screenshot with id $screenshotId not found.")
        }
    }

    override suspend fun getMostUsedTags(size: Int): List<TagWithCount> {
        val screenshots = getScreenshots(null).firstOrNull() ?: emptyList()
        val tagCounts = mutableMapOf<String, Int>()

        // 모든 스크린샷의 태그를 수집하고 카운트
        screenshots.forEach { screenshot ->
            screenshot.tags.forEach { tag ->
                tagCounts[tag.name] = tagCounts.getOrDefault(tag.name, 0) + 1
            }
        }

        // 카운트가 높은 순으로 정렬하고 상위 size개만 반환
        val mostUsedTags = tagCounts.entries
            .map { TagWithCount(0, it.key, it.value) }
            .sortedByDescending { it.count }
            .take(size)

        Timber.d("Local getMostUsedTags: returning ${mostUsedTags.size} tags from ${tagCounts.size} total tags")
        return mostUsedTags
    }

    override suspend fun getFavoriteTags(size: Int): List<TagWithCount> {
        val screenshots = dao.getBookmarked().firstOrNull()?.map { it.toDomain() }.orEmpty()

        val tagCounts = screenshots.asSequence()
            .flatMap { it.tags.asSequence() }
            .groupingBy { it.name }
            .eachCount()

        return tagCounts.entries.asSequence()
            .sortedByDescending { it.value }
            .take(size)
            .map { TagWithCount(0, it.key, it.value) }
            .toList()
    }

    override suspend fun getBookmarkedPagedFiltered(
        tagId: Int,
        page: Int,
        size: Int
    ): List<UiScreenshotModel> {
        // 1) 즐겨찾기만 읽기
        val favorites = dao.getBookmarked().firstOrNull()
            ?.map { it.toDomain() }
            .orEmpty()

        // 2) tagId 필터 (tagId == 0이면 필터 생략)
        val filtered = if (tagId == 0) {
            favorites
        } else {
            favorites.filter { sc ->
                sc.tags.any { t -> t.id?.toInt() == tagId }
            }
        }

        // 3) 페이징
        val start = page * size
        val end = (start + size).coerceAtMost(filtered.size)
        return if (start in 0..filtered.lastIndex || (start == 0 && filtered.isEmpty())) {
            filtered.subList(start, end)
        } else {
            emptyList()
        }
    }


    override suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<UiScreenshotModel> {
        val allScreenshots = getScreenshots(null).firstOrNull() ?: emptyList()

        // 모든 태그가 포함된 스크린샷만 필터링
        val filteredScreenshots = allScreenshots.filter { screenshot ->
            tagNames.all { searchTag ->
                screenshot.tags.any { tag ->
                    tag.name.equals(searchTag, ignoreCase = true)
                }
            }
        }

        // 페이징 처리
        val startIndex = page * size
        val endIndex = minOf(startIndex + size, filteredScreenshots.size)

        val pagedResults = if (startIndex < filteredScreenshots.size) {
            filteredScreenshots.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        Timber.d("Local searchImagesByTags: found ${pagedResults.size} screenshots for tags $tagNames (page=$page, size=$size)")
        return pagedResults
    }

    override suspend fun getRelatedTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<String> {
        val allScreenshots = getScreenshots(null).firstOrNull() ?: emptyList()

        // 선택된 모든 태그를 가진 스크린샷들 찾기
        val screenshotsWithAllTags = allScreenshots.filter { screenshot ->
            tagNames.all { searchTag ->
                screenshot.tags.any { tag ->
                    tag.name.equals(searchTag, ignoreCase = true)
                }
            }
        }

        // 해당 스크린샷들의 다른 태그들을 수집하고 빈도수 계산
        val tagCounts = mutableMapOf<String, Int>()
        screenshotsWithAllTags.forEach { screenshot ->
            screenshot.tags.forEach { tag ->
                // 이미 선택된 태그는 제외
                if (!tagNames.contains(tag.name)) {
                    tagCounts[tag.name] = tagCounts.getOrDefault(tag.name, 0) + 1
                }
            }
        }

        // 빈도순으로 정렬하고 페이징 처리
        val sortedTags = tagCounts.entries
            .sortedByDescending { it.value }
            .map { it.key }

        val startIndex = page * size
        val endIndex = minOf(startIndex + size, sortedTags.size)

        val pagedResults = if (startIndex < sortedTags.size) {
            sortedTags.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        Timber.d("Local getRelatedTags: found ${pagedResults.size} related tags for $tagNames (page=$page, size=$size)")
        return pagedResults
    }

    override suspend fun getSearchAutoComplete(
        keyword: String,
        size: Int
    ): List<TagModel> {
        val screenshots = getScreenshots(null).firstOrNull() ?: emptyList()
        val tags = mutableListOf<TagModel>()

        screenshots.forEach { screenshot ->
            screenshot.tags.forEach { tag ->
                if (tag.name.contains(keyword, ignoreCase = true)) {
                    tags.add(TagModel(id = tag.id, name = tag.name))
                }
            }
        }

        return tags.distinctBy { it.name }.take(size)
    }

    override suspend fun deleteTag(tagId: Int): Result<Unit> {
        return try {
            // 로컬에서는 단순히 성공을 반환 (실제 삭제는 서버에서 처리)
            Timber.d("Local deleteTag called for tagId: $tagId")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTags(tagIds: List<Int>): Result<Unit> {
        return try {
            // 로컬에서는 단순히 성공을 반환 (실제 삭제는 서버에서 처리)
            Timber.d("Local deleteTags called for tagIds: $tagIds")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
