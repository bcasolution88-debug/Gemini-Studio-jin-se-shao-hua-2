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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.components.PrimaryVioletButton
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientStart

import android.net.Uri
import coil.compose.AsyncImage
import com.example.ui.components.CameraCaptureView

@Composable
fun NewMemoryScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    var title by remember { mutableStateOf("家庭年度聚会") }
    var description by remember { mutableStateOf("今年的家庭聚会，爸妈笑得格外开心。岁月如歌，家是我们最温暖的归宿。") }
    var location by remember { mutableStateOf("北京·朝阳") }
    var isLinkAnniversary by remember { mutableStateOf(true) }
    var isEncrypted by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var capturedPhotos by remember { mutableStateOf(listOf<Uri>()) }
    var tags by remember { mutableStateOf("family, birthday, trip, 全家福, 金婚") }
    var isAnalyzingTags by remember { mutableStateOf(false) }

    if (showCamera) {
        CameraCaptureView(
            isEnglish = isEnglish,
            onPhotoCaptured = { uri ->
                if (uri != Uri.EMPTY) {
                    capturedPhotos = capturedPhotos + uri
                }
                showCamera = false
            },
            onDismiss = { showCamera = false }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "New Memory" else "发布回忆",
            englishTitle = "NEW MEMORY",
            showBack = true,
            onBackClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) },
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Media Picker Grid
            Text(
                text = if (isEnglish) "Photos & Videos (Up to 18)" else "照片与视频 (最多18张)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Default Cover image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_family_reunion_1785079042483),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(topEnd = 8.dp))
                            .background(GoldenGradientStart)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(if (isEnglish) "Cover" else "封面", color = Color.White, fontSize = 9.sp)
                    }
                }

                // Render photos taken with CameraX
                for ((index, uri) in capturedPhotos.withIndex()) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Camera photo $index",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable {
                                    capturedPhotos = capturedPhotos.filterIndexed { i, _ -> i != index }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                }

                // CameraX Take Photo Trigger Box
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .clickable { showCamera = true },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "CameraX", tint = MaterialTheme.colorScheme.primary)
                        Text(if (isEnglish) "CameraX" else "拍照/拍摄", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (isEnglish) "Memory Title" else "回忆标题") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // AI Gemini Tagging & Caption Auto-Generator Banner Buttons
            val coroutineScope = rememberCoroutineScope()
            var isGeneratingCaption by remember { mutableStateOf(false) }

            // Gemini Auto-Tagging Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isAnalyzingTags) {
                        coroutineScope.launch {
                            isAnalyzingTags = true
                            val result = com.example.data.GeminiCaptionService.analyzePhotoAndGenerateTags(
                                photoTitle = title,
                                photoDescription = description,
                                location = location,
                                isEnglish = isEnglish
                            )
                            if (result.generatedTags.isNotEmpty()) {
                                tags = result.generatedTags.joinToString(", ")
                            }
                            if (result.narrativeCaptionZh.isNotBlank()) {
                                description = if (isEnglish) result.narrativeCaptionEn else result.narrativeCaptionZh
                            }
                            isAnalyzingTags = false
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini Auto Tagging",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Gemini Auto-Tag Photo & Metadata" else "Gemini API 智能识别与标签生成",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAnalyzingTags)
                                    (if (isEnglish) "Analyzing image & metadata for tags ('family', 'trip', 'birthday')..." else "Gemini 正在分析照片与背景生成检索标签 ('family', 'birthday', 'trip')...")
                                else
                                    (if (isEnglish) "Click to auto-generate tags (family, birthday, trip)" else "点击自动提取主题分类标签与搜索关键字"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isAnalyzingTags) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // Display Auto-Generated Tags Chips
            if (tags.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tagList = tags.split(",", "，", " ").map { it.trim() }.filter { it.isNotBlank() }.take(6)
                    for (tag in tagList) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Tags Input Field
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text(if (isEnglish) "Tags (e.g. family, birthday, trip)" else "标签分类 (如: family, birthday, trip, 全家福)") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isGeneratingCaption) {
                        coroutineScope.launch {
                            isGeneratingCaption = true
                            val caption = com.example.data.GeminiCaptionService.generatePhotoCaption(
                                photoTitle = title.ifBlank { if (isEnglish) "Family Reunion" else "金婚幸福时光" },
                                photoTags = "家庭聚会,金婚纪念,四代同堂",
                                location = location,
                                isEnglish = isEnglish
                            )
                            description = caption
                            isGeneratingCaption = false
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Gemini AI Photo Captioning" else "Gemini AI 智能照片配文",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isGeneratingCaption)
                                    (if (isEnglish) "Gemini is analyzing memory & writing poetic caption..." else "Gemini 正在分析相册照片并创作感人文案...")
                                else
                                    (if (isEnglish) "Click to auto-generate heartwarming story text" else "点击一键生成感人温馨回忆故事"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isGeneratingCaption) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Description Input with Speech Button
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (isEnglish) "Story Description" else "回忆故事 / 故事描述") },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable {
                                description += if (isEnglish) " Warm family memories from Beijing." else " 温馨的京城金婚聚会回忆。"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                minLines = 3,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Location Selector Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = GoldenGradientStart)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(if (isEnglish) "Location" else "拍摄地点", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(location, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Link to Golden Anniversary Toggle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isEnglish) "Link to Golden Anniversary" else "关联到金婚纪念相册",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Link to Anniversary Album",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isLinkAnniversary,
                        onCheckedChange = { isLinkAnniversary = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldenGradientStart)
                    )
                }
            }

            // Encrypted Browsing Toggle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GoldenGradientStart)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Encrypted Vault" else "加密浏览 (保密保险库)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Requires PIN to view",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isEncrypted,
                        onCheckedChange = { isEncrypted = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = GoldenGradientStart)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryVioletButton(
                text = if (isEnglish) "Publish Memory" else "发布回忆 Publish Memory",
                onClick = {
                    viewModel.addNewMemory(
                        title = title,
                        titleEn = title,
                        desc = description,
                        location = location,
                        tags = tags.ifBlank { "family, birthday, trip" },
                        isEncrypted = isEncrypted
                    )
                    viewModel.navigateTo(AppScreen.MAIN_TAB)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
