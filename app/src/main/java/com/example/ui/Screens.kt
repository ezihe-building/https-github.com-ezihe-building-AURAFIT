package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.delay
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

// ==========================================
// CENTRAL GLASSMORPHISM & GRADIENT HELPER COMPOSABLES
// ==========================================

@Composable
fun AuraGradientBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .drawBehind {
                // Frosted Glass Dynamic Mesh Background
                // Top-Left Indigo-600/40 glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x664F46E5), Color.Transparent),
                        radius = size.width * 1.1f
                    ),
                    center = androidx.compose.ui.geometry.Offset(x = -size.width * 0.1f, y = -size.height * 0.1f)
                )
                // Bottom-Right Rose-600/30 glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x4CE11D48), Color.Transparent),
                        radius = size.width * 1.2f
                    ),
                    center = androidx.compose.ui.geometry.Offset(x = size.width * 1.1f, y = size.height * 1.1f)
                )
                // Middle-Right Violet-600/30 glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x4C7C3AED), Color.Transparent),
                        radius = size.width * 1.0f
                    ),
                    center = androidx.compose.ui.geometry.Offset(x = size.width * 1.1f, y = size.height * 0.4f)
                )
            }
    ) {
        content()
    }
}

@Composable
fun AuraGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.White.copy(alpha = 0.20f),
    backgroundColor: Color = Color.White.copy(alpha = 0.10f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(borderColor, Color.White.copy(alpha = 0.02f))
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Glass is usually flat/ambient with zero hard shadows
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun AuraFloatingCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(16.dp),
        content = content
    )
}

// ==========================================
// 1. SPLASH / INTRO ROUTE
// ==========================================

@Composable
fun SplashScreen() {
    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AuraNeonCyan, AuraAestheticPurple)
                        )
                    )
                    .border(1.5.dp, FrostWhite.copy(alpha = 0.25f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AF",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = ObsidianBlack,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.testTag("splash_logo")
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "AURAFIT",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FrostWhite,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Unbreakable discipline. Seamless streaks.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AuraNeonCyan.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = AuraNeonCyan,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Initializing secure session...",
                fontSize = 12.sp,
                color = FrostWhite.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
        }
    }
}

