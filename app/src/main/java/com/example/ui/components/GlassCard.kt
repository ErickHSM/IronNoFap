package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkOutline

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color(0xD9171B22), // Frosted semi-translucent dark slate
    borderStrokeWidth: Dp = 1.dp,
    borderColor: Color? = null,
    glowColor: Color = Color(0x3300E676),
    content: @Composable BoxScope.() -> Unit
) {
    val borderBrush = if (borderColor != null) {
        Brush.linearGradient(listOf(borderColor, borderColor.copy(alpha = 0.3f)))
    } else {
        Brush.linearGradient(
            colors = listOf(
                glowColor,
                Color(0x22FFFFFF),
                Color(0x05000000)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderStrokeWidth, borderBrush, shape),
        content = content
    )
}
