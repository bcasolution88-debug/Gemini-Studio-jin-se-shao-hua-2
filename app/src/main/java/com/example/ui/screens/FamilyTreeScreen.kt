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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FamilyMemberNode(
    val name: String,
    val role: String,
    val avatarRes: Int,
    val birthYear: String,
    val relationTag: String
)

@Composable
fun FamilyTreeScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val memories by viewModel.memories.collectAsState()
    val haptic = LocalHapticFeedback.current

    var selectedMember by remember { mutableStateOf<FamilyMemberNode?>(null) }

    val patriarch = FamilyMemberNode("陈建国", if (isEnglish) "Patriarch · Grandfather" else "太爷爷 · 户主", R.drawable.img_anniversary_golden_1785079056294, "1948", "老长辈")
    val matriarch = FamilyMemberNode("李秀英", if (isEnglish) "Matriarch · Grandmother" else "太奶奶 · 长辈", R.drawable.img_elder_portrait_1785079069040, "1951", "老长辈")
    val son1 = FamilyMemberNode("陈大明", if (isEnglish) "Eldest Son" else "大儿子", R.drawable.img_family_reunion_1785079042483, "1975", "第二代")
    val daughter1 = FamilyMemberNode("陈小雅", if (isEnglish) "Daughter · Admin" else "小女儿 · 管理员", R.drawable.img_elder_portrait_1785079069040, "1982", "第二代")
    val grandson1 = FamilyMemberNode("陈浩", if (isEnglish) "Grandson" else "长孙", R.drawable.img_family_reunion_1785079042483, "2008", "第三代")
    val granddaughter1 = FamilyMemberNode("陈晶", if (isEnglish) "Granddaughter" else "孙女", R.drawable.img_family_reunion_1785079042483, "2012", "第三代")
    val granddaughter2 = FamilyMemberNode("小可", if (isEnglish) "Granddaughter" else "外孙女", R.drawable.img_elder_portrait_1785079069040, "2016", "第三代")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "Interactive Family Tree" else "交互式四代家族树",
            englishTitle = "FAMILY TREE",
            showBack = true,
            onBackClick = { viewModel.navigateTo(AppScreen.FAMILY_MEMBERS) },
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(GoldenGradientStart, GoldenGradientEnd)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TreeStatItem("11", if (isEnglish) "Members" else "家族成员")
                        TreeStatItem("4", if (isEnglish) "Generations" else "代同堂")
                        TreeStatItem("50年", if (isEnglish) "Golden Years" else "流金岁月 💍")
                    }
                }
            }

            // Gemini Auto-Grouping Banner
            var isAutoGrouping by remember { mutableStateOf(false) }
            var autoGroupComplete by remember { mutableStateOf(false) }
            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            isAutoGrouping = true
                            kotlinx.coroutines.delay(1300)
                            isAutoGrouping = false
                            autoGroupComplete = true
                        }
                    },
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.5f)),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldenGradientStart.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAutoGrouping) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = GoldenGradientStart,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(22.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEnglish) "✨ Gemini Auto-Group Photos by Member" else "✨ Gemini 智能辨识与个人成长时间轴自动聚类",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (autoGroupComplete)
                                (if (isEnglish) "Recognized 7 family members across 32 gallery photos!" else "已成功归类7位家族成员的32张照片，生成个人独家岁月图谱！")
                            else
                                (if (isEnglish) "Tap to run Gemini AI facial recognition on gallery photos" else "点击运行 Gemini 算法分析全册面容，自动归类成员独家相册"),
                            fontSize = 11.sp,
                            color = if (autoGroupComplete) GoldenGradientStart else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isEnglish) "💡 Tap any family member to view their AI-grouped visual memory timeline!" else "💡 点击任意成员卡片，查看 Gemini 智能归类的个人专属时光轴",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            // GENERATION I
            Text(
                text = if (isEnglish) "Generation I · Elders & Founders" else "第一代 · 创世长辈",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoldenGradientStart
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TreeNodeCard(member = patriarch, isEnglish = isEnglish) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedMember = patriarch
                }

                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))

                TreeNodeCard(member = matriarch, isEnglish = isEnglish) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedMember = matriarch
                }
            }

            // Connecting Line Down
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(28.dp)
                    .background(GoldenGradientStart)
            )

            // GENERATION II
            Text(
                text = if (isEnglish) "Generation II · Children" else "第二代 · 中流砥柱 (儿女)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TreeNodeCard(member = son1, isEnglish = isEnglish) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedMember = son1
                }
                TreeNodeCard(member = daughter1, isEnglish = isEnglish) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedMember = daughter1
                }
            }

            // Connecting Line Down
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(28.dp)
                    .background(MaterialTheme.colorScheme.primary)
            )

            // GENERATION III
            Text(
                text = if (isEnglish) "Generation III · Grandchildren" else "第三代 · 茁壮后辈 (孙辈)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TreeNodeCard(member = grandson1, isEnglish = isEnglish) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedMember = grandson1
                }
                TreeNodeCard(member = granddaughter1, isEnglish = isEnglish) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedMember = granddaughter1
                }
                TreeNodeCard(member = granddaughter2, isEnglish = isEnglish) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    selectedMember = granddaughter2
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Modal Sheet showing member's shared memories
    selectedMember?.let { member ->
        val memberMemories = remember(memories, member) {
            memories.filter {
                it.title.contains(member.name) ||
                it.description.contains(member.name) ||
                it.tags.contains(member.name) ||
                it.tags.contains("合影") ||
                it.tags.contains("金婚")
            }.ifEmpty { memories.take(3) }
        }

        Dialog(onDismissRequest = { selectedMember = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = member.avatarRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = member.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${member.role} · ${if (isEnglish) "Born" else "出生于"} ${member.birthYear}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        IconButton(onClick = { selectedMember = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PhotoAlbum, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isEnglish) "Shared Memories (${memberMemories.size})" else "${member.name} 的共享家庭回忆 (${memberMemories.size}篇)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenGradientStart
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(memberMemories) { mem ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMember = null
                                        viewModel.navigateTo(AppScreen.MEMORY_DETAIL)
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_family_reunion_1785079042483),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isEnglish) mem.titleEn else mem.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = mem.date,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isEnglish) mem.descriptionEn else mem.description,
                                                fontSize = 10.sp,
                                                color = GoldenGradientStart,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            selectedMember = null
                            viewModel.navigateTo(AppScreen.MEMORY_DETAIL)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenGradientStart),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(if (isEnglish) "Open Album Details" else "查看全屏故事相册")
                    }
                }
            }
        }
    }
}

@Composable
fun TreeStatItem(valStr: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valStr, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
    }
}

@Composable
fun TreeNodeCard(member: FamilyMemberNode, isEnglish: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(104.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = member.avatarRes),
                contentDescription = null,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = member.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = member.role,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
