package com.prography.home.bottomNav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.prography.ui.R


// 네비게이션 아이템 정의
sealed class BottomNavItem(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int
) {
    object Storage : BottomNavItem("storage", R.string.title_storage, R.drawable.ic_home_storage_selected, R.drawable.ic_home_storage)
    object Home : BottomNavItem("home", R.string.title_home, R.drawable.ic_home_selected, R.drawable.ic_home)
    object Search : BottomNavItem("search", R.string.title_search, R.drawable.ic_search, R.drawable.ic_search)
}