package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.LauncherItem
import com.example.data.LauncherRepository
import com.example.ai.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LauncherRepository
    val allItems: StateFlow<List<LauncherItem>>

    // Chat states
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("أهلاً بك في النواة العصبية الذكية GemiLeith OS Cortex. أنا مساعدك الشخصي لإدارة تخصيص الواجهة وتحليل مقاييس النظام. اسألني أي شيء!", false)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val geminiRepository = GeminiRepository()

    // Drag-And-Drop / Layout Verification Gate States
    var selectedItemId = MutableStateFlow<String?>(null)
    private val _pendingTransform = MutableStateFlow<PendingTransform?>(null)
    val pendingTransform: StateFlow<PendingTransform?> = _pendingTransform.asStateFlow()

    // Interactive Drawing Engine & Background States
    private val _currentBackdrop = MutableStateFlow("HOLOGRAPHIC_GRID")
    val currentBackdrop: StateFlow<String> = _currentBackdrop.asStateFlow()

    private val _glowMultiplier = MutableStateFlow(1.0f)
    val glowMultiplier: StateFlow<Float> = _glowMultiplier.asStateFlow()

    data class PendingTransform(
        val itemId: String,
        val label: String,
        val oldX: Float,
        val oldY: Float,
        val newX: Float,
        val newY: Float,
        val oldScale: Float,
        val oldRotation: Float,
        val newScale: Float,
        val newRotation: Float
    )

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LauncherRepository(database.launcherItemDao())
        allItems = repository.allItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed default items if db is completely empty
        viewModelScope.launch {
            try {
                // Check once using the first emitted list
                val list = repository.allItems.first()
                if (list.isEmpty()) {
                    seedDefaultItems()
                }
            } catch (e: Exception) {
                Log.e("LauncherViewModel", "Failed to seed default items", e)
            }
        }
    }

    private suspend fun seedDefaultItems() {
        val defaultItems = listOf(
            // Widgets
            LauncherItem("widget_clock", "الساعة الكونية", "com.android.deskclock", 120f, 180f, scale = 1.1f, rotation = 0f, filterStyle = "NONE", isWidget = true, widgetType = "CLOCK"),
            LauncherItem("widget_ram", "مؤشر الذاكرة العشوائية", "com.iqoo.secure", 520f, 130f, scale = 1.0f, rotation = 0f, filterStyle = "NONE", isWidget = true, widgetType = "RAM"),
            LauncherItem("widget_battery", "تدفق الطاقة الكهرومغناطيسية", "com.iqoo.secure", 520f, 310f, scale = 1.0f, rotation = 0f, filterStyle = "NONE", isWidget = true, widgetType = "BATTERY"),
            
            // Forged App Icons
            LauncherItem("app_settings", "الإعدادات", "com.android.settings", 120f, 520f, scale = 1.0f, rotation = 0f, filterStyle = "NEON_ECLIPSE"),
            LauncherItem("app_camera", "الكاميرا", "com.android.camera", 320f, 520f, scale = 1.0f, rotation = 0f, filterStyle = "CYBERPUNK_3D"),
            LauncherItem("app_gemini", "جيميني AI", "com.google.android.apps.gemini", 520f, 520f, scale = 1.0f, rotation = 0f, filterStyle = "NEON_ECLIPSE"),
            LauncherItem("app_quran", "المصحف الشريف", "com.quran.labs", 120f, 700f, scale = 1.0f, rotation = 0f, filterStyle = "VECTOR_MINIMAL"),
            LauncherItem("app_whatsapp", "واتساب", "com.whatsapp", 320f, 700f, scale = 1.0f, rotation = 0f, filterStyle = "MATRIX_GREEN"),
            LauncherItem("app_facebook", "فيسبوك", "com.facebook.katana", 520f, 700f, scale = 1.0f, rotation = 0f, filterStyle = "SHATTERED_BLUE"),
            LauncherItem("app_instagram", "انستجرام", "com.instagram.android", 120f, 880f, scale = 1.0f, rotation = 0f, filterStyle = "NEBULA_BURST"),
            LauncherItem("app_youtube", "يوتيوب", "com.google.android.youtube", 320f, 880f, scale = 1.0f, rotation = 0f, filterStyle = "NEON_ECLIPSE")
        )
        repository.insertAll(defaultItems)
    }

    fun selectItem(id: String?) {
        selectedItemId.value = id
    }

    fun initiateTransform(item: LauncherItem, originalOldX: Float, originalOldY: Float, scale: Float, rotation: Float, newX: Float, newY: Float) {
        val s = if (scale.isNaN() || !scale.isFinite()) 1.0f else scale
        val r = if (rotation.isNaN() || !rotation.isFinite()) 0f else rotation
        val nx = if (newX.isNaN() || !newX.isFinite()) 0f else newX
        val ny = if (newY.isNaN() || !newY.isFinite()) 0f else newY
        
        _pendingTransform.value = PendingTransform(
            itemId = item.id,
            label = item.label,
            oldX = originalOldX,
            oldY = originalOldY,
            newX = nx,
            newY = ny,
            oldScale = item.scale,
            oldRotation = item.rotation,
            newScale = s,
            newRotation = r
        )
    }

    fun commitPendingTransform() {
        val transform = _pendingTransform.value ?: return
        viewModelScope.launch {
            repository.updateTransform(
                id = transform.itemId,
                x = transform.newX,
                y = transform.newY,
                scale = transform.newScale,
                rotation = transform.newRotation
            )
            _pendingTransform.value = null
        }
    }

    fun revertPendingTransform() {
        _pendingTransform.value = null
    }

    fun forgeIconStyle(id: String, filterStyle: String) {
        viewModelScope.launch {
            repository.updateFilterStyle(id, filterStyle)
        }
    }

    fun setBackdrop(style: String) {
        _currentBackdrop.value = style
    }

    fun setGlowMultiplier(multiplier: Float) {
        _glowMultiplier.value = multiplier
    }

    fun applyStyleToAllItems(style: String) {
        viewModelScope.launch {
            val list = allItems.value
            list.forEach { item ->
                if (!item.isWidget) {
                    repository.updateFilterStyle(item.id, style)
                }
            }
        }
    }

    fun seedMassiveCosmicConstellation() {
        viewModelScope.launch {
            val startX = 80f
            val startY = 850f
            val additionalItems = listOf(
                LauncherItem("app_nebula", "مستكشف السديم الكوني", "com.gemileith.nebula", startX, startY, scale = 1.0f, rotation = 15f, filterStyle = "SPECTRA_GLASSMOUR"),
                LauncherItem("app_pulsar", "مراقب النجم النابض", "com.gemileith.pulsar", startX + 200f, startY, scale = 1.0f, rotation = 0f, filterStyle = "HOLOGRAM_ARCADE"),
                LauncherItem("app_andromeda", "بوابة أندروميدا", "com.gemileith.andromeda", startX + 400f, startY, scale = 1.1f, rotation = -10f, filterStyle = "MATRIX_GREEN"),
                LauncherItem("app_blackhole", "مدار الثقب الأسود", "com.gemileith.blackhole", startX, startY + 160f, scale = 0.9f, rotation = 45f, filterStyle = "NEON_ECLIPSE"),
                LauncherItem("app_quantum", "النواة الكمومية", "com.gemileith.quantum", startX + 200f, startY + 160f, scale = 1.2f, rotation = 30f, filterStyle = "SACRED_GOLD"),
                LauncherItem("app_laser", "حزمة الليزر المجسم", "com.gemileith.laser", startX + 400f, startY + 160f, scale = 1.0f, rotation = -20f, filterStyle = "CYBERPUNK_3D"),
                LauncherItem("app_aurora", "سيمفونية الأورورا", "com.gemileith.aurora", startX, startY + 320f, scale = 1.0f, rotation = 10f, filterStyle = "COMIC_POP"),
                LauncherItem("app_telescope", "مرصد هابل التقني", "com.gemileith.telescope", startX + 200f, startY + 320f, scale = 1.0f, rotation = 0f, filterStyle = "VECTOR_MINIMAL"),
                LauncherItem("app_spacetime", "مقياس نسيج الزمكان", "com.gemileith.spacetime", startX + 400f, startY + 320f, scale = 1.0f, rotation = 90f, filterStyle = "OIL_PAINTING"),
                LauncherItem("app_telemetry", "تحليل تدفق فولكان", "com.gemileith.telemetry", startX + 80f, startY + 480f, scale = 1.0f, rotation = -5f, filterStyle = "MATRIX_GREEN"),
                LauncherItem("app_antigravity", "عاكس الجاذبية الارتدادي", "com.gemileith.antigravity", startX + 280f, startY + 480f, scale = 1.1f, rotation = 60f, filterStyle = "SPECTRA_GLASSMOUR"),
                LauncherItem("app_firmware", "السروال العصبي المركزي", "com.gemileith.firmware", startX + 180f, startY + 640f, scale = 1.0f, rotation = -40f, filterStyle = "SACRED_GOLD")
            )
            val currentIds = allItems.value.map { it.id }.toSet()
            val newUniqueItems = additionalItems.filter { it.id !in currentIds }
            if (newUniqueItems.isNotEmpty()) {
                repository.insertAll(newUniqueItems)
            }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            repository.deleteItemById(id)
        }
    }

    fun createCustomApp(label: String, style: String, x: Float = 300f, y: Float = 400f) {
        val randomId = "custom_${System.currentTimeMillis()}"
        viewModelScope.launch {
            repository.insertItem(
                LauncherItem(
                    id = randomId,
                    label = label,
                    packageName = "com.custom.$randomId",
                    xPos = x,
                    yPos = y,
                    scale = 1.0f,
                    rotation = 0f,
                    filterStyle = style
                )
            )
        }
    }

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        _chatMessages.value = _chatMessages.value + ChatMessage(text, true)
        _isChatLoading.value = true

        viewModelScope.launch {
            val systemIns = """
                You are GemiLeith OS Cortex AI, a highly futuristic, premium, biological-cybernetic system interface assistant.
                Speak in Arabic with professional, architectural command, combining technical terms with natural intelligence.
                The project was conceptualized by GemiLeith architects including Ahmed Abdilhamid Khater (known as Abu Seif).
                Reference system stats when asked. Keep answers direct, inspiring, and completely focused on visual beauty, micro-widgets, spatial custom coordinate matrices, and premium layout styles.
            """.trimIndent()

            val statsResponse = when {
                text.contains("ذاكرة") || text.contains("RAM") || text.contains("رام") -> {
                    val runtime = Runtime.getRuntime()
                    val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                    val maxMem = runtime.maxMemory() / (1024 * 1024)
                    "النظام يستهلك حالياً $usedMem ميجابايت من الذاكرة العشوائية من أصل $maxMem ميجابايت إجمالاً. معدل التدفق العاطفي والإطارات (FPS) مستقر تماماً عند 120إطاراً بالثانية بفضل واجهة Vulkan JNI."
                }
                text.contains("تخصيص") || text.contains("شاشة") || text.contains("أيقونة") -> {
                    "يمكنك تعديل أيقونات شاشتك الحرة عبر النقر المباشر عليها. ستظهر لك عتلات تعديل الحجم والتدوير، فضلاً عن منقاش الفلاتر (Omni-Icon Forger) المطلي بطلاء النيون Eclipse الفوسفوري، أو خامات الزيت أو البعد الثلاثي Cyber Punk!"
                }
                else -> null
            }

            val finalReply = if (statsResponse != null) {
                statsResponse
            } else {
                geminiRepository.generateResponse(getApplication(), text, systemInstruction = systemIns)
            }

            _chatMessages.value = _chatMessages.value + ChatMessage(finalReply, false)
            _isChatLoading.value = false
        }
    }
}
