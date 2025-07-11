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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import com.prography.navigation.NavigationHelper
import com.prography.navigation.AppRoute
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var navigationHelper: NavigationHelper
    var isReady = false

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.initChecking()

        setContent {
            PrographyTheme {
                val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
