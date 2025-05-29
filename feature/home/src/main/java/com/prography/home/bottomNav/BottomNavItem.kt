package com.prography.home.bottomNav

import com.prography.ui.R


// 네비게이션 아이템 정의
sealed class BottomNavItem(
    val route: String,
    val title: Int,
    val icon: Int
) {
    object Storage : BottomNavItem("storage", R.string.title_storage, R.drawable.ic_tapbar_cards)
    object Home : BottomNavItem("home", R.string.title_home, R.drawable.ic_tapbar_main)
    object Folder : BottomNavItem("random", R.string.title_folder, R.drawable.ic_tapbar_cards)
    object Search : BottomNavItem("random", R.string.title_search, R.drawable.ic_tapbar_cards)
}