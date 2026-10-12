package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MusicTrack(
    val id: String,
    val nameZh: String,
    val nameEn: String,
    val duration: String,
    val genre: String,
    val icon: String
)

@Composable
fun VideoStudioScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val memories by viewModel.memories.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedCategory by remember { mutableStateOf("ALL") }
    val categories = listOf("全部", "金婚", "银婚", "钻石婚", "寿宴")

    // Music Tracks State
    val musicTracks = remember {
        listOf(
            MusicTrack("m1", "黄金岁月 · 钢琴弦乐", "Golden Memories (Piano & Strings)", "2:45", "怀旧经典", "🎹"),
            MusicTrack("m2", "温暖怀旧 · 木吉他独奏", "Warm Nostalgia (Acoustic Guitar)", "3:10", "舒缓温馨", "🎸"),
            MusicTrack("m3", "欢聚一堂 · 民族交响", "Joyful Reunion (Family Symphony)", "2:30", "喜庆欢快", "🎻"),
            MusicTrack("m4", "岁月静好 · 晨曦竖琴", "Peaceful Evening (Sunrise Harp)", "3:30", "典雅宁静", "🌸"),
            MusicTrack("m5", "祖孙同乐 · 轻快民谣", "Generations Harmony (Light Folk)", "2:15", "温馨调皮", "🎶")
        )
    }

    var selectedMusicTrack by remember { mutableStateOf(musicTracks[0]) }
    var selectedAlbumMemoryIds by remember { mutableStateOf(memories.take(4).map { it.id }.toSet()) }

    // Video Synthesis State
    var isSynthesizing by remember { mutableStateOf(false) }
    var synthesisProgress by remember { mutableStateOf(0f) }
    var isSynthesizedDone by remember { mutableStateOf(false) }
    var isPreviewPlaying by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "Video Studio" else "视频工坊",
            englishTitle = "VIDEO STUDIO",
            showBack = true,
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) },
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SYNTHESIZE MEMORY HIGHLIGHTS VIDEO PANEL
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.5f)),
                    shadowElevation = 6.dp
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
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(GoldenGradientStart),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Memory Highlights Video Synthesizer" else "相册集锦精彩短视频合成器",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEnglish) "Auto Ken Burns zoom + BGM background track" else "选集图集 · 智能推拉运镜 · 自由选择配乐合成",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Step 1: Select Album Photos
                        Text(
                            text = if (isEnglish) "1. Select Photos for Highlight Video (${selectedAlbumMemoryIds.size} selected):" else "1. 选择参演照片相册集 (${selectedAlbumMemoryIds.size} 张已勾选):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(memories) { mem ->
                                val isSelected = selectedAlbumMemoryIds.contains(mem.id)
                                Surface(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            selectedAlbumMemoryIds = if (isSelected) {
                                                if (selectedAlbumMemoryIds.size > 1) selectedAlbumMemoryIds - mem.id else selectedAlbumMemoryIds
                                            } else {
                                                selectedAlbumMemoryIds + mem.id
                                            }
                                        }
                                        .border(
                                            2.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            RoundedCornerShape(14.dp)
                                        ),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Box {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_family_reunion_1785079042483),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                                            )
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.align(Alignment.Center).size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Step 2: Select Background Music Track
                        Text(
                            text = if (isEnglish) "2. Select Background Music Track (BGM):" else "2. 选择背景音乐原声配乐 (BGM):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            musicTracks.forEach { track ->
                                val isTrackSelected = selectedMusicTrack.id == track.id
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedMusicTrack = track },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isTrackSelected) GoldenGradientStart.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isTrackSelected) GoldenGradientStart else Color.Transparent
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(track.icon, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (isEnglish) track.nameEn else track.nameZh,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "${track.genre} · ${track.duration}",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        if (isTrackSelected) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isEnglish) "Selected" else "已选中", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldenGradientStart)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Synthesis Action Button or Progress
                        if (isSynthesizing) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LinearProgressIndicator(
                                    progress = { synthesisProgress },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                    color = GoldenGradientStart,
                                    trackColor = GoldenGradientStart.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isEnglish) "Synthesizing Highlight Video... ${(synthesisProgress * 100).toInt()}%" else "正在合成相册精彩短视频... ${(synthesisProgress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch {
                                            isSynthesizing = true
                                            synthesisProgress = 0f
                                            for (i in 1..10) {
                                                delay(120)
                                                synthesisProgress = i / 10f
                                            }
                                            isSynthesizing = false
                                            isSynthesizedDone = true
                                            isPreviewPlaying = true
                                        }
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = GoldenGradientStart
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isEnglish) "Synthesize Highlight Video with Music" else "一键合成精彩短视频 (搭配选定BGM配乐)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Synthesized Video Preview Box
                        if (isSynthesizedDone) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = Color.Black
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(180.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_anniversary_golden_1785079056294),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.4f))
                                        )

                                        // Play/Pause button
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(GoldenGradientStart)
                                                .align(Alignment.Center)
                                                .clickable { isPreviewPlaying = !isPreviewPlaying },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isPreviewPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }

                                        // Playing Audio Subtitle Overlay
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .padding(10.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.Black.copy(alpha = 0.75f))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "🎵 ${if (isEnglish) selectedMusicTrack.nameEn else selectedMusicTrack.nameZh} · (Ken Burns 运镜模式)",
                                                    color = Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isEnglish) "Synthesized Memory Highlight Film" else "已合成相册精彩微电影集锦",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = GoldenGradientStart,
                                            modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) }
                                        ) {
                                            Text(
                                                text = if (isEnglish) "Full Screen Play" else "全屏放映",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Featured Hero Video Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_anniversary_golden_1785079056294),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )

                        // HD Badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("1:20 · HD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Content
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "Golden Anniversary Film · 50 Years" else "金婚回忆相册 · 50年",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .clickable { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = if (isEnglish) "Generate One-Click" else "一键生成",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.3f))
                                        .clickable { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = if (isEnglish) "Preview" else "预览",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Filter Pills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = (cat == "全部" && selectedCategory == "ALL") || selectedCategory == cat
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = if (cat == "全部") "ALL" else cat },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = if (isSelected) 4.dp else 1.dp
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Video Grid (2 Column)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VideoCardItem(
                        title = "岁月金婚",
                        duration = "2:30",
                        resId = R.drawable.img_anniversary_golden_1785079056294,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) }
                    )
                    VideoCardItem(
                        title = "银婚回忆录",
                        duration = "1:45",
                        resId = R.drawable.img_family_reunion_1785079042483,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VideoCardItem(
                        title = "钻石喜宴",
                        duration = "3:10",
                        resId = R.drawable.img_elder_portrait_1785079069040,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) }
                    )
                    VideoCardItem(
                        title = "全家福典藏",
                        duration = "2:15",
                        resId = R.drawable.img_family_reunion_1785079042483,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.VIDEO_PLAYER) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun VideoCardItem(
    title: String,
    duration: String,
    resId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Play Icon overlay
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("$duration · HD", color = Color.White, fontSize = 10.sp)
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("$duration · HD", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

