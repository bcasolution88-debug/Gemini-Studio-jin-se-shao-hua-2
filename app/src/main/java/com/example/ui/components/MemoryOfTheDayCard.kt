package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MemoryEntity
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MemoryOfTheDayCard(
    memories: List<MemoryEntity>,
    isEnglish: Boolean,
    isHighContrast: Boolean = false,
    fontScale: Float = 1.0f,
    onSelectMemory: (String) -> Unit,
    onSendNotification: (String) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var isNarrating by remember { mutableStateOf(false) }
    var notificationSent by remember { mutableStateOf(false) }
    var geminiNarrative by remember {
        mutableStateOf(
            if (isEnglish)
                "✨ Gemini Memory Insight: July 28th holds timeless warmth for the Chen family. Exactly 50 years ago, grandparents shared their wedding vow in Beijing. Today, 4 generations gather in gratitude."
            else
                "✨ Gemini 那年今日回忆叙事：7月28日是陈氏家族极其特殊的日子。五十年前的今天，老长辈在北京四合院许下白头偕老誓言；如今四代同堂，流金岁月熠熠生辉。"
        )
    }

    val samplePhotoRes = R.drawable.img_anniversary_golden_1785079056294

    // Subtle pulsing animation for Memory of the Day card highlight
    val infiniteTransition = rememberInfiniteTransition(label = "glowTransition")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(glowScale),
        shape = RoundedCornerShape(24.dp),
        color = if (isHighContrast) Color(0xFF000000) else Color(0xFF2C1810),
        border = androidx.compose.foundation.BorderStroke(
            if (isHighContrast) 3.dp else 1.5.dp,
            if (isHighContrast) Color(0xFFFFEA00) else GoldenGradientStart
        ),
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row with Notification Badge
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
                            .background(if (isHighContrast) Color(0xFFFFEA00) else GoldenGradientStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Memory of Day",
                            tint = if (isHighContrast) Color.Black else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isEnglish) "Memory of the Day" else "那年今日 · 岁月记忆推送",
                                fontSize = (16 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighContrast) Color(0xFFFFEA00) else Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isHighContrast) Color(0xFF00E676) else Color(0xFFE53935)
                            ) {
                                Text(
                                    text = "7月28日",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isEnglish) "Gemini surfaced past memories from July 28" else "Gemini 智能比对往年7月28日历史珍藏全家福",
                            fontSize = (11 * fontScale).sp,
                            color = if (isHighContrast) Color.White else GoldenGradientStart
                        )
                    }
                }

                // Bell Notification Trigger
                Surface(
                    modifier = Modifier.clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        notificationSent = true
                        onSendNotification("🔔 已将‘那年今日’家庭记忆卡片推送至全家群聊与通知栏！")
                    },
                    shape = CircleShape,
                    color = if (notificationSent) Color(0xFF2E7D32) else GoldenGradientStart.copy(alpha = 0.25f)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            imageVector = if (notificationSent) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Notify Family",
                            tint = if (notificationSent) Color.White else GoldenGradientStart,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Photo Preview with Year Tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        memories.firstOrNull()?.let { onSelectMemory(it.id) }
                    }
            ) {
                Image(
                    painter = painterResource(id = samplePhotoRes),
                    contentDescription = "Memory of the Day Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Year Tag Overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart)
                ) {
                    Text(
                        text = "⏳ 1976年7月28日 (50年前)",
                        fontSize = (12 * fontScale).sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = GoldenGradientStart
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEnglish) "View Memory" else "打开回忆",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gemini Generated Storytelling Reflection Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (isHighContrast) Color(0xFF1C1C1C) else Color(0xFF3D271D)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = geminiNarrative,
                        fontSize = (13 * fontScale).sp,
                        color = if (isHighContrast) Color.White else Color(0xFFF1F1F1),
                        lineHeight = (19 * fontScale).sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                coroutineScope.launch {
                                    isNarrating = true
                                    delay(2000)
                                    isNarrating = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Voice Narration",
                                tint = if (isHighContrast) Color(0xFFFFEA00) else GoldenGradientStart,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isNarrating) (if (isEnglish) "Narrating..." else "正在朗读中...") else (if (isEnglish) "Voice Read Aloud" else "🎙️ Gemini 暖心语音朗读"),
                                fontSize = (11 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighContrast) Color(0xFFFFEA00) else GoldenGradientStart
                            )
                        }

                        if (notificationSent) {
                            Text(
                                text = "✅ " + (if (isEnglish) "Notification Sent!" else "已成功推送"),
                                fontSize = 11.sp,
                                color = Color(0xFF00E676),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
