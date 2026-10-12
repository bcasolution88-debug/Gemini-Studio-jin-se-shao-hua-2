package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.EditEmergencyContactDialog
import com.example.ui.components.EmergencyContactSettingCard
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart

import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

import androidx.compose.material.icons.filled.Contrast

@Composable
fun ElderModeScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val isElderMode by viewModel.isElderMode.collectAsState()
    val isHighContrastMode by viewModel.isHighContrastMode.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val voiceInputEnabled by viewModel.voiceInputEnabled.collectAsState()
    val handwritingEnabled by viewModel.handwritingEnabled.collectAsState()
    val simpleUiEnabled by viewModel.simpleUiEnabled.collectAsState()
    val voiceReminderEnabled by viewModel.voiceReminderEnabled.collectAsState()
    val memories by viewModel.memories.collectAsState()

    val haptic = LocalHapticFeedback.current

    val emergencyContactName by viewModel.emergencyContactName.collectAsState()
    val emergencyContactPhone by viewModel.emergencyContactPhone.collectAsState()
    var showEditEmergencyDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var showElderSlideshow by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val filteredMemories = remember(memories, searchQuery) {
        if (searchQuery.isBlank()) {
            memories
        } else {
            memories.filter { mem ->
                mem.date.contains(searchQuery, ignoreCase = true) ||
                        mem.title.contains(searchQuery, ignoreCase = true) ||
                        mem.titleEn.contains(searchQuery, ignoreCase = true) ||
                        mem.location.contains(searchQuery, ignoreCase = true) ||
                        mem.tags.contains(searchQuery, ignoreCase = true) ||
                        mem.eventName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "Elder Mode" else "长辈模式",
            englishTitle = "ELDER MODE",
            showBack = true,
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) },
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(GoldenGradientStart, GoldenGradientEnd)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Accessibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Elder Mode" else "长辈关怀模式",
                                    fontSize = (18 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnglish) "High Contrast · Large Fonts · Voice Search" else "大字大图 · 专属高对比度 · 语音找老照片",
                                    fontSize = (12 * fontScale).sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Switch(
                            checked = isElderMode,
                            onCheckedChange = { viewModel.toggleElderMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = GoldenGradientStart, checkedTrackColor = Color.White)
                        )
                    }
                }
            }

            // Requirement 2: Adaptive High Contrast Setting Card for Visually Impaired Elder Users
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = if (isHighContrastMode) Color(0xFF000000) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    if (isHighContrastMode) 3.dp else 1.dp,
                    if (isHighContrastMode) Color(0xFFFFEA00) else MaterialTheme.colorScheme.outline
                ),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isHighContrastMode) Color(0xFFFFEA00) else GoldenGradientStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Contrast,
                                    contentDescription = "High Contrast Mode",
                                    tint = if (isHighContrastMode) Color.Black else Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isEnglish) "Adaptive High Contrast" else "视障关怀 · 自适应高对比度",
                                        fontSize = (16 * fontScale).sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHighContrastMode) Color(0xFFFFEA00) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF00E676)
                                    ) {
                                        Text(
                                            text = "WCAG AAA",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isEnglish) "7:1 Ratio · Pure black & vivid yellow text for low vision" else "对比度达到 7:1 • 135%超大字 • 纯黑底色高敏易读",
                                    fontSize = (11 * fontScale).sp,
                                    color = if (isHighContrastMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isHighContrastMode,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleHighContrastMode(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color(0xFFFFEA00)
                            )
                        )
                    }

                    if (isHighContrastMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1C1C1C),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEA00))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isEnglish) "👁️ High-Contrast Accessibility Mode Active" else "👁️ 已开启无障碍超清晰对比度渲染",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFEA00)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isEnglish) "All photos, text titles, and controls automatically boosted for maximum legibility." else "全应用元素已自动适配纯高反差黄色与高亮白字，完美保障老花眼及视障长辈轻松查阅。",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showElderSlideshow = true
                    },
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF2E7D32),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📺", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEnglish) "📺 Elder Hands-Free Photo Slideshow" else "📺 开启大字长辈无障碍幻灯片",
                            fontSize = (16 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEnglish) "Adjustable speeds (slow/medium/fast) · Large text" else "免动手全屏播放 · 慢/中/快变速设定 · 暖心字幕",
                            fontSize = (12 * fontScale).sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = if (isEnglish) "START" else "播放",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Emergency Contact Setting for Elders
            EmergencyContactSettingCard(
                contactName = emergencyContactName,
                contactPhone = emergencyContactPhone,
                isEnglish = isEnglish,
                onEditClick = { showEditEmergencyDialog = true },
                onTestDialClick = { viewModel.openEmergencyDialDialog() }
            )

            if (showEditEmergencyDialog) {
                EditEmergencyContactDialog(
                    initialName = emergencyContactName,
                    initialPhone = emergencyContactPhone,
                    isEnglish = isEnglish,
                    onSave = { name, phone -> viewModel.updateEmergencyContact(name, phone) },
                    onDismiss = { showEditEmergencyDialog = false }
                )
            }

            // Speech-To-Text Voice Search for Elders
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isEnglish) "🎙️ Voice Search Photos (Date, Person, Occasion)" else "🎙️ 语音找老照片（说年份、人物或大事）",
                        fontSize = (16 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Large Mic Button for Elder Accessibility
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isListening) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    coroutineScope.launch {
                                        isListening = true
                                        delay(1500)
                                        isListening = false
                                        searchQuery = if (isEnglish) "1976" else "1976"
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Input Mic",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isListening)
                                    (if (isEnglish) "Listening... Speak date or person" else "正在倾听中... 请说年份或事件，如: 1976年、金婚")
                                else if (searchQuery.isNotBlank())
                                    (if (isEnglish) "Voice result: '$searchQuery'" else "语音识别结果: 『$searchQuery』")
                                else
                                    (if (isEnglish) "Tap mic to speak search query" else "按红色按键说话，按年份/事件查老照片"),
                                fontSize = (14 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (searchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isEnglish) "Clear" else "清空筛选",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { searchQuery = "" }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Voice Query Shortcuts / Prompts
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "1976" to "1976年婚礼",
                            "2025" to "2025金婚",
                            "北京" to "北京聚会",
                            "全部" to "查看全部"
                        ).forEach { (queryKey, labelText) ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (searchQuery == queryKey) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                modifier = Modifier.clickable {
                                    searchQuery = if (queryKey == "全部") "" else queryKey
                                }
                            ) {
                                Text(
                                    text = labelText,
                                    fontSize = (13 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (searchQuery == queryKey) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // High-Contrast Elder Photo Cards Result List
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isEnglish) "Elder Photo Album List (${filteredMemories.size})" else "长辈专属高清相册结果 (${filteredMemories.size}张)",
                    fontSize = (16 * fontScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                filteredMemories.forEach { mem ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                        shadowElevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = mem.date + " · " + (if (isEnglish) mem.eventNameEn else mem.eventName),
                                    fontSize = (16 * fontScale).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.clickable {
                                        // Voice read aloud title
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = "Read", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isEnglish) "Read" else "语音朗读", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isEnglish) mem.titleEn else mem.title,
                                fontSize = (18 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isEnglish) mem.descriptionEn else mem.description,
                                fontSize = (14 * fontScale).sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = (22 * fontScale).sp
                            )
                        }
                    }
                }
            }

            // Font Size adjustment slider
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isEnglish) "Font Size Adjustment" else "字体大小调节 Font Scale",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("A", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Slider(
                            value = fontScale,
                            onValueChange = { viewModel.setFontScale(it) },
                            valueRange = 1.0f..1.5f,
                            colors = SliderDefaults.colors(thumbColor = GoldenGradientStart, activeTrackColor = GoldenGradientStart),
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )
                        Text("A", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Preview text
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isEnglish) "Sample text: Memory brings warmth to family." else "示例文字：金婚五十载，儿孙满堂幸福长存。",
                                fontSize = (14 * fontScale).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Function Toggles
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column {
                    ElderToggleRow(
                        icon = Icons.Default.Mic,
                        title = if (isEnglish) "Voice Input" else "语音输入",
                        subtitle = "按住即可说话，自动识别评论",
                        checked = voiceInputEnabled,
                        onCheckedChange = { viewModel.toggleVoiceInput() }
                    )
                    ElderToggleRow(
                        icon = Icons.Default.Edit,
                        title = if (isEnglish) "Handwriting Input" else "手写输入",
                        subtitle = "方便长辈手写汉字输入",
                        checked = handwritingEnabled,
                        onCheckedChange = { viewModel.toggleHandwriting() }
                    )
                    ElderToggleRow(
                        icon = Icons.Default.ViewCompact,
                        title = if (isEnglish) "Simple UI" else "简洁界面",
                        subtitle = "隐藏复杂功能，保留超大图文",
                        checked = simpleUiEnabled,
                        onCheckedChange = { viewModel.toggleSimpleUi() }
                    )
                    ElderToggleRow(
                        icon = Icons.Default.NotificationsActive,
                        title = if (isEnglish) "Voice Reminder" else "语音提醒",
                        subtitle = "朗读纪念日提醒与互动通知",
                        checked = voiceReminderEnabled,
                        onCheckedChange = { viewModel.toggleVoiceReminder() }
                    )
                    ElderToggleRow(
                        icon = Icons.Default.Contrast,
                        title = if (isEnglish) "High-Contrast Visual Mode" else "视障高对比度显示模式 (AAA 级)",
                        subtitle = if (isEnglish) "Pure black background & vivid yellow/white text for low vision" else "纯黑底色、明亮荧黄与高反差白字，保障弱视长辈清晰读写",
                        checked = isHighContrastMode,
                        onCheckedChange = { viewModel.toggleHighContrastMode(it) }
                    )
                }
            }

            // Family Guardian Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_elder_portrait_1785079069040),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isEnglish) "Family Guardian" else "家庭守护者 Guardian",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "陈小雅 Daughter · Admin",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showElderSlideshow) {
            com.example.ui.components.ElderPhotoSlideshowDialog(
                memories = filteredMemories,
                isEnglish = isEnglish,
                fontScale = fontScale,
                onDismiss = { showElderSlideshow = false }
            )
        }
    }
}

@Composable
fun ElderToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GoldenGradientStart.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = GoldenGradientStart)
        )
    }
}
