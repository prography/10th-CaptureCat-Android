package com.prography.setting.route

import androidx.compose.runtime.Composable
import com.prography.navigation.NavigationHelper
import com.prography.setting.ui.notice.NoticeScreen

@Composable
fun NoticeRoute(
    navigationHelper: NavigationHelper
) {
    NoticeScreen(navigationHelper = navigationHelper)
}