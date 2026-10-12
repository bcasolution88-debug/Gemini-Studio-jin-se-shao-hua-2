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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.MemoryEntity
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart

data class MapLocationCluster(
    val locationName: String,
    val locationNameEn: String,
    val lat: Double,
    val lng: Double,
    val photoCount: Int,
    val mapX: Float, // 0.0f to 1.0f relative canvas position
    val mapY: Float, // 0.0f to 1.0f relative canvas position
    val sampleImageResId: Int
)

@Composable
fun MapGalleryDialog(
    memories: List<MemoryEntity>,
    isEnglish: Boolean,
    onMemorySelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedLocationName by remember { mutableStateOf<String?>("ALL") }

    val clusters = listOf(
        MapLocationCluster("北京", "Beijing", 39.9042, 116.4074, 5, 0.65f, 0.32f, R.drawable.img_family_reunion_1785079042483),
        MapLocationCluster("上海", "Shanghai", 31.2304, 121.4737, 3, 0.78f, 0.55f, R.drawable.img_anniversary_golden_1785079056294),
        MapLocationCluster("广州", "Guangzhou", 23.1291, 113.2644, 2, 0.68f, 0.78f, R.drawable.img_elder_portrait_1785079069040),
        MapLocationCluster("成都", "Chengdu", 30.5728, 104.0668, 3, 0.40f, 0.58f, R.drawable.img_elder_portrait_1785079069040),
        MapLocationCluster("杭州", "Hangzhou", 30.2741, 120.1551, 2, 0.75f, 0.60f, R.drawable.img_family_reunion_1785079042483),
        MapLocationCluster("西安", "Xi'an", 34.3416, 108.9398, 2, 0.50f, 0.44f, R.drawable.img_anniversary_golden_1785079056294)
    )

    val filteredMemories = remember(memories, selectedLocationName) {
        if (selectedLocationName == "ALL" || selectedLocationName == null) {
            memories
        } else {
            memories.filter { mem ->
                mem.location.contains(selectedLocationName!!, ignoreCase = true) ||
                        mem.locationEn.contains(selectedLocationName!!, ignoreCase = true) ||
                        mem.tags.contains(selectedLocationName!!, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(640.dp)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Row
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
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isEnglish) "GPS Map Photo Gallery" else "🗺️ 地理足迹相册 (GPS Map Gallery)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isEnglish) "Geographic photo clusters & location pins" else "按 GPS 经纬度聚合家族相册，点击地图标记查阅",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Visual Map Canvas Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2833)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Map Stylized Overlay Canvas Representation
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = GoldenGradientStart,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isEnglish) "China Geographic Footprint Map" else "🌐 家族足迹地图 (GPS Verified)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldenGradientStart.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = if (isEnglish) "6 Active Clusters" else "6 个 GPS 聚簇",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldenGradientStart,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Map Pin Badges Overlay
                        clusters.forEach { cluster ->
                            val isSelected = selectedLocationName == cluster.locationName
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(
                                        start = (cluster.mapX * 280).dp,
                                        top = (cluster.mapY * 160).dp
                                    )
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedLocationName = if (isSelected) "ALL" else cluster.locationName
                                    }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) GoldenGradientStart else Color(0xEE2C3E50),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.5.dp,
                                            if (isSelected) Color.White else GoldenGradientStart
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else GoldenGradientStart,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = if (isEnglish) "${cluster.locationNameEn} (${cluster.photoCount})" else "${cluster.locationName} (${cluster.photoCount}张)",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal Location Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAllSelected = selectedLocationName == "ALL" || selectedLocationName == null
                        AssistChip(
                            onClick = { selectedLocationName = "ALL" },
                            label = { Text(if (isEnglish) "🌟 All Locations (${memories.size})" else "🌟 全部足迹 (${memories.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isAllSelected) GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    items(clusters) { cluster ->
                        val isSelected = selectedLocationName == cluster.locationName
                        val labelText = if (isEnglish) "📍 ${cluster.locationNameEn} (${cluster.photoCount})" else "📍 ${cluster.locationName} (${cluster.photoCount})"
                        AssistChip(
                            onClick = {
                                selectedLocationName = if (isSelected) "ALL" else cluster.locationName
                            },
                            label = { Text(labelText, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected) GoldenGradientStart else MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                enabled = true,
                                borderColor = if (isSelected) GoldenGradientStart else Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Header for location album list
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedLocationName == "ALL" || selectedLocationName == null)
                            (if (isEnglish) "All Location Albums (${filteredMemories.size})" else "包含 GPS 定位的相册概览 (${filteredMemories.size})")
                        else
                            (if (isEnglish) "Album Photos in ${selectedLocationName} (${filteredMemories.size})" else "📍 ${selectedLocationName} 地理专属相册 (${filteredMemories.size} 张照片)"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Corresponding Location Albums List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredMemories) { mem ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    onMemorySelect(mem.id)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_family_reunion_1785079042483),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isEnglish) mem.titleEn else mem.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = GoldenGradientStart,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "${mem.location} | ${mem.date}",
                                            fontSize = 11.sp,
                                            color = GoldenGradientStart,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "🏷️ ${mem.tags}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "View",
                                    tint = GoldenGradientStart,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
