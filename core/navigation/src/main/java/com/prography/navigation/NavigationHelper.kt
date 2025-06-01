package com.prography.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationHelper @Inject constructor() {
    private val _navigationFlow = Channel<NavigationEvent>(Channel.BUFFERED)
    val navigationFlow = _navigationFlow.receiveAsFlow()

    fun navigate(event: NavigationEvent) {
        _navigationFlow.trySend(event)
    }
}
