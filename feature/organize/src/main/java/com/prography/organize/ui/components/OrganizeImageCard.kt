package com.prography.organize.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.prography.organize.model.OrganizeScreenshotItem
import kotlinx.coroutines.delay

@Composable
fun OrganizeImageCard(
    screenshot: OrganizeScreenshotItem,
    onFavoriteToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val deleteThreshold = -200f

    // 삭제 애니메이션
    val deleteScale by animateFloatAsState(
        targetValue = if (isDeleting) 0.8f else 1f,
        animationSpec = tween(300),
        label = "deleteScale"
    )

    val deleteAlpha by animateFloatAsState(
        targetValue = if (isDeleting) 0f else 1f,
        animationSpec = tween(300),
        label = "deleteAlpha"
    )

    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(300) // 애니메이션 완료 대기
            onDelete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .graphicsLayer {
                    translationY = offsetY
                    alpha = deleteAlpha
                    scaleX = deleteScale
                    scaleY = deleteScale
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            if (offsetY < deleteThreshold && !isDeleting) {
                                isDeleting = true
                            } else {
                                offsetY = 0f
                            }
                            isDragging = false
                        }
                    ) { _, dragAmount ->
                        if (!isDeleting) {
                            offsetY += dragAmount.y
                            // 아래로 드래그는 제한
                            if (offsetY > 0) offsetY = 0f
                        }
                    }
                },
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(screenshot.uri),
                    contentDescription = "스크린샷",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 삭제 표시 gradient 오버레이
                if (isDragging && offsetY < deleteThreshold) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.7f),
                                        Color.Black.copy(alpha = 0.9f)
                                    ),
                                    startY = 0f,
                                    endY = 600f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 쓰레기통 아이콘 배경
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.8f),
                                        RoundedCornerShape(40.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = com.prography.ui.R.drawable.ic_organize_delete),
                                    contentDescription = "삭제",
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "삭제할래요",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Default
                                )
                            )
                        }
                    }
                }
            }
        }

        // 드래그 힌트 (위로 스와이프 표시)
        if (!isDragging && !isDeleting) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-16).dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                Color.Gray.copy(alpha = 0.5f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
        }
    }
}
