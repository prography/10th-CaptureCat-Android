package com.prography.presentation

import AppNavGraph
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import com.prography.ui.theme.PrographyTheme
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.prography.ui.common.GlobalUiHandler
import android.os.Handler
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.prography.navigation.NavigationHelper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var navigationHelper: NavigationHelper

    var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // 스플래시 화면 설치 (setContent 전에 호출해야 함)
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2초 후에 스플래시 화면 종료
        splashScreen.setKeepOnScreenCondition { !isReady }

        isReady = true
        
        setContent {
            PrographyTheme {
                AppNavGraph(navigationHelper = navigationHelper)
                GlobalUiHandler()
            }
        }
    }
}
