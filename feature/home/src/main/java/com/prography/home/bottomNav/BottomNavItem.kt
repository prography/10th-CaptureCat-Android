package com.prography.home.bottomNav

import com.prography.ui.R


// 네비게이션 아이템 정의
sealed class BottomNavItem(
    val route: String,
    val title: Int,
    val icon: Int
) {
    object Storage : BottomNavItem("storage", R.string.title_storage, R.drawable.ic_tapbar_main)
    object Home : BottomNavItem("home", R.string.title_home, R.drawable.ic_tapbar_main)
    object Folder : BottomNavItem("folder", R.string.title_folder, R.drawable.ic_tapbar_main)
    object Search : BottomNavItem("search", R.string.title_search, R.drawable.ic_tapbar_main)
}