package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.ui.components.AiPhotoCleanupDialog
import com.example.data.SmartAlbumCategory
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import com.example.ui.theme.GoldenGradientStart
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MemoryEntity
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.TopHeaderBar

data class TimelineNode(
    val id: String,
    val labelZh: String,
    val labelEn: String,
    val yearKey: String,
    val icon: String
)

data class FilterItem(
    val key: String,
    val labelCn: String,
    val labelEn: String
)

private fun getPhotoDrawableRes(resName: String): Int {
    return when {
        resName.contains("img_anniversary_golden") -> R.drawable.img_anniversary_golden_1785079056294
        resName.contains("img_elder_portrait") -> R.drawable.img_elder_portrait_1785079069040
        else -> R.drawable.img_family_reunion_1785079042483
    }
}

@Composable
fun HistoricalTimelineNode(
    memory: MemoryEntity,
    isEnglish: Boolean,
    isMultiSelectMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelect: () -> Unit = {},
    onMemoryClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Vertical Timeline Node Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) GoldenGradientStart else if (memory.isHistoricalHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                } else if (memory.isHistoricalHighlight) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(280.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            )
        }

        // Timeline Node Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = memory.date,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (memory.eventName.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isEnglish) memory.eventNameEn else memory.eventName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (memory.isOfflineCached) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OfflinePin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Room Cached",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val nodeHaptic = LocalHapticFeedback.current
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        nodeHaptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMemoryClick()
                    },
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                border = if (isSelected)
                    androidx.compose.foundation.BorderStroke(2.5.dp, GoldenGradientStart)
                else if (memory.isHistoricalHighlight)
                    androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                else
                    null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Image container
                    Box {
                        Image(
                            painter = painterResource(id = getPhotoDrawableRes(memory.photoResNames)),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // Multi-select Checkbox badge overlay on image
                        if (isMultiSelectMode || isSelected) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopEnd)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldenGradientStart else Color.Black.copy(alpha = 0.6f))
                                    .clickable { onToggleSelect() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.CheckCircle,
                                    contentDescription = "Select photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        if (memory.isHistoricalHighlight) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.TopStart)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEnglish) "HISTORICAL MILESTONE" else "重大历史节点",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isEnglish) memory.titleEn else memory.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isEnglish) memory.descriptionEn else memory.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = if (isEnglish) memory.locationEn else memory.location,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = memory.cameraModel,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${memory.likesCount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${memory.commentsCount}", fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TimelineScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val memories by viewModel.memories.collectAsState()
    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val isOfflineCacheSynced by viewModel.isOfflineCacheSynced.collectAsState()
    
    val haptic = LocalHapticFeedback.current

    val smartAlbums by viewModel.smartAlbums.collectAsState()
    val selectedSmartAlbumId by viewModel.selectedSmartAlbumId.collectAsState()
    val showPhotoCleanupDialog by viewModel.showPhotoCleanupDialog.collectAsState()
    val cleanupSuggestions by viewModel.cleanupSuggestions.collectAsState()

    // Requirement 1 & 2 states
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val isSearchDropdownExpanded by viewModel.isSearchDropdownExpanded.collectAsState()

    val isMultiSelectMode by viewModel.isMultiSelectMode.collectAsState()
    val selectedMemoryIds by viewModel.selectedMemoryIds.collectAsState()
    val showFamilyShareLinkDialog by viewModel.showFamilyShareLinkDialog.collectAsState()
    val generatedDynamicLink by viewModel.generatedDynamicLink.collectAsState()
    val isGeneratingDynamicLink by viewModel.isGeneratingDynamicLink.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var showRestoreDialogForMemory by remember { mutableStateOf<MemoryEntity?>(null) }
    var showElderSlideshowDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.triggerGeminiSmartGrouping()
    }

    val filterOptions = listOf(
        FilterItem("ALL", "全部", "ALL"),
        FilterItem("1972", "初遇 1972", "1972"),
        FilterItem("1976", "婚礼 1976", "1976"),
        FilterItem("2001", "银婚 2001", "2001"),
        FilterItem("2025", "金婚 2025", "2025"),
        FilterItem("2026", "四代 2026", "2026")
    )

    val currentSmartAlbum = remember(smartAlbums, selectedSmartAlbumId) {
        smartAlbums.find { it.id == selectedSmartAlbumId }
    }

    val filteredMemories = remember(memories, selectedFilter, currentSmartAlbum, searchQuery) {
        var result = memories
        if (searchQuery.isNotEmpty()) {
            val q = searchQuery.lowercase()
            result = result.filter { mem ->
                mem.title.lowercase().contains(q) ||
                mem.titleEn.lowercase().contains(q) ||
                mem.description.lowercase().contains(q) ||
                mem.descriptionEn.lowercase().contains(q) ||
                mem.tags.lowercase().contains(q) ||
                mem.eventName.lowercase().contains(q) ||
                mem.eventNameEn.lowercase().contains(q) ||
                mem.location.lowercase().contains(q)
            }
        }
        if (currentSmartAlbum != null && currentSmartAlbum.id != "ALL") {
            result = result.filter { currentSmartAlbum.memoryIds.contains(it.id) }
        }
        if (selectedFilter != "ALL") {
            result = result.filter { mem ->
                mem.date.startsWith(selectedFilter) || mem.tags.contains(selectedFilter) || mem.eventName.contains(selectedFilter)
            }
        }
        result
    }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scrollToTopEvent by viewModel.scrollToTopEvent.collectAsState()
    LaunchedEffect(scrollToTopEvent) {
        if (scrollToTopEvent > 0L) {
            listState.animateScrollToItem(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopHeaderBar(
                title = if (isEnglish) "Memory Timeline" else "回忆时光轴",
                englishTitle = "HISTORICAL TIMELINE",
                showBack = true,
                onBackClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) },
                isEnglishLang = isEnglish,
                onLangToggle = { viewModel.toggleLanguage() }
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
                        IconButton(
                            onClick = { viewModel.toggleMultiSelectMode() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isMultiSelectMode) Icons.Default.CheckCircle else Icons.Default.PhotoLibrary,
                                contentDescription = "Toggle Multi Select",
                                tint = if (isMultiSelectMode) GoldenGradientStart else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (isMultiSelectMode) (if (isEnglish) "${selectedMemoryIds.size} Photos Selected" else "已选中 ${selectedMemoryIds.size} 张时光照片") else (if (isEnglish) "Multi-Photo Family Share" else "多图选集 · Firebase 动态链接分享"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isMultiSelectMode) (if (isEnglish) "Tap cards below to toggle selection" else "勾选相片生成 1 键 Firebase 亲情动态链接") else (if (isEnglish) "Select photos to generate family share link" else "勾选多张时光相片，一键生成动态分享链接发给家人"),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (isMultiSelectMode) {
                            Button(
                                onClick = { viewModel.generateFamilyShareLinkForSelected() },
                                enabled = selectedMemoryIds.isNotEmpty(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldenGradientStart),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
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
                            OutlinedButton(
                                onClick = { viewModel.selectAllMemories() },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isEnglish) "Select All" else "全选相片",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Room Offline Caching Indicator Banner
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Room Local Offline Storage" else "Room 数据库离线缓存已激活",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEnglish)
                                            "All historical family photos are cached locally in SQLite via Room."
                                        else
                                            "所有历史照片已通过 Room SQLite 本地全量缓存，无网络也可随时浏览",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp,
                                modifier = Modifier.clickable { viewModel.syncOfflineCache() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isOfflineCacheSynced) Icons.Default.CloudDone else Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isOfflineCacheSynced)
                                            (if (isEnglish) "Room Cache Synced" else "Room 本地缓存已就绪")
                                        else
                                            (if (isEnglish) "Syncing..." else "正在同步 Room 缓存..."),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isOfflineMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { viewModel.toggleOfflineMode() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OfflinePin,
                                        contentDescription = null,
                                        tint = if (isOfflineMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isOfflineMode)
                                            (if (isEnglish) "Offline Mode ON" else "离线游览模式已开启")
                                        else
                                            (if (isEnglish) "Test Offline Mode" else "模拟离线模式"),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOfflineMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Requirement 4: Gemini AI Smart Grouping Categorized Tabs
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
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.GoldenGradientStart,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Gemini Smart Albums" else "Gemini AI 智能归类相册",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = com.example.ui.theme.GoldenGradientStart.copy(alpha = 0.15f),
                                modifier = Modifier.clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    viewModel.triggerGeminiSmartGrouping()
                                }
                            ) {
                                Text(
                                    text = if (isEnglish) "⚡ Refresh Grouping" else "⚡ 重新智能分组",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = com.example.ui.theme.GoldenGradientStart,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (smartAlbums.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(smartAlbums) { cat ->
                                    val isSelected = cat.id == selectedSmartAlbumId
                                    Surface(
                                        modifier = Modifier.clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.selectSmartAlbumCategory(cat.id)
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) com.example.ui.theme.GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${cat.icon} ${cat.name}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant
                                            ) {
                                                Text(
                                                    text = "${cat.memoryIds.size}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Requirement 3: AI Photo Cleanup Scan Suggestion Utility
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.startAiPhotoCleanupScan()
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E2836)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isEnglish) "AI Photo Storage Cleanup" else "AI 照片清理与相册瘦身",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFE53935).copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "FREE UP SPACE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF8A80),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isEnglish) "AI detected blurry & duplicate photos. Tap to review Delete/Keep." else "智能检测模糊/重复连拍照片，提供一键「清理/保留」帮手机瘦身",
                                fontSize = 11.sp,
                                color = Color(0xFFB0BEC5)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            // Interactive Scrollable Horizontal Timeline Axis Component (Requirement 1)
            item {
                val timelineNodes = listOf(
                    TimelineNode("ALL", "全部纪元", "All Eras", "ALL", "📸"),
                    TimelineNode("1970s", "1970年代", "1970s Era", "1970", "📜"),
                    TimelineNode("1972", "1972 初遇", "1972 Met", "1972", "💍"),
                    TimelineNode("1976", "1976 婚礼", "1976 Wedding", "1976", "🌸"),
                    TimelineNode("1980s", "1980年代", "1980s Era", "1980", "🚲"),
                    TimelineNode("1985", "1985 创业", "1985 Venture", "1985", "🏠"),
                    TimelineNode("1990s", "1990年代", "1990s Era", "1990", "📺"),
                    TimelineNode("1998", "1998 全家福", "1998 Family", "1998", "👨‍👩‍👧‍👦"),
                    TimelineNode("2000s", "2000年代", "2000s Era", "2000", "🎉"),
                    TimelineNode("2001", "2001 银婚", "2001 Silver", "2001", "🥈"),
                    TimelineNode("2010s", "2010年代", "2010s Era", "2010", "✈️"),
                    TimelineNode("2018", "2018 旅游", "2018 Travel", "2018", "🏞️"),
                    TimelineNode("2020s", "2020年代", "2020s Era", "2020", "🌟"),
                    TimelineNode("2025", "2025 金婚", "2025 Golden", "2025", "👑"),
                    TimelineNode("2026", "2026 四代", "2026 Reunion", "2026", "❤️")
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.GoldenGradientStart.copy(alpha = 0.5f)),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = com.example.ui.theme.GoldenGradientStart,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Horizontal Interactive Timeline Axis" else "可滑动交互式年代时光轴",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = if (isEnglish) "Tap year/decade to jump" else "点击快速跳转至对应年份",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Scrollable Horizontal Axis Track
                        Box(modifier = Modifier.fillMaxWidth()) {
                            // Connecting horizontal line
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .align(Alignment.Center)
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                com.example.ui.theme.GoldenGradientStart.copy(alpha = 0.2f),
                                                com.example.ui.theme.GoldenGradientStart,
                                                com.example.ui.theme.GoldenGradientStart.copy(alpha = 0.2f)
                                            )
                                        )
                                    )
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(timelineNodes) { node ->
                                    val isSelected = selectedFilter == node.yearKey
                                    Surface(
                                        modifier = Modifier.clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            selectedFilter = node.yearKey
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) com.example.ui.theme.GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.GoldenGradientStart.copy(alpha = 0.3f)),
                                        shadowElevation = if (isSelected) 4.dp else 1.dp
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "${node.icon} ${if (isEnglish) node.labelEn else node.labelZh}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (node.yearKey == "ALL") "全库相册" else "${node.yearKey}纪元",
                                                fontSize = 9.sp,
                                                color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions: Elder Slideshow Mode & Gemini AI Photo Restore
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Elder Slideshow Trigger Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showElderSlideshowDialog = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF2E7D32),
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📺", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "Elder Slideshow" else "长辈无障碍幻灯片",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnglish) "Hands-free viewing" else "大字大图 · 变速放映",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    // Gemini AI Photo Restore Trigger Button
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (filteredMemories.isNotEmpty()) {
                                    showRestoreDialogForMemory = filteredMemories.first()
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = com.example.ui.theme.GoldenGradientStart,
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isEnglish) "AI Photo Restore" else "Gemini 旧照修复",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isEnglish) "Denoise & contrast" else "去噪增强 · 细节还原",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // AI Timeline Highlight Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.VIDEO_STUDIO) },
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEnglish) "AI Milestone Video" else "AI 家族里程碑画卷",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isEnglish) "50 Years span" else "跨越50载",
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isEnglish)
                                "50 Years of Golden Memories (1976 - 2026)"
                            else
                                "半世纪金婚风雨同舟 (1976 - 2026)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = if (isEnglish)
                                "AI compiled 6 major historical milestones and 128 high-res photos cached in Room."
                            else
                                "AI 自动串联 6 个家族历史里程碑与 Room 本地缓存的 128 张高清照片",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Timeline Items
            if (filteredMemories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEnglish) "No photos found for this era" else "该年代暂无历史照片记录",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredMemories) { mem ->
                    HistoricalTimelineNode(
                        memory = mem,
                        isEnglish = isEnglish,
                        isMultiSelectMode = isMultiSelectMode,
                        isSelected = selectedMemoryIds.contains(mem.id),
                        onToggleSelect = { viewModel.toggleMemorySelection(mem.id) },
                        onMemoryClick = {
                            if (isMultiSelectMode) {
                                viewModel.toggleMemorySelection(mem.id)
                            } else {
                                viewModel.selectMemory(mem.id)
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showPhotoCleanupDialog) {
            AiPhotoCleanupDialog(
                suggestions = cleanupSuggestions,
                isEnglish = isEnglish,
                onKeepPhoto = { memoryId -> viewModel.keepPhotoSuggestion(memoryId) },
                onDeletePhoto = { memoryId -> viewModel.deletePhotoSuggestion(memoryId) },
                onDismiss = { viewModel.dismissCleanupDialog() }
            )
        }

        // Dialog Renderers for AI Restore and Elder Slideshow
        showRestoreDialogForMemory?.let { memToRestore ->
            com.example.ui.components.AiPhotoRestoreDialog(
                memory = memToRestore,
                isEnglish = isEnglish,
                onRestoredSave = {
                    // Restore saved
                },
                onDismiss = { showRestoreDialogForMemory = null }
            )
        }

        if (showElderSlideshowDialog) {
            com.example.ui.components.ElderPhotoSlideshowDialog(
                memories = filteredMemories,
                isEnglish = isEnglish,
                fontScale = 1.25f,
                onDismiss = { showElderSlideshowDialog = false }
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
    } // End Box
}
}
