package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.GoldenGradientStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FamilyGroupQrScreen(viewModel: AppStateViewModel) {
    val isEnglish = viewModel.isEnglish.value
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf(0) } // 0: My QR Code, 1: Scan QR Code
    var isScanning by remember { mutableStateOf(false) }
    var scanSuccessGroup by remember { mutableStateOf<String?>(null) }
    var copiedToast by remember { mutableStateOf(false) }

    // Scanner Laser Line Animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeaderBar(
            title = if (isEnglish) "Family Album Group QR Code" else "家庭相册群组二维码",
            englishTitle = "FAMILY GROUP ONBOARDING",
            isEnglishLang = isEnglish,
            onLangToggle = { viewModel.toggleLanguage() },
            onNotificationClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Toggle Switcher
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeTab = 0 },
                            shape = RoundedCornerShape(14.dp),
                            color = if (activeTab == 0) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = if (activeTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Family QR Code" else "群二维码",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeTab = 1 },
                            shape = RoundedCornerShape(14.dp),
                            color = if (activeTab == 1) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = if (activeTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEnglish) "Scan to Join" else "扫码加群",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeTab == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // TAB 0: GENERATE FAMILY QR CODE
            if (activeTab == 0) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldenGradientStart.copy(alpha = 0.4f)),
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Group Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(GoldenGradientStart.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Group, contentDescription = null, tint = GoldenGradientStart, modifier = Modifier.size(26.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isEnglish) "Zhang Family Heirloom Album Group" else "张氏传承全家福相册群",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isEnglish) "Group ID: GOLDEN_2026_888 · 8 Members" else "群号: GOLDEN_2026_888 · 已有 8 位长辈亲友",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Stylized QR Code Graphic Container
                            Surface(
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(2.dp, GoldenGradientStart, RoundedCornerShape(20.dp)),
                                color = Color.White
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Custom Vector Stylized QR Code Pattern
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val squareSize = size.width / 11f
                                        val color = Color(0xFF2C1810)

                                        // Draw Corner Finder Patterns
                                        drawRect(color = color, topLeft = Offset(0f, 0f), size = Size(squareSize * 3, squareSize * 3))
                                        drawRect(color = Color.White, topLeft = Offset(squareSize, squareSize), size = Size(squareSize, squareSize))

                                        drawRect(color = color, topLeft = Offset(size.width - squareSize * 3, 0f), size = Size(squareSize * 3, squareSize * 3))
                                        drawRect(color = Color.White, topLeft = Offset(size.width - squareSize * 2, squareSize), size = Size(squareSize, squareSize))

                                        drawRect(color = color, topLeft = Offset(0f, size.height - squareSize * 3), size = Size(squareSize * 3, squareSize * 3))
                                        drawRect(color = Color.White, topLeft = Offset(squareSize, size.height - squareSize * 2), size = Size(squareSize, squareSize))

                                        // Random matrix blocks simulating encrypted token QR
                                        val qrPattern = listOf(
                                            Pair(4, 1), Pair(5, 1), Pair(6, 2), Pair(4, 3), Pair(7, 3),
                                            Pair(2, 4), Pair(5, 4), Pair(8, 4), Pair(1, 5), Pair(6, 5),
                                            Pair(3, 6), Pair(7, 6), Pair(9, 6), Pair(4, 7), Pair(8, 7),
                                            Pair(5, 8), Pair(9, 8), Pair(4, 9), Pair(7, 9), Pair(8, 10)
                                        )

                                        qrPattern.forEach { (x, y) ->
                                            drawRect(
                                                color = color,
                                                topLeft = Offset(x * squareSize, y * squareSize),
                                                size = Size(squareSize * 0.9f, squareSize * 0.9f)
                                            )
                                        }
                                    }

                                    // Center Family Avatar Badge
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(GoldenGradientStart)
                                            .border(2.dp, Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("👴👵", fontSize = 18.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (isEnglish) "Scan QR Code with camera to join group easily" else "扫一扫上面二维码，长辈亲友一秒加入全家福相册",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons: Copy Link & Share Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            copiedToast = true
                                            scope.launch {
                                                delay(2000)
                                                copiedToast = false
                                            }
                                        },
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            if (copiedToast) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (copiedToast) (if (isEnglish) "Link Copied!" else "已复制邀请链接") else (if (isEnglish) "Copy Join Link" else "复制加群链接"),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { },
                                    shape = RoundedCornerShape(14.dp),
                                    color = GoldenGradientStart
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isEnglish) "Share Invitation Card" else "分享邀请卡片",
                                            fontSize = 12.sp,
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

            // TAB 1: SCAN QR CODE TO JOIN
            if (activeTab == 1) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Black
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isEnglish) "Align QR code inside frame to scan" else "将长辈/亲友相册群二维码对准框内",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Simulated Viewfinder Frame
                            Box(
                                modifier = Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(2.dp, GoldenGradientStart, RoundedCornerShape(20.dp))
                                    .background(Color(0xFF1A1A1A))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_elder_portrait_1785079069040),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    alpha = 0.3f
                                )

                                // Laser Scanning Line Animation
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val y = size.height * laserYRatio
                                    drawLine(
                                        color = GoldenGradientStart,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = 6f
                                    )
                                }

                                Text(
                                    text = "📷 ML Kit Camera Scanning...",
                                    fontSize = 11.sp,
                                    color = Color.Yellow,
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(10.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Simulate Scanning Button
                            Button(
                                onClick = {
                                    scope.launch {
                                        isScanning = true
                                        delay(1000)
                                        isScanning = false
                                        scanSuccessGroup = "张氏全家福相册群 (Zhang Family Group)"
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GoldenGradientStart)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isScanning) (if (isEnglish) "Scanning..." else "正在识别二维码...") else (if (isEnglish) "Simulate Scanning QR Code" else "点击模拟扫描二维码加群"),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Success Dialog Popup
                            scanSuccessGroup?.let { groupName ->
                                Spacer(modifier = Modifier.height(14.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFF1B5E20)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (isEnglish) "Successfully Joined Group!" else "成功扫码加入全家福群组！",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "$groupName · " + (if (isEnglish) "Connected with 8 relatives" else "已与爷爷秀珍奶奶等8位亲友同步相册"),
                                            fontSize = 11.sp,
                                            color = Color.LightGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // FAMILY GROUP MEMBERS LIST
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEnglish) "Group Members Onboarding Status" else "群内亲友加入状态",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isEnglish) "8 Active" else "8 位成员",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val members = listOf(
                            Triple("奶奶秀珍 (Grandma)", "👵 70s · 已经通过扫码加入", true),
                            Triple("爷爷国明 (Grandpa)", "👴 70s · 已经通过扫码加入", true),
                            Triple("明叔 (Uncle Ming)", "👨 40s · 群管理员", true),
                            Triple("小堂弟 (Cousin)", "👦 10s · 微信加群邀请中", false)
                        )

                        members.forEach { (name, desc, joined) ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (joined) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                    ) {
                                        Text(
                                            text = if (joined) (if (isEnglish) "Joined" else "已加入") else (if (isEnglish) "Invited" else "待加入"),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (joined) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
