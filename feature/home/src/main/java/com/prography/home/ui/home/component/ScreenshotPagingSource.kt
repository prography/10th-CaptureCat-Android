package com.prography.home.ui.home.component

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.prography.domain.model.UiScreenshotModel
import com.prography.domain.usecase.screenshot.GetAllScreenshotsUseCase
import timber.log.Timber

class ScreenshotPagingSource(
    private val getScreenshotsUseCase: GetAllScreenshotsUseCase,
) : PagingSource<Int, UiScreenshotModel>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UiScreenshotModel> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            Timber.d("PagingSource called! page=$page, pageSize=$pageSize")
            val screenshots = getScreenshotsUseCase.getScreenshots(
                page = page,
                pageSize = pageSize,
                hasTags = null
            )
            Timber.d("PagingSource loaded screenshots.size=${screenshots.size}, items=$screenshots")
            LoadResult.Page(
                data = screenshots,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (screenshots.isEmpty() || screenshots.size < 20) null else page + 1
            )
        } catch (e: Exception) {
            Timber.e(e,"PagingSource error")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UiScreenshotModel>): Int? = 0
}