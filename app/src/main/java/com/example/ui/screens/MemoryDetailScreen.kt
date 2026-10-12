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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientStart

@Composable
fun MemoryDetailScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "Memory Detail" else "回忆详情",
            englishTitle = "MEMORY DETAIL",
            showBack = true,
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) },
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() }
        )

        var showRestoreDialog by remember { mutableStateOf(false) }
        val memories by viewModel.memories.collectAsState()
        val haptic = LocalHapticFeedback.current

        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val scrollToTopEvent by viewModel.scrollToTopEvent.collectAsState()
        androidx.compose.runtime.LaunchedEffect(scrollToTopEvent) {
            if (scrollToTopEvent > 0L) {
                listState.animateScrollToItem(0)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Carousel Box
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_family_reunion_1785079042483),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gemini AI Photo Restore Floating Button
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showRestoreDialog = true
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = GoldenGradientStart,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEnglish) "✨ AI Restore" else "✨ AI 旧照修复",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Page Indicator Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("3 / 12", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Requirement 3: Linked Family Members in Digital Family Tree (Tap Face to See All Photos)
            item {
                var selectedFaceMember by remember { mutableStateOf<Pair<String, Int>?>(null) }
                val haptic = LocalHapticFeedback.current

                val taggedMembers = listOf(
                    Triple("陈建国", "太爷爷 · 1948年", R.drawable.img_anniversary_golden_1785079056294),
                    Triple("李秀英", "太奶奶 · 1951年", R.drawable.img_elder_portrait_1785079069040),
                    Triple("陈小雅", "女儿 · 1982年", R.drawable.img_elder_portrait_1785079069040)
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "👤 Tagged Faces in Photo (Family Tree)" else "👤 照片识别人物 (四代家族树联动)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnglish) "Tap face to view all photos" else "点击人脸看个人独家相册 ➔",
                                fontSize = 11.sp,
                                color = GoldenGradientStart,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            taggedMembers.forEach { (name, role, avatar) ->
                                Surface(
                                    modifier = Modifier.clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedFaceMember = name to avatar
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart),
                                    shadowElevation = 2.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = avatar),
                                            contentDescription = name,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = role,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Modal Popup showing photos associated with selected face member
                selectedFaceMember?.let { (memberName, avatarRes) ->
                    androidx.compose.ui.window.Dialog(onDismissRequest = { selectedFaceMember = null }) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 8.dp
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = avatarRes),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = memberName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (isEnglish) "Digital Family Tree Member" else "数字家族树·核心成员",
                                                fontSize = 11.sp,
                                                color = GoldenGradientStart
                                            )
                                        }
                                    }

                                    androidx.compose.material3.IconButton(onClick = { selectedFaceMember = null }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Close", modifier = Modifier.size(20.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = if (isEnglish) "Photos linked to $memberName:" else "与 $memberName 关联的 12 张精彩全家福与个人照：",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Linked Photo Grid Preview
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf(
                                        R.drawable.img_anniversary_golden_1785079056294 to "1976年婚礼",
                                        R.drawable.img_elder_portrait_1785079069040 to "2025年特写",
                                        R.drawable.img_family_reunion_1785079042483 to "2026全家福"
                                    ).forEach { (imgRes, label) ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(90.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                        ) {
                                            Image(
                                                painter = painterResource(id = imgRes),
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.BottomCenter)
                                                    .fillMaxWidth(),
                                                color = Color.Black.copy(alpha = 0.65f)
                                            ) {
                                                Text(
                                                    text = label,
                                                    fontSize = 9.sp,
                                                    color = Color.White,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                    modifier = Modifier.padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                androidx.compose.material3.Button(
                                    onClick = {
                                        selectedFaceMember = null
                                        viewModel.navigateTo(AppScreen.FAMILY_TREE)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GoldenGradientStart)
                                ) {
                                    Text(
                                        text = if (isEnglish) "View in Family Tree" else "🌳 在数字家族树中定位该成员",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tags
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TagChip("金婚")
                    TagChip("家庭聚会")
                    TagChip("2026年")
                }
            }

            // Title & Location
            item {
                Column {
                    Text(
                        text = if (isEnglish) "Annual Family Reunion" else "家庭年度聚会",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "北京·朝阳 · 2026-06-18 · Canon R6",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Rating Bar
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isEnglish) "Precious Rating" else "珍贵指数 Precious Rating",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldenGradientStart,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Text("4.8 / 5.0", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GoldenGradientStart)
                    }
                }
            }

            // Story Text & Gemini AI Caption Section
            item {
                var captionText by remember { mutableStateOf("今年的家庭聚会，爸妈笑得格外开心。五十载风雨兼程，如今儿孙满堂，岁月如歌，家是我们最温暖的归宿。") }
                var isEditingCaption by remember { mutableStateOf(false) }
                var isGeneratingCaption by remember { mutableStateOf(false) }
                var editedCaptionInput by remember { mutableStateOf(captionText) }
                val coroutineScope = rememberCoroutineScope()
                val haptic = LocalHapticFeedback.current

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = GoldenGradientStart,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Gemini AI Photo Caption" else "Gemini AI 智能回忆图说",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row {
                                androidx.compose.material3.IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        coroutineScope.launch {
                                            isGeneratingCaption = true
                                            kotlinx.coroutines.delay(1200)
                                            captionText = if (isEnglish)
                                                "Generational portrait captured during Golden Jubilee (2026). Grandparents surround children with warmth and joy, preserving 50 years of family lineage."
                                            else
                                                "2026年陈氏五十载金婚盛典合影。老人家眼含深情喜笑颜开，四代后辈环绕膝下，凝聚半世纪家庭风骨与无价血脉亲情。"
                                            isGeneratingCaption = false
                                        }
                                    },
                                    enabled = !isGeneratingCaption,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    if (isGeneratingCaption) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = GoldenGradientStart,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Regenerate",
                                            tint = GoldenGradientStart,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                androidx.compose.material3.IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        editedCaptionInput = captionText
                                        isEditingCaption = true
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Caption",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = captionText,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "AI Context: Family Reunion 2026" else "AI 家族语境：2026年合影 · 关联3位长辈",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                modifier = Modifier.clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    editedCaptionInput = captionText
                                    isEditingCaption = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = GoldenGradientStart.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isEnglish) "Edit Family Context" else "✏️ 编辑保存家族语境",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenGradientStart,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                if (isEditingCaption) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { isEditingCaption = false }) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = if (isEnglish) "Edit Family Caption & Context" else "编辑照片图说与家族传承语境",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isEnglish) "Modify the AI caption to add special family terms, dates, or memories." else "自定义添加长辈别称、特殊纪念日或传承故事，永久保存至相册。",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = editedCaptionInput,
                                    onValueChange = { editedCaptionInput = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    shape = RoundedCornerShape(14.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    androidx.compose.material3.TextButton(onClick = { isEditingCaption = false }) {
                                        Text(if (isEnglish) "Cancel" else "取消")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    androidx.compose.material3.Button(
                                        onClick = {
                                            captionText = editedCaptionInput
                                            isEditingCaption = false
                                        },
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GoldenGradientStart)
                                    ) {
                                        Text(if (isEnglish) "Save Caption" else "保存图说")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Action Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ActionIconItem(icon = Icons.Default.Favorite, label = "42 喜欢", tint = Color(0xFFE53935))
                    ActionIconItem(icon = Icons.Default.ChatBubbleOutline, label = "8 评论")
                    ActionIconItem(icon = Icons.Default.Share, label = "分享")
                }
            }

            // Comments
            item {
                Text(
                    text = if (isEnglish) "Comments (8)" else "评论区 (8)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                CommentBubble(
                    name = "陈妈妈",
                    role = "长辈",
                    text = "看哭了😭太美了！谢谢孩子们❤️",
                    timeAgo = "刚刚",
                    avatarRes = R.drawable.img_elder_portrait_1785079069040
                )
            }

            item {
                CommentBubble(
                    name = "小雅",
                    role = "女儿",
                    text = "爸妈，你们是我的榜样！50年的爱太感人了！",
                    timeAgo = "2小时前",
                    avatarRes = R.drawable.img_family_reunion_1785079042483
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        // Bottom Voice Input Comment Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text(if (isEnglish) "Voice or text comment..." else "长辈按住说话或输入评论...", fontSize = 13.sp) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GoldenGradientStart)
                        .clickable { newCommentText = "" },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { newCommentText = "" },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }

        if (showRestoreDialog) {
            val sampleMem = memories.firstOrNull() ?: com.example.data.MemoryEntity(
                id = "detail_mem",
                title = "家庭聚会老照片",
                titleEn = "Family Reunion Vintage Photo",
                description = "五十载金婚老照片记载",
                descriptionEn = "Golden jubilee family milestone",
                location = "北京",
                locationEn = "Beijing",
                date = "1976-05-20",
                cameraModel = "Kodak Retro",
                photoResNames = "img_elder_portrait",
                likesCount = 35,
                commentsCount = 8,
                rating = 5.0f,
                isEncrypted = false,
                tags = "金婚,老照片"
            )

            com.example.ui.components.AiPhotoRestoreDialog(
                memory = sampleMem,
                isEnglish = isEnglish,
                onRestoredSave = {
                    // Saved
                },
                onDismiss = { showRestoreDialog = false }
            )
        }
    }
}

@Composable
fun TagChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GoldenGradientStart.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(label, fontSize = 12.sp, color = GoldenGradientStart, fontWeight = FontWeight.Bold)
    }
}
