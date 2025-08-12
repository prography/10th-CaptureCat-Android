package com.prography.home.ui.mypage.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.home.ui.mypage.contract.SettingEffect
import com.prography.home.ui.mypage.viewmodel.SettingViewModel
import androidx.core.net.toUri

@Composable
fun SettingsScreen(
    viewModel: SettingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect
    val context = LocalContext.current

    // Handle effects
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                is SettingEffect.OpenExternalLink -> {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, effect.url.toUri())
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // 브라우저가 없거나 URL이 잘못된 경우 처리
                    }
                }
            }
        }
    }

    SettingContent(
        state = state,
        onAction = viewModel::sendAction
    )
}