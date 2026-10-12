package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MonthlyRecapData(
    val monthTitle: String,
    val monthTitleEn: String,
    val geminiSummary: String,
    val geminiSummaryEn: String,
    val photoResList: List<Int> // 5 selected best photos
)

@Composable
fun MonthlyRecapDialog(
    isEnglish: Boolean,
    onShareToFeed: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var selectedMonthIndex by remember { mutableIntStateOf(0) }
    var isGenerating by remember { mutableStateOf(false) }
    var sharedSuccess by remember { mutableStateOf(false) }

    val monthOptions = listOf(
        "2026年7月" to "July 2026",
        "2026年6月" to "June 2026",
        "2026年5月" to "May 2026"
    )

    // Best 5 photos for 5-photo grid layout
    val defaultRecap = MonthlyRecapData(
        monthTitle = "2026年7月 · 月度幸福回顾",
        monthTitleEn = "July 2026 Monthly Recap",
        geminiSummary = "✨ Gemini 智能月度汇总：7月份陈氏家族共计记录 18 个温馨时刻。AI 从 124 张相片中精选出最卓越的 5 张，涵盖金婚50周年盛典、四代全家福与北京朝阳聚会。幸福满盈，流金岁月！",
        geminiSummaryEn = "✨ Gemini Monthly Insight: July featured 18 precious family moments. AI curated the top 5 photos highlighting the 50th Golden Anniversary and 4-generation reunions.",
        photoResList = listOf(
            R.drawable.img_anniversary_golden_1785079056294,
            R.drawable.img_family_reunion_1785079042483,
            R.drawable.img_elder_portrait_1785079069040,
            R.drawable.img_anniversary_golden_1785079056294,
            R.drawable.img_family_reunion_1785079042483
        )
    )

    var currentRecap by remember { mutableStateOf(defaultRecap) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Bar
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
                                .background(GoldenGradientStart.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = null,
                                tint = GoldenGradientStart,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Gemini Monthly Recap" else "✨ Gemini 月度精选拼图",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnglish) "Auto selects top 5 best photos of the month" else "AI 智能筛选本月最佳 5 张相片合成排版",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Month Selector Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    monthOptions.forEachIndexed { index, (cnLabel, enLabel) ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedMonthIndex = index
                                    coroutineScope.launch {
                                        isGenerating = true
                                        delay(1000)
                                        isGenerating = false
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedMonthIndex == index) GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (selectedMonthIndex == index) GoldenGradientStart else Color.Transparent
                            )
                        ) {
                            Text(
                                text = if (isEnglish) enLabel else cnLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMonthIndex == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isGenerating) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = GoldenGradientStart)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isEnglish) "Gemini is analyzing photos & crafting 5-grid layout..." else "Gemini 正在全量分析当月照片并计算最佳 5 张拼图布局...",
                                fontSize = 12.sp,
                                color = GoldenGradientStart,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    // 5-Photo Auto-Arranged Shareable Grid Layout
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF1E1E1E),
                        border = androidx.compose.foundation.BorderStroke(2.dp, GoldenGradientStart)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Top Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isEnglish) currentRecap.monthTitleEn else currentRecap.monthTitle,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldenGradientStart
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldenGradientStart
                                ) {
                                    Text(
                                        text = "TOP 5",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 5-Photo Grid Arrangement (1 Big Main + 4 Sub-Grid Tiles)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Left Big Hero Photo (Photo #1)
                                Box(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = currentRecap.photoResList[0]),
                                        contentDescription = "Main Feature Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Surface(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp),
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.7f)
                                    ) {
                                        Text(
                                            text = "👑 #1 封面主图",
                                            fontSize = 9.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Right 2x2 Sub-Grid (Photos #2, #3, #4, #5)
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        ) {
                                            Image(
                                                painter = painterResource(id = currentRecap.photoResList[1]),
                                                contentDescription = "Photo 2",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        ) {
                                            Image(
                                                painter = painterResource(id = currentRecap.photoResList[2]),
                                                contentDescription = "Photo 3",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        ) {
                                            Image(
                                                painter = painterResource(id = currentRecap.photoResList[3]),
                                                contentDescription = "Photo 4",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp))
                                        ) {
                                            Image(
                                                painter = painterResource(id = currentRecap.photoResList[4]),
                                                contentDescription = "Photo 5",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Gemini Narrative Summary
                            Text(
                                text = if (isEnglish) currentRecap.geminiSummaryEn else currentRecap.geminiSummary,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                sharedSuccess = true
                                onShareToFeed(if (isEnglish) "Shared Gemini Monthly Recap Collage!" else "分享了 Gemini 月度精选 5 张照片拼图！")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenGradientStart)
                        ) {
                            Icon(
                                imageVector = if (sharedSuccess) Icons.Default.Check else Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (sharedSuccess) (if (isEnglish) "Shared!" else "已分享!") else (if (isEnglish) "Share to Family" else "一键分享全家群"),
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                coroutineScope.launch {
                                    isGenerating = true
                                    delay(1200)
                                    isGenerating = false
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
