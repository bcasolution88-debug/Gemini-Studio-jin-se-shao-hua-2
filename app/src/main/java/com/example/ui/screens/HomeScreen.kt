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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart

import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import com.example.data.MemoryEntity
import com.example.ui.components.CameraCaptureView
import com.example.ui.components.InteractiveTourOverlay
import com.example.util.OnThisDayUtil

@Composable
fun HomeScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val memories by viewModel.memories.collectAsState()
    val familyActivities by viewModel.familyActivities.collectAsState()
    val isElderMode by viewModel.isElderMode.collectAsState()
    val showTour by viewModel.showInteractiveTour.collectAsState()

    // Requirement 1: Search History State
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val isSearchDropdownExpanded by viewModel.isSearchDropdownExpanded.collectAsState()

    // Requirement 2: Multi-Photo Select & Firebase Dynamic Links Sharing State
    val isMultiSelectMode by viewModel.isMultiSelectMode.collectAsState()
    val selectedMemoryIds by viewModel.selectedMemoryIds.collectAsState()
    val showFamilyShareLinkDialog by viewModel.showFamilyShareLinkDialog.collectAsState()
    val generatedDynamicLink by viewModel.generatedDynamicLink.collectAsState()
    val isGeneratingDynamicLink by viewModel.isGeneratingDynamicLink.collectAsState()

    // Monthly Recap, Cloud Sync, and AI Photo Classification State
    val showMonthlyRecapDialog by viewModel.showMonthlyRecapDialog.collectAsState()
    val isSyncingToCloud by viewModel.isSyncingToCloud.collectAsState()
    val lastCloudSyncTime by viewModel.lastCloudSyncTime.collectAsState()
    val cloudSyncMessage by viewModel.cloudSyncMessage.collectAsState()
    val selectedAiCategoryFilter by viewModel.selectedAiCategoryFilter.collectAsState()
    val isClassifyingPhotos by viewModel.isClassifyingPhotos.collectAsState()
    val showMapGalleryDialog by viewModel.showMapGalleryDialog.collectAsState()

    var showCameraOverlay by remember { mutableStateOf(false) }

    // Filter memories based on search query and Gemini AI classification filter chip
    val filteredMemories = remember(memories, searchQuery, selectedAiCategoryFilter) {
        memories.filter { mem ->
            val matchesQuery = searchQuery.isBlank() ||
                    mem.title.contains(searchQuery, ignoreCase = true) ||
                    mem.titleEn.contains(searchQuery, ignoreCase = true) ||
                    mem.tags.contains(searchQuery, ignoreCase = true) ||
                    mem.description.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedAiCategoryFilter) {
                "FAMILY" -> mem.tags.contains("全家福") || mem.tags.contains("合影") || mem.tags.contains("聚会") || mem.title.contains("合影") || mem.title.contains("全家福") || mem.title.contains("Family")
                "ANNIVERSARY" -> mem.tags.contains("金婚") || mem.tags.contains("婚礼") || mem.tags.contains("50") || mem.title.contains("金婚") || mem.title.contains("婚礼")
                "VINTAGE" -> mem.tags.contains("老照片") || mem.date.startsWith("19") || mem.tags.contains("老宅") || mem.title.contains("老照片") || mem.title.contains("1976")
                "PORTRAIT" -> mem.tags.contains("长辈") || mem.tags.contains("肖像") || mem.tags.contains("特写") || mem.title.contains("肖像")
                "MILESTONES" -> mem.tags.contains("节点") || mem.tags.contains("聚会") || mem.tags.contains("重聚") || mem.title.contains("聚会")
                "TRAVEL" -> mem.tags.contains("旅行") || mem.tags.contains("北京") || mem.location.contains("北京") || mem.title.contains("北京")
                else -> true
            }

            matchesQuery && matchesCategory
        }
    }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scrollToTopEvent by viewModel.scrollToTopEvent.collectAsState()
    androidx.compose.runtime.LaunchedEffect(scrollToTopEvent) {
        if (scrollToTopEvent > 0L) {
            listState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopHeaderBar(
                title = if (isEnglish) "Golden Memory" else "家庭共享相册",
                englishTitle = "GOLDEN MEMORY",
                isEnglishLang = isEnglish,
                onLangToggle = { viewModel.toggleLanguage() },
                onNotificationClick = { viewModel.selectTab(com.example.ui.MainTab.MESSAGES) }
            )

            // Requirement 1: Search Bar with Local Search History Dropdown
            com.example.ui.components.SearchBarWithHistory(
                searchQuery = searchQuery,
                searchHistory = searchHistory,
                isDropdownExpanded = isSearchDropdownExpanded,
                isEnglish = isEnglish,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                onSearchExecute = { viewModel.executeSearch(it) },
                onRemoveHistoryItem = { viewModel.removeSearchHistoryItem(it) },
                onClearHistory = { viewModel.clearSearchHistory() },
                onDropdownToggle = { viewModel.setSearchDropdownExpanded(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Requirement 2: Multi-Photo Selection & Firebase Dynamic Share Link Action Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isMultiSelectMode) GoldenGradientStart.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isMultiSelectMode) GoldenGradientStart else Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        androidx.compose.material3.IconButton(
                            onClick = { viewModel.toggleMultiSelectMode() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isMultiSelectMode) androidx.compose.material.icons.Icons.Default.CheckCircle else androidx.compose.material.icons.Icons.Default.PhotoLibrary,
                                contentDescription = "Toggle Multi Select",
                                tint = if (isMultiSelectMode) GoldenGradientStart else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (isMultiSelectMode) (if (isEnglish) "${selectedMemoryIds.size} Photos Selected" else "多选模式：已选中 ${selectedMemoryIds.size} 张相片") else (if (isEnglish) "Multi-Photo Family Share" else "多图选集 · Firebase 动态链接分享"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isMultiSelectMode) (if (isEnglish) "Tap photos below to select/unselect" else "点击下方相片勾选，一键生成全家动态分享链接") else (if (isEnglish) "Select photos to generate 1-click share link" else "勾选多张相片，合成 Firebase 动态分享链接发送家人"),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (isMultiSelectMode) {
                            androidx.compose.material3.Button(
                                onClick = { viewModel.generateFamilyShareLinkForSelected() },
                                enabled = selectedMemoryIds.isNotEmpty(),
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GoldenGradientStart),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isEnglish) "Share Link" else "生成分享链接",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        } else {
                            androidx.compose.material3.OutlinedButton(
                                onClick = { viewModel.selectAllMemories() },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isEnglish) "Select Photos" else "开启多选",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Requirement 2: 'Sync to Cloud' (Firebase Firestore) Action Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clickable { viewModel.syncToCloudWithFirestore() },
                shape = RoundedCornerShape(16.dp),
                color = GoldenGradientStart.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(GoldenGradientStart),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSyncingToCloud) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Sync to Cloud",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Sync to Cloud (Firebase Firestore)" else "☁️ 备份至云端 (Firebase 数据库)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isSyncingToCloud) (if (isEnglish) "Syncing albums & AI tags..." else "正在全量备份非敏感相册与 AI 标签...") else lastCloudSyncTime,
                                fontSize = 10.sp,
                                color = GoldenGradientStart
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldenGradientStart
                    ) {
                        Text(
                            text = if (isSyncingToCloud) "..." else (if (isEnglish) "Sync Now" else "一键备份"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (cloudSyncMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1B5E20)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cloudSyncMessage!!,
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        androidx.compose.material3.IconButton(
                            onClick = { viewModel.dismissCloudSyncMessage() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Quick Elder Mode Bar & Replay Tour Quick Access
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.clickable { viewModel.toggleElderMode() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isElderMode) GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Elderly,
                            contentDescription = null,
                            tint = if (isElderMode) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isElderMode) (if (isEnglish) "Elder Mode: ON (125% Font)" else "关怀模式：已开启 (125%特大字)") else (if (isEnglish) "Elder Mode" else "关怀大字模式"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isElderMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.clickable { viewModel.startTour() },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEnglish) "App Tour" else "亲友引导",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Requirement 1: Memory of the Day notification component with Gemini narrative
                item {
                    com.example.ui.components.MemoryOfTheDayCard(
                        memories = memories,
                        isEnglish = isEnglish,
                        isHighContrast = viewModel.isHighContrastMode.value,
                        fontScale = viewModel.fontScale.value,
                        onSelectMemory = { memId -> viewModel.selectMemory(memId) },
                        onSendNotification = { msg -> viewModel.shareCollageToFamilyFeed() }
                    )
                }

                // Requirement 1: Gemini Monthly Recap Collage Banner
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openMonthlyRecapDialog() },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldenGradientStart),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(GoldenGradientStart),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GridOn,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "✨ Gemini Monthly Recap Collage" else "✨ Gemini 7 月月度精选拼图",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEnglish) "Auto selects top 5 best photos & arranges in 5-grid layout" else "AI 自动挑选全月最佳 5 张照片，一秒生成 5 连拼排版",
                                        fontSize = 11.sp,
                                        color = GoldenGradientStart
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GoldenGradientStart
                            ) {
                                Text(
                                    text = if (isEnglish) "TOP 5 ➔" else "精选 5 张 ➔",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Requirement 2: Gemini Collage of the Week Quick Access Banner
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openCollageOfTheWeek() },
                        shape = RoundedCornerShape(20.dp),
                        color = GoldenGradientStart,
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "✨ Gemini Collage of the Week" else "✨ Gemini 本周精选回忆拼图",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = if (isEnglish) "Auto compiles recent photos & AI captions to share with family" else "AI 智能自动整理近七日照片与感言，一键一秒分享全家",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Family Album Card
                item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shadowElevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Our Family Album" else "我们的幸福 💍",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEnglish) "Our Family Album · 11 Members" else "Our Family Album · 11 Members",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Avatars
                            Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                                AvatarBadge(resId = R.drawable.img_elder_portrait_1785079069040)
                                AvatarBadge(resId = R.drawable.img_anniversary_golden_1785079056294)
                                AvatarBadge(resId = R.drawable.img_family_reunion_1785079042483)
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("+2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isEnglish) "Chen Family · 50 Wonderful Years" else "陈爸爸 & 陈妈妈 · 幸福 50 年",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldenGradientStart
                        )
                        Text(
                            text = "Together for 50 wonderful years",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Countdown Widget Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.GOLDEN_ANNIVERSARY) },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(GoldenGradientStart, GoldenGradientEnd)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "00",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnglish) "DAYS" else "天后",
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = GoldenGradientStart,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Golden Anniversary · 50 Years" else "金婚纪念日 · 50年",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Golden Anniversary · DAYS UNTIL",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clickable { viewModel.navigateTo(AppScreen.NEW_MEMORY) },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Create Album" else "创建相册",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Create Album",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                            .clickable { viewModel.navigateTo(AppScreen.VIDEO_STUDIO) },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoCall,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Make Video" else "制作视频",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Make Video",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Slideshow & PDF Memory Book
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clickable { viewModel.navigateTo(AppScreen.SLIDESHOW) },
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Slideshow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Slideshow Mode" else "幻灯片放映",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEnglish) "Pan/Zoom & AI Captions" else "自动推拉 · 暂停看AI图说",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(76.dp)
                            .clickable { viewModel.navigateTo(AppScreen.MEMORY_BOOK) },
                        shape = RoundedCornerShape(20.dp),
                        color = GoldenGradientStart.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GoldenGradientStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "PDF Memory Book" else "PDF 纪念册",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEnglish) "Printable PDF Layout" else "高精图文排版 · 一键导出",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // RECENT FAMILY ACTIVITY FEED SECTION
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Recent Family Activity" else "最新家庭动态与互动",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEnglish) "Timeline of photo uploads & comments" else "亲友上传照片、留言点赞实时互动动态",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Render each family activity card
                        familyActivities.forEach { act ->
                            var quickCommentText by remember { mutableStateOf("") }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AvatarBadge(resId = act.avatarResId)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (isEnglish) act.userNameEn else act.userName,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = act.timestamp + " · " + (if (isEnglish) act.targetMemoryTitleEn else act.targetMemoryTitle),
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = CircleShape,
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = when (act.activityType) {
                                                    com.example.data.ActivityType.NEW_PHOTO -> if (isEnglish) "New Photo" else "新照片"
                                                    com.example.data.ActivityType.NEW_COMMENT -> if (isEnglish) "Comment" else "留言评论"
                                                    com.example.data.ActivityType.LIKE_PHOTO -> if (isEnglish) "Liked" else "给照片点赞"
                                                    com.example.data.ActivityType.AI_CAPTION -> if (isEnglish) "AI Caption" else "AI 图说"
                                                },
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = if (isEnglish) act.contentEn else act.content,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )

                                    // Render comments if any
                                    if (act.commentsList.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            act.commentsList.forEach { commentStr ->
                                                Text(
                                                    text = "💬 $commentStr",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Likes & Quick Comment Bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.clickable { viewModel.likeActivityItem(act.id) },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ThumbUp,
                                                contentDescription = "Like",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${act.likesCount} " + (if (isEnglish) "Likes" else "赞"),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        // Inline Quick Comment Field
                                        Row(
                                            modifier = Modifier.weight(1f).padding(start = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = quickCommentText,
                                                onValueChange = { quickCommentText = it },
                                                placeholder = { Text(if (isEnglish) "Quick comment..." else "表达关心或评论...", fontSize = 11.sp) },
                                                modifier = Modifier.weight(1f).height(44.dp),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                                    focusedContainerColor = MaterialTheme.colorScheme.surface
                                                )
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .clickable {
                                                        if (quickCommentText.isNotBlank()) {
                                                            viewModel.addCommentToActivity(act.id, "我: $quickCommentText")
                                                            quickCommentText = ""
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Requirement 2: Map-based GPS Gallery View Banner
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clickable { viewModel.openMapGallery() },
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.6f)),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(GoldenGradientStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "🗺️ GPS Map-Based Photo Gallery" else "🗺️ 家族足迹 GPS 地图相册",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isEnglish) "Cluster photos by geographic location & explore map pins" else "按经纬度聚合各地相册，点击地图标记按城市查阅",
                                    fontSize = 11.sp,
                                    color = GoldenGradientStart
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GoldenGradientStart
                        ) {
                            Text(
                                text = if (isEnglish) "Open Map ➔" else "打开地图 ➔",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Requirement 3: AI-driven Classification Engine Quick-Filter Chips
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
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
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEnglish) "AI Vision Photo Categorization" else "🤖 Gemini 视觉 AI 智能分类",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            modifier = Modifier.clickable { viewModel.runGeminiPhotoClassification() },
                            shape = RoundedCornerShape(12.dp),
                            color = GoldenGradientStart.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isClassifyingPhotos) {
                                    androidx.compose.material3.CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp),
                                        color = GoldenGradientStart,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (isClassifyingPhotos) (if (isEnglish) "Analysing..." else "分析中...") else (if (isEnglish) "Re-Classify" else "⚡ AI 重新识别"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenGradientStart
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick-Filter Chips Horizontal Scroll Row
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val filterCategories = listOf(
                            "ALL" to (if (isEnglish) "🌟 All (${memories.size})" else "🌟 全部 (${memories.size})"),
                            "FAMILY" to (if (isEnglish) "👨‍👩‍👧‍👦 Family (5)" else "👨‍👩‍👧‍👦 家族合影 (5)"),
                            "ANNIVERSARY" to (if (isEnglish) "🏆 Golden Jubilee (3)" else "🏆 金婚纪念 (3)"),
                            "VINTAGE" to (if (isEnglish) "📷 Vintage (4)" else "📷 岁月老照片 (4)"),
                            "PORTRAIT" to (if (isEnglish) "👵 Portraits (2)" else "👵 个人肖像 (2)"),
                            "MILESTONES" to (if (isEnglish) "🎈 Milestones (3)" else "🎈 节点重聚 (3)"),
                            "TRAVEL" to (if (isEnglish) "✈️ Gatherings (2)" else "✈️ 欢聚旅行 (2)")
                        )

                        items(filterCategories.size) { idx ->
                            val (catCode, catLabel) = filterCategories[idx]
                            val isSelected = selectedAiCategoryFilter == catCode

                            Surface(
                                modifier = Modifier.clickable { viewModel.setAiCategoryFilter(catCode) },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldenGradientStart else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = catLabel,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Timeline Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Timeline" else "回忆时光",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isEnglish) "More >" else "更多 >",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.TIMELINE) }
                    )
                }
            }

            // Memory Cards List (Filtered by Gemini AI Category & Search Query)
            items(filteredMemories) { mem ->
                MemoryCardItem(
                    mem = mem,
                    isEnglish = isEnglish,
                    onClick = { viewModel.selectMemory(mem.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        } // End LazyColumn
    } // End Column

        // Requirement 3: Persistent Floating Action Button for Quick Toggling between Standard Mode and Elder Mode
        FloatingActionButton(
            onClick = { viewModel.toggleElderMode() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp),
            shape = CircleShape,
            containerColor = if (isElderMode) Color(0xFF1B5E20) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isElderMode) Color.White else MaterialTheme.colorScheme.onSurface,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Elderly,
                    contentDescription = "Toggle Elder Mode",
                    tint = if (isElderMode) Color.White else GoldenGradientStart,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isElderMode) (if (isEnglish) "Elder Mode (ON)" else "👵 长辈大字模式") else (if (isEnglish) "Standard Mode" else "📱 标准模式"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Floating Action Button for Instant Camera Capture
        FloatingActionButton(
            onClick = { showCameraOverlay = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = CircleShape,
            containerColor = GoldenGradientStart,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isEnglish) "Capture" else "一键拍照",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Camera Capture Screen Overlay
        if (showCameraOverlay) {
            CameraCaptureView(
                isEnglish = isEnglish,
                onPhotoCaptured = { uri ->
                    showCameraOverlay = false
                    viewModel.addNewMemory(
                        title = "相机拍摄的相册记录",
                        titleEn = "Camera Captured Memory",
                        desc = "极速拍照自动备份至全家共享相册",
                        location = "北京",
                        tags = "拍照,云备份",
                        isEncrypted = false
                    )
                },
                onDismiss = { showCameraOverlay = false }
            )
        }

        // Feature 2: Interactive Tour Overlay for First-time users
        InteractiveTourOverlay(
            isEnglish = isEnglish,
            isVisible = showTour,
            onDismissTour = { viewModel.dismissTour() },
            onToggleElderModeFromTour = { viewModel.toggleElderMode() }
        )

        // Requirement 2: Gemini Collage of the Week Dialog
        val showCollageDialog by viewModel.showCollageOfTheWeekDialog.collectAsState()
        val weeklyCollageResult by viewModel.weeklyCollageResult.collectAsState()
        val isCompilingCollage by viewModel.isCompilingCollage.collectAsState()

        if (showCollageDialog) {
            com.example.ui.components.CollageOfTheWeekDialog(
                weeklyResult = weeklyCollageResult,
                isCompiling = isCompilingCollage,
                isEnglish = isEnglish,
                onRecompile = { viewModel.compileCollageOfTheWeek() },
                onShareToFamily = { viewModel.shareCollageToFamilyFeed() },
                onDismiss = { viewModel.closeCollageOfTheWeek() }
            )
        }

        // Requirement 2: Family Share Link Dialog (Firebase Dynamic Links)
        if (showFamilyShareLinkDialog) {
            val selectedMems = memories.filter { selectedMemoryIds.contains(it.id) }
            com.example.ui.components.FamilyShareLinkDialog(
                selectedMemories = selectedMems,
                dynamicLink = generatedDynamicLink,
                isGenerating = isGeneratingDynamicLink,
                isEnglish = isEnglish,
                onDismiss = { viewModel.closeFamilyShareLinkDialog() }
            )
        }

        // Feature 1: Monthly Recap Collage Dialog
        if (showMonthlyRecapDialog) {
            com.example.ui.components.MonthlyRecapDialog(
                isEnglish = isEnglish,
                onShareToFeed = { msg -> viewModel.shareCollageToFamilyFeed() },
                onDismiss = { viewModel.closeMonthlyRecapDialog() }
            )
        }

        // Requirement 2: Map Gallery View Dialog (GPS Photo Clustering)
        if (showMapGalleryDialog) {
            com.example.ui.components.MapGalleryDialog(
                memories = memories,
                isEnglish = isEnglish,
                onMemorySelect = { memId -> viewModel.selectMemory(memId) },
                onDismiss = { viewModel.closeMapGallery() }
            )
        }
    } // End Box
}

@Composable
fun OnThisDaySection(
    isEnglish: Boolean,
    memories: List<MemoryEntity>,
    onSelectMemory: (String) -> Unit
) {
    val onThisDayItems = remember(memories) {
        OnThisDayUtil.getOnThisDayMemories(memories, "07-26")
    }

    if (onThisDayItems.isNotEmpty()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF2C1810),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldenGradientStart),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "On This Day · Memory Highlights" else "那年今日 · 历史相册重温",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isEnglish) "Photos taken on July 26 in previous years" else "7月26日 往年今日拍摄的珍贵全家福",
                                fontSize = 11.sp,
                                color = GoldenGradientStart
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldenGradientStart.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${onThisDayItems.size} " + (if (isEnglish) "Memories" else "张往年照片"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenGradientStart,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                onThisDayItems.forEach { item ->
                    val mem = item.memory
                    val photoRes = when {
                        mem.photoResNames.contains("img_elder_portrait") -> R.drawable.img_elder_portrait_1785079069040
                        mem.photoResNames.contains("img_anniversary_golden") -> R.drawable.img_anniversary_golden_1785079056294
                        else -> R.drawable.img_family_reunion_1785079042483
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onSelectMemory(mem.id) },
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF3D271D)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = photoRes),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(10.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldenGradientStart
                                ) {
                                    Text(
                                        text = "⏳ ${item.yearsAgo} " + (if (isEnglish) "Years Ago Today" else "年前的今天"),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(10.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.Black.copy(alpha = 0.7f)
                                ) {
                                    Text(
                                        text = mem.date,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (isEnglish) mem.titleEn else mem.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = item.exifSummary,
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (isEnglish) item.nostalgiaQuoteEn else item.nostalgiaQuoteZh,
                                fontSize = 12.sp,
                                color = Color(0xFFFFD700)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AvatarBadge(resId: Int) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun MemoryCardItem(
    mem: com.example.data.MemoryEntity,
    isEnglish: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Date marker and cached badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mem.date,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldenGradientStart
                )

                if (mem.isOfflineCached) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isEnglish) "💾 Room Cached" else "💾 Room 本地已缓存",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Photo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_family_reunion_1785079042483),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Photo Count Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "📷 12",
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isEnglish) mem.titleEn else mem.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isEnglish) mem.descriptionEn else mem.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${mem.likesCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${mem.commentsCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(
                    text = mem.location,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
