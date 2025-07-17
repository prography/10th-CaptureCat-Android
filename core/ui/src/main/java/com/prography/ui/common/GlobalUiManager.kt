package com.prography.ui.common

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.prography.ui.theme.Error
import com.prography.ui.theme.OverlayDim
import com.prography.ui.theme.subhead02Bold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// 글로벌 UI 상태 관리를 위한 싱글톤 객체
object GlobalUiManager {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun sendEvent(event: UiEvent) {
        CoroutineScope(Dispatchers.Main).launch {
            _uiEvent.emit(event)
        }
    }
}

@Composable
fun GlobalUiHandler() {
    var isLoading by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.Error) }
    var showToast by remember { mutableStateOf(false) }

    // 이벤트 수신
    LaunchedEffect(Unit) {
        GlobalUiManager.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowLoading -> isLoading = true
                is UiEvent.HideLoading -> isLoading = false
                is UiEvent.ShowToast -> {
                    toastMessage = event.message
                    toastType = event.type
                    showToast = true
                }
            }
        }
    }

    // ✅ 2초 후 자동 숨김 처리
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            showToast = true
            kotlinx.coroutines.delay(2000)
            showToast = false
            kotlinx.coroutines.delay(300) // 애니메이션 끝나고 메시지 제거
            toastMessage = null
        }
    }

    AnimatedVisibility(
        visible = showToast,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        toastMessage?.let { message ->
            Box(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 26.dp)
                        .background(OverlayDim, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = message,
                        style = subhead02Bold,
                        color = when (toastType) {
                            ToastType.Default -> Color.White
                            ToastType.Error -> Error
                        }
                    )
                }
            }
        }
    }


    // ✅ 로딩 오버레이
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .pointerInput(Unit) {},
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
