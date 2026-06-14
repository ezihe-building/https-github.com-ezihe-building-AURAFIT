package com.example.ui

import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlin.math.sin

// ==========================================
// CENTRAL GRADIENT & INTERACTION COMPOSABLES
// ==========================================

@Composable
fun AuraGradientBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF04060A))
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x3300FFC2), Color.Transparent),
                        radius = size.width * 1.2f
                    ),
                    center = Offset(x = -size.width * 0.2f, y = -size.height * 0.1f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x228B5CF6), Color.Transparent),
                        radius = size.width * 1.3f
                    ),
                    center = Offset(x = size.width * 1.2f, y = size.height * 1.1f)
                )
            }
    ) {
        content()
    }
}

@Composable
fun PulsingVoiceIndicator(isThinking: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "voicePulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voicePulseScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voicePulseAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(if (isThinking) Color(0xFF8B5CF6).copy(alpha = alpha) else AuraNeonCyan.copy(alpha = alpha))
        )
        Icon(
            imageVector = if (isThinking) Icons.Default.AutoAwesome else Icons.Default.GraphicEq,
            contentDescription = null,
            tint = ObsidianBlack,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun AuraGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.White.copy(alpha = 0.08f),
    backgroundColor: Color = Color(0xFF0D121F).copy(alpha = 0.85f),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, Color.Transparent)
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

data class Badge(
    val title: String, 
    val description: String, 
    val icon: androidx.compose.ui.graphics.vector.ImageVector, 
    val isUnlocked: Boolean
)

@Composable
fun BadgeItemView(badge: Badge) {
    val haptic = LocalHapticFeedback.current
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (badge.isUnlocked) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            kotlinx.coroutines.delay(100)
        }
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
        modifier = Modifier.width(150.dp)
    ) {
        AuraGlassCard {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (badge.isUnlocked) AuraNeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon, 
                    contentDescription = badge.title,
                    tint = if (badge.isUnlocked) AuraNeonCyan else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = badge.title,
                color = if (badge.isUnlocked) Color.White else Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = badge.description,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
            if (!badge.isUnlocked) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(14.dp))
            }
        }
    }
    }
}

// ==========================================
// 1. SPLASH / INTRO SCREEN SLIDER (Image 2 & 20)
// ==========================================