// ==========================================
// 2. AUTHENTICATION (SIGN UP & LOGIN) ROUTE
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: AuraViewModel) {
    val email by viewModel.authEmailInput.collectAsStateWithLifecycle()
    val password by viewModel.authPasswordInput.collectAsStateWithLifecycle()
    val checkToken by viewModel.authResetTokenInput.collectAsStateWithLifecycle()
    val resetPassword by viewModel.authResetPasswordInput.collectAsStateWithLifecycle()
    
    val error by viewModel.authError.collectAsStateWithLifecycle()
    val success by viewModel.authSuccess.collectAsStateWithLifecycle()
    val isLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()

    val authMode by viewModel.authModeModel.collectAsStateWithLifecycle()
    var showGoogleSheet by remember { mutableStateOf(false) }

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
                modifier = Modifier.fillMaxWidth(0.95f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Brand Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(colors = listOf(AuraNeonCyan, AuraAestheticPurple))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = "Fitness Icon",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "A UR A F I T",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = FrostWhite,
                    letterSpacing = 5.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuraGlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = when (authMode) {
                            "login" -> "Secure Sign In"
                            "register" -> "Create Account"
                            "forgot" -> "Recover Password"
                            "verify_otp" -> "Verify Your Identity"
                            else -> "Confirm Reset Code"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FrostWhite,
                        modifier = Modifier.testTag("auth_heading")
                    )

                    Text(
                        text = when (authMode) {
                            "login" -> "Enter your email and keys to sync achievements."
                            "register" -> "Join AuraFit today. Everything is tracked locally."
                            "forgot" -> "Generate secure recovery verification tokens."
                            "verify_otp" -> "Secured with Clerk Security Authentication."
                            else -> "Perform actual resets against local hash algorithms."
                        },
                        fontSize = 13.sp,
                        color = FrostWhite.copy(alpha = 0.65f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                    )

                    // Error & Success indicators
                    AnimatedVisibility(visible = error != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .background(AuraAccentCoral.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .border(1.dp, AuraAccentCoral, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = error ?: "",
                                color = Color.Red,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    AnimatedVisibility(visible = success != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .background(AuraActiveEmerald.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .border(1.dp, AuraActiveEmerald, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = success ?: "",
                                color = AuraNeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (authMode == "login" || authMode == "register" || authMode == "forgot") {
                        // Email input
                        OutlinedTextField(
                            value = email,
                            onValueChange = { 
                                viewModel.clearAuthMessages()
                                viewModel.authEmailInput.value = it 
                            },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, "Email", tint = AuraNeonCyan) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FrostWhite,
                                unfocusedTextColor = FrostWhite,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = FrostWhite.copy(alpha = 0.2f),
                                focusedLabelColor = AuraNeonCyan,
                                unfocusedLabelColor = FrostWhite.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (authMode == "login" || authMode == "register") {
                        // Password input
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                viewModel.clearAuthMessages()
                                viewModel.authPasswordInput.value = it 
                            },
                            label = { Text("Secure Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, "Lock", tint = AuraNeonCyan) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FrostWhite,
                                unfocusedTextColor = FrostWhite,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = FrostWhite.copy(alpha = 0.2f),
                                focusedLabelColor = AuraNeonCyan,
                                unfocusedLabelColor = FrostWhite.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                        )
                    }

                    if (authMode == "reset") {
                        // Token and New Password input
                        OutlinedTextField(
                            value = checkToken,
                            onValueChange = { viewModel.authResetTokenInput.value = it },
                            label = { Text("6-Digit Reset Code") },
                            leadingIcon = { Icon(Icons.Default.Key, "Key", tint = AuraAestheticPurple) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FrostWhite,
                                unfocusedTextColor = FrostWhite,
                                focusedBorderColor = AuraAestheticPurple,
                                unfocusedBorderColor = FrostWhite.copy(alpha = 0.2f),
                                focusedLabelColor = AuraAestheticPurple
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = resetPassword,
                            onValueChange = { viewModel.authResetPasswordInput.value = it },
                            label = { Text("New Secure Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, "Lock", tint = AuraNeonCyan) },
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FrostWhite,
                                unfocusedTextColor = FrostWhite,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = FrostWhite.copy(alpha = 0.2f),
                                focusedLabelColor = AuraNeonCyan
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (authMode == "verify_otp") {
                        val otpInput by viewModel.authOtpInput.collectAsStateWithLifecycle()
                        val expectedCode by viewModel.expectedOtp.collectAsStateWithLifecycle()

                        Text(
                            text = "Please enter the 6-digit OTP code below to verify your device session.",
                            fontSize = 13.sp,
                            color = FrostWhite.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // 6 spacious glass boxes for a premium authentic feel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                        ) {
                            for (i in 0 until 6) {
                                val digit = otpInput.getOrNull(i)?.toString() ?: ""
                                val isFocused = otpInput.length == i

                                Box(
                                    modifier = Modifier
                                        .size(width = 44.dp, height = 54.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(FrostWhite.copy(alpha = 0.06f))
                                        .border(
                                            width = 1.5.dp,
                                            color = if (isFocused) AuraNeonCyan else FrostWhite.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (digit.isNotEmpty()) AuraNeonCyan else FrostWhite.copy(alpha = 0.3f)
                                    )
                                }
                            }
                        }

                        // Input captures numeric entries
                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { 
                                if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                    viewModel.authOtpInput.value = it
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("auth_otp_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = FrostWhite,
                                unfocusedTextColor = FrostWhite,
                                focusedBorderColor = AuraNeonCyan,
                                unfocusedBorderColor = FrostWhite.copy(alpha = 0.12f),
                                focusedLabelColor = AuraNeonCyan
                            ),
                            label = { Text("Enter Verification Code") },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, "Verification", tint = AuraNeonCyan) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp)
                                .background(AuraNeonCyan.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .border(1.dp, AuraNeonCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "🔒 Clerk Live Sandbox Simulation",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraNeonCyan
                                )
                                Text(
                                    text = "Your auto-simulated verification code is: ${expectedCode ?: "......"}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FrostWhite,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Main Act Button
                    Button(
                        onClick = {
                            when (authMode) {
                                "login" -> viewModel.triggerLogIn()
                                "register" -> viewModel.triggerSignUp()
                                "verify_otp" -> viewModel.verifyOtpAndCompleteSignUp()
                                "forgot" -> {
                                    viewModel.triggerForgotPassword()
                                    viewModel.authModeModel.value = "reset"
                                }
                                else -> {
                                    viewModel.triggerResetPassword()
                                    viewModel.authModeModel.value = "login"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("auth_submit_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraNeonCyan,
                            contentColor = ObsidianBlack
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = ObsidianBlack, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = when (authMode) {
                                    "login" -> "Sign In"
                                    "register" -> "Create Account"
                                    "verify_otp" -> "Verify Credentials"
                                    "forgot" -> "Send Code"
                                    else -> "Reset Password"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary action toggle
                    TextButton(
                        onClick = {
                            viewModel.clearAuthMessages()
                            viewModel.authModeModel.value = when (authMode) {
                                "login" -> "register"
                                "register" -> "login"
                                "forgot" -> "login"
                                "verify_otp" -> "register"
                                else -> "login"
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = when (authMode) {
                                "login" -> "Don't have an account? Sign Up"
                                "register" -> "Already have an account? Log In"
                                "verify_otp" -> "Back to Registration"
                                else -> "Back to Sign In"
                            },
                            color = AuraNeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (authMode == "login") {
                        TextButton(
                            onClick = {
                                viewModel.clearAuthMessages()
                                viewModel.authModeModel.value = "forgot"
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Forgot password?",
                                color = FrostWhite.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = FrostWhite.copy(alpha = 0.1f))
                        Text(
                            text = "OR CONTINUE WITH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = FrostWhite.copy(alpha = 0.45f),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = FrostWhite.copy(alpha = 0.1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Authentic Google button
                    OutlinedButton(
                        onClick = { showGoogleSheet = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_sso_button"),
                        border = BorderStroke(1.dp, FrostWhite.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FrostWhite)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                color = AuraNeonCyan,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                            Text("Secure Sign In with Google", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Guest Bypass button
                    Button(
                        onClick = { viewModel.triggerGuestMode() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("guest_bypass_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuraNeonCyan.copy(alpha = 0.15f),
                            contentColor = AuraNeonCyan
                        ),
                        border = BorderStroke(1.dp, AuraNeonCyan.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Quick Bypass icon",
                                tint = AuraNeonCyan,
                                modifier = Modifier.size(18.dp).padding(end = 6.dp)
                            )
                            Text("Bypass: Enter as Instant Guest", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Google account selection simulation dialog
            if (showGoogleSheet) {
                AlertDialog(
                    onDismissRequest = { showGoogleSheet = false },
                    containerColor = ObsidianBlack.copy(alpha = 0.96f),
                    modifier = Modifier.border(1.dp, AuraNeonCyan.copy(alpha = 0.25f), RoundedCornerShape(24.dp)),
                    title = {
                        Text("Verify Google Identity", fontWeight = FontWeight.Bold, color = FrostWhite, fontSize = 18.sp)
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "Choose an active Google Google Identity token to synchronize and secure your statistics.",
                                color = FrostWhite.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                            
                            val accounts = listOf(
                                "alpha.athlete@gmail.com",
                                "discipline.warrior@gmail.com",
                                "aurafit.legacy@gmail.com"
                            )
                            
                            accounts.forEach { acc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(FrostWhite.copy(alpha = 0.04f))
                                        .clickable {
                                            showGoogleSheet = false
                                            viewModel.triggerGoogleSSO(acc)
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(AuraNeonCyan.copy(alpha = 0.15f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(acc.take(1).uppercase(), color = AuraNeonCyan, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(acc, color = FrostWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            
                            var customEmailInput by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = customEmailInput,
                                onValueChange = { customEmailInput = it },
                                label = { Text("Use custom Google account email") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = FrostWhite,
                                    unfocusedTextColor = FrostWhite,
                                    focusedBorderColor = AuraNeonCyan,
                                    unfocusedBorderColor = FrostWhite.copy(alpha = 0.15f)
                                ),
                                trailingIcon = {
                                    if (customEmailInput.contains("@")) {
                                        IconButton(
                                            onClick = {
                                                showGoogleSheet = false
                                                viewModel.triggerGoogleSSO(customEmailInput.trim())
                                            }
                                        ) {
                                            Icon(Icons.Default.Check, "submit", tint = AuraActiveEmerald)
                                        }
                                    }
                                }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showGoogleSheet = false }) {
                            Text("Back", color = AuraAccentCoral)
                        }
                    }
                )
            }
        }
    }
}

// ==========================================
// 3. ONBOARDING QUESTIONS ROUTE
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: AuraViewModel) {
    val name by viewModel.onboardName.collectAsStateWithLifecycle()
    val age by viewModel.onboardAge.collectAsStateWithLifecycle()
    val gender by viewModel.onboardGender.collectAsStateWithLifecycle()
    val height by viewModel.onboardHeight.collectAsStateWithLifecycle()
    val weight by viewModel.onboardWeight.collectAsStateWithLifecycle()
    val goal by viewModel.onboardGoal.collectAsStateWithLifecycle()
    val level by viewModel.onboardExperience.collectAsStateWithLifecycle()
    val minutes by viewModel.onboardWorkoutMinutes.collectAsStateWithLifecycle()
    val disciplinePlan by viewModel.onboardPlan.collectAsStateWithLifecycle()
    
    val error by viewModel.onboardingError.collectAsStateWithLifecycle()

    var statusPhase by remember { mutableStateOf(1) } // Phase 1: profile facts, Phase 2: goals & discipline plans

    AuraGradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .safeDrawingPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Interactive Progress Bar
                LinearProgressIndicator(
                    progress = if (statusPhase == 1) 0.5f else 1.0f,
                    color = AuraNeonCyan,
                    trackColor = FrostWhite.copy(alpha = 0.1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "DISCIPLINE FORM",
                    color = AuraNeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Text(
                    text = if (statusPhase == 1) "About Yourself" else "Your Fitness Blueprint",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FrostWhite
                )

                Text(
                    text = if (statusPhase == 1) 
                        "Input your physical dimensions to compute accurate calorie expenditure models." 
                    else "Choose an actionable milestone period to develop unbroken consistency.",
                    fontSize = 14.sp,
                    color = FrostWhite.copy(alpha = 0.65f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                AnimatedVisibility(visible = error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .background(AuraAccentCoral.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .border(1.dp, AuraAccentCoral, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(text = error ?: "", color = Color.Red, fontSize = 13.sp)
                    }
                }

                if (statusPhase == 1) {
                    // PHASE 1: User facts
                    AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Personal Credentials", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.onboardName.value = it },
                            label = { Text("Display Name") },
                            leadingIcon = { Icon(Icons.Default.Person, "user", tint = AuraNeonCyan) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FrostWhite, unfocusedTextColor = FrostWhite, focusedBorderColor = AuraNeonCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboard_name_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Age
                            OutlinedTextField(
                                value = age,
                                onValueChange = { viewModel.onboardAge.value = it },
                                label = { Text("Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FrostWhite, unfocusedTextColor = FrostWhite, focusedBorderColor = AuraNeonCyan),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            // Gender dropdown button simulation
                            Box(modifier = Modifier.weight(1f)) {
                                Column {
                                    Text("Gender", color = FrostWhite.copy(alpha = 0.5f), fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                            .background(CosmicSlate, RoundedCornerShape(6.dp))
                                            .border(
                                                1.dp,
                                                if (gender == "Male") AuraNeonCyan else FrostWhite.copy(
                                                    alpha = 0.2f
                                                ),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                viewModel.onboardGender.value =
                                                    if (gender == "Male") "Female" else "Male"
                                            }
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(gender, color = FrostWhite, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.SwapVert, "gender", tint = AuraNeonCyan)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Height
                            OutlinedTextField(
                                value = height,
                                onValueChange = { viewModel.onboardHeight.value = it },
                                label = { Text("Height (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FrostWhite, unfocusedTextColor = FrostWhite, focusedBorderColor = AuraNeonCyan),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            // Weight
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { viewModel.onboardWeight.value = it },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FrostWhite, unfocusedTextColor = FrostWhite, focusedBorderColor = AuraNeonCyan),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (name.trim().isEmpty()) {
                                    viewModel.onboardingError.value = "Please complete display name."
                                } else {
                                    statusPhase = 2
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Next: Blueprints", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                } else {
                    // PHASE 2: Fitness settings
                    AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Goal Setting", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                        
                        // Select Goal Button options
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf("Thriving Athleticism", "Aesthetic Lean Mode", "Heavy Weight Gain", "Cardio Conditioning").forEach { opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (goal == opt) AuraNeonCyan.copy(alpha = 0.15f) else CosmicSlate)
                                    .border(
                                        1.dp,
                                        if (goal == opt) AuraNeonCyan else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.onboardGoal.value = opt }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(opt, color = if (goal == opt) AuraNeonCyan else FrostWhite, fontWeight = if (goal == opt) FontWeight.Bold else FontWeight.Medium)
                                RadioButton(
                                    selected = goal == opt,
                                    onClick = { viewModel.onboardGoal.value = opt },
                                    colors = RadioButtonDefaults.colors(selectedColor = AuraNeonCyan, unselectedColor = FrostWhite.copy(alpha = 0.4f))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Commitment Blueprint Period", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Challenge Plans
                        listOf("60 Day Plan", "150 Day Plan", "365 Day Plan", "2 Year Discipline Plan").forEach { plan ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (disciplinePlan == plan) AuraAestheticPurple.copy(alpha = 0.15f) else CosmicSlate)
                                    .border(
                                        1.dp,
                                        if (disciplinePlan == plan) AuraAestheticPurple else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.onboardPlan.value = plan }
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(plan, color = if (disciplinePlan == plan) AuraAestheticPurple else FrostWhite, fontWeight = FontWeight.Bold)
                                    val desc = when (plan) {
                                        "60 Day Plan" -> "Develop baseline active habits."
                                        "150 Day Plan" -> "Deep neurological adaptations."
                                        "365 Day Plan" -> "Complete body reconfiguration."
                                        else -> "Ultimate warrior-tier lifestyle."
                                    }
                                    Text(desc, color = FrostWhite.copy(alpha = 0.5f), fontSize = 11.sp)
                                }
                                Icon(
                                    imageVector = if (disciplinePlan == plan) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (disciplinePlan == plan) AuraAestheticPurple else FrostWhite.copy(alpha = 0.4f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Experience & Commitment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Exp Level
                            Box(modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (level == "Beginner") AuraNeonCyan.copy(alpha = 0.15f) else CosmicSlate)
                                .border(
                                    1.dp,
                                    if (level == "Beginner") AuraNeonCyan else FrostWhite.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onboardExperience.value = "Beginner" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Beginner", color = if (level == "Beginner") AuraNeonCyan else FrostWhite, fontWeight = FontWeight.Bold)
                            }

                            Box(modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (level == "Advanced") AuraNeonCyan.copy(alpha = 0.15f) else CosmicSlate)
                                .border(
                                    1.dp,
                                    if (level == "Advanced") AuraNeonCyan else FrostWhite.copy(alpha = 0.1f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.onboardExperience.value = "Advanced" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Advanced Training", color = if (level == "Advanced") AuraNeonCyan else FrostWhite, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Planned Daily Workout Commitment", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                        Text("How much time will you spend on discipline daily?", fontSize = 11.sp, color = FrostWhite.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("15", "30", "45", "60").forEach { minOpt ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (minutes == minOpt) AuraNeonCyan.copy(alpha = 0.15f) else CosmicSlate)
                                        .border(
                                            1.dp,
                                            if (minutes == minOpt) AuraNeonCyan else FrostWhite.copy(alpha = 0.1f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.onboardWorkoutMinutes.value = minOpt },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("$minOpt min", color = if (minutes == minOpt) AuraNeonCyan else FrostWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Navigation Row Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { statusPhase = 1 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FrostWhite),
                                border = BorderStroke(1.dp, FrostWhite.copy(alpha = 0.3f))
                            ) {
                                Text("Back")
                            }

                            Button(
                                onClick = { viewModel.completeOnboardingSubmission() },
                                modifier = Modifier
                                    .weight(2f)
                                    .height(50.dp)
                                    .testTag("onboard_finish_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack)
                            ) {
                                Text("Generate Blueprint", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. MAIN DASHBOARD ROUTE
// ==========================================

@Composable
fun DashboardScreen(viewModel: AuraViewModel) {
    val activeEmailVal by viewModel.activeEmail.collectAsStateWithLifecycle()
    val profile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val streak by viewModel.activeStreak.collectAsStateWithLifecycle()
    val count by viewModel.totalWorkoutsCompletedCount.collectAsStateWithLifecycle()
    val calories by viewModel.cumulativeCaloriesBurned.collectAsStateWithLifecycle()
    val minutes by viewModel.cumulativeDurationMinutes.collectAsStateWithLifecycle()

    // Dynamically query suitable customized presets
    val recommendedExercises = remember(profile) {
        WorkoutPreset.getRecommendedExercises(
            gender = profile?.gender ?: "Male",
            experience = profile?.experienceLevel ?: "Beginner",
            fitnessGoal = profile?.fitnessGoal ?: "Muscle Gain"
        )
    }

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AuraFit Blueprint",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuraNeonCyan,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Welcome, ${profile?.name ?: "Athlete"}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = FrostWhite
                    )

                    val curStreakVal = streak?.currentStreak ?: 0
                    var selectedFlair by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("Lurking Aspirant") }
                    val unlockedFlares = remember(curStreakVal) {
                        mutableListOf("Lurking Aspirant").apply {
                            if (curStreakVal >= 1) add("🥉 Novice Recruit")
                            if (curStreakVal >= 3) add("🥈 Discipline Champion")
                            if (curStreakVal >= 7) add("🥇 Cyan Flame Sentinel")
                            if (curStreakVal >= 15) add("💎 Emerald Kinetic")
                            if (curStreakVal >= 30) add("🌌 Cosmic Overlord")
                        }
                    }
                    if (selectedFlair !in unlockedFlares) {
                        selectedFlair = unlockedFlares.last()
                    }

                    val flairColor = when (selectedFlair) {
                        "🥉 Novice Recruit" -> Color(0xFFCD7F32)
                        "🥈 Discipline Champion" -> Color(0xFFC0A0FC)
                        "🥇 Cyan Flame Sentinel" -> AuraNeonCyan
                        "💎 Emerald Kinetic" -> AuraActiveEmerald
                        "🌌 Cosmic Overlord" -> AuraAestheticPurple
                        else -> FrostWhite.copy(alpha = 0.5f)
                    }
                    
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(flairColor.copy(alpha = 0.12f))
                            .border(1.dp, flairColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .clickable {
                                val currentIdx = unlockedFlares.indexOf(selectedFlair)
                                val nextIdx = (currentIdx + 1) % unlockedFlares.size
                                selectedFlair = unlockedFlares[nextIdx]
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = flairColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedFlair,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = flairColor
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.currentRoute.value = "notifications" },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.10f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
                            .size(44.dp)
                    ) {
                        Icon(Icons.Default.Notifications, "Inboxes", tint = AuraNeonCyan)
                    }

                    // Frosted Glass Premium Gradient Initial Avatar from CSS Template
                    val nameInitial = (profile?.name ?: "Athlete").first().uppercaseChar().toString()
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .border(width = 2.dp, color = Color.White.copy(alpha = 0.20f), shape = RoundedCornerShape(14.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF6366F1), Color(0xFFFB7185)) // Tailwind from-indigo-500 to-rose-400
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nameInitial,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // STREAK CORNER CARD with live 24h integrity countdown
                item {
                    val activeStreakVal = streak?.currentStreak ?: 0
                    val recordsVal = streak?.longestStreak ?: 0
                    val lastDate = streak?.lastCompletedWorkoutDate

                    val todayStr = viewModel.getTodayString()
                    var timeRemainingStr by remember { mutableStateOf("23:59:59") }
                    var progressFraction by remember { mutableStateOf(1.0f) }

                    LaunchedEffect(lastDate) {
                        while (true) {
                            val now = Calendar.getInstance()
                            val target = Calendar.getInstance()
                            
                            if (lastDate == todayStr) {
                                // Workout already completed today! Secure deadline is midnight of tomorrow
                                target.add(Calendar.DAY_OF_YEAR, 1)
                            }
                            // Set deadline to end of that target day (23:59:59)
                            target.set(Calendar.HOUR_OF_DAY, 23)
                            target.set(Calendar.MINUTE, 59)
                            target.set(Calendar.SECOND, 59)
                            target.set(Calendar.MILLISECOND, 999)

                            val diff = target.timeInMillis - now.timeInMillis
                            if (diff > 0) {
                                val hours = diff / (1000 * 60 * 60)
                                val minutes = (diff / (1000 * 60)) % 60
                                val seconds = (diff / 1000) % 60
                                timeRemainingStr = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
                                
                                val totalDaySecs = 86400f
                                val remainingDaySecs = diff / 1000f
                                progressFraction = (remainingDaySecs / totalDaySecs).coerceIn(0f, 1f)
                            } else {
                                timeRemainingStr = "00:00:00"
                                progressFraction = 0.0f
                            }
                            delay(1000)
                        }
                    }

                    AuraGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1.2f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "streak",
                                        tint = if (lastDate == todayStr) AuraActiveEmerald else AuraAccentCoral,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "$activeStreakVal Day Streak",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = FrostWhite,
                                        modifier = Modifier.padding(start = 4.dp).testTag("streak_headline")
                                    )
                                }

                                Text(
                                    text = "Personal Best: $recordsVal days",
                                    fontSize = 12.sp,
                                    color = FrostWhite.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Real Streak Rule checklist indicator
                                Text(
                                    text = if (lastDate == todayStr) 
                                        "Saved Today! Streak insulated." 
                                    else "Complete a workout today to preserve your streak!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (lastDate == todayStr) AuraActiveEmerald else AuraAccentCoral
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Weekly visual streak row matching Frosted Glass CSS template
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val days = listOf("S", "M", "T", "W", "T", "F", "S")
                                    val completedDays = listOf(true, true, true, false, false, false, false)
                                    days.forEachIndexed { index, day ->
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(
                                                    if (completedDays[index]) AuraNeonCyan.copy(alpha = 0.85f)
                                                    else Color.White.copy(alpha = 0.10f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = day,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (completedDays[index]) ObsidianBlack else FrostWhite.copy(alpha = 0.4f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Dynamic hours countdown graphics representation (iOS glassmorphism details)
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf((if (lastDate == todayStr) AuraActiveEmerald else AuraNeonCyan).copy(alpha = 0.15f), Color.Transparent)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = progressFraction,
                                    color = if (lastDate == todayStr) AuraActiveEmerald else AuraAccentCoral,
                                    trackColor = FrostWhite.copy(alpha = 0.08f),
                                    strokeWidth = 6.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = timeRemainingStr,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (lastDate == todayStr) AuraActiveEmerald else AuraAccentCoral,
                                        modifier = Modifier.testTag("streak_timer_clock")
                                    )
                                    Text(
                                        text = if (lastDate == todayStr) "SECURED" else "WINDOW",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = FrostWhite.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                // DISCIPLINE BADGES & REWARDS Section
                item {
                    val curStreak = streak?.currentStreak ?: 0
                    var selectedBadgeDesc by remember { mutableStateOf<String?>(null) }
                    var selectedBadgeName by remember { mutableStateOf<String?>(null) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .border(1.dp, FrostWhite.copy(alpha = 0.05f), RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicSlate.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Discipline Badges & Ranks",
                                color = FrostWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Build streaks to unlock cosmetic ranks and aura power.",
                                color = FrostWhite.copy(alpha = 0.5f),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            val badgeList = listOf(
                                Triple("Bronze Spark", "🥉", 1),
                                Triple("Discipline Shield", "🥈", 3),
                                Triple("Cyan Flame Sentinel", "🔥", 7),
                                Triple("Emerald Kinetic", "⚡", 15),
                                Triple("Cosmic Overlord", "🌌", 30)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                badgeList.forEach { (bName, bIcon, bMin) ->
                                    val isUnlocked = curStreak >= bMin
                                    val badgeColor = when (bMin) {
                                        1 -> Color(0xFFCD7F32)
                                        3 -> Color(0xFFC0A0FC)
                                        7 -> AuraNeonCyan
                                        15 -> AuraActiveEmerald
                                        else -> AuraAestheticPurple
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(if (isUnlocked) badgeColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f))
                                            .border(
                                                width = 1.5.dp,
                                                color = if (isUnlocked) badgeColor else FrostWhite.copy(alpha = 0.1f),
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                selectedBadgeName = bName
                                                selectedBadgeDesc = if (isUnlocked) {
                                                    "Unlocked! You secured a $bMin+ day streak to claim this level. Click flair to equip cosmetic rank representation."
                                                } else {
                                                    "Locked. Build a workout streak of at least $bMin days to claim this reward item."
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = bIcon,
                                            fontSize = 24.sp,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                        if (!isUnlocked) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    tint = FrostWhite.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Dynamic Badge Expansion details card with smooth Compose AnimatedVisibility details
                            AnimatedVisibility(
                                visible = selectedBadgeDesc != null,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                if (selectedBadgeName != null && selectedBadgeDesc != null) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 12.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .border(0.8.dp, AuraNeonCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedBadgeName!!,
                                                color = AuraNeonCyan,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            IconButton(
                                                onClick = {
                                                    selectedBadgeDesc = null
                                                    selectedBadgeName = null
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, null, tint = FrostWhite.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                                            }
                                        }
                                        Text(
                                            text = selectedBadgeDesc!!,
                                            color = FrostWhite.copy(alpha = 0.8f),
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // CUMULATIVE PHYSICAL STATS GRID
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Exercises
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(24.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Icon(Icons.Default.CheckCircle, null, tint = AuraNeonCyan, modifier = Modifier.size(20.dp))
                                Text("$count Sessions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite, modifier = Modifier.padding(top = 8.dp))
                                Text("Total Done", fontSize = 11.sp, color = FrostWhite.copy(alpha = 0.5f))
                            }
                        }

                        // Burn Estimation
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(24.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Icon(Icons.Default.Whatshot, null, tint = AuraAccentCoral, modifier = Modifier.size(20.dp))
                                Text("$calories kcal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite, modifier = Modifier.padding(top = 8.dp))
                                Text("Aura Burned", fontSize = 11.sp, color = FrostWhite.copy(alpha = 0.5f))
                            }
                        }

                        // Minutes Worked
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.10f))
                                .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(24.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Icon(Icons.Default.HourglassEmpty, null, tint = AuraAestheticPurple, modifier = Modifier.size(20.dp))
                                Text("$minutes mins", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite, modifier = Modifier.padding(top = 8.dp))
                                Text("Active Time", fontSize = 11.sp, color = FrostWhite.copy(alpha = 0.5f))
                            }
                        }
                    }
                }

                // ACTION: TRIGGER RANDOM DEV MOTIVATION INBOX PUSH
                item {
                    AuraFloatingCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
                                Icon(Icons.Default.WorkspacePremium, "shield", tint = AuraNeonCyan, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Dynamic Mental Spark", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                                    Text("Inject a fresh reminder into your feed.", fontSize = 12.sp, color = FrostWhite.copy(alpha = 0.6f))
                                }
                            }
                            Button(
                                onClick = { viewModel.logMotivationalQuotePush() },
                                colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Spark", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // DYNAMIC WORKOUT PLAN PRESET LIST BASED ON ONBOARDING
                item {
                    Text(
                        text = "Your Recommended Daily Plan",
                        color = FrostWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 6.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Blueprint Code: ${profile?.experienceLevel ?: "Standard"} ${profile?.fitnessGoal ?: "Athleticism"}",
                                color = AuraNeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Consists of ${recommendedExercises.size} custom calibrated movements.",
                                color = FrostWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            // Quick Horizontal Exercise List Preview
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                recommendedExercises.forEach { item ->
                                    Box(
                                        modifier = Modifier
                                            .background(ObsidianBlack.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(item.name, color = FrostWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            // LAUNCH BUTTON
                            Button(
                                onClick = {
                                    viewModel.launchWorkoutSession(
                                        name = "Daily Blueprint ${profile?.fitnessGoal ?: "Core"}",
                                        category = "Mixed Plan",
                                        exercises = recommendedExercises
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .testTag("dashboard_start_workout_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PlayArrow, null, tint = ObsidianBlack)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Begin Prescribed Workout", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // MANUAL CATEGORY TRIGGER PLANS
                item {
                    Text(
                        text = "Alternative Calibration Drills",
                        color = FrostWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                val customPacks = listOf(
                    Triple("Full Sculpt Cardio Cardio", "Cardio Blast (Beginner)", WorkoutPreset.exercises.filter { it.category == "Cardio" && it.level == "Beginner" }),
                    Triple("Peak Strength Force", "Strength Shred (Advanced)", WorkoutPreset.exercises.filter { it.category == "Strength" && it.level == "Advanced" }),
                    Triple("Recovery Stretching Static", "Pliable Recovery (Body Stretch)", WorkoutPreset.exercises.filter { it.category == "Stretching" })
                )

                items(customPacks) { pack ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CosmicSlate.copy(alpha = 0.3f))
                            .clickable {
                                viewModel.launchWorkoutSession(
                                    name = pack.second,
                                    category = "Manual Selection",
                                    exercises = pack.third
                                )
                            }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(pack.second, color = FrostWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("${pack.third.size} Exercises • Totaling ~${pack.third.sumOf { it.durationSeconds } / 60} minutes", color = FrostWhite.copy(alpha = 0.5f), fontSize = 11.sp)
                        }
                        Icon(Icons.Default.ArrowForwardIos, null, tint = AuraNeonCyan, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. WORKOUT TIMER SCREEN ROUTE
// ==========================================

@Composable
fun WorkoutTimerScreen(viewModel: AuraViewModel) {
    val workoutName by viewModel.activeWorkoutName.collectAsStateWithLifecycle()
    val list by viewModel.activeExercisesList.collectAsStateWithLifecycle()
    val index by viewModel.currentExerciseIndex.collectAsStateWithLifecycle()
    
    val timerLeft by viewModel.timerSecondsLeft.collectAsStateWithLifecycle()
    val isPaused by viewModel.isTimerPaused.collectAsStateWithLifecycle()
    val isResting by viewModel.isRestingState.collectAsStateWithLifecycle()
    val restLeft by viewModel.restSecondsLeft.collectAsStateWithLifecycle()
    val profile by viewModel.activeProfile.collectAsStateWithLifecycle()

    val currentExercise = list.getOrNull(index)

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Screen Top Header info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = workoutName.uppercase(Locale.US),
                    fontSize = 12.sp,
                    color = AuraNeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = if (isResting) "CATCH YOUR BREATH" else "EXERCISE ${index + 1} OF ${list.size}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = FrostWhite,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 120fps Smooth Procedural Skeleton Joint Model Visualization Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                ExerciseVisualizer(
                    exerciseName = currentExercise?.name ?: "",
                    isResting = isResting,
                    gender = profile?.gender ?: "Male",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // High Fidelity Timer progress + Active movement specs
            val activeTargetSec = if (isResting) 15 else (currentExercise?.durationSeconds ?: 45)
            val activeLeft = if (isResting) restLeft else timerLeft
            val ratio = activeLeft.toFloat() / activeTargetSec.toFloat()

            AuraGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circle Timer ring!
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { ratio.coerceIn(0.0f, 1.0f) },
                            color = if (isResting) AuraAestheticPurple else AuraNeonCyan,
                            trackColor = FrostWhite.copy(alpha = 0.08f),
                            strokeWidth = 8.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isResting) "REST" else "TIMER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FrostWhite.copy(alpha = 0.5f)
                            )
                            Text(
                                text = String.format(Locale.US, "%02d:%02d", activeLeft / 60, activeLeft % 60),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = FrostWhite,
                                modifier = Modifier.testTag("workout_timer_clock")
                            )
                        }
                    }
                    
                    // Exercise description box or current/next details!
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (isResting) {
                            val nextEx = list.getOrNull(index)
                            Text(
                                text = "PREPARING FOR:",
                                color = AuraAestheticPurple,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = nextEx?.name ?: "",
                                color = FrostWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = nextEx?.targetMuscles ?: "",
                                color = AuraNeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "ACTIVE MOVEMENT:",
                                color = AuraNeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentExercise?.name ?: "",
                                color = FrostWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = currentExercise?.targetMuscles ?: "",
                                color = AuraNeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentExercise?.description ?: "",
                                color = FrostWhite.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                maxLines = 2,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // CONTROLS BOX (Pause, skip, Exit / Abort)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Premature Exit
                IconButton(
                    onClick = { viewModel.exitActiveWorkoutPrematurely() },
                    modifier = Modifier
                        .size(54.dp)
                        .background(FrostWhite.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, "abort", tint = AuraAccentCoral)
                }

                // Play / Pause Float card
                IconButton(
                    onClick = { viewModel.pauseResumeWorkoutTimer() },
                    modifier = Modifier
                        .size(70.dp)
                        .background(if (isPaused) AuraActiveEmerald else AuraNeonCyan, CircleShape)
                        .testTag("workout_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "playpause",
                        tint = ObsidianBlack,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Skip exercise
                IconButton(
                    onClick = { viewModel.skipCurrentExercise() },
                    modifier = Modifier
                        .size(54.dp)
                        .background(FrostWhite.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowForward, "skip", tint = FrostWhite)
                }
            }
        }
    }
}

// ==========================================
// 6. HISTORIC LOGS LIST ROUTE
// ==========================================

@Composable
fun WorkoutHistoryScreen(viewModel: AuraViewModel) {
    val history by viewModel.completedHistory.collectAsStateWithLifecycle()

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .safeDrawingPadding()
        ) {
            Text(
                text = "LOGS",
                color = AuraNeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Discipline History",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FrostWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (history.isEmpty()) {
                // Empty state to satisfy technical mandates
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = FrostWhite.copy(alpha = 0.2f),
                            modifier = Modifier.size(72.dp)
                        )
                        Text(
                            text = "No workouts recorded yet.",
                            color = FrostWhite.copy(alpha = 0.4f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = "Start and complete a daily blueprint plan!",
                            color = AuraNeonCyan.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(history) { log ->
                        val dateString = remember(log.completedAtTimestamp) {
                            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                            sdf.format(Date(log.completedAtTimestamp))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CosmicSlate.copy(alpha = 0.4f))
                                .border(1.dp, FrostWhite.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(2f)) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(AuraNeonCyan.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Done, null, tint = AuraNeonCyan)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(log.workoutName, color = FrostWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(dateString, color = FrostWhite.copy(alpha = 0.4f), fontSize = 11.sp)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "+${log.caloriesBurned} kcal",
                                    color = AuraAccentCoral,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${log.durationSeconds / 60}m ${log.durationSeconds % 60}s",
                                    color = FrostWhite.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. SECURE SETTINGS SCREEN ROUTE
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: AuraViewModel) {
    val activeEmailVal by viewModel.activeEmail.collectAsStateWithLifecycle()
    val profile by viewModel.activeProfile.collectAsStateWithLifecycle()
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    var nameInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }

    // Synchronize inputs dynamically on load
    LaunchedEffect(profile) {
        if (profile != null) {
            nameInput = profile!!.name
            heightInput = profile!!.height.toInt().toString()
            weightInput = profile!!.weight.toInt().toString()
        }
    }

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
        ) {
            Text(
                text = "SYSTEM",
                color = AuraNeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Control Center",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = FrostWhite,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // DUAL ENGINE SETTINGS CARD
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Supabase Dual Sync Protocol", color = AuraNeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Status: ${if (SupabaseConfig.isConfigured) "SYNCED ACTIVE" else "OFFLINE LOCAL LOCK"}", color = FrostWhite, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                Text(
                    text = "AuraFit is automatically saving to local Room tables. Setting up Supabase variables pushes real-time cloud schemas.",
                    fontSize = 11.sp,
                    color = FrostWhite.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PROFILE EDIT CARD Section
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Edit Physical Dimensions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FrostWhite, unfocusedTextColor = FrostWhite, focusedBorderColor = AuraNeonCyan),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("Height (cm)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FrostWhite, unfocusedTextColor = FrostWhite, focusedBorderColor = AuraNeonCyan),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Weight (kg)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = FrostWhite, unfocusedTextColor = FrostWhite, focusedBorderColor = AuraNeonCyan),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        viewModel.updateOnboardStatsDirectly(nameInput, heightInput, weightInput)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("settings_save_profile_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AuraNeonCyan, contentColor = ObsidianBlack),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply Dimensions", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // PRESETS & CONFIGURATION TOGGLES Section
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Operational Preferences", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                Spacer(modifier = Modifier.height(12.dp))

                // Dark Theme toggle fallback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Forced Dark Mode", color = FrostWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Frosted glass look requires dark overlay.", color = FrostWhite.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings?.isDarkMode ?: true,
                        onCheckedChange = { viewModel.updateThemeToggle(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AuraNeonCyan)
                    )
                }

                Divider(color = FrostWhite.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                // Workout reminders
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Reminders", color = FrostWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Trigger daily consistency status.", color = FrostWhite.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings?.areRemindersEnabled ?: true,
                        onCheckedChange = { viewModel.updateRemindersToggle(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AuraNeonCyan)
                    )
                }

                Divider(color = FrostWhite.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 10.dp))

                // Privacy lock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Biometric Security Cover", color = FrostWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Require secure auth on every resume.", color = FrostWhite.copy(alpha = 0.4f), fontSize = 11.sp)
                    }
                    Switch(
                        checked = settings?.isPrivacyEnabled ?: false,
                        onCheckedChange = { viewModel.updatePrivacyToggle(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AuraNeonCyan)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Help Support Ticket Section
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Text("Discipline Helpline", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FrostWhite)
                Text("Experiencing fatigue, joint strain, or need plan modifications? Direct support is available 24/7.", fontSize = 12.sp, color = FrostWhite.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 4.dp))
                OutlinedButton(
                    onClick = {
                        viewModel.triggerLogout() // Quick logout bypass
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("settings_logout_button"),
                    border = BorderStroke(1.dp, AuraAccentCoral),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AuraAccentCoral)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ExitToApp, null, tint = AuraAccentCoral)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Secure Account Log out")
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ==========================================
// 8. NOTIFICATIONS INBOX ROUTE
// ==========================================

@Composable
fun NotificationsScreen(viewModel: AuraViewModel) {
    val inbox by viewModel.notificationLogs.collectAsStateWithLifecycle()

    AuraGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .safeDrawingPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ALERTS", color = AuraNeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(text = "Discipline Inbox", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = FrostWhite)
                }

                TextButton(onClick = { viewModel.clearNotificationsList() }) {
                    Text("Clear All", color = AuraNeonCyan, fontWeight = FontWeight.SemiBold)
                }
            }

            if (inbox.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.NotificationsNone, null, tint = FrostWhite.copy(alpha = 0.2f), modifier = Modifier.size(64.dp))
                        Text("No active notifications.", color = FrostWhite.copy(alpha = 0.4f), fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(inbox) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CosmicSlate.copy(alpha = 0.5f))
                                .border(
                                    width = 1.dp,
                                    color = when (item.type) {
                                        "streak" -> AuraNeonCyan.copy(alpha = 0.2f)
                                        "workout" -> AuraActiveEmerald.copy(alpha = 0.2f)
                                        else -> AuraAestheticPurple.copy(alpha = 0.2f)
                                    },
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        when (item.type) {
                                            "streak" -> AuraAccentCoral.copy(alpha = 0.15f)
                                            "workout" -> AuraActiveEmerald.copy(alpha = 0.15f)
                                            else -> AuraAestheticPurple.copy(alpha = 0.15f)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.type) {
                                        "streak" -> Icons.Default.LocalFireDepartment
                                        "workout" -> Icons.Default.DirectionsRun
                                        else -> Icons.Default.Lightbulb
                                    },
                                    contentDescription = null,
                                    tint = when (item.type) {
                                        "streak" -> AuraAccentCoral
                                        "workout" -> AuraActiveEmerald
                                        else -> AuraAestheticPurple
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, color = FrostWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(item.message, color = FrostWhite.copy(alpha = 0.7f), fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
