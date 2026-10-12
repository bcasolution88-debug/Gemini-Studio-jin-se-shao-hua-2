package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MemoryEntity
import com.example.data.PhotoRestorationResult
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.mutableFloatStateOf

@Composable
fun AiPhotoRestoreDialog(
    memory: MemoryEntity,
    isEnglish: Boolean = false,
    onRestoredSave: (restoredPhotoId: String) -> Unit,
    onDismiss: () -> Unit
) {
    var isAnalyzing by remember { mutableStateOf(true) }
    var showRestoredVersion by remember { mutableStateOf(true) }
    var restorationResult by remember { mutableStateOf<PhotoRestorationResult?>(null) }
    
    // Interactive Restoration Sliders
    var scratchRepairLevel by remember { mutableFloatStateOf(85f) }
    var colorBoostLevel by remember { mutableFloatStateOf(75f) }
    var denoiseLevel by remember { mutableFloatStateOf(90f) }
    var showAdvancedAdjustments by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(memory) {
        delay(1400) // Simulate Gemini AI Restoration Analysis
        restorationResult = com.example.data.GeminiCaptionService.analyzeAndRestorePhoto(memory, isEnglish)
        isAnalyzing = false
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
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
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEnglish) "Gemini AI Old Photo Restore" else "Gemini AI 智能旧照修复",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GoldenGradientStart.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "HD ENHANCED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenGradientStart,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Image Container with Scanning Animation or Before/After
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, GoldenGradientStart.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                ) {
                    val result = restorationResult
                    val imageRes = if (!showRestoredVersion || result == null) {
                        when {
                            memory.photoResNames.contains("img_elder_portrait") -> com.example.R.drawable.img_elder_portrait_1785079069040
                            memory.photoResNames.contains("img_anniversary_golden") -> com.example.R.drawable.img_anniversary_golden_1785079056294
                            else -> com.example.R.drawable.img_family_reunion_1785079042483
                        }
                    } else {
                        result.restoredPhotoRes
                    }

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (isAnalyzing) {
                        // Animated Scanning Line
                        val infiniteTransition = rememberInfiniteTransition()
                        val scanOffsetY by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 210f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .offset(y = scanOffsetY.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, GoldenGradientStart, Color.White, GoldenGradientStart, Color.Transparent)
                                    )
                                )
                        )

                        Surface(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.75f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = GoldenGradientStart,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEnglish) "Gemini AI Analyzing & Denoising..." else "Gemini 神经网络去噪与高光修复中...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        // Badge indicating state
                        Surface(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.TopEnd),
                            shape = RoundedCornerShape(10.dp),
                            color = if (showRestoredVersion) GoldenGradientStart else Color.Black.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (showRestoredVersion) Icons.Default.AutoAwesome else Icons.Default.Restore,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (showRestoredVersion) (if (isEnglish) "HD Restored" else "✨ Gemini 修复增强版") else (if (isEnglish) "Original Photo" else "📷 修复前原图"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Toggle Before / After Floating Pill
                        Surface(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.BottomCenter)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    showRestoredVersion = !showRestoredVersion
                                },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Compare, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Tap to Compare Before/After" else "点击按住 对比修复前后效果",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!isAnalyzing && restorationResult != null) {
                    val res = restorationResult!!
                    // Metric Enhancement Chips with dynamic slider calculations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricChip(if (isEnglish) "Scratch Fix" else "划痕修复", "${scratchRepairLevel.toInt()}%")
                        MetricChip(if (isEnglish) "Color Restoration" else "色彩还原", "+${colorBoostLevel.toInt()}%")
                        MetricChip(if (isEnglish) "Denoise" else "去噪增强", "-${(denoiseLevel * 0.22f).toInt()}dB")
                        MetricChip(if (isEnglish) "Clarity" else "面部清晰度", "+${((scratchRepairLevel + denoiseLevel) / 7f).toInt()}%")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Toggle Advanced Processing Sliders
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showAdvancedAdjustments = !showAdvancedAdjustments
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = GoldenGradientStart.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "🎛️ Manual Image Processing Controls" else "🎛️ AI 图像细节与划痕色彩微调微控",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenGradientStart
                                )
                            }
                            Text(
                                text = if (showAdvancedAdjustments) "▲ 收起" else "▼ 展开调节",
                                fontSize = 11.sp,
                                color = GoldenGradientStart,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (showAdvancedAdjustments) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "Scratch & Scratch Repair: ${scratchRepairLevel.toInt()}%" else "折痕划痕修复强度: ${scratchRepairLevel.toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Slider(
                                value = scratchRepairLevel,
                                onValueChange = { scratchRepairLevel = it },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = GoldenGradientStart, activeTrackColor = GoldenGradientStart)
                            )

                            Text(
                                text = if (isEnglish) "Color Saturation & Warmth Boost: ${colorBoostLevel.toInt()}%" else "老照片色彩饱和与暖色还原: ${colorBoostLevel.toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Slider(
                                value = colorBoostLevel,
                                onValueChange = { colorBoostLevel = it },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = GoldenGradientStart, activeTrackColor = GoldenGradientStart)
                            )

                            Text(
                                text = if (isEnglish) "Neural Denoise & Sharpening: ${denoiseLevel.toInt()}%" else "神经网络超清去噪与噪点抑制: ${denoiseLevel.toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Slider(
                                value = denoiseLevel,
                                onValueChange = { denoiseLevel = it },
                                valueRange = 0f..100f,
                                colors = SliderDefaults.colors(thumbColor = GoldenGradientStart, activeTrackColor = GoldenGradientStart)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = if (isEnglish) res.geminiAnalysisReportEn else res.geminiAnalysisReport,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = if (isEnglish) "Close" else "关闭",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onRestoredSave(memory.id)
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldenGradientStart),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Save Restored HD" else "保存 Gemini 修复版",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = GoldenGradientStart.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldenGradientStart)
        }
    }
}