@Composable
fun SplashScreen(viewModel: AuraViewModel) {
    var slideIndex by remember { mutableStateOf(0) }
    
    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OfflineBolt,
                    contentDescription = null,
                    tint = AuraNeonCyan,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ULPIFIT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            // Image Slide Container with Glowing Rings
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Glow circles
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .scaleAnimation()
                        .background(
                            Brush.radialGradient(
                                colors = if (slideIndex == 0) {
                                    listOf(AuraNeonCyan.copy(alpha = 0.15f), Color.Transparent)
                                } else {
                                    listOf(AuraAestheticPurple.copy(alpha = 0.15f), Color.Transparent)
                                }
                            )
                        )
                )

                // Simulated dynamic illustration
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1524))
                        .border(1.5.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (slideIndex == 0) Icons.Default.RestaurantMenu else Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = if (slideIndex == 0) AuraNeonCyan else AuraAestheticPurple,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            // Carousel Slide Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = slideIndex,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "slide_text"
                ) { index ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = if (index == 0) "Personalized Nutrition Starts Here" else "Personalized Workout & Training",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (index == 0) {
                                "Based on your unique goals, body type, and dietary preferences, find the perfect fuel for your routine."
                            } else {
                                "Tailored exercises for your strength level. Build solid back, chest, and lower body structures with perfect form."
                            },
                            fontSize = 14.sp,
                            color = FrostWhite.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(height = 6.dp, width = if (slideIndex == 0) 18.dp else 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (slideIndex == 0) AuraNeonCyan else Color.White.copy(alpha = 0.2f))
                    )
                    Box(
                        modifier = Modifier
                            .size(height = 6.dp, width = if (slideIndex == 1) 18.dp else 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (slideIndex == 1) AuraNeonCyan else Color.White.copy(alpha = 0.2f))
                    )
                }

                // Interactive Navigation CTA
                AuraButton(
                    onClick = {
                        if (slideIndex == 0) {
                            slideIndex = 1
                        } else {
                            viewModel.currentRoute.value = "auth"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("splash_next_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (slideIndex == 0) AuraNeonCyan else AuraAestheticPurple,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (slideIndex == 0) "Explore  →" else "Browse Workouts  →",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// Scale pulsation animation utility
@Composable
fun Modifier.scaleAnimation(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    return this.scale(scale)
}

// ==========================================
// 2. REDESIGNED AUTH SCREENS (Image 4 & 5 & 3 & 9)
// ==========================================

@Composable
fun AuthScreen(viewModel: AuraViewModel) {
    val email by viewModel.authEmailInput.collectAsStateWithLifecycle()
    val password by viewModel.authPasswordInput.collectAsStateWithLifecycle()
    val nameInput by viewModel.authNameInput.collectAsStateWithLifecycle()
    val resetToken by viewModel.authResetTokenInput.collectAsStateWithLifecycle()
    val resetPassword by viewModel.authResetPasswordInput.collectAsStateWithLifecycle()
    val otpInput by viewModel.authOtpInput.collectAsStateWithLifecycle()

    val error by viewModel.authError.collectAsStateWithLifecycle()
    val success by viewModel.authSuccess.collectAsStateWithLifecycle()
    val isLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val authMode by viewModel.authModeModel.collectAsStateWithLifecycle() // login, register, forgot, verify_otp

    AuraGradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // App Brand Identity
                Icon(
                    imageVector = Icons.Default.OfflineBolt,
                    contentDescription = null,
                    tint = AuraNeonCyan,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                AnimatedContent(
                    targetState = authMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "auth_headers"
                ) { mode ->
                    Text(
                        text = when (mode) {
                            "register" -> "Sign up for free"
                            "login" -> "Sign in to AURAFIT"
                            "forgot" -> "Reset password"
                            "verify_otp" -> "OTP Verification"
                            else -> "Sign in to AURAFIT"
                        },
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error / Success Message
                if (error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF3B0B14))
                            .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = error ?: "",
                            color = Color(0xFFFCA5A5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (success != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2E190A))
                            .border(1.dp, AuraNeonCyan, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = success ?: "",
                            color = Color(0xFFFFCC99),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Dynamic forms based on Auth Modes
                when (authMode) {
                    "register" -> {
                        // Sign up for free (Image 4)
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { viewModel.authNameInput.value = it },
                            label = { Text("Display Name", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_name_field")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.authEmailInput.value = it },
                            label = { Text("Email address", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_email_field")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.authPasswordInput.value = it },
                            label = { Text("Password", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("auth_password_field")
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AuraButton(
                            onClick = {
                                viewModel.onboardName.value = nameInput
                                viewModel.triggerSignUp()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_signup_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = ObsidianBlack, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Sign Up", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Already have an account? ", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                            Text(
                                "Sign In",
                                color = AuraNeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    viewModel.clearAuthMessages()
                                    viewModel.authModeModel.value = "login"
                                }
                            )
                        }
                    }

                    "login" -> {
                        // Sign in to AURAFIT (Image 5)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.authEmailInput.value = it },
                            label = { Text("Email address", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_email_field")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { viewModel.authPasswordInput.value = it },
                            label = { Text("Password", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("auth_password_field")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                "Forgot password?",
                                color = AuraNeonCyan.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    viewModel.clearAuthMessages()
                                    viewModel.authModeModel.value = "forgot"
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AuraButton(
                            onClick = { viewModel.triggerLogIn() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_login_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = ObsidianBlack, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Don't have an account? ", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                            Text(
                                "Sign Up",
                                color = AuraNeonCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    viewModel.clearAuthMessages()
                                    viewModel.authModeModel.value = "register"
                                }
                            )
                        }
                    }

                    "forgot" -> {
                        // Reset Password Screen (Image 3)
                        Text(
                            text = "We'll send you offline instruction tokens to reset your password.",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { viewModel.authEmailInput.value = it },
                            label = { Text("Email address", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AuraButton(
                            onClick = { viewModel.triggerForgotPassword() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Send instruction", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (resetToken.isNotEmpty()) {
                            // Offline verification token received -> directly key in password change!
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFF0F1626))
                                    .border(1.dp, AuraNeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text("Reset Code Generated: $resetToken", fontWeight = FontWeight.Bold, color = AuraNeonCyan)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = resetPassword,
                                        onValueChange = { viewModel.authResetPasswordInput.value = it },
                                        label = { Text("New Password") },
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = AuraNeonCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        visualTransformation = PasswordVisualTransformation(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    AuraButton(
                                        onClick = { viewModel.triggerResetPassword() },
                                        colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Save Password")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Back to Login",
                            color = AuraNeonCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                viewModel.clearAuthMessages()
                                viewModel.authModeModel.value = "login"
                            }
                        )
                    }

                    "verify_otp" -> {
                        // Clerk Code OTP view (Image 7/Verification setup)
                        Text(
                            text = "Please enter the 6-digit verification code sent to your inbox.",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { viewModel.authOtpInput.value = it },
                            label = { Text("Security Code", color = Color.White.copy(alpha = 0.4f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("auth_otp_field")
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AuraButton(
                            onClick = { viewModel.verifyOtpAndCompleteSignUp() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("auth_verify_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Complete Registration", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Social Single Sign On Integration Widget
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.08f))
                    Text("Or continue with", modifier = Modifier.padding(horizontal = 14.dp), color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp)
                    Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.08f))
                }

                Spacer(modifier = Modifier.height(18.dp))

                AuraButton(
                    onClick = { viewModel.triggerGoogleSSO("sso.athlete@ulpifit.com") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.03f), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = AuraNeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ==========================================
// 3. STEP-BY-STEP ONBOARDING WIZARD (Image 7 & 11)
// ==========================================

@Composable
fun OnboardingScreen(viewModel: AuraViewModel) {
    var step by remember { mutableStateOf(1) } // 1 to 6 steps

    val name by viewModel.onboardName.collectAsStateWithLifecycle()
    val age by viewModel.onboardAge.collectAsStateWithLifecycle()
    val gender by viewModel.onboardGender.collectAsStateWithLifecycle()
    val height by viewModel.onboardHeight.collectAsStateWithLifecycle()
    val weight by viewModel.onboardWeight.collectAsStateWithLifecycle()
    val goal by viewModel.onboardGoal.collectAsStateWithLifecycle()
    val error by viewModel.onboardingError.collectAsStateWithLifecycle()

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Assessment Progress Indicators
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ATHLETE ASSESSMENT",
                        color = AuraNeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "$step of 6",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Graphical progress bar dividers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 1..6) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (i <= step) AuraNeonCyan else Color.White.copy(alpha = 0.1f)
                                )
                        )
                    }
                }
            }

            // Step Content Holder
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        1 -> {
                            Text("What is your display name?", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { viewModel.onboardName.value = it },
                                placeholder = { Text("E.g., Eren Jaeger", color = Color.White.copy(alpha = 0.3f)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = AuraNeonCyan
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("onboard_name_field")
                            )
                        }

                        2 -> {
                            Text("Provide your age", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedTextField(
                                value = age,
                                onValueChange = { viewModel.onboardAge.value = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = AuraNeonCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        3 -> {
                            // What is your gender? (Image 7)
                            Text("What is your gender?", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Male Selection Card
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (gender == "Male") AuraNeonCyan.copy(alpha = 0.15f) else Color(0xFF0F1422))
                                        .border(
                                            2.dp,
                                            if (gender == "Male") AuraNeonCyan else Color.White.copy(alpha = 0.05f),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .clickable { viewModel.onboardGender.value = "Male" }
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Male, null, tint = if (gender == "Male") AuraNeonCyan else Color.White, modifier = Modifier.size(54.dp))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("Male", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Female Selection Card
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (gender == "Female") AuraNeonCyan.copy(alpha = 0.15f) else Color(0xFF0F1422))
                                        .border(
                                            2.dp,
                                            if (gender == "Female") AuraNeonCyan else Color.White.copy(alpha = 0.05f),
                                            RoundedCornerShape(18.dp)
                                        )
                                        .clickable { viewModel.onboardGender.value = "Female" }
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Female, null, tint = if (gender == "Female") AuraNeonCyan else Color.White, modifier = Modifier.size(54.dp))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text("Female", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        4 -> {
                            Text("What is your height in cm?", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedTextField(
                                value = height,
                                onValueChange = { viewModel.onboardHeight.value = it },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = AuraNeonCyan),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        5 -> {
                            Text("What is your body weight in kg?", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { viewModel.onboardWeight.value = it },
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = AuraNeonCyan),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        6 -> {
                            // What is your fitness goal? (Image 11)
                            Text("What is your fitness goal?", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(18.dp))

                            val goals = listOf(
                                "Lose weight",
                                "Try AI Coach",
                                "Get bulks",
                                "Gain endurance",
                                "Trying out the app"
                            )

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(goals) { choice ->
                                    val isChosen = goal == choice
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(if (isChosen) AuraNeonCyan.copy(alpha = 0.15f) else Color(0xFF0F1422))
                                            .border(
                                                1.5.dp,
                                                if (isChosen) AuraNeonCyan else Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable { viewModel.onboardGoal.value = choice }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isChosen) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isChosen) AuraNeonCyan else Color.White.copy(alpha = 0.3f),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(choice, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (error != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(error ?: "", color = AuraAccentCoral, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Bottom Nav Wizard CTAs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (step > 1) {
                    AuraOutlinedButton(
                        onClick = { step -= 1 },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                }

                AuraButton(
                    onClick = {
                        if (step < 6) {
                            if (step == 1 && name.trim().isEmpty()) {
                                viewModel.onboardingError.value = "Please enter your display name."
                                return@AuraButton
                            }
                            viewModel.onboardingError.value = null
                            step += 1
                        } else {
                            viewModel.completeOnboardingSubmission()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(2f).height(50.dp).testTag("onboard_next_button")
                ) {
                    Text(
                        text = if (step == 6) "Finish Assessment" else "Next Step",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. UNIFIED DASHBOARD WITH 4 BEAUTIFUL TABS
// ==========================================

@Composable
fun DashboardScreen(viewModel: AuraViewModel) {
    val activeTab by viewModel.activeDashboardTab.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = activeTab,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInHorizontally(animationSpec = tween(250)) { width -> width / 3 } + fadeIn(animationSpec = tween(220)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(220)) { width -> -width / 3 } + fadeOut(animationSpec = tween(180)))
            } else {
                (slideInHorizontally(animationSpec = tween(250)) { width -> -width / 3 } + fadeIn(animationSpec = tween(220)))
                    .togetherWith(slideOutHorizontally(animationSpec = tween(220)) { width -> width / 3 } + fadeOut(animationSpec = tween(180)))
            }
        },
        label = "dashboard_tab_routing"
    ) { targetTab ->
        when (targetTab) {
            0 -> TabHomeView(viewModel)
            1 -> TabStatsView(viewModel)
            2 -> TabProgramsView(viewModel)
            3 -> TabProfileView(viewModel)
            else -> TabHomeView(viewModel)
        }
    }
}

// === TAB 0 : HOME VIEW (Image 1 & Image 6) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabHomeView(viewModel: AuraViewModel) {
    val profile by viewModel.activeProfile.collectAsStateWithLifecycle()
    var searchMealInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Vegetable") }

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Greeting & Search (Image 6)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${profile?.name ?: "Athlete"}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Looking for a healthy meal?",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                AuraIconButton(
                    onClick = { viewModel.currentRoute.value = "dashboard"; viewModel.activeDashboardTab.value = 1 }, // open AI Chat directly
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Face, null, tint = AuraNeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar Input
            OutlinedTextField(
                value = searchMealInput,
                onValueChange = { searchMealInput = it },
                placeholder = { Text("Search meals or active recipes...", color = Color.White.copy(alpha = 0.3f)) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.4f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = AuraNeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Categories Row (Image 6)
            val categories = listOf(
                "Vegetable" to Icons.Default.Agriculture,
                "Beef/Meat" to Icons.Default.LocalPizza,
                "Fruit" to Icons.Default.Icecream,
                "Carbs" to Icons.Default.BreakfastDining
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { (name, icon) ->
                    val isChosen = selectedCategory == name
                    Row(
                        modifier = Modifier
                            .animateItem()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isChosen) AuraNeonCyan else Color.White.copy(alpha = 0.04f))
                            .clickable { selectedCategory = name }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .animateContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(icon, null, tint = if (isChosen) ObsidianBlack else Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(name, color = if (isChosen) ObsidianBlack else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Runner's Balanced Diet (Image 6)
            Text("ACTIVE NUTRIENTS DIETS", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(10.dp))

            AuraGlassCard {
                Text("Runner's Balanced Recipe Diet", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                Text("Provides high complex carbohydrates and lean proteins and fats designed for maximal cardiovascular replenishment.", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(vertical = 6.dp))
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CARBS", color = AuraNeonCyan, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        LinearProgressIndicator(progress = 0.65f, color = AuraNeonCyan, trackColor = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(top = 4.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PROTEIN", color = AuraAestheticPurple, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        LinearProgressIndicator(progress = 0.45f, color = AuraAestheticPurple, trackColor = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(top = 4.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("FATS", color = AuraAccentCoral, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        LinearProgressIndicator(progress = 0.25f, color = AuraAccentCoral, trackColor = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Week calendar (Image 1)
            Text("COMPLIANCE PROTOCOL CALENDAR", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val week = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                week.forEachIndexed { i, day ->
                    val isToday = i == 2 // Mock wednesday
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isToday) AuraNeonCyan else Color.White.copy(alpha = 0.03f))
                            .border(1.dp, if (isToday) AuraNeonCyan else Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                    ) {
                        Text(day, color = if (isToday) ObsidianBlack else Color.White.copy(alpha = 0.4f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("${15 + i}", color = if (isToday) ObsidianBlack else Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // MOVE Category (Image 1)
            Text("MOVE PROTOCOL", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))

            AuraGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.OfflineBolt, null, tint = AuraNeonCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Power Shred in Gym", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                        }
                        Text("Strength Training • 45 min • 350 Kcal", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }

                    AuraButton(
                        onClick = {
                            // Find and trigger gym routine preset
                            val drills = WorkoutPreset.exercises.filter { it.category == "Strength" }.take(6)
                            viewModel.launchWorkoutSession("Power Shred Gym Classic", "Strength", drills)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Start", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Meal Plan Shopping list card (Image 1)
            Text("MEAL PLANNING", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))
            AuraGlassCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingBag, null, tint = AuraNeonCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Active Shopping List", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("E.g., Chicken breast, spinach, avocados, sweet potato", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

// === TAB 1 : MY AI CHATS (Image 17, Chat Stream Image 19) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabExploreView(viewModel: AuraViewModel) {
    /*
    var searchChatStr by remember { mutableStateOf("") }
    var selectedMiniTab by remember { mutableStateOf("AI") } // AI, Archived, Deleted
    var activeChatByTitle by remember { mutableStateOf<String?>(null) } // if not null, show Chat thread screen!

    val chatsMessagesFlow by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val isAiDemoMode by viewModel.isAiDemoMode.collectAsStateWithLifecycle()
    val isThinkingMode by viewModel.isThinkingMode.collectAsStateWithLifecycle()

    var chatMessageInput by remember { mutableStateOf("") }

    AuraGradientBackground {
        if (activeChatByTitle != null) {
            // Full Chat Stream screen representing Image 19
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                // Chat Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AuraIconButton(onClick = { activeChatByTitle = null }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(activeChatByTitle ?: "AURAFIT AI Coach", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Active Fitness assistant", color = AuraNeonCyan, fontSize = 11.sp)
                    }
                    
                    // Thinking Mode Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("DEEP THINK", color = if (isThinkingMode) AuraNeonCyan else Color.White.copy(alpha=0.5f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Switch(
                            checked = isThinkingMode,
                            onCheckedChange = { viewModel.toggleThinkingMode(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = ObsidianBlack, checkedTrackColor = AuraNeonCyan)
                        )
                    }
                }

                // Chat bubble timeline
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatsMessagesFlow) { (sender, text) ->
                        val isAI = sender == "AURAFIT Assistant"
                        Row(
                            modifier = Modifier.fillMaxWidth().animateContentSize(),
                            horizontalArrangement = if (isAI) Arrangement.Start else Arrangement.End
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isAI) 2.dp else 14.dp,
                                            bottomEnd = if (!isAI) 2.dp else 14.dp
                                        )
                                    )
                                    .background(if (isAI) Color(0xFF131A2B) else AuraNeonCyan)
                                    .border(1.dp, if (isAI) Color.White.copy(alpha = 0.05f) else Color.Transparent)
                                    .padding(12.dp)
                                    .widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = text,
                                    color = if (isAI) Color.White else ObsidianBlack,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                    
                    if (isAiLoading) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp).animateItem(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PulsingVoiceIndicator(isThinking = isThinkingMode)
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFF131A2B))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = if (isThinkingMode) "AURAFIT is thinking deeply..." else "AURAFIT is speaking...",
                                        color = AuraNeonCyan.copy(alpha = 0.8f),
                                        fontSize = 13.sp,
                                        style = androidx.compose.ui.text.TextStyle(
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                
                AnimatedVisibility(visible = isAiDemoMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AuraAccentCoral.copy(alpha = 0.15f))
                            .border(1.dp, AuraAccentCoral.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "⚠️ Voice Demo Active: Showing offline responses. For live Gemini voice integration, populate GEMINI_API_KEY in secrets.",
                            color = AuraAccentCoral,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Bottom Send area
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AuraIconButton(
                        onClick = { viewModel.speakText("Recording initialized. Say your command") },
                        modifier = Modifier.background(Color(0xFF131A2B), CircleShape)
                    ) {
                        Icon(Icons.Default.Mic, null, tint = AuraNeonCyan)
                    }
                    OutlinedTextField(
                        value = chatMessageInput,
                        onValueChange = { chatMessageInput = it },
                        placeholder = { Text("Ask or press mic...", color = Color.White.copy(alpha = 0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = AuraNeonCyan
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    AuraIconButton(
                        onClick = {
                            if (chatMessageInput.trim().isNotEmpty()) {
                                val query = chatMessageInput
                                chatMessageInput = ""
                                viewModel.sendChatMessage(query)
                            }
                        },
                        modifier = Modifier.background(AuraNeonCyan, CircleShape)
                    ) {
                        Icon(Icons.Default.Send, null, tint = ObsidianBlack)
                    }
                }
            }
        } else {
            // Chat Lists index screen representing Image 17
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .safeDrawingPadding()
            ) {
                Spacer(modifier = Modifier.height(14.dp))

                Text("EXPLORE INTELLIGENCE", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                Text("My AI Chats", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

                Spacer(modifier = Modifier.height(14.dp))

                // Search Chat Input
                OutlinedTextField(
                    value = searchChatStr,
                    onValueChange = { searchChatStr = it },
                    placeholder = { Text("Search instructions...", color = Color.White.copy(alpha = 0.3f)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = AuraNeonCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Chat Lists SubTabs: AI, Archived, Deleted (Image 17)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val subtabs = listOf("AI", "Archived", "Deleted")
                    subtabs.forEach { text ->
                        val active = selectedMiniTab == text
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) AuraNeonCyan else Color.White.copy(alpha = 0.02f))
                                .border(1.dp, if (active) AuraNeonCyan else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .clickable { selectedMiniTab = text }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text, color = if (active) ObsidianBlack else Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Chats items array matching picture titles exactly
                val queriesList = listOf(
                    "How to bulk faster?" to "Sure, let's talk about calorie surplus...",
                    "Optimal Fitness Score" to "Your current fitness score is...",
                    "How much water daily?" to "Typically 2000ml but based on...",
                    "Gaining muscle fast" to "We can set you up with the Strength...",
                    "Nutrition upgrade" to "Eat more rich vegetables and carbs...",
                    "Fitness data ready" to "Awesome! Let's check status..."
                ).filter {
                    it.first.lowercase(Locale.US).contains(searchChatStr.lowercase(Locale.US))
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(queriesList) { (title, subtitle) ->
                        AuraGlassCard(
                            modifier = Modifier.fillMaxWidth().animateItem(),
                            onClick = { 
                                activeChatByTitle = title 
                                viewModel.selectActiveChat(title)
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(Color(0xFF1B2233), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ChatBubble, null, tint = AuraNeonCyan, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    */
}

// === TAB 1 : STATS & PROGRESS ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabStatsView(viewModel: AuraViewModel) {
    val profile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var selectedMetric by remember { mutableStateOf("Volume") }

    val metrics = listOf("Volume", "Weight", "Calories", "Active Time")

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .safeDrawingPadding()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            
            Text("YOUR PERFORMANCE", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text("Workout Stats", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

            Spacer(modifier = Modifier.height(16.dp))

            // Chips for metrics
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                metrics.forEach { metric ->
                    val isSelected = selectedMetric == metric
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) AuraNeonCyan else Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .clickable { selectedMetric = metric }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = metric,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = selectedMetric,
                modifier = Modifier.fillMaxWidth(),
                transitionSpec = {
                    (slideInHorizontally(animationSpec = tween(300)) { width -> width / 2 } + fadeIn(animationSpec = tween(300)))
                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width / 2 } + fadeOut(animationSpec = tween(300)))
                },
                label = "stats_chart_animation"
            ) { targetMetric ->
                when (targetMetric) {
                    "Volume" -> StatsLineChart("Workout Volume", listOf(45f, 50f, 48f, 60f, 65f, 58f, 70f, 85f, 80f, 95f), Modifier.fillMaxWidth())
                    "Weight" -> StatsLineChart("Body Weight (lbs)", listOf(185f, 184f, 184.5f, 183f, 182f, 181.5f, 180f, 179f, 179.5f, 178f), Modifier.fillMaxWidth())
                    "Calories" -> StatsLineChart("Calories Burned", listOf(300f, 350f, 320f, 400f, 450f, 420f, 500f, 600f, 550f, 700f), Modifier.fillMaxWidth())
                    "Active Time" -> StatsLineChart("Active Time (mins)", listOf(30f, 40f, 35f, 50f, 60f, 45f, 70f, 90f, 85f, 100f), Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AuraGlassCard {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, null, tint = AuraNeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Total Workouts", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    Text("14", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("+2 from last week", color = AuraNeonCyan, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AuraGlassCard {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, null, tint = AuraNeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Total Calories Burned", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    Text("4,200 kcal", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            AuraGlassCard {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = AuraNeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Total Time Active", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    Text("12h 45m", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun StatsLineChart(title: String, dataPoints: List<Float>, modifier: Modifier = Modifier) {
    val neonOrange = Color(0xFFFF6D00)
    
    AuraGlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                val maxPoint = dataPoints.maxOrNull() ?: 100f
                val minPoint = dataPoints.minOrNull() ?: 0f
                val range = (maxPoint - minPoint).takeIf { it > 0 } ?: 1f
                
                val width = size.width
                val height = size.height
                
                val stepX = width / (dataPoints.size - 1)
                
                val path = androidx.compose.ui.graphics.Path()
                dataPoints.forEachIndexed { index, value ->
                    val x = index * stepX
                    // Flip Y axis: larger values are drawn higher up
                    val y = height - ((value - minPoint) / range * height * 0.8f) - (height * 0.1f)
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        val prevX = (index - 1) * stepX
                        val prevY = height - ((dataPoints[index - 1] - minPoint) / range * height * 0.8f) - (height * 0.1f)
                        
                        val controlPointX1 = prevX + (x - prevX) / 2
                        val controlPointX2 = prevX + (x - prevX) / 2
                        
                        path.cubicTo(controlPointX1, prevY, controlPointX2, y, x, y)
                    }
                }
                
                // Draw graph path
                drawPath(
                    path = path,
                    color = neonOrange,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )

                dataPoints.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = height - ((value - minPoint) / range * height * 0.8f) - (height * 0.1f)
                    // Draw point
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                }
                
                // Draw bottom axis
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = androidx.compose.ui.geometry.Offset(0f, height),
                    end = androidx.compose.ui.geometry.Offset(width, height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Last Month", color = Color.White.copy(alpha=0.5f), fontSize = 12.sp)
                Text("Today", color = neonOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// === TAB 2 : PROGRAMS & PRESET WORKOUT PLANS (Image 20, Preset List Image 21, Plan Detail Image 22) ===
@Composable
fun TabProgramsView(viewModel: AuraViewModel) {
    val profile by viewModel.activeProfile.collectAsStateWithLifecycle()
    var selectedPlanToView by remember { mutableStateOf<String?>(null) } // Show full exercise details page (Image 22)

    AuraGradientBackground {
        AnimatedContent(
            targetState = selectedPlanToView,
            transitionSpec = {
                if (targetState != null) {
                    (slideInVertically(animationSpec = tween(300)) { height -> height / 2 } + fadeIn(animationSpec = tween(280)))
                        .togetherWith(fadeOut(animationSpec = tween(180)))
                } else {
                    fadeIn(animationSpec = tween(200)) togetherWith (slideOutVertically(animationSpec = tween(300)) { height -> height / 2 } + fadeOut(animationSpec = tween(200)))
                }
            },
            label = "program_details_transition"
        ) { activePlan ->
            if (activePlan != null) {
                // Full Back Workout Details Screen (Image 22)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .safeDrawingPadding()
                ) {
                    // Large picture header replacement
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(AuraAestheticPurple.copy(alpha = 0.4f), Color(0xFF04060A))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AuraIconButton(onClick = { selectedPlanToView = null }) {
                                Icon(Icons.Default.ArrowBack, null, tint = Color.White, modifier = Modifier.size(36.dp))
                            }
                            Text("Back Workout Plan", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                            Text("V-taped Hypertrophy Program", color = AuraNeonCyan, fontSize = 12.sp)
                        }
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        // Plan parameters row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(
                                "58 min" to Icons.Default.Timeline,
                                "254 kcal" to Icons.Default.LocalFireDepartment,
                                "3-4 Sets" to Icons.Default.FitnessCenter
                            ).forEach { (desc, icon) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(icon, null, tint = AuraNeonCyan, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(desc, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Description", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            "This back training focus builds a strong and wide V-taper. Make sure to perform slow negatives on pull-ups and lat pulldowns for maximum hypertrophy and athletic performance.",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Drill list matching Image 22 list item styling
                        Text("TRAINING DRILLS", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        val drills = listOf(
                            "Wide-Grip Pull-ups" to "3 Sets x 8-10 reps safely",
                            "Cable Lat Pulldown" to "4 Sets x 12 reps slow pull",
                            "Seated Cable row" to "3 Sets x 12 reps control",
                            "Barbell Deadlift" to "3 Sets x 5 reps power hold",
                            "Child's pose recovery" to "90 seconds complete stretch"
                        )

                        drills.forEachIndexed { i, (name, reps) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(AuraNeonCyan.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${i + 1}", color = AuraNeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(reps, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                                }
                                Icon(Icons.Default.CheckCircle, null, tint = AuraNeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Launch layout CTA
                        AuraButton(
                            onClick = {
                                selectedPlanToView = null
                                val exercises = WorkoutPreset.exercises.filter { it.category == "Strength" }.take(5)
                                viewModel.launchWorkoutSession("Back V-Taper Blueprint", "Strength", exercises)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Start Workout Plan", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            } else {
                // Programs Main Feed Screen representing Image 20 & 21
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState())
                        .safeDrawingPadding()
                ) {
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("TRAINING PROGRAMS", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Strength & Training Table", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Personalized training banner card (Image 20 visual clone)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0F1524), Color(0xFF1B112D))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(18.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("Personalized Workout & Training", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("Tailored exercises for your strength level. Build absolute compliance and form.", fontSize = 11.sp, color = FrostWhite.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            AuraButton(
                                onClick = { selectedPlanToView = "Back Workout" },
                                colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Browse Workouts", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Workout presets list with circular completion indicators (Image 21 style)
                    Text("ALL PLANS", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val mockPlans = listOf(
                        "Back Workout Plan" to "5 exercises • 58 min • 254 kcal",
                        "Chest Shredder Plan" to "6 exercises • 45 min • 310 kcal",
                        "Cardio Blaze Protocol" to "7 exercises • 30 min • 420 kcal",
                        "Leg Power Day Plan" to "5 exercises • 50 min • 280 kcal"
                    )

                    mockPlans.forEachIndexed { idx, (planName, metrics) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
                                .clickable { selectedPlanToView = planName },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D121F))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Circular sets progress indicator (Image 21 visual representation)
                                Box(
                                    modifier = Modifier.size(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        progress = 0.25f * (idx + 1),
                                        color = AuraNeonCyan,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Icon(Icons.Default.Adjust, null, tint = AuraNeonCyan.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(planName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(metrics, color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                                }

                                AuraIconButton(onClick = { selectedPlanToView = planName }) {
                                    Icon(Icons.Default.ArrowForwardIos, null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(110.dp))
                }
            }
        }
    }
}

// === TAB 3 : PROFILE & METRICS PERFORMANCE (Image 12 & 13 & 14 & 15 & Settings 16 & Alerts 18) ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabProfileView(viewModel: AuraViewModel) {
    val profile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val totalWorkouts by viewModel.totalWorkoutsCompletedCount.collectAsStateWithLifecycle()
    val caloriesBurnedByVM by viewModel.cumulativeCaloriesBurned.collectAsStateWithLifecycle()
    val durationMinutes by viewModel.cumulativeDurationMinutes.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    var loggedCalorieInput by remember { mutableStateOf("") }
    var currentHydrationMl by remember { mutableStateOf(500) } // tracking ml hydration (Image 12)

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Upper profile info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(AuraNeonCyan, AuraAestheticPurple))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (profile?.name ?: "A").first().uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = ObsidianBlack
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(profile?.name ?: "Active Athlete", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(profile?.email ?: "sso.athlete@ulpifit.com", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. AURAFIT Score Radar/Doughnut Card (Image 13 visual recreation)
            Text("AURAFIT SPORT PERFORMANCE ASSESSMENT", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))
            AuraGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Custom Doughnut drawing inside profile
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Doughnut rings
                            // Base gray
                            drawCircle(color = Color.White.copy(alpha = 0.05f), style = Stroke(width = 8.dp.toPx()))
                            // Strength
                            drawArc(
                                color = AuraNeonCyan,
                                startAngle = -90f,
                                sweepAngle = 210f,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Agility
                            drawArc(
                                color = AuraAestheticPurple,
                                startAngle = 120f,
                                sweepAngle = 90f,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("84", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
                            Text("AURAFIT", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(AuraNeonCyan, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Strength: 54%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(AuraAestheticPurple, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Agility: 26%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(AuraAccentCoral, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Endurance: 24%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Your fitness score indicates high cardiorespiratory and muscle fiber compliance.", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("ACHIEVEMENTS & MILESTONES", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))

            val sampleBadges = listOf(
                Badge("First Steps", "Complete your first workout", Icons.Default.DirectionsRun, true),
                Badge("Week Warrior", "Hit a 7-day workout streak", Icons.Default.LocalFireDepartment, true),
                Badge("Iron Lifter", "Lift over 1000 lbs in total", Icons.Default.FitnessCenter, false),
                Badge("Century Club", "Complete 100 workouts", Icons.Default.EmojiEvents, false)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sampleBadges.forEach { badge ->
                    BadgeItemView(badge)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Calorie Intake Tracker & Chart (Image 14 & 15 visual recreation)
            Text("CALORIE COMPLIANCE ENGINE", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))
            AuraGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("1,745 Kcal Left", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("Goal limit: 2000 calorie today", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Eaten: 255 Kcal", color = AuraNeonCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Canvas Bezier Intake Graph
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val pts = listOf(10f, 25f, 15f, 44f, 28f, 60f, 50f)
                        val stepX = size.width / (pts.size - 1)
                        val scaleY = size.height / 80f
                        
                        // Vertical guideline grids
                        pts.forEachIndexed { i, _ ->
                            drawLine(color = Color.White.copy(alpha = 0.03f), start = Offset(i * stepX, 0f), end = Offset(i * stepX, size.height))
                        }

                        // Draw spline line
                        for (i in 0 until pts.size - 1) {
                            val startOpt = Offset(i * stepX, size.height - (pts[i] * scaleY))
                            val endOpt = Offset((i + 1) * stepX, size.height - (pts[i+1] * scaleY))
                            drawLine(
                                color = AuraNeonCyan,
                                start = startOpt,
                                end = endOpt,
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                        Text(day, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interaction: Quick add food calorie text area
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = loggedCalorieInput,
                        onValueChange = { loggedCalorieInput = it },
                        placeholder = { Text("Enter logged food kcal...", color = Color.White.copy(alpha = 0.3f)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = AuraNeonCyan),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    AuraButton(
                        onClick = {
                            val v = loggedCalorieInput.toIntOrNull()
                            if (v != null) {
                                // simulated success notification push
                                loggedCalorieInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Hydration Water Thermometer (Image 12 visual outline)
            Text("HYDRATION CONTROL COMPLIANCE", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))
            AuraGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vertical Liquid water glass column
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(130.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .border(1.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        val fraction = currentHydrationMl.toFloat() / 2000f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(fraction)
                                .background(AuraNeonCyan)
                        )
                        Column(
                            modifier = Modifier.padding(bottom = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${currentHydrationMl}ml", fontSize = 11.sp, fontWeight = FontWeight.Black, color = if (fraction > 0.3f) ObsidianBlack else Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hydration Level Status", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        val remainingWater = (2000 - currentHydrationMl).coerceAtLeast(0)
                        Text(
                            text = if (remainingWater > 0) {
                                "You need ${remainingWater}ml to reach daily goal today."
                            } else {
                                "Daily target is completed! Excellent discipline."
                            },
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick water adder rows
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AuraButton(
                                onClick = { currentHydrationMl = (currentHydrationMl + 250).coerceAtMost(2000) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+250ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            AuraButton(
                                onClick = { currentHydrationMl = (currentHydrationMl + 500).coerceAtMost(2000) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f), contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+500ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Account Settings (Image 16)
            Text("ACCOUNT MANAGEMENT", color = AuraNeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(10.dp))

            AuraGlassCard {
                // Dark mode preference toggle
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        viewModel.updateThemeToggle(!(userSettings?.isDarkMode ?: true))
                    },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, null, tint = AuraNeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Forced Dark Mode", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Switch(
                        checked = userSettings?.isDarkMode ?: true,
                        onCheckedChange = { viewModel.updateThemeToggle(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AuraNeonCyan)
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.04f), modifier = Modifier.padding(vertical = 12.dp))

                // Custom logout bypass
                AuraOutlinedButton(
                    onClick = { viewModel.triggerLogout() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraAccentCoral),
                    border = BorderStroke(1.dp, AuraAccentCoral.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("settings_logout_button")
                ) {
                    Text("Secure Account Log Out", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}

// ==========================================
// 5. INTENSITY WORKOUT COUNTDOWN COUNTER (Image 23 Complete / running)
// ==========================================

@Composable
fun WorkoutTimerScreen(viewModel: AuraViewModel) {
    val activeWorkoutName by viewModel.activeWorkoutName.collectAsStateWithLifecycle()
    val activeExercisesList by viewModel.activeExercisesList.collectAsStateWithLifecycle()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsStateWithLifecycle()

    val secondsLeft by viewModel.timerSecondsLeft.collectAsStateWithLifecycle()
    val isPaused by viewModel.isTimerPaused.collectAsStateWithLifecycle()
    val isResting by viewModel.isRestingState.collectAsStateWithLifecycle()
    val restSecondsLeft by viewModel.restSecondsLeft.collectAsStateWithLifecycle()

    val caloriesTrend by viewModel.liveEstimatedCaloriesBurned.collectAsStateWithLifecycle()
    val workingSecsTotal by viewModel.liveWorkingSecondsCount.collectAsStateWithLifecycle()

    val currentDrill = activeExercisesList.getOrNull(currentExerciseIndex)

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Stats Area
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = activeWorkoutName.uppercase(Locale.US),
                    color = AuraNeonCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = if (isResting) "Rest transition..." else (currentDrill?.name ?: "Prepare Drill"),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Central Ring Counter drawing
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F1524))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                // Progress radial sweep
                val fraction = if (isResting) {
                    if (restSecondsLeft > 0) restSecondsLeft.toFloat() / 15f else 0f
                } else {
                    val full = currentDrill?.durationSeconds ?: 45
                    if (full > 0) secondsLeft.toFloat() / full.toFloat() else 0f
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = Color.White.copy(alpha = 0.05f), style = Stroke(width = 8.dp.toPx()))
                    drawArc(
                        color = if (isResting) AuraAestheticPurple else AuraNeonCyan,
                        startAngle = -90f,
                        sweepAngle = fraction * 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isResting) "$restSecondsLeft" else "$secondsLeft",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = if (isResting) "seconds rest" else "seconds remaining",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }

            // Dynamic Skeletal Joint visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
            ) {
                ExerciseVisualizer(
                    exerciseName = currentDrill?.name ?: "Workout",
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    isResting = isResting
                )
            }

            // Realtime accumulator specs card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("WORKING DURATION", color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("${workingSecsTotal}s", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BURNED CALORIES", color = AuraAccentCoral, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("${caloriesTrend.toInt()} kcal", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                }
            }

            // Command Control Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Premature exit button
                AuraOutlinedButton(
                    onClick = { viewModel.exitActiveWorkoutPrematurely() },
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Exit Plan", fontWeight = FontWeight.Bold)
                }

                // Pause active control
                AuraButton(
                    onClick = { viewModel.pauseResumeWorkoutTimer() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaused) AuraNeonCyan else Color.White,
                        contentColor = ObsidianBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.5f).height(50.dp)
                ) {
                    Text(
                        text = if (isPaused) "Resume" else "Pause Drill",
                        fontWeight = FontWeight.Bold
                    )
                }

                // Skip drill control
                AuraIconButton(
                    onClick = { viewModel.skipCurrentExercise() },
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.SkipNext, "Skip next drill", tint = Color.White)
                }
            }
        }
    }
}

// ==========================================
// HAPTIC BUTTON WRAPPERS
// ==========================================

@Composable
fun AuraButton(
    onClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: androidx.compose.foundation.BorderStroke? = null,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun AuraOutlinedButton(
    onClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = ButtonDefaults.outlinedShape,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: androidx.compose.foundation.BorderStroke? = ButtonDefaults.outlinedButtonBorder,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun AuraIconButton(
    onClick: () -> Unit,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    IconButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
        content = content
    )
}
