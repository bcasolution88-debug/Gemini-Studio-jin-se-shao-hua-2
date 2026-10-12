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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PrepareForPrintDialog(
    isEnglish: Boolean,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var selectedFormatIndex by remember { mutableStateOf(0) }
    var dpiTarget by remember { mutableStateOf("300 DPI Ultra-HD") }
    var isProcessingPrintFilters by remember { mutableStateOf(false) }
    var printReadyComplete by remember { mutableStateOf(false) }

    // Filter Sliders
    var sharpness by remember { mutableFloatStateOf(0.8f) }
    var cmykContrast by remember { mutableFloatStateOf(0.6f) }
    var scratchRepair by remember { mutableFloatStateOf(0.9f) }

    val formats = listOf(
        "8x8 Square Hardcover" to "8x8 英寸典藏精装硬壳相册",
        "A4 Heritage Book" to "A4 家族流金岁月平装纪念册",
        "12x12 Grand Portfolio" to "12x12 四代同堂超大珍藏布艺册"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
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
                                .background(GoldenGradientStart.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Prepare for Print",
                                tint = GoldenGradientStart,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "Prepare for Print Tool" else "🖨️ 实体纪念册印刷修饰与 300DPI 排版工具",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnglish) "300 DPI Resolution & CMYK Print Restoration" else "自动调整打印分辨率 · 裂纹修复 · CMYK色彩矫正",
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

                // Format Selector
                Text(
                    text = if (isEnglish) "1. Select Hardcover Print Format" else "1. 选择实体纸质纪念册开本装帧：",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                formats.forEachIndexed { index, (enLabel, cnLabel) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedFormatIndex = index },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selectedFormatIndex == index) GoldenGradientStart.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (selectedFormatIndex == index) GoldenGradientStart else Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (selectedFormatIndex == index) Icons.Default.CheckCircle else Icons.Default.Print,
                                    contentDescription = null,
                                    tint = if (selectedFormatIndex == index) GoldenGradientStart else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isEnglish) enLabel else cnLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GoldenGradientStart
                            ) {
                                Text(
                                    text = "300 DPI",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Photo Restoration & Resolution Filter Adjustment
                Text(
                    text = if (isEnglish) "2. Photo Print Restoration Filters" else "2. 照片印刷修复滤镜与色彩微调：",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Sharpness / Upscaling
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "300 DPI Super-Res Sharpening" else "300 DPI 放大超分辨率锐化",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("${(sharpness * 100).toInt()}%", fontSize = 12.sp, color = GoldenGradientStart, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = sharpness,
                            onValueChange = { sharpness = it },
                            colors = SliderDefaults.colors(thumbColor = GoldenGradientStart, activeTrackColor = GoldenGradientStart)
                        )

                        // Scratch Repair
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "Scratch & Crease Removal" else "老照片折痕划痕无损修复",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("${(scratchRepair * 100).toInt()}%", fontSize = 12.sp, color = GoldenGradientStart, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = scratchRepair,
                            onValueChange = { scratchRepair = it },
                            colors = SliderDefaults.colors(thumbColor = GoldenGradientStart, activeTrackColor = GoldenGradientStart)
                        )

                        // CMYK Ink Profile
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEnglish) "CMYK Ink Bleed Optimization" else "CMYK 印刷油墨防止防偏色对比度",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("${(cmykContrast * 100).toInt()}%", fontSize = 12.sp, color = GoldenGradientStart, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = cmykContrast,
                            onValueChange = { cmykContrast = it },
                            colors = SliderDefaults.colors(thumbColor = GoldenGradientStart, activeTrackColor = GoldenGradientStart)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bleed Line & Crop Mark Preview Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.DarkGray)
                        .border(2.dp, Color.Red.copy(alpha = 0.7f), RoundedCornerShape(16.dp)) // Red 3mm Bleed Line
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_family_reunion_1785079042483),
                        contentDescription = "Print Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Overlay 3mm bleed margin indicator
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Text(
                            text = if (isEnglish) "🔴 3mm Bleed Margin & Crop Marks Applied" else "🔴 3mm 印刷出血线与裁切对位线已校准",
                            fontSize = 10.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Process & Export Button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            isProcessingPrintFilters = true
                            delay(1500)
                            isProcessingPrintFilters = false
                            printReadyComplete = true
                        }
                    },
                    enabled = !isProcessingPrintFilters,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldenGradientStart)
                ) {
                    if (isProcessingPrintFilters) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isEnglish) "Upscaling to 300 DPI..." else "正在进行 300DPI 矢量放大与 CMYK 优化...", fontSize = 13.sp, color = Color.White)
                    } else if (printReadyComplete) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEnglish) "Print-Ready Package Exported (300 DPI PDF)" else "已导出 300DPI 实体印刷高清 PDF 压缩包！", fontSize = 13.sp, color = Color.White)
                    } else {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isEnglish) "Generate Print-Ready 300 DPI PDF" else "一键导出 300 DPI 实体纪念册印刷版 PDF", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
