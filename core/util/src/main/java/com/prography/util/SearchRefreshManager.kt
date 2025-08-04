package com.prography.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRefreshManager @Inject constructor() {

    private val _refreshEvent = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val refreshEvent: SharedFlow<Unit> = _refreshEvent.asSharedFlow()

    fun triggerRefresh() {
        val result = _refreshEvent.tryEmit(Unit)
        Timber.d("🔄 SearchRefreshManager: triggerRefresh() called, tryEmit result = $result")
        Timber.d("🔄 SearchRefreshManager: subscribers count = ${_refreshEvent.subscriptionCount.value}")
    }
}