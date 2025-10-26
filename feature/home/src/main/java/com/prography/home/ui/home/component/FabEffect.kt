package com.prography.home.ui.home.component

import android.graphics.Paint
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.nativeCanvas

// ====== util: CSS 스타일 다중 섀도우 ======
data class ShadowLayer(
    val offsetY: Dp,
    val blur: Dp,
    val color: Color
)

/** Figma/CSS 처럼 여러 box-shadow 레이어를 합성해서 그림 */
fun Modifier.multiShadow(
    layers: List<ShadowLayer>,
    radius: Dp
) = this.drawBehind {
    val r = radius.toPx()
    val w = size.width
    val h = size.height

    drawIntoCanvas { canvas ->
        val paint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.TRANSPARENT // 본체는 투명, 그림자만 그림
        }
        layers.forEach { layer ->
            paint.setShadowLayer(
                /* radius(blur) = */ layer.blur.toPx(),
                /* dx */ 0f,
                /* dy */ layer.offsetY.toPx(),
                /* color */ layer.color.toArgb()
            )
            canvas.nativeCanvas.drawRoundRect(
                0f, 0f, w, h, r, r, paint
            )
            // 다음 레이어를 위해 shadowLayer 초기화
            paint.setShadowLayer(0f, 0f, 0f, 0)
        }
    }
}

// ====== CSS → Compose 매핑한 FAB 섀도우 레이어 ======
val FabShadowLayers = listOf(
    // box-shadow: 0px 1px 3px  0px #0000001A;
    ShadowLayer(offsetY = 1.dp,  blur = 3.dp,  color = Color(0x1A000000)),
    // box-shadow: 0px 6px 6px  0px #00000017;
    ShadowLayer(offsetY = 6.dp,  blur = 6.dp,  color = Color(0x17000000)),
    // box-shadow: 0px 13px 8px 0px #0000000D;
    ShadowLayer(offsetY = 13.dp, blur = 8.dp,  color = Color(0x0D000000)),
    // box-shadow: 0px 24px 10px 0px #00000003;
    ShadowLayer(offsetY = 24.dp, blur = 10.dp, color = Color(0x03000000)),
)