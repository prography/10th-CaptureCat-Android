package com.prography.setting.ui

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.prography.setting.contract.SettingEffect
import com.prography.setting.viewmodel.SettingViewModel

@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToStorage: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val effectFlow = viewModel.effect

    // Handle effects
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {
                SettingEffect.NavigateUp -> onNavigateUp()
                SettingEffect.NavigateToLogin -> onNavigateToLogin()
                SettingEffect.NavigateToWithdraw -> onNavigateToWithdraw()
                SettingEffect.ShowLogoutSuccess -> onNavigateToStorage()
                SettingEffect.ShowWithdrawSuccess -> {
                    // 회원 탈퇴 완료 시 effect
                }
            }
        }
    }

    SettingContent(
        state = state,
        onAction = viewModel::sendAction
    )
}