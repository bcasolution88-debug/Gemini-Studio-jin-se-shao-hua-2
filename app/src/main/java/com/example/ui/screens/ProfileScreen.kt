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
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.EditEmergencyContactDialog
import com.example.ui.components.EmergencyContactSettingCard
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Slideshow

import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.AutoAwesome

@Composable
fun ProfileScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val isElderMode by viewModel.isElderMode.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isHighContrastMode by viewModel.isHighContrastMode.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()

    val autoSyncSchedule by viewModel.autoSyncSchedule.collectAsState()
    val showExportPdfDialog by viewModel.showExportPdfDialog.collectAsState()

    val emergencyContactName by viewModel.emergencyContactName.collectAsState()
    val emergencyContactPhone by viewModel.emergencyContactPhone.collectAsState()
    var showEditEmergencyDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val scrollToTopEvent by viewModel.scrollToTopEvent.collectAsState()
    androidx.compose.runtime.LaunchedEffect(scrollToTopEvent) {
        if (scrollToTopEvent > 0L) {
            scrollState.animateScrollTo(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "User Profile" else "个人中心",
            englishTitle = "USER PROFILE",
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Header Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_elder_portrait_1785079069040),
                            contentDescription = null,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("陈小雅", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(GoldenGradientStart)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("发起人 Owner", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(userEmail, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ProfileStatItem("128", if (isEnglish) "Photos" else "照片")
                        ProfileStatItem("34", if (isEnglish) "Videos" else "视频")
                        ProfileStatItem("3", if (isEnglish) "Albums" else "相册")
                        ProfileStatItem("11", if (isEnglish) "Family" else "家庭")
                    }
                }
            }

            // Elder Mode Banner Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.ELDER_MODE_SETTINGS) },
                shape = RoundedCornerShape(20.dp),
                color = GoldenGradientStart
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Accessibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Elder Mode" else "👴 长辈模式 Elder Mode",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (isElderMode) "已开启 · 字体放大 125%" else "点击配置关怀功能",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Switch(
                        checked = isElderMode,
                        onCheckedChange = { viewModel.toggleElderMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White)
                    )
                }
            }

            // Requirement 1: Emergency Contact Setting Card
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

            // Options List
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column {
                    ProfileMenuItem(
                        icon = Icons.Default.Language,
                        title = if (isEnglish) "Language / 语言: English (US)" else "Language / 语言: 简体中文",
                        subtitle = if (isEnglish) "Tap to switch to Simplified Chinese" else "点击切换为 English (US) 动态更新界面",
                        onClick = { viewModel.toggleLanguage() }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.EmojiEvents,
                        title = if (isEnglish) "Anniversary Settings" else "纪念日设置",
                        onClick = { viewModel.navigateTo(AppScreen.GOLDEN_ANNIVERSARY) }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.QrCode,
                        title = if (isEnglish) "Family Album Group QR Code & Scan" else "家庭相册群组二维码与扫码",
                        subtitle = if (isEnglish) "Quick QR onboarding for non-technical family members" else "一秒扫码或分享二维码，免登录轻松邀请长辈亲友加群",
                        onClick = { viewModel.navigateTo(AppScreen.FAMILY_GROUP_QR) }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.AccountTree,
                        title = if (isEnglish) "Family Tree" else "家族树管理",
                        onClick = { viewModel.navigateTo(AppScreen.FAMILY_TREE) }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Book,
                        title = if (isEnglish) "Export Formatted Album PDF" else "📄 导出排版 PDF 实体纪念册",
                        subtitle = if (isEnglish) "Generate printable PDF booklet with AI narratives & metadata tags" else "包含 AI 叙事图说与元数据标注，一键导出可打印 PDF 纪念册",
                        onClick = { viewModel.openExportPdfDialog() }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Slideshow,
                        title = if (isEnglish) "Interactive Photo Slideshow" else "交互式放映幻灯片",
                        subtitle = if (isEnglish) "Ken Burns auto pan/zoom with AI captions" else "推拉特写动画 · 暂停即刻展示 AI 图说",
                        onClick = { viewModel.navigateTo(AppScreen.SLIDESHOW) }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.AutoAwesome,
                        title = if (isEnglish) "Gemini Collage of the Week" else "✨ Gemini 本周精选回忆拼图",
                        subtitle = if (isEnglish) "AI compiles recent photos & captions for instant sharing" else "Gemini 自动提炼近七日照片故事，一键合成与分享亲情拼图",
                        onClick = { viewModel.openCollageOfTheWeek() }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.MenuBook,
                        title = if (isEnglish) "Photo Book Orders" else "相册印制 Photo Book Orders",
                        onClick = { }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Cloud,
                        title = if (isEnglish) "Cloud Storage & Backup Schedule" else "☁️ 云端备份策略与存储空间",
                        subtitle = if (isEnglish) "Schedule: $autoSyncSchedule (Conserves mobile data for elders)" else "当前备份周期: $autoSyncSchedule (可为长辈省流量)",
                        onClick = { }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        title = if (isEnglish) "Encrypted Vault (PIN Protected)" else "保密保险库 (加密相册)",
                        onClick = { viewModel.navigateTo(AppScreen.ENCRYPTED_VAULT) }
                    )
                    ProfileMenuItem(
                        icon = Icons.Default.ExitToApp,
                        title = if (isEnglish) "Sign Out (Firebase Auth)" else "退出 Firebase 账户登录",
                        onClick = { viewModel.signOutFirebase() }
                    )
                }
            }

            // Requirement 2: Auto-Sync Cloud Backup Interval Config Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Cloud, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Auto-Sync to Cloud Interval" else "☁️ 云端自动备份时间间隔 (按需调配)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnglish) "Configure backup frequency to conserve mobile data for elderly users" else "可自由切换‘每天’、‘每周’或‘仅手动’，帮助长辈节省流量",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "Manual-only" to (if (isEnglish) "Manual-only" else "仅手动 (最省流量)"),
                            "Daily" to (if (isEnglish) "Daily" else "每天自动 (Daily)"),
                            "Weekly" to (if (isEnglish) "Weekly" else "每周自动 (Weekly)")
                        ).forEach { (code, label) ->
                            val isSelected = autoSyncSchedule == code
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setAutoSyncSchedule(code) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldenGradientStart else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Dark Mode Switch
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isEnglish) "Dark Mode" else "深色/夜间显示模式 Dark Mode",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            }

            // Requirement 1: High Contrast Theme Option Switch for Visual Impairment
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = if (isHighContrastMode) Color.Black else MaterialTheme.colorScheme.surface,
                border = if (isHighContrastMode) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFEA00)) else null,
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
                        Icon(
                            imageVector = Icons.Default.Contrast,
                            contentDescription = null,
                            tint = if (isHighContrastMode) Color(0xFFFFEA00) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "High-Contrast Visual Mode" else "视障高对比度显示模式 (AAA 级)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighContrastMode) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnglish) "Pure black background, vivid yellow & pure white text with high-visibility outlines for low vision" else "纯黑底色、明亮荧黄与高反差白字，超大描边与高清晰读写体验",
                                fontSize = 11.sp,
                                color = if (isHighContrastMode) Color(0xFFFFEA00) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isHighContrastMode,
                        onCheckedChange = { viewModel.toggleHighContrastMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = Color(0xFFFFEA00)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

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

        if (showExportPdfDialog) {
            com.example.ui.components.ExportAlbumPdfDialog(
                isEnglish = isEnglish,
                onDismiss = { viewModel.closeExportPdfDialog() }
            )
        }
    }
}

@Composable
fun ProfileStatItem(valStr: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valStr, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
