package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.components.GoldenButton
import com.example.ui.components.LanguagePillToggle
import com.example.ui.components.PrimaryVioletButton
import com.example.ui.theme.GoldenGradientEnd
import com.example.ui.theme.GoldenGradientStart

enum class LegalDocType {
    TERMS_OF_SERVICE,
    PRIVACY_POLICY
}

@Composable
fun LoginScreen(viewModel: AppStateViewModel) {
    AuthModalDialog(viewModel = viewModel, isRegisterInitial = false)
}

@Composable
fun RegisterScreen(viewModel: AppStateViewModel) {
    AuthModalDialog(viewModel = viewModel, isRegisterInitial = true)
}

@Composable
fun AuthModalDialog(
    viewModel: AppStateViewModel,
    isRegisterInitial: Boolean
) {
    val isEnglish by viewModel.isEnglish.collectAsState()
    var isRegisterTab by remember { mutableStateOf(isRegisterInitial) }

    // Form states
    var email by remember { mutableStateOf("xiya@family.com") }
    var password by remember { mutableStateOf("12345678") }
    var confirmPassword by remember { mutableStateOf("12345678") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var agreedToPolicy by remember { mutableStateOf(true) }

    // Legal Document Modal states
    var activeLegalDoc by remember { mutableStateOf<LegalDocType?>(null) }
    var socialAuthMessage by remember { mutableStateOf<String?>(null) }

    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    // Full screen background dim overlay representing modern modal popup
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        // Modal Card Container (弹窗样式)
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 480.dp)
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(28.dp)),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 20.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar: Close Button, Title Badge, Language Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.MAIN_TAB) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Modal",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Logo Icon with gradient ring
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(listOf(GoldenGradientStart, GoldenGradientEnd))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Golden Memory Logo",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Dual-Language Pill Toggle (中英文切换按钮)
                    LanguagePillToggle(
                        isEnglish = isEnglish,
                        onToggle = { viewModel.toggleLanguage() }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title & Subtitle
                Text(
                    text = if (isEnglish) {
                        if (isRegisterTab) "Create Account" else "Welcome Back"
                    } else {
                        if (isRegisterTab) "创建新账号" else "欢迎回到金色韶华"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isEnglish) {
                        if (isRegisterTab) "Register with email & setup password" else "Sign in to access family memories & AI photo tools"
                    } else {
                        if (isRegisterTab) "使用邮箱快速注册，安全保护家族回忆" else "登录即可共享家庭相册、旧照修复与长辈大字关怀"
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Switcher: Login vs Register inside Modal
                TabRow(
                    selectedTabIndex = if (isRegisterTab) 1 else 0,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[if (isRegisterTab) 1 else 0]),
                            height = 3.dp,
                            color = GoldenGradientStart
                        )
                    }
                ) {
                    Tab(
                        selected = !isRegisterTab,
                        onClick = { isRegisterTab = false },
                        text = {
                            Text(
                                text = if (isEnglish) "🔑 Log In" else "🔑 账号登录",
                                fontSize = 14.sp,
                                fontWeight = if (!isRegisterTab) FontWeight.Bold else FontWeight.Medium,
                                color = if (!isRegisterTab) GoldenGradientStart else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = isRegisterTab,
                        onClick = { isRegisterTab = true },
                        text = {
                            Text(
                                text = if (isEnglish) "✉️ Register" else "✉️ 邮箱注册",
                                fontSize = 14.sp,
                                fontWeight = if (isRegisterTab) FontWeight.Bold else FontWeight.Medium,
                                color = if (isRegisterTab) GoldenGradientStart else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Email Address Input Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (isEnglish) "Email Address" else "电子邮箱", fontSize = 13.sp) },
                    placeholder = { Text(if (isEnglish) "e.g. xiya@family.com" else "请输入您的常用邮箱地址", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = GoldenGradientStart
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenGradientStart,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Input Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (isEnglish) "Password" else "设置登录密码", fontSize = 13.sp) },
                    placeholder = { Text(if (isEnglish) "At least 8 characters" else "请输入密码 (至少8位，含字母与数字)", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = GoldenGradientStart
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldenGradientStart,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Password Strength Bar & Confirm Field (for Registration)
                if (isRegisterTab) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val passLength = password.length
                    val strengthColor = when {
                        passLength >= 8 -> Color(0xFF4CAF50)
                        passLength >= 4 -> GoldenGradientStart
                        else -> Color(0xFFE53935)
                    }
                    val strengthText = when {
                        passLength >= 8 -> if (isEnglish) "Strong password ✓" else "密码强度高 ✓"
                        passLength >= 4 -> if (isEnglish) "Moderate strength" else "密码强度中等"
                        else -> if (isEnglish) "Weak (at least 6 chars)" else "密码较弱 (建议8位以上)"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(if (passLength > 0) strengthColor else Color.LightGray))
                        Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(if (passLength >= 4) strengthColor else Color.LightGray))
                        Box(modifier = Modifier.weight(1f).height(4.dp).clip(CircleShape).background(if (passLength >= 8) Color(0xFF4CAF50) else Color.LightGray))
                    }
                    Text(
                        text = strengthText,
                        fontSize = 11.sp,
                        color = strengthColor,
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(if (isEnglish) "Confirm Password" else "再次确认密码", fontSize = 13.sp) },
                        placeholder = { Text(if (isEnglish) "Re-enter password" else "请再次输入一致的密码", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = GoldenGradientStart
                            )
                        },
                        trailingIcon = {
                            if (confirmPassword.isNotEmpty() && confirmPassword == password) {
                                Icon(Icons.Default.Check, contentDescription = "Match", tint = Color(0xFF4CAF50))
                            } else {
                                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                    Icon(
                                        imageVector = if (showConfirmPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldenGradientStart,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Checkbox & Clickable Links for User Agreement & Privacy Policy
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = agreedToPolicy,
                        onCheckedChange = { agreedToPolicy = it },
                        colors = CheckboxDefaults.colors(checkedColor = GoldenGradientStart)
                    )
                    Row(
                        modifier = Modifier.padding(start = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEnglish) "I agree to " else "我已阅读并同意",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isEnglish) "Terms of Service" else "《用户协议》",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenGradientStart,
                            modifier = Modifier.clickable { activeLegalDoc = LegalDocType.TERMS_OF_SERVICE }
                        )
                        Text(
                            text = if (isEnglish) " & " else "与",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isEnglish) "Privacy Policy" else "《隐私政策》",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldenGradientStart,
                            modifier = Modifier.clickable { activeLegalDoc = LegalDocType.PRIVACY_POLICY }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (authError != null) {
                    Text(
                        text = authError ?: "",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (socialAuthMessage != null) {
                    Text(
                        text = socialAuthMessage ?: "",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Primary Submit Button (Login or Register)
                if (isRegisterTab) {
                    GoldenButton(
                        text = if (isAuthLoading) (if (isEnglish) "Creating Account..." else "正在注册账号...") else (if (isEnglish) "Register Account" else "完成注册并登录"),
                        onClick = {
                            if (!agreedToPolicy) {
                                socialAuthMessage = if (isEnglish) "Please accept Terms & Privacy Policy first" else "请先勾选同意《用户协议》与《隐私政策》"
                                return@GoldenButton
                            }
                            if (password.isEmpty() || password != confirmPassword) {
                                socialAuthMessage = if (isEnglish) "Passwords do not match" else "两次输入的密码不一致，请重新检查"
                                return@GoldenButton
                            }
                            viewModel.registerWithFirebase(email, password) {
                                viewModel.navigateTo(AppScreen.MAIN_TAB)
                            }
                        }
                    )
                } else {
                    PrimaryVioletButton(
                        text = if (isAuthLoading) (if (isEnglish) "Signing in..." else "正在安全登录...") else (if (isEnglish) "Email Log In" else "邮箱一键登录"),
                        onClick = {
                            if (!agreedToPolicy) {
                                socialAuthMessage = if (isEnglish) "Please accept Terms & Privacy Policy first" else "请先勾选同意《用户协议》与《隐私政策》"
                                return@PrimaryVioletButton
                            }
                            viewModel.loginWithFirebase(email, password) {
                                viewModel.navigateTo(AppScreen.MAIN_TAB)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Divider: Quick Social Login Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                    Text(
                        text = if (isEnglish) "  Quick Social Sign-In  " else "  第三方账号快捷登录  ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Requirement 5: Two Quick Social Login Methods: Google & Facebook
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Google Account One-Click Sign-In
                    GoogleSocialButton(
                        isEnglish = isEnglish,
                        isLoading = isAuthLoading,
                        onClick = {
                            if (!agreedToPolicy) {
                                socialAuthMessage = if (isEnglish) "Please accept Terms & Privacy Policy first" else "请先勾选同意《用户协议》与《隐私政策》"
                                return@GoogleSocialButton
                            }
                            socialAuthMessage = if (isEnglish) "Connecting to Google Account..." else "正在通过 Google 账号一键验证..."
                            viewModel.loginWithGoogle {
                                viewModel.navigateTo(AppScreen.MAIN_TAB)
                            }
                        }
                    )

                    // Facebook Account One-Click Sign-In
                    FacebookSocialButton(
                        isEnglish = isEnglish,
                        isLoading = isAuthLoading,
                        onClick = {
                            if (!agreedToPolicy) {
                                socialAuthMessage = if (isEnglish) "Please accept Terms & Privacy Policy first" else "请先勾选同意《用户协议》与《隐私政策》"
                                return@FacebookSocialButton
                            }
                            socialAuthMessage = if (isEnglish) "Connecting to Facebook Account..." else "正在通过 Facebook 账号一键验证..."
                            viewModel.loginWithFacebook {
                                viewModel.navigateTo(AppScreen.MAIN_TAB)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Footer Switch
                Row(
                    modifier = Modifier.clickable { isRegisterTab = !isRegisterTab },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) {
                            if (isRegisterTab) "Already have an account? " else "Don't have an account yet? "
                        } else {
                            if (isRegisterTab) "已有金色韶华账号？ " else "还没有注册账号？ "
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isEnglish) {
                            if (isRegisterTab) "Switch to Log In" else "Register Now"
                        } else {
                            if (isRegisterTab) "切换至登录" else "立即免费注册"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldenGradientStart
                    )
                }
            }
        }
    }

    // Requirement 3: User Agreement & Privacy Policy Dialog with Dual Chinese and English Versions
    activeLegalDoc?.let { type ->
        LegalDocumentDialog(
            docType = type,
            initialIsEnglish = isEnglish,
            onDismiss = { activeLegalDoc = null }
        )
    }
}

// Requirement 5: Google One-Click Login Button Component
@Composable
fun GoogleSocialButton(
    isEnglish: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Branded Google Multi-Color Badge Icon
            Canvas(modifier = Modifier.size(20.dp)) {
                val w = size.width
                val h = size.height
                // Draw Google G representation icon shape
                drawCircle(color = Color(0xFF4285F4), radius = w * 0.45f)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (isEnglish) "Google One-Tap Login" else "Google 帐号一键登录",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Requirement 5: Facebook One-Click Login Button Component
@Composable
fun FacebookSocialButton(
    isEnglish: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1877F2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Facebook White 'f' Badge Icon
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "f",
                    color = Color(0xFF1877F2),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (isEnglish) "Facebook Quick Sign-In" else "Facebook 帐号一键登录",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// Requirement 3: Dedicated Legal Document Dialog supporting both Chinese & English Versions
@Composable
fun LegalDocumentDialog(
    docType: LegalDocType,
    initialIsEnglish: Boolean,
    onDismiss: () -> Unit
) {
    var isDocEnglish by remember { mutableStateOf(initialIsEnglish) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar with Document Title & Language Version Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldenGradientStart.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (docType == LegalDocType.TERMS_OF_SERVICE) Icons.Default.Gavel else Icons.Default.PrivacyTip,
                                contentDescription = null,
                                tint = GoldenGradientStart,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (docType == LegalDocType.TERMS_OF_SERVICE) {
                                    if (isDocEnglish) "Terms of Service" else "《用户服务协议》"
                                } else {
                                    if (isDocEnglish) "Privacy Policy" else "《隐私保护政策》"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDocEnglish) "Golden Memory Legal Specification" else "金色韶华法律条款与隐私承诺",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // In-Dialog Language Version Selector (中英文双版本切换)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isDocEnglish) GoldenGradientStart else Color.Transparent)
                            .clickable { isDocEnglish = false }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🇨🇳 中文原版条款",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isDocEnglish) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDocEnglish) GoldenGradientStart else Color.Transparent)
                            .clickable { isDocEnglish = true }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🇺🇸 English Version",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDocEnglish) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Legal Text Content Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 4.dp)
                ) {
                    if (docType == LegalDocType.TERMS_OF_SERVICE) {
                        if (!isDocEnglish) {
                            LegalSectionTitle("1. 服务概述与平台定位")
                            LegalBodyText("欢迎使用《金色韶华》（Golden Memory）。本平台致力于为家庭及长辈提供云端记忆保存、旧照AI修复、诗意描述生成及长辈无障碍服务。使用本平台即代表您同意本协议全部条款。")

                            LegalSectionTitle("2. 账号注册与安全保密")
                            LegalBodyText("用户须通过真实电子邮箱注册，或通过授权的 Google / Facebook 第三方账号进行快捷登录。您有责任妥善保管您的登录凭证与密码，对账号下的所有活动承担法律责任。")

                            LegalSectionTitle("3. 家族相册与媒体版权归属")
                            LegalBodyText("您在《金色韶华》上传的照片、老旧视频及家族故事回忆，其著作权及所有权严格归属于上传用户及其授权的家族成员。本平台绝不将您的私密家族内容售卖、出租或授权给第三方广告机构。")

                            LegalSectionTitle("4. AI 智能功能授权说明")
                            LegalBodyText("当您开启旧照修复、智能面部人物分类、画质增强及长辈语音朗读功能时，系统将使用端到端加密传输技术将媒体特征提炼并处理。AI 生成内容仅供家族内部查阅与回忆留存。")

                            LegalSectionTitle("5. 服务变更与免责条款")
                            LegalBodyText("本平台采取多重云端备份保障您的回忆不丢失。因网络故障或不可抗力导致的服务中断，平台将全力进行恢复与技术维护。")
                        } else {
                            LegalSectionTitle("1. Platform Overview & Scope")
                            LegalBodyText("Welcome to Golden Memory. Our platform is built to safeguard family history, provide AI photo restoration, and deliver senior accessibility care. By accessing our services, you agree to these Terms.")

                            LegalSectionTitle("2. Account Registration & Security")
                            LegalBodyText("Users must register with a valid email or sign in using authorized Google or Facebook social accounts. You are responsible for safeguarding password credentials and managing access to your family space.")

                            LegalSectionTitle("3. Family Media Ownership & Intellectual Property")
                            LegalBodyText("All photos, home videos, and oral history recordings uploaded remain your exclusive property and that of your authorized family members. Golden Memory strictly prohibits monetization or selling of your private media.")

                            LegalSectionTitle("4. AI Features & Data Processing")
                            LegalBodyText("By using AI Photo Restoration, Face Tagging, and Voice Narration features, media attributes are processed securely using end-to-end encrypted pipelines. Generated visual outputs are intended solely for family memory preservation.")

                            LegalSectionTitle("5. Service Disclaimers & Redundancy")
                            LegalBodyText("We implement multi-region cloud backup redundancy to shield your family records from physical loss or hardware failure.")
                        }
                    } else {
                        if (!isDocEnglish) {
                            LegalSectionTitle("1. 个人信息收集与范围")
                            LegalBodyText("为提供完善的家族相册服务，《金色韶华》仅收集以下必要信息：\n• 注册电子邮箱地址（用于登录与密码找回）\n• 照片内置的 GPS 经纬度位置（用于地图足迹聚合功能）\n• AI面部特征识别标签（用于全家福成员智能归类）")

                            LegalSectionTitle("2. 云端数据安全与 AES-256 加密")
                            LegalBodyText("所有上传的图像与视频数据均经由 AES-256 军工级算法加密存储于高度安全的物理数据中心，传输过程全面支持 TLS 1.3 协议，防止网络监听与泄露。")

                            LegalSectionTitle("3. 第三方平台登录与隐私隔离")
                            LegalBodyText("当您选择 Google 或 Facebook 快捷登录时，我们仅接收 OAuth2 协议授权的公共 Token 令牌，绝不侵入或收集您在社交平台上的非公开好友或隐私信息。")

                            LegalSectionTitle("4. 用户控制权与数据一键清除")
                            LegalBodyText("您随时拥有导出全部高清原图、编辑隐私防护密码以及彻底注销账号并删除所有云端记忆副本的权利。")
                        } else {
                            LegalSectionTitle("1. Information Collection Notice")
                            LegalBodyText("To power modern family memory services, Golden Memory collects:\n• Registration email address (for account credentials)\n• Photo embedded GPS metadata (for Map Cluster view)\n• AI face tag labels (for smart family album grouping)")

                            LegalSectionTitle("2. Cloud Storage & AES-256 Encryption")
                            LegalBodyText("All media assets and personal records are encrypted at rest using AES-256 standards and transmitted via secure TLS 1.3 protocols, safeguarding data against unauthorized access.")

                            LegalSectionTitle("3. Social Login Privacy Protections")
                            LegalBodyText("When logging in via Google or Facebook single sign-on, we only process standardized OAuth2 tokens. We do not access or collect private social friends or external chat history.")

                            LegalSectionTitle("4. User Control & Data Erasure Rights")
                            LegalBodyText("You retain full rights to export your family gallery at full resolution or request permanent erasure of your account and cloud backups at any time.")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Button: Confirm & Close
                GoldenButton(
                    text = if (isDocEnglish) "I Have Read & Understood" else "我已阅读并知悉",
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
fun LegalSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = GoldenGradientStart,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    )
}

@Composable
fun LegalBodyText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
