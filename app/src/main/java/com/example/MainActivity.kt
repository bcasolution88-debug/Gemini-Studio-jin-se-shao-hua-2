package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.AppScreen
import com.example.ui.AppStateViewModel
import com.example.ui.MainTab
import com.example.ui.components.AppBottomNavBar
import com.example.ui.components.BottomLeftNavigationControls
import com.example.ui.components.EditEmergencyContactDialog
import com.example.ui.components.EmergencyCallDialog
import com.example.ui.screens.ElderModeScreen
import com.example.ui.screens.EncryptedVaultScreen
import com.example.ui.screens.FamilyGroupQrScreen
import com.example.ui.screens.FamilyMembersScreen
import com.example.ui.screens.FamilyTreeScreen
import com.example.ui.screens.GoldenAnniversaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MemoryBookScreen
import com.example.ui.screens.MemoryDetailScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.NewMemoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegisterScreen
import com.example.ui.screens.SlideshowScreen
import com.example.ui.screens.StoriesScreen
import com.example.ui.screens.TimelineScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.screens.VideoStudioScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.GoldenMemoryTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AppStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isElderMode by viewModel.isElderMode.collectAsState()
            val isHighContrastMode by viewModel.isHighContrastMode.collectAsState()
            val isEnglish by viewModel.isEnglish.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val selectedTab by viewModel.selectedTab.collectAsState()

            val showEmergencyDialDialog by viewModel.showEmergencyDialDialog.collectAsState()
            val emergencyContactName by viewModel.emergencyContactName.collectAsState()
            val emergencyContactPhone by viewModel.emergencyContactPhone.collectAsState()
            var showEditContactDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

            GoldenMemoryTheme(darkTheme = isDarkMode, isElderMode = isElderMode, isHighContrast = isHighContrastMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentScreen == AppScreen.MAIN_TAB) {
                            AppBottomNavBar(
                                selectedTab = selectedTab,
                                onTabSelected = { viewModel.selectTab(it) },
                                isEnglish = isEnglish,
                                onEmergencyDialClick = { viewModel.openEmergencyDialDialog() }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (currentScreen) {
                            AppScreen.WELCOME -> WelcomeScreen(viewModel)
                            AppScreen.LOGIN -> LoginScreen(viewModel)
                            AppScreen.REGISTER -> RegisterScreen(viewModel)
                            AppScreen.MAIN_TAB -> {
                                when (selectedTab) {
                                    MainTab.HOME -> HomeScreen(viewModel)
                                    MainTab.STORIES -> StoriesScreen(viewModel)
                                    MainTab.MESSAGES -> MessagesScreen(viewModel)
                                    MainTab.ALBUMS -> TimelineScreen(viewModel)
                                    MainTab.ME -> ProfileScreen(viewModel)
                                }
                            }
                            AppScreen.TIMELINE -> TimelineScreen(viewModel)
                            AppScreen.GOLDEN_ANNIVERSARY -> GoldenAnniversaryScreen(viewModel)
                            AppScreen.FAMILY_MEMBERS -> FamilyMembersScreen(viewModel)
                            AppScreen.FAMILY_TREE -> FamilyTreeScreen(viewModel)
                            AppScreen.NEW_MEMORY -> NewMemoryScreen(viewModel)
                            AppScreen.MEMORY_DETAIL -> MemoryDetailScreen(viewModel)
                            AppScreen.VIDEO_STUDIO -> VideoStudioScreen(viewModel)
                            AppScreen.VIDEO_PLAYER -> VideoPlayerScreen(viewModel)
                            AppScreen.ELDER_MODE_SETTINGS -> ElderModeScreen(viewModel)
                            AppScreen.ENCRYPTED_VAULT -> EncryptedVaultScreen(viewModel)
                            AppScreen.MEMORY_BOOK -> MemoryBookScreen(viewModel)
                            AppScreen.SLIDESHOW -> SlideshowScreen(viewModel)
                            AppScreen.FAMILY_GROUP_QR -> FamilyGroupQrScreen(viewModel)
                        }

                        // Bottom-Left Floating Navigation Buttons: "返回上一页面" (Go Back) & "回到顶端" (Scroll To Top)
                        BottomLeftNavigationControls(
                            onBackClick = { viewModel.goBack() },
                            onScrollToTopClick = { viewModel.triggerScrollToTop() },
                            isBottomBarPresent = (currentScreen == AppScreen.MAIN_TAB),
                            isEnglish = isEnglish,
                            modifier = Modifier.align(androidx.compose.ui.Alignment.BottomStart)
                        )
                    }

                    if (showEmergencyDialDialog) {
                        EmergencyCallDialog(
                            contactName = emergencyContactName,
                            contactPhone = emergencyContactPhone,
                            isEnglish = isEnglish,
                            onDismiss = { viewModel.closeEmergencyDialDialog() },
                            onEditContactRequested = {
                                viewModel.closeEmergencyDialDialog()
                                showEditContactDialog = true
                            }
                        )
                    }

                    if (showEditContactDialog) {
                        EditEmergencyContactDialog(
                            initialName = emergencyContactName,
                            initialPhone = emergencyContactPhone,
                            isEnglish = isEnglish,
                            onSave = { name, phone -> viewModel.updateEmergencyContact(name, phone) },
                            onDismiss = { showEditContactDialog = false }
                        )
                    }
                }
            }
        }
    }
}
