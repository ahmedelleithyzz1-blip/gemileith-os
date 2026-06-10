package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Shadow

@Composable
fun CosmicQuickSettingsPanel(
    isVisible: Boolean,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount.y < -20f) {
                                onClose()
                            }
                        }
                    )
                }
        ) {
            // Background blur / holographic overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xBB000000))
                    .graphicsLayer { alpha = 0.95f } // In a real app we'd use blur modifier (Requires Android 12+)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "qspanel_grid")
                val gridOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 60f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "gridOffset"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw a subtle animated grid or matrix rain to give it the GemiLeith signature
                    val step = 60f
                    var x = gridOffset
                    while (x < size.width) {
                        drawLine(
                            color = CosmicTheme.NeonCyan.copy(alpha = 0.08f),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1.5f
                        )
                        x += step
                    }
                    var y = gridOffset
                    while (y < size.height) {
                        drawLine(
                            color = CosmicTheme.NeonCyan.copy(alpha = 0.08f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.5f
                        )
                        y += step
                    }
                }
            }

            // Quick Settings Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F172A).copy(alpha = 0.9f),
                                Color(0xFF000000).copy(alpha = 0.95f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = CosmicTheme.NeonCyan.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(top = 48.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
            ) {
                // Header / Clock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "10:30",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(shadow = Shadow(CosmicTheme.NeonCyan, blurRadius = 15f))
                    )
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallStatusIndicator(color = CosmicTheme.NeonMagenta)
                        SmallStatusIndicator(color = CosmicTheme.NeonYellow)
                        SmallStatusIndicator(color = CosmicTheme.NeonCyan)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // Tiles Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickSettingTile(title = "Wi-Fi", iconLabel = "WIFI", initialActive = true, accentColor = CosmicTheme.NeonCyan)
                    QuickSettingTile(title = "Bluetooth", iconLabel = "BT", initialActive = false, accentColor = CosmicTheme.NeonBlue)
                    QuickSettingTile(title = "Mobile Data", iconLabel = "DATA", initialActive = true, accentColor = CosmicTheme.NeonMagenta)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickSettingTile(title = "Location", iconLabel = "GPS", initialActive = false, accentColor = CosmicTheme.NeonYellow)
                    QuickSettingTile(title = "Auto-Rotate", iconLabel = "ROT", initialActive = true, accentColor = CosmicTheme.NeonCyan)
                    QuickSettingTile(title = "Gaming Mode", iconLabel = "GAME", initialActive = false, accentColor = Color(0xFFFF3300))
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Brightness Slider (Cosmic style)
                CosmicBrightnessSlider()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun SmallStatusIndicator(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "indicator")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800 + (color.hashCode() % 500), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color.copy(alpha = alpha), CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.5f * alpha), CircleShape)
            .graphicsLayer {
                shadowElevation = 8f * alpha
                shape = CircleShape
            }
    )
}

@Composable
fun QuickSettingTile(title: String, iconLabel: String, initialActive: Boolean, accentColor: Color) {
    var isActive by remember { mutableStateOf(initialActive) }
    
    // Global breathing state
    val globalBreathing by androidx.compose.animation.core.animateFloatAsState(
        targetValue = (kotlin.math.sin(System.currentTimeMillis() / 1500.0) * 0.5 + 0.5).toFloat(),
        animationSpec = androidx.compose.animation.core.tween(500, easing = LinearEasing),
        label = "globalBreathing"
    )

    val pulse by rememberInfiniteTransition(label = "tilePulse").animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilePulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { isActive = !isActive }
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    if (isActive) accentColor.copy(alpha = 0.15f + 0.1f * globalBreathing) else Color(0x33000000),
                    RoundedCornerShape(24.dp)
                )
                .border(
                    width = if (isActive) 2.dp else 1.dp,
                    color = if (isActive) accentColor.copy(alpha = 0.5f + 0.5f * pulse) else Color(0x66FFFFFF),
                    shape = RoundedCornerShape(24.dp)
                )
                .graphicsLayer {
                    shadowElevation = if (isActive) 12f * pulse else 0f
                    shape = RoundedCornerShape(24.dp)
                    clip = true
                },
            contentAlignment = Alignment.Center
        ) {
            // Glassmorphic Frost overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.05f)
                        )
                    )
                )
            }

            // Internal Glow
            if (isActive) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = accentColor.copy(alpha = 0.3f * pulse * (0.8f + 0.2f * globalBreathing)),
                        radius = size.minDimension / 1.2f
                    )
                }
            }
            
            // Icon Placeholder
            Text(
                text = iconLabel,
                color = if (isActive) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 16.sp,
                style = TextStyle(
                    shadow = if (isActive) Shadow(accentColor, blurRadius = 15f * pulse * (0.5f + 0.5f * globalBreathing)) else null
                )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            color = if (isActive) Color.White else Color.Gray,
            fontSize = 12.sp,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun CosmicBrightnessSlider() {
    var progress by remember { mutableStateOf(0.7f) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("☀️", fontSize = 16.sp) // Just a tiny placeholder, could be a real icon
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        progress = (progress + dragAmount.x / size.width).coerceIn(0f, 1f)
                    }
                }
        ) {
            // Background track
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = Color(0xFF0F172A),
                    size = size,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                )
                
                // Filled track
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(CosmicTheme.NeonCyan, CosmicTheme.NeonMagenta)
                    ),
                    size = Size(width = size.width * progress, height = size.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                )
                
                // Weathered mechanical details on top
                for (i in 1..20) {
                    val xPos = (size.width / 20) * i
                    drawLine(
                        color = Color.Black.copy(alpha = 0.3f),
                        start = Offset(xPos, 0f),
                        end = Offset(xPos, size.height),
                        strokeWidth = 2f
                    )
                }
            }
        }
    }
}
