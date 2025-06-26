package com.prography.ui.common

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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

// 글로벌 UI 상태 컴포넌트
@Composable
fun GlobalUiHandler() {
    var isLoading by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // 글로벌 이벤트 구독
    LaunchedEffect(Unit) {
        GlobalUiManager.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowLoading -> isLoading = true
                is UiEvent.HideLoading -> isLoading = false
                is UiEvent.ShowToast -> toastMessage = event.message
            }
        }
    }

    // 토스트 메시지 처리
    toastMessage?.let { message ->
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
        toastMessage = null
    }

    // 로딩 오버레이 (터치 이벤트 차단)
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .pointerInput(Unit) {
                    // 모든 터치 이벤트를 소비하여 하위 컴포넌트로 전달되지 않도록 함
                },
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
