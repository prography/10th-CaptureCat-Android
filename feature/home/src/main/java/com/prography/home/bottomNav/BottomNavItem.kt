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
    object Storage : BottomNavItem("favorite", R.string.tab_favorite, R.drawable.ic_tab_favorite_checked, R.drawable.ic_tab_favorite_unchecked)
    object Home : BottomNavItem("home", R.string.tab_home, R.drawable.ic_home_selected, R.drawable.ic_home_unselected)
    object Search : BottomNavItem("search", R.string.tab_search, R.drawable.ic_tab_search_selected, R.drawable.ic_tab_search_unselected)
}