package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.MemoryEntity
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay

enum class SlideshowSpeed(val labelZh: String, val labelEn: String, val intervalMs: Long) {
    SLOW("🐢 慢速 (8秒)", "🐢 Slow (8s)", 8000L),
    MEDIUM("🚶 中速 (5秒)", "🚶 Medium (5s)", 5000L),
    FAST("🐇 快速 (3秒)", "🐇 Fast (3s)", 3000L)
}

@Composable
fun ElderPhotoSlideshowDialog(
    memories: List<MemoryEntity>,
    isEnglish: Boolean = false,
    fontScale: Float = 1.2f,
    onDismiss: () -> Unit
) {
    if (memories.isEmpty()) return

    var currentIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var selectedSpeed by remember { mutableStateOf(SlideshowSpeed.SLOW) }
    var progressProgress by remember { mutableFloatStateOf(0f) }
    val haptic = LocalHapticFeedback.current

    // Auto-advance Timer Logic
    LaunchedEffect(currentIndex, isPlaying, selectedSpeed) {
        if (!isPlaying) return@LaunchedEffect

        val steps = 50
        val stepTime = selectedSpeed.intervalMs / steps

        for (i in 0..steps) {
            progressProgress = i.toFloat() / steps
            delay(stepTime)
        }

        progressProgress = 0f
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        currentIndex = (currentIndex + 1) % memories.size
    }

    val currentMemory = memories[currentIndex]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF141218) // Rich Dark background for comfortable elder viewing
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = GoldenGradientStart,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${currentIndex + 1}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEnglish) "Elder Hands-Free Album Slideshow (${currentIndex + 1}/${memories.size})"
                            else "长辈无障碍播放幻灯片 (${currentIndex + 1}/${memories.size})",
                            fontSize = (15 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar for Next Slide
                LinearProgressIndicator(
                    progress = { progressProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = GoldenGradientStart,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Large Main Photo Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black)
                ) {
                    AnimatedContent(
                        targetState = currentMemory,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "PhotoTransition"
                    ) { targetMem ->
                        val imageRes = when {
                            targetMem.photoResNames.contains("img_elder_portrait") -> com.example.R.drawable.img_elder_portrait_1785079069040
                            targetMem.photoResNames.contains("img_anniversary_golden") -> com.example.R.drawable.img_anniversary_golden_1785079056294
                            else -> com.example.R.drawable.img_family_reunion_1785079042483
                        }

                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = targetMem.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Floating Year Tag
                    Surface(
                        modifier = Modifier
                            .padding(14.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(16.dp),
                        color = GoldenGradientStart
                    ) {
                        Text(
                            text = currentMemory.date + " · " + (if (isEnglish) currentMemory.eventNameEn else currentMemory.eventName),
                            fontSize = (16 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // High Contrast Extra Large Story Text
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.12f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) currentMemory.titleEn else currentMemory.title,
                                fontSize = (18 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldenGradientStart
                            )

                            Surface(
                                shape = CircleShape,
                                color = GoldenGradientStart.copy(alpha = 0.25f),
                                modifier = Modifier.clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isEnglish) "Voice Read" else "语音朗读", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isEnglish) currentMemory.descriptionEn else currentMemory.description,
                            fontSize = (15 * fontScale).sp,
                            color = Color.White,
                            lineHeight = (22 * fontScale).sp,
                            maxLines = 3
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Speed Selector Options Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEnglish) "Speed:" else "播放速度：",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        SlideshowSpeed.entries.forEach { speed ->
                            val isSelected = selectedSpeed == speed
                            Surface(
                                modifier = Modifier.clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedSpeed = speed
                                    progressProgress = 0f
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) GoldenGradientStart else Color.Transparent
                            ) {
                                Text(
                                    text = if (isEnglish) speed.labelEn else speed.labelZh,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Large Elder Touch Controls Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                currentIndex = if (currentIndex > 0) currentIndex - 1 else memories.size - 1
                                progressProgress = 0f
                            },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }

                    // Main Play / Pause Button
                    Surface(
                        modifier = Modifier
                            .size(68.dp)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isPlaying = !isPlaying
                            },
                        shape = CircleShape,
                        color = GoldenGradientStart
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    // Next Button
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                currentIndex = (currentIndex + 1) % memories.size
                                progressProgress = 0f
                            },
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}
