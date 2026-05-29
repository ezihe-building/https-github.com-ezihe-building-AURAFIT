package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    private val viewModel: AuraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Mandate 1: Edge-to-Edge interface support
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val route by viewModel.currentRoute.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ObsidianBlack,
                    bottomBar = {
                        // Display modern glassy bottom bar ONLY for primary screens
                        if (route == "dashboard" || route == "history" || route == "settings" || route == "notifications") {
                            NavigationBar(
                                containerColor = ObsidianBlack.copy(alpha = 0.85f),
                                contentColor = AuraNeonCyan,
                                tonalElevation = 0.dp,
                                modifier = Modifier
                                    .border(width = 1.dp, brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)), shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    .navigationBarsPadding() // Mandate: Keep notch/navigation bar safe areas
                                    .testTag("app_navigation_bar")
                            ) {
                                NavigationBarItem(
                                    selected = route == "dashboard" || route == "notifications",
                                    onClick = { viewModel.currentRoute.value = "dashboard" },
                                    icon = { Icon(Icons.Default.Dashboard, "Home") },
                                    label = { Text("Aura") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = AuraNeonCyan,
                                        indicatorColor = AuraNeonCyan,
                                        unselectedIconColor = FrostWhite.copy(alpha = 0.4f),
                                        unselectedTextColor = FrostWhite.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_dashboard")
                                )

                                NavigationBarItem(
                                    selected = route == "history",
                                    onClick = { viewModel.currentRoute.value = "history" },
                                    icon = { Icon(Icons.Default.Timeline, "Logs") },
                                    label = { Text("History") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = AuraNeonCyan,
                                        indicatorColor = AuraNeonCyan,
                                        unselectedIconColor = FrostWhite.copy(alpha = 0.4f),
                                        unselectedTextColor = FrostWhite.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_history")
                                )

                                NavigationBarItem(
                                    selected = route == "settings",
                                    onClick = { viewModel.currentRoute.value = "settings" },
                                    icon = { Icon(Icons.Default.Settings, "Settings") },
                                    label = { Text("Settings") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = AuraNeonCyan,
                                        indicatorColor = AuraNeonCyan,
                                        unselectedIconColor = FrostWhite.copy(alpha = 0.4f),
                                        unselectedTextColor = FrostWhite.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_settings")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = if (route == "dashboard" || route == "history" || route == "settings" || route == "notifications") 
                                    innerPadding.calculateBottomPadding() 
                                else 0.dp
                            )
                    ) {
                        // Premium Staggered Animation Route switcher (transitions under 300ms)
                        AnimatedContent(
                            targetState = route,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                            },
                            label = "screen_routing"
                        ) { targetRoute ->
                            when (targetRoute) {
                                "splash" -> SplashScreen()
                                "auth" -> AuthScreen(viewModel = viewModel)
                                "onboarding" -> OnboardingScreen(viewModel = viewModel)
                                "dashboard" -> DashboardScreen(viewModel = viewModel)
                                "timer" -> WorkoutTimerScreen(viewModel = viewModel)
                                "history" -> WorkoutHistoryScreen(viewModel = viewModel)
                                "settings" -> SettingsScreen(viewModel = viewModel)
                                "notifications" -> NotificationsScreen(viewModel = viewModel)
                                else -> SplashScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
