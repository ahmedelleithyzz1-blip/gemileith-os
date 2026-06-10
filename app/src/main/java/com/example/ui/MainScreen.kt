package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var isQuickSettingsOpen by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .drawBehind {
                        val stroke = Stroke(width = 4f * (1f + (backdropAnimValue % 100f) / 100f))
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(CosmicTheme.NeonMagenta, CosmicTheme.NeonCyan, Color.Transparent),
                                startY = 0f,
                                endY = size.height * 0.5f + (backdropAnimValue % 500f)
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                            style = stroke
                        )
                    },
                drawerContainerColor = CosmicTheme.DeepSpaceBlack.copy(alpha = 0.9f),
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

                                    Button(
                                        onClick = { viewModel.toggleNotification("app_whatsapp") },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF00FF44),
                                            contentColor = Color.Black
                                        ),
                                        modifier = Modifier.weight(0.7f).height(38.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("إشعار", fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
            var touchOffset by remember { mutableStateOf(Offset.Zero) }
            val touchAlpha by animateFloatAsState(if (touchOffset != Offset.Zero) 1f else 0f, tween(1000))

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
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                                val ptr = event.changes.firstOrNull()
                                if (ptr != null && ptr.pressed) {
                                    touchOffset = ptr.position
                                } else {
                                    touchOffset = Offset.Zero
                                }
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        var startY = 0f
                        detectDragGestures(
                            onDragStart = { offset ->
                                startY = offset.y
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (dragAmount.y < -30f) { // Swipe up
                                    if (isQuickSettingsOpen) {
                                        isQuickSettingsOpen = false
                                    } else {
                                        if (cinematicPhase == 0) cinematicPhase = 1
                                        else if (cinematicPhase == 1) cinematicPhase = 2
                                    }
                                } else if (dragAmount.y > 30f) { // Swipe down
                                    if (startY < 200f && cinematicPhase == 2) {
                                        isQuickSettingsOpen = true
                                    } else {
                                        if (cinematicPhase == 2) cinematicPhase = 1
                                        else if (cinematicPhase == 1) cinematicPhase = 0
                                    }
                                } else if (dragAmount.x > 30f) { // Swipe right
                                    scope.launch { drawerState.open() }
                                } else if (dragAmount.x < -30f) { // Swipe left
                                    scope.launch { drawerState.close() }
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }
                    // Universal Holographic Neon Grid paper backdrop representing free coordinates space
                    .drawBehind {
                        val breathingPhase = (kotlin.math.sin(System.currentTimeMillis() / 3000.0) * 0.5 + 0.5).toFloat()
                        
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
                        
                        if (cinematicPhase == 2) {
                            // Phase 2 (Home Screen - Power Explosion): Magic circles and light pillars
                            val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1080f
                            val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f

                            // Light pillars
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF00FF44).copy(alpha = 0.1f), Color.Transparent),
                                    startY = 0f,
                                    endY = h * 0.5f
                                ),
                                topLeft = Offset(w * 0.3f, 0f),
                                size = androidx.compose.ui.geometry.Size(w * 0.1f, h)
                            )
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF00FFFF).copy(alpha = 0.1f), Color.Transparent),
                                    startY = 0f,
                                    endY = h * 0.6f
                                ),
                                topLeft = Offset(w * 0.7f, 0f),
                                size = androidx.compose.ui.geometry.Size(w * 0.15f, h)
                            )

                            // Faint magic circles matching positions
                            drawCircle(
                                color = Color(0xFF00FF44).copy(alpha = 0.05f),
                                radius = 250f,
                                center = Offset(w * 0.35f, h * 0.35f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                            )
                            drawCircle(
                                color = Color(0xFF00FF44).copy(alpha = 0.03f),
                                radius = 220f,
                                center = Offset(w * 0.35f, h * 0.35f),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 4f, 
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                                )
                            )
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
                                    var drawX = xCoord
                                    if (touchAlpha > 0.01f) {
                                        val distX = kotlin.math.abs(xCoord - touchOffset.x)
                                        if (distX < 200f) {
                                            drawX += (if (xCoord > touchOffset.x) 1f else -1f) * (200f - distX) * 0.1f * touchAlpha
                                        }
                                    }
                                    drawLine(
                                        color = if (index % 5 == 0) accentGridColor else gridColor,
                                        start = Offset(drawX, 0f),
                                        end = Offset(drawX, h),
                                        strokeWidth = if (index % 5 == 0) 1.5f else 0.8f
                                    )
                                    xCoord += gridSpace
                                    index++
                                }

                                var yCoord = 0f
                                index = 0
                                while (yCoord < h) {
                                    var drawY = yCoord
                                    if (touchAlpha > 0.01f) {
                                        val distY = kotlin.math.abs(yCoord - touchOffset.y)
                                        if (distY < 200f) {
                                            drawY += (if (yCoord > touchOffset.y) 1f else -1f) * (200f - distY) * 0.1f * touchAlpha
                                        }
                                    }
                                    drawLine(
                                        color = if (index % 5 == 0) accentGridColor else gridColor,
                                        start = Offset(0f, drawY),
                                        end = Offset(w, drawY),
                                        strokeWidth = if (index % 5 == 0) 1.5f else 0.8f
                                    )
                                    yCoord += gridSpace
                                    index++
                                }
                            }
                            "STARFIELD_DUST" -> {
                                // Infinite cosmic starry twinkling particle dust with touch repelling interaction
                                drawRect(color = Color(0xFF030308))
                                val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1080f
                                val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                                val starCount = 65
                                val breathingScale = 0.95f + 0.1f * breathingPhase
                                val breathingOpacity = 0.8f + 0.2f * breathingPhase
                                
                                for (i in 0 until starCount) {
                                    val seedX = ((31f * i * 17f) % w - w/2) * breathingScale + w/2
                                    val seedY = ((23f * i * 37f) % h - h/2) * breathingScale + h/2
                                    // Use backdropAnimValue to twinkle star opacity smoothly
                                    val sinVal = kotlin.math.sin(i * 12.3f + backdropAnimValue / 3f)
                                    val starAlpha = (0.2f + 0.8f * sinVal).coerceIn(0f, 1f) * breathingOpacity
                                    val sizeFactor = (if (i % 7 == 0) 4f else 2.5f) * breathingScale

                                    // Touch repelling effect
                                    var drawX = seedX
                                    var drawY = seedY
                                    if (touchAlpha > 0.01f) {
                                        val dist = kotlin.math.hypot((seedX - touchOffset.x).toDouble(), (seedY - touchOffset.y).toDouble()).toFloat()
                                        if (dist < 400f && dist > 1f) {
                                            val push = (1f - dist / 400f) * 120f * touchAlpha
                                            val dx = (seedX - touchOffset.x) / dist
                                            val dy = (seedY - touchOffset.y) / dist
                                            drawX += dx * push
                                            drawY += dy * push
                                        }
                                    }

                                    drawCircle(
                                        color = Color.White.copy(alpha = starAlpha),
                                        radius = sizeFactor,
                                        center = Offset(drawX, drawY)
                                    )
                                    if (i % 12 == 0) {
                                        drawLine(
                                            color = CosmicTheme.NeonCyan.copy(alpha = starAlpha * 0.35f),
                                            start = Offset(drawX - 10f * breathingScale, drawY),
                                            end = Offset(drawX + 10f * breathingScale, drawY),
                                            strokeWidth = 1f
                                        )
                                        drawLine(
                                            color = CosmicTheme.NeonCyan.copy(alpha = starAlpha * 0.35f),
                                            start = Offset(drawX, drawY - 10f * breathingScale),
                                            end = Offset(drawX, drawY + 10f * breathingScale),
                                            strokeWidth = 1f
                                        )
                                    }
                                }
                            }
                            "NEBULA_GLOW" -> {
                                // Floating galactic auroras & nebulae blends with touch attraction
                                drawRect(color = Color(0xFF020106))
                                val w = size.width.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1080f
                                val h = size.height.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 2000f
                                val radiusBase = size.minDimension.takeIf { it.isFinite() && !it.isNaN() }?.coerceIn(1f, 4000f) ?: 1000f

                                val breathRadius = 0.9f + 0.2f * breathingPhase
                                val breathAlpha = 0.85f + 0.15f * breathingPhase

                                val c1X = w * 0.2f + (touchOffset.x - w * 0.2f) * 0.15f * touchAlpha
                                val c1Y = h * 0.3f + (touchOffset.y - h * 0.3f) * 0.15f * touchAlpha
                                drawCircle(
                                    color = Color(0xFF3F1B85).copy(alpha = 0.35f * breathAlpha),
                                    center = Offset(c1X, c1Y),
                                    radius = maxOf(1f, radiusBase * 0.8f * breathRadius)
                                )

                                val c2X = w * 0.8f + (touchOffset.x - w * 0.8f) * 0.15f * touchAlpha
                                val c2Y = h * 0.7f + (touchOffset.y - h * 0.7f) * 0.15f * touchAlpha
                                drawCircle(
                                    color = Color(0xFF861879).copy(alpha = 0.3f * breathAlpha),
                                    center = Offset(c2X, c2Y),
                                    radius = maxOf(1f, radiusBase * 0.9f * breathRadius)
                                )

                                val c3X = w * 0.5f + (touchOffset.x - w * 0.5f) * 0.3f * touchAlpha
                                val c3Y = h * 0.5f + (touchOffset.y - h * 0.5f) * 0.3f * touchAlpha
                                drawCircle(
                                    color = CosmicTheme.NeonCyan.copy(alpha = (0.18f + (0.1f * touchAlpha)) * breathAlpha),
                                    center = Offset(c3X, c3Y),
                                    radius = maxOf(1f, radiusBase * (0.60f + 0.1f * touchAlpha) * breathRadius)
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
                                    var drawX = x
                                    if (touchAlpha > 0.01f) {
                                        val distX = kotlin.math.abs(x - touchOffset.x)
                                        if (distX < 300f) {
                                            drawX += (if (x > touchOffset.x) 1f else -1f) * (300f - distX) * 0.15f * touchAlpha
                                        }
                                    }
                                    drawLine(
                                        color = Color(0x1A00FF33),
                                        start = Offset(drawX, 0f),
                                        end = Offset(drawX, h),
                                        strokeWidth = 1f
                                    )
                                    // Simulated animated digital green signal nodes falling
                                    val speedOffset = (backdropAnimValue * 12f + i * 220f) % h
                                    drawCircle(
                                        color = Color(0xFF00FF33).copy(alpha = 0.75f + (0.25f * touchAlpha)),
                                        radius = 3.5f + (if (touchAlpha > 0f) 1.5f else 0f),
                                        center = Offset(drawX, speedOffset)
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
                
                CosmicQuickSettingsPanel(
                    isVisible = isQuickSettingsOpen,
                    onClose = { isQuickSettingsOpen = false }
                )
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
    val context = androidx.compose.ui.platform.LocalContext.current
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

    val dragScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (temporaryDragOffset != null) 1.15f else 1f, 
        animationSpec = androidx.compose.animation.core.tween(300, easing = androidx.compose.animation.core.FastOutSlowInEasing)
    )

    val infiniteIconTransition = rememberInfiniteTransition(label = "iconBreathing")
    val breatheScale by infiniteIconTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000 + kotlin.math.abs(item.hashCode()) % 1000, easing = androidx.compose.animation.core.FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )

    val floatY by infiniteIconTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000 + kotlin.math.abs(item.hashCode()) % 2000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    if (cinematicPhase == 0) {
        if (item.isWidget && item.widgetType == "CLOCK") {
            // Keep clock visible in AOD, custom logic in the widget itself
        } else {
            // AOD Notification cinders for other apps
            if (item.hasNotification) {
                val infiniteTransition = rememberInfiniteTransition(label = "aodPulse")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )
                
                val glowColor = when (item.filterStyle) {
                    "WHATSAPP_MATRIX" -> Color(0xFF00FF44)
                    "SETTINGS_GEAR" -> Color(0xFFFF5500)
                    "CAMERA_LENS" -> Color(0xFF00FFFF)
                    "CHROME_SPHERICAL" -> Color(0xFF4285F4)
                    "SPOTIFY_WAVES" -> Color(0xFF1DB954)
                    "PHONE_HOLOGRAPHIC" -> Color(0xFF00A2FF)
                    "CALENDAR_QUARTZ" -> Color(0xFF0066FF)
                    "FILES_VAULT" -> Color(0xFFFFCC00)
                    else -> CosmicTheme.NeonCyan
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                        .size(12.dp)
                        .background(glowColor.copy(alpha = pulseAlpha), CircleShape)
                        .drawBehind {
                            drawCircle(
                                color = glowColor.copy(alpha = pulseAlpha * 0.5f),
                                radius = size.width
                            )
                        }
                )
            }
            return
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    currentX.roundToInt(),
                    (currentY + floatY).roundToInt()
                )
            }
            .graphicsLayer {
                rotationZ = currentRotation
                scaleX = currentScale * dragScale * breatheScale
                scaleY = currentScale * dragScale * breatheScale
                shadowElevation = if (temporaryDragOffset != null) 30f else 0f
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
            .clickable { 
                viewModel.selectItem(item.id) 
                
                val packageName = item.packageName
                if (packageName.isNotEmpty()) {
                    viewModel.incrementUsageCount(item.id)
                    val pm = context.packageManager
                    val intent = pm.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentSize()
        ) {
            if (item.isWidget) {
                // Draw dynamic widgets
                when (item.widgetType) {
                    "CLOCK" -> DeepEclipseClockWidget(cinematicPhase = cinematicPhase, hasNotification = item.hasNotification)
                    "RAM" -> RamCrescentWidget(cinematicPhase = cinematicPhase)
                    "BATTERY" -> BatteryElectromagneticWidget(cinematicPhase = cinematicPhase)
                }
            } else {
                // Draw App forged icon
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val w = screenWidthPx
                    val h = screenHeightPx
                    var rechargeMultiplier = 1.0f
                    if (cinematicPhase == 2) {
                        val inPillar1 = currentX >= w * 0.3f && currentX <= w * 0.4f && currentY <= h * 0.5f
                        val inPillar2 = currentX >= w * 0.7f && currentX <= w * 0.85f && currentY <= h * 0.6f
                        if (inPillar1 || inPillar2) {
                            rechargeMultiplier = 2.5f
                        }
                    }

                    ForgedIcon(
                        label = item.label,
                        style = item.filterStyle,
                        cinematicPhase = cinematicPhase,
                        hasNotification = item.hasNotification,
                        rechargeMultiplier = rechargeMultiplier,
                        usageCount = item.usageCount
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
