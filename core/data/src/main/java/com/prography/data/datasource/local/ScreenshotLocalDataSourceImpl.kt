package com.prography.data.datasource.local

import com.prography.data.mapper.toUiScreenshotModel
import com.prography.data.mapper.toUiScreenshotModels
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


class ScreenshotLocalDataSourceImpl @Inject constructor(
    private val dao: ScreenshotDao
) : ScreenshotLocalDataSource {

    override suspend fun getScreenshots(): Flow<List<UiScreenshotModel>> {
        return dao.getAll().map { list -> list.map { it.toDomain() } }
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

    override suspend fun deleteById(screenshotId: String) {
        dao.deleteById(screenshotId)
    }

    override suspend fun deleteTag(imageId: String, tagName: String) {
        // 전체 스크린샷을 가져와서 해당 ID의 태그를 삭제
        val screenshots = dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

        // 이 구현은 실시간 업데이트가 아니므로 나중에 개선 필요
        // 현재는 ViewModel에서 UpdateScreenshotUseCase를 사용하는 것이 더 적합
        Timber.d("deleteTag called for imageId: $imageId, tagName: $tagName")
        throw UnsupportedOperationException("Use UpdateScreenshotUseCase instead for tag deletion")
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
                    id = UUID.randomUUID().toString(),
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
        val screenshots = getScreenshots().firstOrNull() ?: emptyList()
        val tagCounts = mutableMapOf<String, Int>()

        // 모든 스크린샷의 태그를 수집하고 카운트
        screenshots.forEach { screenshot ->
            screenshot.tags.forEach { tag ->
                tagCounts[tag.name] = tagCounts.getOrDefault(tag.name, 0) + 1
            }
        }

        // 카운트가 높은 순으로 정렬하고 상위 size개만 반환
        val mostUsedTags = tagCounts.entries
            .map { TagWithCount(it.key, it.value) }
            .sortedByDescending { it.count }
            .take(size)

        Timber.d("Local getMostUsedTags: returning ${mostUsedTags.size} tags from ${tagCounts.size} total tags")
        return mostUsedTags
    }

    override suspend fun searchImagesByTags(
        tagNames: List<String>,
        page: Int,
        size: Int
    ): List<UiScreenshotModel> {
        val allScreenshots = getScreenshots().firstOrNull() ?: emptyList()

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
        val allScreenshots = getScreenshots().firstOrNull() ?: emptyList()

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
}
