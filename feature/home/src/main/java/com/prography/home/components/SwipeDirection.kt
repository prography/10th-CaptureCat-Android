package com.prography.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// 스와이프 방향 열거형
enum class SwipeDirection {
    LEFT, RIGHT, NONE
}

/**
 * 스와이프 가능한 카드 컴포넌트
 *
 * @param modifier 기본 모디파이어
 * @param onSwiped 스와이프 완료 시 호출될 콜백, 방향 정보 포함
 * @param swipeThreshold 스와이프 인식 임계값 (0.0 ~ 1.0)
 * @param content 카드 내용 컴포저블
 */
@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwiped: (SwipeDirection) -> Unit,
    swipeThreshold: Float = 0.3f,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    // 카드의 X 위치와 회전 애니메이션
    val offsetX = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    
    // 카드 너비를 기록
    var cardWidth by remember { mutableStateOf(0f) }
    
    // 스와이프 상태 관리
    var isSwiping by remember { mutableStateOf(false) }
    
    // 스와이프 애니메이션 사양
    val animationSpec: AnimationSpec<Float> = SpringSpec(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    // 스와이프 액션 함수
    fun swipeCard(direction: SwipeDirection) {
        val targetX = when (direction) {
            SwipeDirection.LEFT -> -cardWidth * 1.5f
            SwipeDirection.RIGHT -> cardWidth * 1.5f
            SwipeDirection.NONE -> 0f
        }
        
        coroutineScope.launch {
            // 스와이프 애니메이션 시작
            offsetX.animateTo(
                targetValue = targetX,
                animationSpec = tween(300)
            )
            
            // 스와이프 방향이 NONE이 아니면 콜백 호출
            if (direction != SwipeDirection.NONE) {
                onSwiped(direction)
            }
        }
    }
    
    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
            .rotate(rotation.value)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isSwiping = true },
                    onDragEnd = {
                        isSwiping = false
                        
                        // 스와이프 임계치를 넘었는지 확인
                        val offsetRatio = offsetX.value / cardWidth
                        
                        // 스와이프 방향 결정
                        val direction = when {
                            offsetRatio <= -swipeThreshold -> SwipeDirection.LEFT
                            offsetRatio >= swipeThreshold -> SwipeDirection.RIGHT
                            else -> SwipeDirection.NONE
                        }
                        
                        swipeCard(direction)
                    },
                    onDragCancel = {
                        isSwiping = false
                        // 원래 위치로 복귀
                        coroutineScope.launch {
                            offsetX.animateTo(0f, animationSpec)
                            rotation.animateTo(0f, animationSpec)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        
                        // X 방향으로 드래그
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            
                            // 회전 효과 (X 위치에 비례)
                            val rotationFactor = (offsetX.value / cardWidth) * 10 // 최대 10도 회전
                            rotation.snapTo(rotationFactor)
                        }
                    }
                )
            }
            .onGloballyPositioned { coordinates ->
                cardWidth = coordinates.size.width.toFloat()
            }
            .graphicsLayer {
                // 카드가 스와이프될 때 크기 변경 금지
                if (!isSwiping) {
                    scaleX = 1f
                    scaleY = 1f
                }
            }
    ) {
        content()
    }
}

/**
 * 프로그래밍 방식으로 왼쪽으로 스와이프 동작 실행
 */
fun performSwipeLeft(
    cardWidth: Float,
    offsetX: Animatable<Float, *>,
    onSwiped: (SwipeDirection) -> Unit,
    scope: kotlinx.coroutines.CoroutineScope
) {
    scope.launch {
        offsetX.animateTo(
            targetValue = -cardWidth * 1.5f,
            animationSpec = tween(300)
        )
        onSwiped(SwipeDirection.LEFT)
    }
}

/**
 * 프로그래밍 방식으로 오른쪽으로 스와이프 동작 실행
 */
fun performSwipeRight(
    cardWidth: Float,
    offsetX: Animatable<Float, *>,
    onSwiped: (SwipeDirection) -> Unit,
    scope: kotlinx.coroutines.CoroutineScope
) {
    scope.launch {
        offsetX.animateTo(
            targetValue = cardWidth * 1.5f,
            animationSpec = tween(300)
        )
        onSwiped(SwipeDirection.RIGHT)
    }
}