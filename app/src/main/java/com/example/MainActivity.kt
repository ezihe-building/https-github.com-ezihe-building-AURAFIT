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
                val activeTab by viewModel.activeDashboardTab.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = ObsidianBlack,
                    bottomBar = {
                        // Display modern glassy bottom bar ONLY for dashboard primary screens
                        if (route == "dashboard") {
                            NavigationBar(
                                containerColor = ObsidianBlack.copy(alpha = 0.92f),
                                contentColor = AuraNeonCyan,
                                tonalElevation = 0.dp,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.verticalGradient(
                                            listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                                        ),
                                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                                    )
                                    .navigationBarsPadding() // Mandate: Keep notch/navigation bar safe areas
                                    .testTag("app_navigation_bar")
                            ) {
                                NavigationBarItem(
                                    selected = activeTab == 0,
                                    onClick = { viewModel.activeDashboardTab.value = 0 },
                                    icon = { Icon(Icons.Default.Home, "Home") },
                                    label = { Text("Home") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = AuraNeonCyan,
                                        indicatorColor = AuraNeonCyan,
                                        unselectedIconColor = FrostWhite.copy(alpha = 0.4f),
                                        unselectedTextColor = FrostWhite.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_home")
                                )

                                NavigationBarItem(
                                    selected = activeTab == 1,
                                    onClick = { viewModel.activeDashboardTab.value = 1 },
                                    icon = { Icon(Icons.Default.Explore, "Explore") },
                                    label = { Text("Explore") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = AuraNeonCyan,
                                        indicatorColor = AuraNeonCyan,
                                        unselectedIconColor = FrostWhite.copy(alpha = 0.4f),
                                        unselectedTextColor = FrostWhite.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_explore")
                                )

                                NavigationBarItem(
                                    selected = activeTab == 2,
                                    onClick = { viewModel.activeDashboardTab.value = 2 },
                                    icon = { Icon(Icons.Default.Star, "Programs") },
                                    label = { Text("Programs") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = AuraNeonCyan,
                                        indicatorColor = AuraNeonCyan,
                                        unselectedIconColor = FrostWhite.copy(alpha = 0.4f),
                                        unselectedTextColor = FrostWhite.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_programs")
                                )

                                NavigationBarItem(
                                    selected = activeTab == 3,
                                    onClick = { viewModel.activeDashboardTab.value = 3 },
                                    icon = { Icon(Icons.Default.Person, "Profile") },
                                    label = { Text("Profile") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ObsidianBlack,
                                        selectedTextColor = AuraNeonCyan,
                                        indicatorColor = AuraNeonCyan,
                                        unselectedIconColor = FrostWhite.copy(alpha = 0.4f),
                                        unselectedTextColor = FrostWhite.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_profile")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                bottom = if (route == "dashboard") 
                                    innerPadding.calculateBottomPadding() 
                                else 0.dp
                            )
                    ) {
                        // Premium Staggered Animation Route switcher (transitions under 300ms)
                        AnimatedContent(
                            targetState = route,
                            transitionSpec = {
                                val order = listOf("splash", "auth", "onboarding", "dashboard", "timer")
                                val fromIdx = order.indexOf(initialState).takeIf { it >= 0 } ?: 0
                                val toIdx = order.indexOf(targetState).takeIf { it >= 0 } ?: 0
                                if (toIdx > fromIdx) {
                                    (slideInHorizontally(animationSpec = tween(320)) { width -> width } + fadeIn(animationSpec = tween(280)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(285)) { width -> -width } + fadeOut(animationSpec = tween(220)))
                                } else {
                                    (slideInHorizontally(animationSpec = tween(320)) { width -> -width } + fadeIn(animationSpec = tween(280)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(285)) { width -> width } + fadeOut(animationSpec = tween(220)))
                                }
                            },
                            label = "screen_routing"
                        ) { targetRoute ->
                            when (targetRoute) {
                                "splash" -> SplashScreen(viewModel = viewModel)
                                "auth" -> AuthScreen(viewModel = viewModel)
                                "onboarding" -> OnboardingScreen(viewModel = viewModel)
                                "dashboard" -> DashboardScreen(viewModel = viewModel)
                                "timer" -> WorkoutTimerScreen(viewModel = viewModel)
                                else -> SplashScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
