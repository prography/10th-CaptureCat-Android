package com.prography.presentation

import AppNavGraph
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import com.prography.ui.theme.PrographyTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.prography.ui.common.GlobalUiHandler
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prography.navigation.NavigationHelper
import com.prography.navigation.AppRoute
import com.prography.navigation.NavigationEvent
import javax.inject.Inject
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.SystemBarStyle
import android.graphics.Color
import androidx.compose.foundation.background
import com.prography.ui.theme.PureWhite

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var navigationHelper: NavigationHelper

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 배경이 항상 흰색이므로 상태바 아이콘을 검정색으로 설정
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.WHITE,
                darkScrim = Color.WHITE
            )
        )
        viewModel.initChecking()

        setContent {
            PrographyTheme {
                val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
                val shouldNavigateToLogin by viewModel.shouldNavigateToLogin.collectAsStateWithLifecycle()

                LaunchedEffect(shouldNavigateToLogin) {
                    if (shouldNavigateToLogin) {
                        navigationHelper.navigate(NavigationEvent.To(AppRoute.Login))
                        viewModel.onNavigatedToLogin()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PureWhite)
                        .navigationBarsPadding()
                ) {
                    startDestination?.let {
                        AppNavGraph(
                            navigationHelper = navigationHelper,
                            startDestination = it
                        )
                    }
                }
                GlobalUiHandler()
            }
        }
    }
}
