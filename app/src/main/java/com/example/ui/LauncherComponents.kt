package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// Theme Colors for GemiLeith OS Launcher (Cosmic Deep Eclipse)
object CosmicTheme {
    val DeepSpaceBlack = Color(0xFF06060C)
    val GlassOverlay = Color(0xAA0B0C16)
    val NeonCyan = Color(0xFF00FFCC)
    val NeonMagenta = Color(0xFFFF007F)
    val NeonYellow = Color(0xFFFFE600)
    val NeonBlue = Color(0xFF0066FF)
    val MutedSlate = Color(0xFF4A5568)
    val AlertGlow = Color(0xFFFF3333)
}

// 1. Clock Widget (Deep Metallic Eclipse Analog & Digital composite)
@Composable
fun DeepEclipseClockWidget(modifier: Modifier = Modifier) {
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    val calendar = remember(currentTimeMillis) {
        java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = currentTimeMillis
        }
    }
    val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
    val minute = calendar.get(java.util.Calendar.MINUTE)
    val second = calendar.get(java.util.Calendar.SECOND)
    val millisecond = calendar.get(java.util.Calendar.MILLISECOND)

    // Smooth second sweep
    val smoothSecond = second + (millisecond / 1000f)

    // Breathing loop for clock backglow
    val infiniteTransition = rememberInfiniteTransition(label = "clockBackglow")
    val backglowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "backglow"
    )

    Box(
        modifier = modifier
            .size(160.dp)
            .drawBehind {
                // Background radial glow representing direct holographic laser backlights
                drawCircle(
                    color = CosmicTheme.NeonCyan.copy(alpha = 0.15f * backglowScale),
                    radius = size.minDimension / 1.8f
                )
            }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF141A35), CosmicTheme.DeepSpaceBlack),
                    radius = 240f
                ),
                shape = CircleShape
            )
            .border(2.dp, CosmicTheme.NeonCyan.copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Clock dial markup
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2

            // Hour dial ticks lines
            for (i in 0 until 12) {
                val angle = i * 30 * (Math.PI / 180)
                val startX = center.x + (radius - (8.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 8f)) * cos(angle).toFloat()
                val startY = center.y + (radius - (8.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 8f)) * sin(angle).toFloat()
                val endX = center.x + radius * cos(angle).toFloat()
                val endY = center.y + radius * sin(angle).toFloat()
                drawLine(
                    color = if (i % 3 == 0) CosmicTheme.NeonCyan else CosmicTheme.MutedSlate,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (i % 3 == 0) 2.5f else 1.5f
                )
            }

            // Analog clock hands:
            // Hours
            val hourAngle = (hour % 12 + minute / 60f) * 30 * (Math.PI / 180) - Math.PI / 2
            val hourLen = radius * 0.5f
            drawLine(
                color = CosmicTheme.NeonMagenta,
                start = center,
                end = Offset(center.x + hourLen * cos(hourAngle).toFloat(), center.y + hourLen * sin(hourAngle).toFloat()),
                strokeWidth = 4f
            )

            // Minutes
            val minAngle = (minute + second / 60f) * 6 * (Math.PI / 180) - Math.PI / 2
            val minLen = radius * 0.75f
            drawLine(
                color = CosmicTheme.NeonCyan,
                start = center,
                end = Offset(center.x + minLen * cos(minAngle).toFloat(), center.y + minLen * sin(minAngle).toFloat()),
                strokeWidth = 3f
            )

            // Smooth sweeping Seconds (Sweeper Hand)
            val secAngle = (smoothSecond) * 6 * (Math.PI / 180) - Math.PI / 2
            val secLen = radius * 0.85f
            drawLine(
                color = CosmicTheme.NeonYellow,
                start = center,
                end = Offset(center.x + secLen * cos(secAngle).toFloat(), center.y + secLen * sin(secAngle).toFloat()),
                strokeWidth = 1.5f
            )

            // Central core cap
            drawCircle(color = CosmicTheme.DeepSpaceBlack, radius = (5.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 5f))
            drawCircle(color = CosmicTheme.NeonYellow, radius = (2.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 2f))
        }

        // Digital fallback HUD text at bottom
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val timeString = remember(currentTimeMillis) {
                val sdf = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).apply {
                    timeZone = java.util.TimeZone.getTimeZone("UTC")
                }
                sdf.format(java.util.Date(currentTimeMillis))
            }
            Text(
                text = timeString,
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(CosmicTheme.NeonYellow, blurRadius = 4f)
                )
            )
            Text(
                text = "UTC 2026",
                color = CosmicTheme.NeonCyan.copy(alpha = 0.8f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

// 2. RAM Memory Crescent Widget (Concentric system meters)
@Composable
fun RamCrescentWidget(modifier: Modifier = Modifier) {
    val runtime = Runtime.getRuntime()
    var usedMemBytes by remember { mutableStateOf(runtime.totalMemory() - runtime.freeMemory()) }
    val maxMemBytes = runtime.maxMemory()

    LaunchedEffect(Unit) {
        while (true) {
            usedMemBytes = runtime.totalMemory() - runtime.freeMemory()
            delay(1500)
        }
    }

    val usedMB = usedMemBytes / (1024 * 1024)
    val maxMB = if (maxMemBytes > 0) maxMemBytes / (1024 * 1024) else 1L
    val safeMaxMB = if (maxMB > 0) maxMB else 1L
    val ratio = (usedMB.toFloat() / safeMaxMB.toFloat()).coerceIn(0f, 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "ramIndicator")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseArc"
    )

    Box(
        modifier = modifier
            .size(160.dp)
            .background(CosmicTheme.DeepSpaceBlack.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
            .border(1.5.dp, CosmicTheme.NeonMagenta.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = (10.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 10f)
            val canvasSize = maxOf(0f, size.minDimension - strokeWidth)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Draw base crescent arc background
            drawArc(
                color = CosmicTheme.MutedSlate.copy(alpha = 0.2f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                size = androidx.compose.ui.geometry.Size(canvasSize, canvasSize),
                topLeft = topLeft,
                style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )

            // Draw usage indicator arc with colorful neon glow
            drawArc(
                brush = Brush.horizontalGradient(
                    colors = listOf(CosmicTheme.NeonMagenta, CosmicTheme.NeonYellow)
                ),
                startAngle = 135f,
                sweepAngle = 270f * ratio,
                useCenter = false,
                size = androidx.compose.ui.geometry.Size(canvasSize, canvasSize),
                topLeft = topLeft,
                style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-4).dp)
        ) {
            Text(
                text = "${(ratio * 100).toInt()}%",
                color = CosmicTheme.NeonMagenta,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                style = TextStyle(shadow = Shadow(CosmicTheme.NeonMagenta, blurRadius = 8f))
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "CORTEX RAM",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$usedMB / $maxMB MB",
                color = CosmicTheme.MutedSlate,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Live JNI indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .drawBehind { 
                    drawRoundRect(
                        color = CosmicTheme.NeonMagenta.copy(alpha = 0.15f * pulseAlpha), 
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius((4.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 4f))
                    )
                }
                .border(0.5.dp, CosmicTheme.NeonMagenta, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "120 FPS // VULKAN",
                color = CosmicTheme.NeonMagenta,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// 3. Battery electromagnetic flow widget (with dynamic pulse animation on tap)
@Composable
fun BatteryElectromagneticWidget(modifier: Modifier = Modifier) {
    var isChargingState by remember { mutableStateOf(true) }
    var sparkSignalTrigger by remember { mutableStateOf(0) }

    val sparkOffsetAnim = remember { Animatable(0f) }

    LaunchedEffect(sparkSignalTrigger) {
        if (sparkSignalTrigger > 0) {
            sparkOffsetAnim.snapTo(0f)
            sparkOffsetAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(1000, easing = FastOutLinearInEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .size(160.dp)
            .background(CosmicTheme.DeepSpaceBlack.copy(alpha = 0.9f), RoundedCornerShape(24.dp))
            .border(1.5.dp, CosmicTheme.NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable {
                isChargingState = !isChargingState
                sparkSignalTrigger++
            }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "قوة التدفق الكوني",
                color = CosmicTheme.NeonCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Dynamic interactive vertical Battery Container
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(68.dp)
                    .border(2.dp, CosmicTheme.NeonCyan, RoundedCornerShape(6.dp))
                    .padding(3.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Wave energy fill indicator
                val pulseRatio = if (isChargingState) 0.85f else 0.45f
                val colorGradient = if (isChargingState) {
                    listOf(CosmicTheme.NeonCyan, CosmicTheme.NeonYellow)
                } else {
                    listOf(CosmicTheme.NeonMagenta, CosmicTheme.AlertGlow)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(pulseRatio)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            Brush.verticalGradient(colorGradient)
                        )
                )

                // The Fracture Spark animation overlay
                if (sparkOffsetAnim.value > 0f && sparkOffsetAnim.value < 1f) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val flowY = size.height * (1f - sparkOffsetAnim.value)
                        drawCircle(
                            color = Color.White,
                            radius = (6.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 6f),
                            center = Offset(size.width / 2, flowY)
                        )
                        drawCircle(
                            color = CosmicTheme.NeonYellow.copy(alpha = 0.5f),
                            radius = (12.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 12f),
                            center = Offset(size.width / 2, flowY)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(if (isChargingState) CosmicTheme.NeonCyan else CosmicTheme.AlertGlow, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isChargingState) "تنبيه: تدفق نشط" else "الوضعية المستقرة",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// 4. Custom forged icons with custom dynamic filter styles (Neon Eclipse, Cyberpunk 3D, Minimal, Oil Painting)
@Composable
fun ForgedIcon(
    label: String,
    style: String,
    modifier: Modifier = Modifier,
    cinematicPhase: Int = 2
) {
    val displayName = if (label.length > 8) label.substring(0, 7) + ".." else label
    val dynamicFontSize = if (displayName.length > 5) 12.sp else 16.sp
    val dynamicLineHeight = if (displayName.length > 5) 12.sp else 16.sp

    val infiniteTransition = rememberInfiniteTransition(label = "forgerGlow")
    val hoverGlow by infiniteTransition.animateFloat(
        initialValue = if (cinematicPhase == 1) 0.1f else 0.6f,
        targetValue = if (cinematicPhase == 1) 0.3f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (cinematicPhase == 1) 5000 else 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hoverGlow"
    )

    val lockModifier = if (cinematicPhase == 1) {
        Modifier.graphicsLayer(alpha = 0.7f)
    } else Modifier

    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .then(lockModifier),
        contentAlignment = Alignment.Center
    ) {
        when (style) {
            "NEON_ECLIPSE" -> {
                // Glow bloom shadow, totally blackened core eclipse center with a bright neon cyan outline ring
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = CosmicTheme.NeonCyan.copy(alpha = 0.4f * hoverGlow),
                                radius = size.minDimension / 1.6f
                            )
                        }
                        .background(Color.Black, CircleShape)
                        .border(2.5.dp, CosmicTheme.NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = CosmicTheme.NeonCyan,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        style = TextStyle(
                            shadow = Shadow(CosmicTheme.NeonCyan, blurRadius = 6f)
                        )
                    )
                }
            }
            "CYBERPUNK_3D" -> {
                // Two layered sub-pixel offset dual shadows reproducing high-tension spatial chromatic displacement
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Magenta back outline slightly displaced to bottom-right
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(3.dp, 3.dp)
                            .background(Color.Transparent, CircleShape)
                            .border(2.dp, CosmicTheme.NeonMagenta, CircleShape)
                    )
                    // Cyan front outline displaced to top-left
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset((-3).dp, (-3).dp)
                            .background(Color(0xFF0C101F), CircleShape)
                            .border(2.dp, CosmicTheme.NeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName,
                            color = Color.White,
                            fontSize = dynamicFontSize,
                            lineHeight = dynamicLineHeight,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            style = TextStyle(
                                shadow = Shadow(CosmicTheme.NeonMagenta, blurRadius = 4f, offset = Offset(1f, 1f))
                            )
                        )
                    }
                }
            }
            "VECTOR_MINIMAL" -> {
                // Highly refined white path vectors inside absolute thin elegant geometric boundaries
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF171923), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .border(1.dp, CosmicTheme.NeonYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color.White,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            "OIL_PAINTING" -> {
                // Highly contrast warm textured backgrounds imitating traditional tactile oils
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFF5E36), Color(0xFFFFAE33), Color(0xFF900C3F))
                            ),
                            shape = CircleShape
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color.White,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            shadow = Shadow(Color.Black.copy(alpha = 0.5f), offset = Offset(1f, 2f), blurRadius = 4f)
                        )
                    )
                }
            }
            "SPECTRA_GLASSMOUR" -> {
                // Translucent acrylic glassmorphism with radial neon spectrum overlay and silver contour edge
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0x33FFFFFF), Color(0x11000000))
                            ),
                            shape = CircleShape
                        )
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(CosmicTheme.NeonMagenta, CosmicTheme.NeonCyan, Color.Transparent),
                                    radius = maxOf(1f, size.minDimension * 0.7f)
                                ),
                                alpha = 0.5f * hoverGlow
                            )
                        }
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color.White,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        style = TextStyle(
                            shadow = Shadow(CosmicTheme.NeonCyan, blurRadius = 10f)
                        )
                    )
                }
            }
            "SHATTERED_BLUE" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                color = Color(0xFF000044).copy(alpha = 0.8f * hoverGlow),
                                radius = size.minDimension / 1.5f
                            )
                        }
                        .background(Color(0xFF01061A), CircleShape)
                        .border(1.dp, Color(0xFF2255FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color(0xFF3388FF),
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Cursive,
                        style = TextStyle(
                            shadow = Shadow(Color(0xFF001144), blurRadius = 8f)
                        )
                    )
                }
            }
            "NEBULA_BURST" -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF0055).copy(alpha = 0.6f * hoverGlow),
                                        Color(0xFF8800FF).copy(alpha = 0.4f * hoverGlow),
                                        Color(0xFFFFB300).copy(alpha = 0.2f * hoverGlow),
                                        Color.Transparent
                                    )
                                ),
                                radius = size.minDimension * 0.9f
                            )
                        }
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color.White,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        style = TextStyle(
                            shadow = Shadow(Color(0xFF8800FF), blurRadius = 4f)
                        )
                    )
                }
            }
            "MATRIX_GREEN" -> {
                // High contrast arcade green terminal with horizontal scan lines
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF030A05), CircleShape)
                        .border(2.dp, Color(0xFF00FF33), CircleShape)
                        .drawBehind {
                            // Render virtual scanlines
                            val rawSpacing = 4.dp.toPx()
                            val scanSpacing = if (rawSpacing.isNaN() || !rawSpacing.isFinite() || rawSpacing <= 10f) 10f else rawSpacing
                            val maxH = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                            val maxW = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                            var y = 0f
                            while (y < maxH) {
                                drawLine(
                                    color = Color(0x3300FF33),
                                    start = Offset(0f, y),
                                    end = Offset(maxW, y),
                                    strokeWidth = 1f
                                )
                                y += scanSpacing
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color(0xFF00FF33),
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        style = TextStyle(
                            shadow = Shadow(Color(0xFF00FF33), blurRadius = 8f)
                        )
                    )
                }
            }
            "HOLOGRAM_ARCADE" -> {
                // Projected blue cyber holographic look with dynamic radial concentric waveforms
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0B122C), CircleShape)
                        .border(2.dp, CosmicTheme.NeonBlue, CircleShape)
                        .drawBehind {
                            drawCircle(
                                color = CosmicTheme.NeonCyan.copy(alpha = 0.25f),
                                radius = (size.minDimension / 2.3f) * hoverGlow,
                                style = Stroke(width = (1.5.dp.toPx().takeIf { !it.isNaN() && it.isFinite() } ?: 1.5f))
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = CosmicTheme.NeonCyan,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        style = TextStyle(
                            shadow = Shadow(CosmicTheme.NeonBlue, blurRadius = 6f)
                        )
                    )
                }
            }
            "SACRED_GOLD" -> {
                // Sacred geometric look with amber outer core and golden star shape base
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF3A2906), Color(0xFF0C0801))
                            ),
                            shape = CircleShape
                        )
                        .border(2.dp, Color(0xFFFFD700), CircleShape)
                        .drawBehind {
                            val radius = size.minDimension / 2
                            drawLine(Color(0x55FFD700), Offset(0f, radius), Offset(size.width, radius), 1f)
                            drawLine(Color(0x55FFD700), Offset(radius, 0f), Offset(radius, size.height), 1f)
                            drawCircle(
                                color = Color(0x33FFD700),
                                radius = radius * 0.7f,
                                style = Stroke(width = 1f)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color(0xFFFFD700),
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            shadow = Shadow(Color(0xFFFFB300), blurRadius = 8f)
                        )
                    )
                }
            }
            "COMIC_POP" -> {
                // Bubblegum comic styled design with extreme pop art gradients and thick borders
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(CosmicTheme.NeonMagenta, CosmicTheme.NeonYellow)
                            ),
                            shape = CircleShape
                        )
                        .border(3.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color.Black,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
            else -> {
                // Default Standard Elegant Neon System representation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF101223), CircleShape)
                        .border(1.5.dp, CosmicTheme.NeonBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName,
                        color = Color.White,
                        fontSize = dynamicFontSize,
                        lineHeight = dynamicLineHeight,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
