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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.prography.organize.R
import com.prography.organize.model.OrganizeScreenshotItem
import kotlinx.coroutines.delay

@Composable
fun OrganizeImageCard(
    screenshot: OrganizeScreenshotItem,
    isCurrentPage: Boolean = true,
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

    // 페이지 전환 애니메이션
    val pageScale by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.85f,
        animationSpec = tween(300),
        label = "pageScale"
    )

    // 삭제 그라데이션 애니메이션 (드래그 진행도에 따라)
    val deleteProgress = if (isDragging) {
        ((-offsetY) / (-deleteThreshold)).coerceIn(0f, 1f)
    } else {
        0f
    }

    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(300) // 애니메이션 완료 대기
            onDelete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RectangleShape)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f)
                .offset(y = 42.dp)
                .graphicsLayer {
                    translationY = offsetY
                    alpha = deleteAlpha * if (isCurrentPage) 1f else 0.6f
                    scaleX = pageScale * deleteScale
                    scaleY = pageScale * deleteScale
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
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .blur(
                        radius = if (isCurrentPage) 0.dp else 4.dp
                    )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(screenshot.uri),
                    contentDescription = "스크린샷",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 삭제 표시 gradient 오버레이
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.05f * deleteProgress),
                                    Color.Black.copy(alpha = 0.1f * deleteProgress),
                                    Color.Black.copy(alpha = 0.2f * deleteProgress),
                                    Color.Black.copy(alpha = 0.35f * deleteProgress),
                                    Color.Black.copy(alpha = 0.5f * deleteProgress),
                                    Color.Black.copy(alpha = 0.7f * deleteProgress)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .alpha(deleteProgress)
                    ) {
                        Icon(
                            painter = painterResource(id = com.prography.ui.R.drawable.ic_organize_delete),
                            contentDescription = "삭제",
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "삭제할래요",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 20.sp,
                                lineHeight = 28.sp,
                                fontFamily = FontFamily(Font(com.prography.ui.R.font.pretendard_semibold))
                            )
                        )
                    }
                }
            }
        }
    }
}
