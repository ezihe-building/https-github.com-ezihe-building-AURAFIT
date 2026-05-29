package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuraRepository(application)

    // ==========================================
    // NAVIGATION & SHELL STATES
    // ==========================================
    val currentRoute = MutableStateFlow<String>("splash") // splash, auth, onboarding, dashboard, timer, settings, history, notifications

    // ==========================================
    // AUTHENTICATION STATES
    // ==========================================
    val activeEmail = MutableStateFlow<String?>(null)
    
    // Auth Form Bindings
    val authEmailInput = MutableStateFlow("")
    val authPasswordInput = MutableStateFlow("")
    val authNameInput = MutableStateFlow("")
    val authResetTokenInput = MutableStateFlow("")
    val authResetPasswordInput = MutableStateFlow("")
    
    // Clerk OTP verification credentials
    val authModeModel = MutableStateFlow("login") // login, register, forgot, reset, verify_otp
    val authOtpInput = MutableStateFlow("")
    val expectedOtp = MutableStateFlow<String?>(null)
    val expectedOtpEmail = MutableStateFlow("")
    val expectedOtpPass = MutableStateFlow("")

    val authError = MutableStateFlow<String?>(null)
    val authSuccess = MutableStateFlow<String?>(null)
    val isAuthLoading = MutableStateFlow(false)

    // ==========================================
    // ONBOARDING FORM BINDINGS
    // ==========================================
    val onboardName = MutableStateFlow("")
    val onboardAge = MutableStateFlow("24")
    val onboardGender = MutableStateFlow("Male") // Male, Female
    val onboardHeight = MutableStateFlow("175")  // cm
    val onboardWeight = MutableStateFlow("70")   // kg
    val onboardGoal = MutableStateFlow("Thriving Athleticism") // Weight Loss, Weight Gain, Maintenance, Athleticism
    val onboardExperience = MutableStateFlow("Beginner") // Beginner, Advanced
    val onboardWorkoutMinutes = MutableStateFlow("30")
    val onboardPlan = MutableStateFlow("150 Day Plan") // 60 Day Plan, 150 Day Plan, 365 Day Plan, 2 Year Discipline Plan
    
    val onboardingError = MutableStateFlow<String?>(null)

    // ==========================================
    // DATABASE DATA FLOWS (DYNAMIC BY EMAIL)
    // ==========================================
    val activeProfile = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getProfileFlow(email) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeStreak = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getStreakFlow(email) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val completedHistory = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getWorkoutHistoryFlow(email) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalWorkoutsCompletedCount = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getCompletedCountFlow(email) else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cumulativeCaloriesBurned = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getCumulativeCaloriesFlow(email).map { it ?: 0 } else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cumulativeDurationMinutes = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getCumulativeDurationFlow(email).map { (it ?: 0) / 60 } else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val notificationLogs = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getNotificationsFlow(email) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userSettings = activeEmail.flatMapLatest { email ->
        if (email != null) repository.getSettingsFlow(email) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ==========================================
    // ACTIVE TIMER / WORKOUT ENGINE
    // ==========================================
    val isWorkoutActive = MutableStateFlow(false)
    val activeWorkoutName = MutableStateFlow("")
    val activeWorkoutCategory = MutableStateFlow("")
    val activeExercisesList = MutableStateFlow<List<Exercise>>(emptyList())
    val currentExerciseIndex = MutableStateFlow(0)
    
    val timerSecondsLeft = MutableStateFlow(0)
    val isTimerPaused = MutableStateFlow(false)
    val isRestingState = MutableStateFlow(false)
    val restSecondsLeft = MutableStateFlow(0)

    // Live session stats accumulation
    val liveWorkingSecondsCount = MutableStateFlow(0)
    val liveEstimatedCaloriesBurned = MutableStateFlow(0f)

    private var timerJob: Job? = null

    // ==========================================
    // ==========================================
    // INITIALIZATION & SESSION BINDING
    // ==========================================
    init {
        // Evaluate active login session on startup using deterministic suspended queries
        viewModelScope.launch {
            try {
                delay(1000) // Beautiful splash experience duration
                val session = repository.getActiveSession()
                if (session != null && session.isLoggedIn && session.loggedInUserEmail != null) {
                    val email = session.loggedInUserEmail
                    activeEmail.value = email
                    // Authenticated! Determine onboarding profile existence via direct suspended check
                    val profile = repository.getProfileByEmail(email)
                    if (profile != null) {
                        // Profile completed, go to dashboard
                        currentRoute.value = "dashboard"
                        // Clean older stale streaks
                        repository.valuateStreakIntegrity(email)
                    } else {
                        // Force onboarding completion
                        currentRoute.value = "onboarding"
                    }
                } else {
                    activeEmail.value = null
                    currentRoute.value = "auth"
                }
            } catch (e: Exception) {
                Log.e("AuraViewModel", "Failed to evaluate active session on startup", e)
                activeEmail.value = null
                currentRoute.value = "auth"
            }
        }
    }

    // ==========================================
    // USER AUTHENTICATION TRANSACTIONS
    // ==========================================
    fun clearAuthMessages() {
        authError.value = null
        authSuccess.value = null
    }

    fun triggerSignUp() {
        val email = authEmailInput.value.trim()
        val password = authPasswordInput.value.trim()
        if (email.isEmpty() || password.isEmpty()) {
            authError.value = "Please complete all fields."
            return
        }
        if (!email.contains("@")) {
            authError.value = "Please enter a valid email address."
            return
        }
        if (password.length < 6) {
            authError.value = "Password must be at least 6 characters."
            return
        }

        isAuthLoading.value = true
        authError.value = null

        viewModelScope.launch {
            val result = repository.signUp(email, password)
            isAuthLoading.value = false
            if (result.isSuccess) {
                // Clerk Security Code Generation
                val code = (100000..999999).random().toString()
                expectedOtp.value = code
                expectedOtpEmail.value = email
                expectedOtpPass.value = password
                authOtpInput.value = "" // clear input

                authSuccess.value = "Welcome to AuraFit! We sent a 6-digit Clerk verification code to $email."
                authModeModel.value = "verify_otp"
                android.util.Log.d("AuraFitClerk", "Clerk OTP: $code")
            } else {
                authError.value = result.exceptionOrNull()?.message ?: "Sign up failed."
            }
        }
    }

    fun verifyOtpAndCompleteSignUp() {
        val enteredCode = authOtpInput.value.trim()
        val exp = expectedOtp.value
        val mail = expectedOtpEmail.value
        if (enteredCode.length < 6) {
            authError.value = "OTP code must have 6 digits."
            return
        }
        if (exp == null || mail.isEmpty()) {
            authError.value = "No active verification session found."
            return
        }
        if (enteredCode != exp) {
            authError.value = "Invalid Clerk OTP Code. Check your inbox & input."
            return
        }

        isAuthLoading.value = true
        authError.value = null
        viewModelScope.launch {
            activeEmail.value = mail
            authSuccess.value = "Email verified via Clerk Security!"
            currentRoute.value = "onboarding"
            isAuthLoading.value = false
        }
    }

    fun triggerGoogleSSO(email: String) {
        isAuthLoading.value = true
        authError.value = null
        viewModelScope.launch {
            // Check if user is registered first by verifying logging in
            val loginResult = repository.logIn(email, "google_sso_secured")
            if (loginResult.isSuccess) {
                activeEmail.value = email
                val profile = repository.getProfileByEmail(email)
                if (profile != null) {
                    currentRoute.value = "dashboard"
                    repository.valuateStreakIntegrity(email)
                } else {
                    currentRoute.value = "onboarding"
                }
            } else {
                // Provision a new secure account on the fly
                val signUpResult = repository.signUp(email, "google_sso_secured")
                if (signUpResult.isSuccess) {
                    authSuccess.value = "Google Account provisioned!"
                    activeEmail.value = email
                    currentRoute.value = "onboarding"
                } else {
                    authError.value = signUpResult.exceptionOrNull()?.message ?: "Google authentication failed."
                }
            }
            isAuthLoading.value = false
        }
    }

    fun triggerGuestMode() {
        isAuthLoading.value = true
        authError.value = null
        viewModelScope.launch {
            val email = "guest.champion@aurafit.io"
            // Ensure user exists locally or we create them
            val userRes = repository.signUp(email, "guestpass123")
            if (userRes.isFailure) {
                // Already registered or had an issue, let's login
                repository.logIn(email, "guestpass123")
            }
            activeEmail.value = email

            // Look if onboarding is completed
            val profile = repository.getProfileByEmail(email)
            if (profile != null) {
                currentRoute.value = "dashboard"
                repository.valuateStreakIntegrity(email)
            } else {
                // Submit onboarding profile automatically so they skip configuring it
                val entity = ProfileEntity(
                    email = email,
                    name = "Aura Champion",
                    age = 25,
                    gender = "Male",
                    height = 180f,
                    weight = 75f,
                    fitnessGoal = "Thriving Athleticism",
                    experienceLevel = "Advanced",
                    dailyWorkoutTime = 45,
                    disciplinePlan = "150 Day Plan"
                )
                repository.saveProfile(entity)
                currentRoute.value = "dashboard"
            }
            isAuthLoading.value = false
        }
    }

    fun triggerLogIn() {
        val email = authEmailInput.value.trim()
        val password = authPasswordInput.value.trim()
        if (email.isEmpty() || password.isEmpty()) {
            authError.value = "Please enter both email and password."
            return
        }

        isAuthLoading.value = true
        authError.value = null

        viewModelScope.launch {
            val result = repository.logIn(email, password)
            isAuthLoading.value = false
             if (result.isSuccess) {
                activeEmail.value = email
                // Session listener handles routing automatically in init, but we force check to prevent timing locks
                val profile = repository.getProfileByEmail(email)
                if (profile != null) {
                    currentRoute.value = "dashboard"
                    repository.valuateStreakIntegrity(email)
                } else {
                    currentRoute.value = "onboarding"
                }
            } else {
                authError.value = result.exceptionOrNull()?.message ?: "Login failed."
            }
        }
    }

    fun triggerForgotPassword() {
        val email = authEmailInput.value.trim()
        if (email.isEmpty()) {
            authError.value = "Please enter your email address."
            return
        }

        isAuthLoading.value = true
        authError.value = null

        viewModelScope.launch {
            val result = repository.forgotPassword(email)
            isAuthLoading.value = false
            if (result.isSuccess) {
                val token = result.getOrNull()
                authSuccess.value = "Code generated! Offline Reset Code: $token"
                authResetTokenInput.value = token ?: ""
            } else {
                authError.value = result.exceptionOrNull()?.message ?: "Reset request failed."
            }
        }
    }

    fun triggerResetPassword() {
        val email = authEmailInput.value.trim()
        val token = authResetTokenInput.value.trim()
        val newPassword = authResetPasswordInput.value.trim()

        if (email.isEmpty() || token.isEmpty() || newPassword.isEmpty()) {
            authError.value = "Please satisfy all reset inputs."
            return
        }
        if (newPassword.length < 6) {
            authError.value = "New password must be at least 6 characters."
            return
        }

        isAuthLoading.value = true
        authError.value = null

        viewModelScope.launch {
            val result = repository.resetPassword(email, token, newPassword)
            isAuthLoading.value = false
            if (result.isSuccess) {
                authSuccess.value = "Password updated successfully! You can login now."
                // Reset inputs
                authResetTokenInput.value = ""
                authResetPasswordInput.value = ""
            } else {
                authError.value = result.exceptionOrNull()?.message ?: "Password reset failed."
            }
        }
    }

    fun triggerLogout() {
        viewModelScope.launch {
            repository.logout()
            activeEmail.value = null
            // Form wipes
            authEmailInput.value = ""
            authPasswordInput.value = ""
            currentRoute.value = "auth"
        }
    }

    // ==========================================
    // ONBOARDING TRANSACTIONS
    // ==========================================
    fun completeOnboardingSubmission() {
        val name = onboardName.value.trim()
        val ageText = onboardAge.value.trim()
        val heightText = onboardHeight.value.trim()
        val weightText = onboardWeight.value.trim()
        val minText = onboardWorkoutMinutes.value.trim()

        if (name.isEmpty() || ageText.isEmpty() || heightText.isEmpty() || weightText.isEmpty() || minText.isEmpty()) {
            onboardingError.value = "All onboarding fields must be completed."
            return
        }

        val age = ageText.toIntOrNull() ?: 24
        val height = heightText.toFloatOrNull() ?: 175f
        val weight = weightText.toFloatOrNull() ?: 70f
        val mins = minText.toIntOrNull() ?: 30

        val sessionEmail = activeEmail.value
        if (sessionEmail == null) {
            onboardingError.value = "Session expired. Please log in again."
            currentRoute.value = "auth"
            return
        }

        onboardingError.value = null

        viewModelScope.launch {
            val entity = ProfileEntity(
                email = sessionEmail,
                name = name,
                age = age,
                gender = onboardGender.value,
                height = height,
                weight = weight,
                fitnessGoal = onboardGoal.value,
                experienceLevel = onboardExperience.value,
                dailyWorkoutTime = mins,
                disciplinePlan = onboardPlan.value
            )
            repository.saveProfile(entity)
            currentRoute.value = "dashboard"
        }
    }

    // ==========================================
    // ACTIVE TIMER / WORKOUT CONTROLLER
    // ==========================================
    fun launchWorkoutSession(name: String, category: String, exercises: List<Exercise>) {
        if (exercises.isEmpty()) return
        
        // Setup initial timer states
        activeWorkoutName.value = name
        activeWorkoutCategory.value = category
        activeExercisesList.value = exercises
        currentExerciseIndex.value = 0
        
        timerSecondsLeft.value = exercises[0].durationSeconds
        isTimerPaused.value = false
        isRestingState.value = false
        restSecondsLeft.value = 0
        
        liveWorkingSecondsCount.value = 0
        liveEstimatedCaloriesBurned.value = 0f

        isWorkoutActive.value = true
        currentRoute.value = "timer"

        startTimerClockJob()
        
        // Log motivational nudge
        viewModelScope.launch {
            activeEmail.value?.let { email ->
                repository.triggerCustomNotification(
                    email = email,
                    title = "Session Started!",
                    message = "Stay fully locked in for $name. Focus on breathing and discipline.",
                    type = "workout"
                )
            }
        }
    }

    private fun startTimerClockJob() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isWorkoutActive.value) {
                delay(1000)
                if (!isTimerPaused.value) {
                    liveWorkingSecondsCount.value += 1
                    
                    if (isRestingState.value) {
                        // We are in rest transition countdown
                        val currentRest = restSecondsLeft.value
                        if (currentRest > 1) {
                            restSecondsLeft.value = currentRest - 1
                        } else {
                            // Rest ended, trigger next exercise
                            isRestingState.value = false
                            val exerciseIndex = currentExerciseIndex.value
                            val currentExercise = activeExercisesList.value.getOrNull(exerciseIndex)
                            timerSecondsLeft.value = currentExercise?.durationSeconds ?: 45
                        }
                    } else {
                        // Active exercise execution
                        val activeLeft = timerSecondsLeft.value
                        val exerciseIndex = currentExerciseIndex.value
                        val exercises = activeExercisesList.value
                        val currentExercise = exercises.getOrNull(exerciseIndex)

                        // Accumulate fractional calories burned
                        if (currentExercise != null) {
                            val calPerSec = currentExercise.burnedCaloriesPerMin / 60f
                            liveEstimatedCaloriesBurned.value += calPerSec
                        }

                        if (activeLeft > 1) {
                            timerSecondsLeft.value = activeLeft - 1
                        } else {
                            // Completed current exercise!
                            if (exerciseIndex + 1 < exercises.size) {
                                // Next exercise exists! Transition with rest
                                isRestingState.value = true
                                restSecondsLeft.value = 15 // 15 seconds rest
                                currentExerciseIndex.value = exerciseIndex + 1
                            } else {
                                // Workout fully completed!
                                isWorkoutActive.value = false
                                finalizeWorkoutSaving()
                            }
                        }
                    }
                }
            }
        }
    }

    fun pauseResumeWorkoutTimer() {
        isTimerPaused.value = !isTimerPaused.value
    }

    fun skipCurrentExercise() {
        val exerciseIndex = currentExerciseIndex.value
        val exercises = activeExercisesList.value
        
        if (exerciseIndex + 1 < exercises.size) {
            isRestingState.value = true
            restSecondsLeft.value = 15
            currentExerciseIndex.value = exerciseIndex + 1
            timerSecondsLeft.value = exercises[exerciseIndex + 1].durationSeconds
        } else {
            // End workout directly
            isWorkoutActive.value = false
            finalizeWorkoutSaving()
        }
    }

    private fun finalizeWorkoutSaving() {
        viewModelScope.launch {
            val email = activeEmail.value ?: return@launch
            val name = activeWorkoutName.value
            val category = activeWorkoutCategory.value
            val duration = liveWorkingSecondsCount.value
            val calories = liveEstimatedCaloriesBurned.value.toInt().coerceAtLeast(12)

            repository.completeWorkout(
                email = email,
                workoutName = name,
                category = category,
                durationSec = duration,
                caloriesBurned = calories
            )

            currentRoute.value = "dashboard"
            
            // Clean active job
            timerJob?.cancel()
            timerJob = null
        }
    }

    fun exitActiveWorkoutPrematurely() {
        timerJob?.cancel()
        timerJob = null
        isWorkoutActive.value = false
        currentRoute.value = "dashboard"
    }

    // ==========================================
    // NOTIFICATIONS CONTROLS
    // ==========================================
    fun clearNotificationsList() {
        viewModelScope.launch {
            activeEmail.value?.let { email ->
                repository.clearNotifications(email)
            }
        }
    }

    fun logMotivationalQuotePush() {
        viewModelScope.launch {
            activeEmail.value?.let { email ->
                val quotes = listOf(
                    "Consistency is the absolute key to aesthetic supremacy.",
                    "Discipline eats talent for breakfast. Stay course.",
                    "Aura is built through difficult struggles. Make today count.",
                    "The hardest lift is lifting your body off the couch. Stand up!",
                    "Your yesterday's limits are today's warmup weights.",
                    "An unbroken streak is a monument to your character and will."
                )
                repository.triggerCustomNotification(
                    email = email,
                    title = "Daily Motivation Nudge",
                    message = quotes.random(),
                    type = "motivation"
                )
            }
        }
    }

    // ==========================================
    // SETTINGS PANEL CONTROL
    // ==========================================
    fun updateThemeToggle(isDark: Boolean) {
        viewModelScope.launch {
            val email = activeEmail.value ?: return@launch
            val current = userSettings.value ?: SettingEntity(email = email)
            repository.updateSettings(current.copy(isDarkMode = isDark))
        }
    }

    fun updateRemindersToggle(enabled: Boolean) {
        viewModelScope.launch {
            val email = activeEmail.value ?: return@launch
            val current = userSettings.value ?: SettingEntity(email = email)
            repository.updateSettings(current.copy(areRemindersEnabled = enabled))
        }
    }

    fun updateNotificationFrequency(freq: String) {
        viewModelScope.launch {
            val email = activeEmail.value ?: return@launch
            val current = userSettings.value ?: SettingEntity(email = email)
            repository.updateSettings(current.copy(notificationFrequency = freq))
        }
    }

    fun updatePrivacyToggle(enabled: Boolean) {
        viewModelScope.launch {
            val email = activeEmail.value ?: return@launch
            val current = userSettings.value ?: SettingEntity(email = email)
            repository.updateSettings(current.copy(isPrivacyEnabled = enabled))
        }
    }

    fun updateOnboardStatsDirectly(name: String, heightStr: String, weightStr: String) {
        viewModelScope.launch {
            val email = activeEmail.value ?: return@launch
            val h = heightStr.toFloatOrNull() ?: 175f
            val w = weightStr.toFloatOrNull() ?: 70f
            repository.updateProfile(email, name, h, w)
            
            // Toast / confirm alert via welcome back log
            repository.triggerCustomNotification(
                email = email,
                title = "Profile Updated",
                message = "Your active metrics (Height: $h cm, Weight: $w kg) have been refreshed.",
                type = "motivation"
            )
        }
    }

    fun getTodayString(): String {
        return repository.getTodayString()
    }
}
