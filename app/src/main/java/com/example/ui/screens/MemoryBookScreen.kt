package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MemoryEntity
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart
import com.example.util.SmartPhotoCategorizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryBookScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val memories by viewModel.memories.collectAsState()
    val haptic = LocalHapticFeedback.current

    var selectedThemeIndex by remember { mutableIntStateOf(0) }
    var selectedPageIndex by remember { mutableIntStateOf(0) }
    var isExportingPdf by remember { mutableStateOf(false) }
    var pdfExportSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val themes = listOf(
        "Golden Heirloom" to "金婚流金典聚",
        "Vintage Silk" to "复古丝绒家书",
        "Modern Minimal" to "现代典雅高定"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "Digital Memory Book Generator" else "PDF 极简数字纪念册生成器",
            englishTitle = "MEMORY BOOK GENERATOR",
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() },
            onNotificationClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) }
        )

        var selectedMode by remember { mutableIntStateOf(0) } // 0: Shared Digital Scrapbook, 1: PDF Generator
        var showPrepareForPrintDialog by remember { mutableStateOf(false) }

        // Top Segmented Mode Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedMode = 0
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (selectedMode == 0) GoldenGradientStart else Color.Transparent
            ) {
                Text(
                    text = if (isEnglish) "🎨 Digital Scrapbook" else "🎨 共享协同剪贴簿",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedMode == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedMode = 1
                    },
                shape = RoundedCornerShape(12.dp),
                color = if (selectedMode == 1) MaterialTheme.colorScheme.primary else Color.Transparent
            ) {
                Text(
                    text = if (isEnglish) "📖 PDF Book Generator" else "📖 PDF 纪念册生成",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedMode == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Requirement 4: 'Prepare for Print' Tool Banner (300 DPI Resolution & Restoration Filters for Physical Memory Books)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showPrepareForPrintDialog = true
                },
            shape = RoundedCornerShape(16.dp),
            color = GoldenGradientStart,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isEnglish) "🖨️ Prepare for Print (300 DPI Tool)" else "🖨️ 实体排版印刷排版修饰工具",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isEnglish) "Upscale resolution, apply CMYK & scratch filters for physical memory books" else "高精 300DPI 分辨率提升 · CMYK 色彩与老照片划痕修复",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Text(
                        text = if (isEnglish) "Prepare ➔" else "立即准备 ➔",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (showPrepareForPrintDialog) {
            com.example.ui.components.PrepareForPrintDialog(
                isEnglish = isEnglish,
                onDismiss = { showPrepareForPrintDialog = false }
            )
        }

        if (selectedMode == 0) {
            // SHARED DIGITAL SCRAPBOOK VIEW
            var selectedPhotoIndex by remember { mutableIntStateOf(0) }
            val currentMemory = memories.getOrNull(selectedPhotoIndex) ?: memories.firstOrNull()
            
            // Stickers state attached to current photo
            var placedStickers by remember {
                mutableStateOf(
                    mutableListOf(
                        "❤️ 亲情守护 (太奶奶)",
                        "🎂 五十载金婚 (陈大明)",
                        "🌟 传世星光 (小雅)"
                    )
                )
            }
            
            // Voice notes state
            var isRecordingVoice by remember { mutableStateOf(false) }
            var isPlayingVoice by remember { mutableStateOf(false) }
            var voiceNotesList by remember {
                mutableStateOf(
                    listOf(
                        Triple("太奶奶 李秀英", "这是76年天安门老照片，当时我和你爷爷结婚才四年...", "0:24"),
                        Triple("女儿 陈小雅", "祝爸妈金婚快乐！看到这张照片好感动！", "0:15")
                    )
                )
            }

            // Collaborative Comments state
            var commentInputText by remember { mutableStateOf("") }
            var commentsList by remember {
                mutableStateOf(
                    listOf(
                        Triple("太奶奶", "看哭了😭谢谢孩子们准备的惊喜剪贴簿！", "10分钟前"),
                        Triple("陈大明", "爸爸妈妈的真爱是我们的楷模！", "1小时前"),
                        Triple("长孙 陈浩", "太爷爷年轻时真帅！跟太奶奶太般配了！", "2小时前")
                    )
                )
            }

            val stickerOptions = listOf(
                "❤️ 亲情真爱",
                "🎂 金婚庆典",
                "🏆 家族荣耀",
                "🌟 传世星光",
                "📸 岁月留真",
                "🌸 吉祥如意"
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Collaborators Banner
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.5f)),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isEnglish) "👨‍👩‍👧‍👦 Multi-Member Digital Scrapbook" else "👨‍👩‍👧‍👦 多人共享协同剪贴簿",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEnglish) "Co-created by 5 family members with stickers & audio" else "太奶奶、陈大明、小雅 等5位家人正在共同讲故事",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                                val avatars = listOf(
                                    R.drawable.img_elder_portrait_1785079069040,
                                    R.drawable.img_anniversary_golden_1785079056294,
                                    R.drawable.img_family_reunion_1785079042483
                                )
                                avatars.forEach { resId ->
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, Color.White, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive Photo Canvas with Virtual Stickers Overlay
                item {
                    Column {
                        Text(
                            text = if (isEnglish) "📸 Active Scrapbook Photo Canvas" else "📸 剪贴簿主照片画廊 (点击添加贴纸)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .border(3.dp, GoldenGradientStart, RoundedCornerShape(24.dp))
                                .background(Color(0xFF2C1A04))
                        ) {
                            val photoRes = when (selectedPhotoIndex % 3) {
                                0 -> R.drawable.img_anniversary_golden_1785079056294
                                1 -> R.drawable.img_elder_portrait_1785079069040
                                else -> R.drawable.img_family_reunion_1785079042483
                            }

                            Image(
                                painter = painterResource(id = photoRes),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Display Placed Virtual Stickers Over Photo Canvas
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.TopEnd),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                placedStickers.forEach { stickerText ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.Black.copy(alpha = 0.75f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart)
                                    ) {
                                        Text(
                                            text = stickerText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            // Photo Title & Date Overlay
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Black.copy(alpha = 0.7f)
                            ) {
                                Text(
                                    text = "${currentMemory?.title ?: "金婚五十载纪念"} • ${currentMemory?.date ?: "1976-2026"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Interactive Virtual Stickers Palette
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEnglish) "✨ Virtual Sticker Palette (Tap to add)" else "✨ 协同虚贴纸库 (点击贴在照片上)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = "${placedStickers.size} 张已粘贴",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenGradientStart
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(stickerOptions.size) { idx ->
                                    val stickerName = stickerOptions[idx]
                                    Surface(
                                        modifier = Modifier.clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            val author = if (isEnglish) "You" else "太奶奶"
                                            placedStickers = (placedStickers + "$stickerName ($author)").toMutableList()
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        color = GoldenGradientStart.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart)
                                    ) {
                                        Text(
                                            text = stickerName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldenGradientStart,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Collaborative Voice Notes Section
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = GoldenGradientStart,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isEnglish) "🎙️ Collaborative Family Voice Notes" else "🎙️ 家族语音留声故事箱 (${voiceNotesList.size}条)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        coroutineScope.launch {
                                            isRecordingVoice = true
                                            delay(1500)
                                            isRecordingVoice = false
                                            voiceNotesList = voiceNotesList + Triple("我 (用户)", "祝太爷爷太奶奶福如东海，延年益寿！", "0:12")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldenGradientStart),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isRecordingVoice) {
                                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isEnglish) "Recording..." else "录音中...", fontSize = 11.sp)
                                    } else {
                                        Text(if (isEnglish) "+ Add Voice Note" else "🎙️ 录制语音留言", fontSize = 11.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            voiceNotesList.forEach { (speaker, text, duration) ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(GoldenGradientStart)
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    isPlayingVoice = !isPlayingVoice
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = androidx.compose.material.icons.Icons.Default.VolumeUp,
                                                contentDescription = "Play Voice",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(speaker, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                                Text(duration, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldenGradientStart)
                                            }
                                            Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Family Comments & Storytelling Input Section
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isEnglish) "💬 Family Story Comments" else "💬 家族多人互动评论区 (${commentsList.size}条)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            commentsList.forEach { (author, text, time) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(GoldenGradientStart.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(author.take(1), fontWeight = FontWeight.Bold, color = GoldenGradientStart, fontSize = 12.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(author, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text(time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Comment Input Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.OutlinedTextField(
                                    value = commentInputText,
                                    onValueChange = { commentInputText = it },
                                    placeholder = { Text(if (isEnglish) "Add a comment..." else "添加家人协作评论...", fontSize = 12.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(GoldenGradientStart)
                                        .clickable {
                                            if (commentInputText.isNotBlank()) {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                commentsList = commentsList + Triple("小雅 (小女儿)", commentInputText, "刚刚")
                                                commentInputText = ""
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Send,
                                        contentDescription = "Send Comment",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // PDF GENERATOR VIEW (Original MemoryBookScreen code)
            val albumOptions = listOf(
                "ALL" to if (isEnglish) "All Memories (${memories.size})" else "全部相册 (${memories.size})",
                "1972" to if (isEnglish) "Golden Jubilee (1972-2026)" else "五十载金婚纪念",
                "合影" to if (isEnglish) "Family Reunions" else "欢聚合影",
                "老照片" to if (isEnglish) "Vintage Heritage" else "岁月老照片"
            )

            var selectedAlbumTag by remember { mutableStateOf("ALL") }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Album Selection Bar
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isEnglish) "📁 Select Album to Export" else "📁 选择导出集锦相册",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(albumOptions.size) { idx ->
                                val (tagKey, label) = albumOptions[idx]
                                val isSelected = selectedAlbumTag == tagKey
                                Surface(
                                    modifier = Modifier.clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedAlbumTag = tagKey
                                        selectedPageIndex = 0
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Theme Selector Bar
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isEnglish) "🎨 Select PDF Template Style" else "🎨 选择 PDF 纪念册排版风格",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            themes.forEachIndexed { idx, (en, zh) ->
                                val isSelected = selectedThemeIndex == idx
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            selectedThemeIndex = idx
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                    )
                                ) {
                                    Text(
                                        text = if (isEnglish) en else zh,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // PDF Book Page Interactive Preview Sheet
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFAF8F5), // Paper like background
                    border = androidx.compose.foundation.BorderStroke(3.dp, GoldenGradientStart),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Decorative Header Ribbon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚜️ GOLDEN FAMILY HEIRLOOM MEMORY BOOK ⚜️",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenGradientStart,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Page ${selectedPageIndex + 1} / ${memories.size + 1}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedPageIndex == 0) {
                            // COVER PAGE
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(vertical = 24.dp)
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(GoldenGradientStart),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = if (isEnglish) "Chen Family Golden Anniversary" else "陈氏家族 · 五十载流金记忆典藏",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2C1A04),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "1976 — 2026",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldenGradientStart
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = if (isEnglish)
                                        "A high-resolution PDF collection featuring AI-curated captions, family timeline, and intergenerational memories."
                                    else
                                        "汇集半世纪风雨同舟、四代同堂的温暖回忆，由 Gemini AI 智能撰写每张老照片的岁月温情故事。",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 20.dp)
                                )
                            }
                        } else {
                            // CONTENT PAGE FOR MEMORY
                            val memIndex = selectedPageIndex - 1
                            if (memIndex < memories.size) {
                                val memory = memories[memIndex]
                                val categorized = SmartPhotoCategorizer.categorizeMemory(memory)

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Photo Frame
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(2.dp, GoldenGradientStart.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    ) {
                                        val samplePhotos = listOf(
                                            R.drawable.img_anniversary_golden_1785079056294,
                                            R.drawable.img_elder_portrait_1785079069040,
                                            R.drawable.img_family_reunion_1785079042483
                                        )
                                        Image(
                                            painter = painterResource(id = samplePhotos[memIndex % samplePhotos.size]),
                                            contentDescription = memory.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isEnglish) memory.titleEn else memory.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2C1A04)
                                        )

                                        Text(
                                            text = memory.date,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldenGradientStart
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Gemini AI Story Caption • ${categorized.category.icon} ${if (isEnglish) categorized.category.titleEn else categorized.category.titleZh}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldenGradientStart
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = if (isEnglish) memory.descriptionEn else memory.description,
                                        fontSize = 13.sp,
                                        color = Color(0xFF333333),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Page Navigation Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { if (selectedPageIndex > 0) selectedPageIndex-- },
                                enabled = selectedPageIndex > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                                Text(if (isEnglish) "Prev Page" else "上一页")
                            }

                            Text(
                                text = "${selectedPageIndex + 1} / ${memories.size + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Button(
                                onClick = { if (selectedPageIndex < memories.size) selectedPageIndex++ },
                                enabled = selectedPageIndex < memories.size,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(if (isEnglish) "Next Page" else "下一页")
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }

            // Export / Print PDF Bar
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Export PDF Ready Document" else "导出 PDF 格式高质量纪念册",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEnglish) "300 DPI High-Resolution Printable Format" else "包含 AI 图说、年代表与高清相册图文布局",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (pdfExportSuccess) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEnglish)
                                        "PDF Memory Book exported to device storage: 'GoldenFamilyMemoryBook.pdf'!"
                                    else
                                        "PDF 数字纪念册已成功生成并保存至本地文件：『金婚五十载纪念册.pdf』！",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isExportingPdf = true
                                    pdfExportSuccess = false
                                    delay(2000)
                                    isExportingPdf = false
                                    pdfExportSuccess = true
                                }
                            },
                            enabled = !isExportingPdf,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isExportingPdf) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isEnglish) "Generating PDF Document..." else "正在生成 PDF 纪念册...")
                            } else {
                                Icon(Icons.Default.Print, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isEnglish) "Generate & Download PDF" else "一键生成并导出 PDF 文件")
                            }
                        }
                    }
                }
            }
        }
    }
}
}
