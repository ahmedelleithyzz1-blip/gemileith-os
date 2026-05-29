package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LauncherItem
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val pendingTransform by viewModel.pendingTransform.collectAsStateWithLifecycle()
    val selectedItemId by viewModel.selectedItemId.collectAsStateWithLifecycle()
    val currentBackdrop by viewModel.currentBackdrop.collectAsStateWithLifecycle()
    val glowMultiplier by viewModel.glowMultiplier.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "backdropAnimation")
    val backdropAnimValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "backdropAnim"
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showForgerDialog by remember { mutableStateOf(false) }
    var forgerStyleInput by remember { mutableStateOf("NEON_ECLIPSE") }

    var appLabelInput by remember { mutableStateOf("") }
    var showAddAppDialog by remember { mutableStateOf(false) }

    var cinematicPhase by remember { mutableIntStateOf(0) } // 0: AOD, 1: LOCK_SCREEN, 2: HOME

    val activeSelectedItem = items.find { it.id == selectedItemId }
    var tempScale by remember(selectedItemId, activeSelectedItem?.scale) { mutableStateOf((activeSelectedItem?.scale?.takeIf { !it.isNaN() && it.isFinite() } ?: 1.0f).coerceIn(0.5f, 2.0f)) }
    var tempRotation by remember(selectedItemId, activeSelectedItem?.rotation) { mutableStateOf((activeSelectedItem?.rotation?.takeIf { !it.isNaN() && it.isFinite() } ?: 0f).coerceIn(-360f, 360f)) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight(),
                drawerContainerColor = CosmicTheme.DeepSpaceBlack,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                CortexDrawerContent(
                    messages = chatMessages,
                    isLoading = isChatLoading,
                    onSendMessage = { viewModel.sendChatMessage(it) },
                    onSortRequest = { category ->
                        viewModel.sendChatMessage("أريد وضع ترتيب ذكي للتطبيقات الكونية المنضوية تحت فئة $category")
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "GEMILEITH OS",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace,
                            style = TextStyle(
                                shadow = Shadow(CosmicTheme.NeonCyan, blurRadius = 8f)
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "قائمة الذكاء المساعد",
                                tint = CosmicTheme.NeonCyan
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAddAppDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "توليد تطبيق جديد",
                                tint = CosmicTheme.NeonYellow
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = CosmicTheme.DeepSpaceBlack.copy(alpha = 0.95f),
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                // Bottom control HUD panel for Forging Styles & Custom Transformations
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    color = CosmicTheme.DeepSpaceBlack.copy(alpha = 0.95f),
                    border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(CosmicTheme.NeonCyan, CosmicTheme.NeonMagenta)))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (activeSelectedItem != null) {
                            Text(
                                text = "العنصر المختار: ${activeSelectedItem.label}",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Transformer sliders interface (Scale & Rotation)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                    Text(
                                        text = "معدل الحجم الإحداثي (Scale): ${"%.2f".format(tempScale)}",
                                        color = CosmicTheme.NeonCyan,
                                        fontSize = 10.sp
                                    )
                                    Slider(
                                        value = tempScale,
                                        onValueChange = { scale ->
                                            tempScale = scale
                                        },
                                        onValueChangeFinished = {
                                            viewModel.initiateTransform(
                                                item = activeSelectedItem,
                                                originalOldX = activeSelectedItem.xPos,
                                                originalOldY = activeSelectedItem.yPos,
                                                scale = tempScale,
                                                rotation = tempRotation,
                                                newX = activeSelectedItem.xPos,
                                                newY = activeSelectedItem.yPos
                                            )
                                            viewModel.commitPendingTransform()
                                        },
                                        valueRange = 0.5f..2.0f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = CosmicTheme.NeonCyan,
                                            activeTrackColor = CosmicTheme.NeonCyan
                                        )
                                    )
                                }

                                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                    Text(
                                        text = "زاوية الحركة الدائرية (Rotation): ${tempRotation.toInt()}°",
                                        color = CosmicTheme.NeonYellow,
                                        fontSize = 10.sp
                                    )
                                    Slider(
                                        value = tempRotation,
                                        onValueChange = { rot ->
                                            tempRotation = rot
                                        },
                                        onValueChangeFinished = {
                                            viewModel.initiateTransform(
                                                item = activeSelectedItem,
                                                originalOldX = activeSelectedItem.xPos,
                                                originalOldY = activeSelectedItem.yPos,
                                                scale = tempScale,
                                                rotation = tempRotation,
                                                newX = activeSelectedItem.xPos,
                                                newY = activeSelectedItem.yPos
                                            )
                                            viewModel.commitPendingTransform()
                                        },
                                        valueRange = -360f..360f,
                                        colors = SliderDefaults.colors(
                                            thumbColor = CosmicTheme.NeonYellow,
                                            activeTrackColor = CosmicTheme.NeonYellow
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Custom filter selectors (Omni-Icon Forger)
                            if (!activeSelectedItem.isWidget) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "منقاش النحت (Style):",
                                        color = CosmicTheme.NeonMagenta,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    
                                    val forgedSelectorScroll = rememberScrollState()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(forgedSelectorScroll),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        listOf(
                                            "NEON_ECLIPSE" to "الكسوف نيون",
                                            "CYBERPUNK_3D" to "سايبر ثلاثي",
                                            "VECTOR_MINIMAL" to "المتجه البسيط",
                                            "OIL_PAINTING" to "البعد الزيتي",
                                            "SPECTRA_GLASSMOUR" to "شبكة الأكريليك",
                                            "MATRIX_GREEN" to "صفيفة الفولاذ",
                                            "HOLOGRAM_ARCADE" to "هولو الهرم",
                                            "SACRED_GOLD" to "السر الذهبي",
                                            "COMIC_POP" to "بوب مضحك"
                                        ).forEach { (styleKey, label) ->
                                            val isSelected = activeSelectedItem.filterStyle == styleKey
                                            Button(
                                                onClick = { viewModel.forgeIconStyle(activeSelectedItem.id, styleKey) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isSelected) CosmicTheme.NeonMagenta else Color(0xFF1E1E2E),
                                                    contentColor = Color.White
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            Button(
                                onClick = { viewModel.deleteItem(activeSelectedItem.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AlertGlow),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminate Node", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("إماتة هذا الجزيء من الشبكة", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 1. Instructions & Backdrop selectors
                                Text(
                                    text = "مرسم صياغة الخلفيات والشبكات الكونية النشطة // COSMOS CANVAS PLATFORM",
                                    color = CosmicTheme.NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    lineHeight = 15.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val backdropsList = listOf(
                                        "HOLOGRAPHIC_GRID" to "الشبكة المجسمة",
                                        "STARFIELD_DUST" to "غبار النجوم",
                                        "NEBULA_GLOW" to "مزيج السديم",
                                        "VULKAN_MATRIX" to "مصفوفة فولكان"
                                    )
                                    backdropsList.forEach { (styleKey, arabicLabel) ->
                                        val isActive = currentBackdrop == styleKey
                                        Button(
                                            onClick = { viewModel.setBackdrop(styleKey) },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isActive) CosmicTheme.NeonCyan else Color(0xFF16192E),
                                                contentColor = if (isActive) Color.Black else Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Text(arabicLabel, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // 2. Mass icon styler
                                Text(
                                    text = "المنقاش الموحد: تطبيق نمط النحت الفوري على كافة عناصر اللوحة:",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                val styleOverloads = listOf(
                                    "NEON_ECLIPSE" to "الكسوف نيون",
                                    "CYBERPUNK_3D" to "سايبر ثلاثي",
                                    "VECTOR_MINIMAL" to "المتجه البسيط",
                                    "OIL_PAINTING" to "البعد الزيتي",
                                    "SPECTRA_GLASSMOUR" to "شبكة الأكريليك",
                                    "MATRIX_GREEN" to "صفيفة الفولاذ",
                                    "HOLOGRAM_ARCADE" to "هولو الهرم",
                                    "SACRED_GOLD" to "السر الذهبي",
                                    "COMIC_POP" to "بوب مضحك"
                                )

                                val scrollState = rememberScrollState()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(scrollState),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    styleOverloads.forEach { (styleKey, label) ->
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF231435), RoundedCornerShape(6.dp))
                                                .border(1.dp, CosmicTheme.NeonMagenta.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                                .clickable { viewModel.applyStyleToAllItems(styleKey) }
                                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                        ) {
                                            Text(label, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // 3. Seeding Constellation and general tutorial
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { viewModel.seedMassiveCosmicConstellation() },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = CosmicTheme.NeonYellow,
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier.weight(1.3f).height(38.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = "Constellation", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إطلاق كوكبة أندروميدا (12 أيقونة مستجدة)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(38.dp)
                                            .background(Color(0x3300FFCC), RoundedCornerShape(10.dp))
                                            .border(1.dp, CosmicTheme.NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "انقر على أي تطبيق لتعديل إحداثياته ومقاييسه الحرة",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 8.sp,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            var screenWidthPx by remember { mutableStateOf(1080f) }
            var screenHeightPx by remember { mutableStateOf(2000f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(CosmicTheme.DeepSpaceBlack)
                    .onSizeChanged { containerSize ->
                        screenWidthPx = containerSize.width.toFloat()
                        screenHeightPx = containerSize.height.toFloat()
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (dragAmount.y < -30f) { // Swipe up
                                    if (cinematicPhase == 0) cinematicPhase = 1
                                    else if (cinematicPhase == 1) cinematicPhase = 2
                                } else if (dragAmount.y > 30f) { // Swipe down
                                    if (cinematicPhase == 2) cinematicPhase = 1
                                    else if (cinematicPhase == 1) cinematicPhase = 0
                                }
                            }
                        )
                    }
                    // Universal Holographic Neon Grid paper backdrop representing free coordinates space
                    .drawBehind {
                        if (cinematicPhase == 0) {
                            drawRect(color = Color.Black)
                            return@drawBehind
                        }
                        
                        if (cinematicPhase == 1) {
                            // Phase 1 (Lock Screen): Dark nebula
                            drawRect(color = Color(0xFF030308))
                            val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1080f
                            val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                            drawCircle(
                                color = Color(0xFF3F1B85).copy(alpha = 0.15f), // Faint glow
                                center = Offset(w * 0.2f, h * 0.3f),
                                radius = maxOf(1f, w * 0.8f)
                            )
                            return@drawBehind
                        }
                        
                        when (currentBackdrop) {
                            "HOLOGRAPHIC_GRID" -> {
                                val rawPx = 40.dp.toPx()
                                val gridSpace = if (rawPx.isNaN() || !rawPx.isFinite() || rawPx <= 20f) 20f else rawPx
                                val gridColor = Color(0xFF1D223B).copy(alpha = 0.4f)
                                val accentGridColor = CosmicTheme.NeonCyan.copy(alpha = 0.12f)
                                val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                                val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f

                                var xCoord = 0f
                                var index = 0
                                while (xCoord < w) {
                                    drawLine(
                                        color = if (index % 5 == 0) accentGridColor else gridColor,
                                        start = Offset(xCoord, 0f),
                                        end = Offset(xCoord, h),
                                        strokeWidth = if (index % 5 == 0) 1.5f else 0.8f
                                    )
                                    xCoord += gridSpace
                                    index++
                                }

                                var yCoord = 0f
                                index = 0
                                while (yCoord < h) {
                                    drawLine(
                                        color = if (index % 5 == 0) accentGridColor else gridColor,
                                        start = Offset(0f, yCoord),
                                        end = Offset(w, yCoord),
                                        strokeWidth = if (index % 5 == 0) 1.5f else 0.8f
                                    )
                                    yCoord += gridSpace
                                    index++
                                }
                            }
                            "STARFIELD_DUST" -> {
                                // Infinite cosmic starry twinkling particle dust
                                drawRect(color = Color(0xFF030308))
                                val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1080f
                                val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                                val starCount = 65
                                for (i in 0 until starCount) {
                                    val seedX = (31f * i * 17f) % w
                                    val seedY = (23f * i * 37f) % h
                                    // Use backdropAnimValue to twinkle star opacity smoothly
                                    val sinVal = kotlin.math.sin(i * 12.3f + backdropAnimValue / 3f)
                                    val starAlpha = (0.2f + 0.8f * sinVal).coerceIn(0f, 1f)
                                    val sizeFactor = if (i % 7 == 0) 4f else 2.5f
                                    drawCircle(
                                        color = Color.White.copy(alpha = starAlpha),
                                        radius = sizeFactor,
                                        center = Offset(seedX, seedY)
                                    )
                                    if (i % 12 == 0) {
                                        drawLine(
                                            color = CosmicTheme.NeonCyan.copy(alpha = starAlpha * 0.35f),
                                            start = Offset(seedX - 10f, seedY),
                                            end = Offset(seedX + 10f, seedY),
                                            strokeWidth = 1f
                                        )
                                        drawLine(
                                            color = CosmicTheme.NeonCyan.copy(alpha = starAlpha * 0.35f),
                                            start = Offset(seedX, seedY - 10f),
                                            end = Offset(seedX, seedY + 10f),
                                            strokeWidth = 1f
                                        )
                                    }
                                }
                            }
                            "NEBULA_GLOW" -> {
                                // Floating galactic auroras & nebulae blends
                                drawRect(color = Color(0xFF020106))
                                val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1080f
                                val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                                val radiusBase = size.minDimension.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1000f
                                drawCircle(
                                    color = Color(0xFF3F1B85).copy(alpha = 0.35f),
                                    center = Offset(w * 0.2f, h * 0.3f),
                                    radius = maxOf(1f, radiusBase * 0.8f)
                                )
                                drawCircle(
                                    color = Color(0xFF861879).copy(alpha = 0.3f),
                                    center = Offset(w * 0.8f, h * 0.7f),
                                    radius = maxOf(1f, radiusBase * 0.9f)
                                )
                                drawCircle(
                                    color = CosmicTheme.NeonCyan.copy(alpha = 0.18f),
                                    center = Offset(w * 0.5f, h * 0.5f),
                                    radius = maxOf(1f, radiusBase * 0.60f)
                                )
                            }
                            "VULKAN_MATRIX" -> {
                                // Binary green matrix streams representing JNI cortex operations
                                drawRect(color = Color(0xFF010402))
                                val lineCount = 14
                                val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1080f
                                val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                                val spacing = w / lineCount
                                for (i in 0..lineCount) {
                                    val x = i * spacing
                                    drawLine(
                                        color = Color(0x1A00FF33),
                                        start = Offset(x, 0f),
                                        end = Offset(x, h),
                                        strokeWidth = 1f
                                    )
                                    // Simulated animated digital green signal nodes falling
                                    val speedOffset = (backdropAnimValue * 12f + i * 220f) % h
                                    drawCircle(
                                        color = Color(0xFF00FF33).copy(alpha = 0.75f),
                                        radius = 3.5f,
                                        center = Offset(x, speedOffset)
                                    )
                                }
                            }
                        }
                    }
            ) {
                // Free coordinates map canvas - elements are positioned absolutely
                items.forEach { item ->
                    LauncherItemNode(
                        item = item,
                        selectedItemId = selectedItemId,
                        tempScale = tempScale,
                        tempRotation = tempRotation,
                        pendingTransform = pendingTransform,
                        viewModel = viewModel,
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx,
                        cinematicPhase = cinematicPhase
                    )
                }
            }
        }
    }

    // 1. Verification Gate: Secure conformation popups preventing layout displacements
    if (pendingTransform != null && pendingTransform?.itemId != "") {
        VerificationGateDialog(
            title = "إقرار التعديل الهيكلي",
            message = "هل تؤكد تحريك العنصر [${pendingTransform?.label}] وحفظ موقعه الجديد في مصفوفة الإحداثيات المستديمة؟",
            onConfirm = { viewModel.commitPendingTransform() },
            onRevert = { viewModel.revertPendingTransform() }
        )
    }

    // 2. Custom App generation dialog HUD
    if (showAddAppDialog) {
        Dialog(onDismissRequest = { showAddAppDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CosmicTheme.DeepSpaceBlack,
                border = BorderStroke(1.dp, CosmicTheme.NeonYellow),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "صياغة جزيء كوني جديد",
                        color = CosmicTheme.NeonYellow,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = appLabelInput,
                        onValueChange = { appLabelInput = it },
                        label = { Text("اسم التطبيق المستجد", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "منقاش النحت المبدئي",
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("NEON_ECLIPSE", "CYBERPUNK_3D", "VECTOR_MINIMAL", "OIL_PAINTING").forEach { style ->
                            Button(
                                onClick = { forgerStyleInput = style },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (forgerStyleInput == style) CosmicTheme.NeonYellow else Color.Gray
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(style.take(6), fontSize = 7.sp, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddAppDialog = false }) {
                            Text("رجوع التخصيص", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (appLabelInput.isNotBlank()) {
                                    viewModel.createCustomApp(appLabelInput, forgerStyleInput)
                                    appLabelInput = ""
                                    showAddAppDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.NeonYellow)
                        ) {
                            Text("نحت وإطلاق", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

// Verification Dialog rendering
@Composable
fun VerificationGateDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onRevert: () -> Unit
) {
    Dialog(onDismissRequest = onRevert) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CosmicTheme.DeepSpaceBlack,
            border = BorderStroke(1.5.dp, CosmicTheme.NeonCyan),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "تحذير البوابة",
                        tint = CosmicTheme.NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = CosmicTheme.NeonCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onRevert,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("إلغاء واستعادة", color = Color.White, fontSize = 10.sp)
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.NeonCyan)
                    ) {
                        Text("نعم (حفظ)", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Sliding Assistant drawer content supporting live predictions and smart categorized sorts
@Composable
fun CortexDrawerContent(
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onSortRequest: (String) -> Unit
) {
    var rawInputText by remember { mutableStateOf("") }
    val listState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicTheme.DeepSpaceBlack)
            .padding(14.dp)
    ) {
        Text(
            text = "نواة الذكاء الكورتكسي // CORTEX AI",
            color = CosmicTheme.NeonCyan,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Category direct smart layouts
        Text(
            text = "فهرسة التطبيقات والتصنيف التلقائي الذكي:",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val categories = listOf(
                "نواة المنظومة" to CosmicTheme.NeonCyan,
                "الذكاء والمساعدين" to CosmicTheme.NeonYellow,
                "المنظومة الروحية" to CosmicTheme.NeonBlue,
                "الشبكة الاجتماعية" to CosmicTheme.NeonMagenta
            )

            // Make columns or wrap flow
            Column {
                Row {
                    categories.take(2).forEach { (cat, color) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(0.5.dp, color, RoundedCornerShape(4.dp))
                                .clickable { onSortRequest(cat) }
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cat, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    categories.drop(2).forEach { (cat, color) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(0.5.dp, color, RoundedCornerShape(4.dp))
                                .clickable { onSortRequest(cat) }
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cat, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dialogue messages viewport
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0F1122), RoundedCornerShape(12.dp))
                .border(0.5.dp, CosmicTheme.MutedSlate, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(listState)
            ) {
                messages.forEach { msg ->
                    ChatBubble(message = msg)
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = CosmicTheme.NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dialogue entry form
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = rawInputText,
                onValueChange = { rawInputText = it },
                placeholder = { Text("تواصل مع النواة العصبية...", color = Color.Gray, fontSize = 11.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = TextStyle(fontSize = 11.sp),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (rawInputText.isNotBlank()) {
                            onSendMessage(rawInputText)
                            rawInputText = ""
                            keyboardController?.hide()
                        }
                    }
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (rawInputText.isNotBlank()) {
                        onSendMessage(rawInputText)
                        rawInputText = ""
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier
                    .background(CosmicTheme.NeonCyan, CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "إرسال صياغة الأسئلة",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Standard bubbles displaying dialogue sequences
@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleColor = if (message.isUser) CosmicTheme.NeonMagenta.copy(alpha = 0.12f) else CosmicTheme.NeonCyan.copy(alpha = 0.08f)
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val textColor = if (message.isUser) CosmicTheme.NeonMagenta else Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 240.dp)
                .background(bubbleColor, RoundedCornerShape(12.dp))
                .border(
                    0.5.dp,
                    if (message.isUser) CosmicTheme.NeonMagenta else CosmicTheme.NeonCyan,
                    RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 11.sp,
                textAlign = TextAlign.Start,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun LauncherItemNode(
    item: LauncherItem,
    selectedItemId: String?,
    tempScale: Float,
    tempRotation: Float,
    pendingTransform: LauncherViewModel.PendingTransform?,
    viewModel: LauncherViewModel,
    screenWidthPx: Float,
    screenHeightPx: Float,
    cinematicPhase: Int = 2
) {
    var temporaryDragOffset by remember(item.id) { mutableStateOf<Offset?>(null) }

    val isSelected = selectedItemId == item.id
    val currentScale = (if (isSelected) tempScale else item.scale).takeIf { !it.isNaN() && it.isFinite() } ?: 1.0f
    val currentRotation = (if (isSelected) tempRotation else item.rotation).takeIf { !it.isNaN() && it.isFinite() } ?: 0f

    val rawCurrentX = when {
        temporaryDragOffset != null -> temporaryDragOffset!!.x
        pendingTransform?.itemId == item.id -> pendingTransform!!.newX
        else -> item.xPos
    }
    val rawCurrentY = when {
        temporaryDragOffset != null -> temporaryDragOffset!!.y
        pendingTransform?.itemId == item.id -> pendingTransform!!.newY
        else -> item.yPos
    }
    
    val currentX = rawCurrentX.takeIf { !it.isNaN() && it.isFinite() } ?: 0f
    val currentY = rawCurrentY.takeIf { !it.isNaN() && it.isFinite() } ?: 0f

    if (cinematicPhase == 0) {
        if (item.isWidget && item.widgetType == "CLOCK") {
            // Keep clock visible in AOD
        } else {
            // AOD Notification cinders for other apps
            Box(
                modifier = Modifier
                    .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                    .size(8.dp)
                    .background(CosmicTheme.NeonCyan.copy(alpha = 0.5f), CircleShape)
            )
            return
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    currentX.roundToInt(),
                    currentY.roundToInt()
                )
            }
            .graphicsLayer {
                rotationZ = currentRotation
                scaleX = currentScale
                scaleY = currentScale
            }
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = {
                        viewModel.selectItem(item.id)
                        temporaryDragOffset = Offset(item.xPos, item.yPos)
                    },
                    onDragEnd = {
                        val finalOffset = temporaryDragOffset
                        if (finalOffset != null) {
                            viewModel.initiateTransform(
                                item = item,
                                originalOldX = item.xPos,
                                originalOldY = item.yPos,
                                scale = item.scale,
                                rotation = item.rotation,
                                newX = finalOffset.x,
                                newY = finalOffset.y
                            )
                        }
                        temporaryDragOffset = null
                    },
                    onDragCancel = {
                        temporaryDragOffset = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val currentDrag = temporaryDragOffset ?: Offset(item.xPos, item.yPos)
                        val maxBoundX = maxOf(0f, screenWidthPx - 100f)
                        val maxBoundY = maxOf(0f, screenHeightPx - 100f)
                        
                        var dx = dragAmount.x
                        if (dx.isNaN() || !dx.isFinite()) dx = 0f
                        var dy = dragAmount.y
                        if (dy.isNaN() || !dy.isFinite()) dy = 0f
                        
                        val nextX = (currentDrag.x + dx).coerceIn(0f, maxBoundX)
                        val nextY = (currentDrag.y + dy).coerceIn(0f, maxBoundY)
                        temporaryDragOffset = Offset(nextX, nextY)
                    }
                )
            }
            .clickable { viewModel.selectItem(item.id) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentSize()
        ) {
            if (item.isWidget) {
                // Draw dynamic widgets
                when (item.widgetType) {
                    "CLOCK" -> DeepEclipseClockWidget()
                    "RAM" -> RamCrescentWidget()
                    "BATTERY" -> BatteryElectromagneticWidget()
                }
            } else {
                // Draw App forged icon
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ForgedIcon(
                        label = item.label,
                        style = item.filterStyle,
                        cinematicPhase = cinematicPhase
                    )

                    // Small highlighting marker if currently selected
                    if (selectedItemId == item.id) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(2.dp, CosmicTheme.NeonYellow, CircleShape)
                        )
                    }
                }
                // Text removed as per request to draw inside icon
            }
        }
    }
}
