package com.prography.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prography.ui.R
import com.prography.ui.theme.PrographyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 배경이 항상 흰색이므로 상태바 아이콘을 검정색으로 설정
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = android.graphics.Color.WHITE,
                darkScrim = android.graphics.Color.WHITE
            )
        )

        setContent {
            PrographyTheme {
                SplashScreen(
                    onNavigateToMain = {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    fun SplashScreen(
        onNavigateToMain: () -> Unit
    ) {

        val alpha = remember {
            Animatable(0f)
        }
        LaunchedEffect(key1 = Unit) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(500)
            )
            delay(1000L)
            onNavigateToMain()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = com.prography.ui.R.drawable.ic_splash_logo),
                contentDescription = "캡처캣 로고",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (LocalConfiguration.current.screenHeightDp * 0.4f).dp)
                    .alpha(alpha.value),
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SplashScreenPreview() {
        PrographyTheme {
            SplashScreen(
                onNavigateToMain = {}
            )
        }
    }
}