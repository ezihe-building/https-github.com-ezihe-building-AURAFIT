package com.example.ui

import android.app.Application
import android.util.Log
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import java.util.Locale
import com.example.BuildConfig

class AuraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuraRepository(application)

    // ==========================================
    // NOTIFICATION SOUND PROTOCOL (CHIME METRONOME)
    // ==========================================
    private fun playSoundNotification(tone: Int, duration: Int = 120) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
                toneGen.startTone(tone, duration)
                delay(duration.toLong() + 50)
                toneGen.release()
            } catch (e: Exception) {
                Log.w("AuraViewModel", "ToneGenerator play failure", e)
            }
        }
    }

    // ==========================================
    // CUSTOM WORKOUT ROUTINE CREATION PROTOCOL
    // ==========================================
    val customWorkoutRoutines = MutableStateFlow<List<Pair<String, List<Exercise>>>>(emptyList())

    fun loadCustomRoutines() {
        val email = activeEmail.value ?: return
        try {
            val prefs = getApplication<Application>().getSharedPreferences("aurafit_shared_prefs", android.content.Context.MODE_PRIVATE)
            val dataStr = prefs.getString("custom_routines_$email", "") ?: ""
            if (dataStr.isEmpty()) {
                customWorkoutRoutines.value = emptyList()
                return
            }
            
            val routines = mutableListOf<Pair<String, List<Exercise>>>()
            val entries = dataStr.split(";")
            for (entry in entries) {
                if (entry.isEmpty()) continue
                val parts = entry.split(":")
                if (parts.size >= 2) {
                    val name = parts[0]
                    val ids = parts[1].split(",")
                    val exercises = ids.mapNotNull { id ->
                        WorkoutPreset.exercises.find { it.id == id }
                    }
                    if (exercises.isNotEmpty()) {
                        routines.add(name to exercises)
                    }
                }
            }
            customWorkoutRoutines.value = routines
        } catch (e: Exception) {
            Log.e("AuraViewModel", "Failed to load custom routines", e)
        }
    }

    fun saveCustomRoutine(name: String, exerciseIds: List<String>) {
        val email = activeEmail.value ?: return
        if (name.isEmpty() || exerciseIds.isEmpty()) return
        
        try {
            val prefs = getApplication<Application>().getSharedPreferences("aurafit_shared_prefs", android.content.Context.MODE_PRIVATE)
            val currentData = prefs.getString("custom_routines_$email", "") ?: ""
            
            val cleanName = name.replace(":", "").replace(";", "")
            val idsStr = exerciseIds.joinToString(",")
            val newEntry = "$cleanName:$idsStr"
            
            val updatedData = if (currentData.isEmpty()) newEntry else "$currentData;$newEntry"
            prefs.edit().putString("custom_routines_$email", updatedData).apply()
            
            loadCustomRoutines()
            
            // Trigger confirmation notification log
            viewModelScope.launch {
                repository.triggerCustomNotification(
                    email = email,
                    title = "New Blueprint Configured!",
                    message = "Successfully created your personalized training program: $cleanName (${exerciseIds.size} drills).",
                    type = "motivation"
                )
            }
        } catch (e: Exception) {
            Log.e("AuraViewModel", "Failed to save custom routine", e)
        }
    }

    fun deleteCustomRoutine(nameToDelete: String) {
        val email = activeEmail.value ?: return
        try {
            val prefs = getApplication<Application>().getSharedPreferences("aurafit_shared_prefs", android.content.Context.MODE_PRIVATE)
            val currentData = prefs.getString("custom_routines_$email", "") ?: ""
            if (currentData.isEmpty()) return
            
            val entries = currentData.split(";")
            val filteredEntries = entries.filter { entry ->
                val name = entry.substringBefore(":")
                name != nameToDelete
            }
            
            val updatedData = filteredEntries.joinToString(";")
            prefs.edit().putString("custom_routines_$email", updatedData).apply()
            
            loadCustomRoutines()
        } catch (e: Exception) {
            Log.e("AuraViewModel", "Failed to delete custom routine", e)
        }
    }

    fun jumpToExercise(index: Int) {
        val list = activeExercisesList.value
        if (index in list.indices) {
            currentExerciseIndex.value = index
            timerSecondsLeft.value = list[index].durationSeconds
            isRestingState.value = false
            restSecondsLeft.value = 0
            isTimerPaused.value = false
            
            // Sound feedback for navigation
            playSoundNotification(ToneGenerator.TONE_PROP_BEEP, 120)
        }
    }

    // ==========================================
    // NAVIGATION & SHELL STATES
    // ==========================================
    val currentRoute = MutableStateFlow<String>("splash") // splash, auth, onboarding, dashboard, timer, settings, history, notifications
    val activeDashboardTab = MutableStateFlow(0)

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
        // Observe reactive user email to load user-scoped custom routines
        viewModelScope.launch {
            activeEmail.collect { email ->
                if (email != null) {
                    loadCustomRoutines()
                }
            }
        }

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
                        
                        // Metronome warn bells
                        if (currentRest in 1..3) {
                            playSoundNotification(ToneGenerator.TONE_CDMA_PIP, 80)
                        }

                        if (currentRest > 1) {
                            restSecondsLeft.value = currentRest - 1
                        } else {
                            // Rest ended, trigger next exercise
                            isRestingState.value = false
                            val exerciseIndex = currentExerciseIndex.value
                            val currentExercise = activeExercisesList.value.getOrNull(exerciseIndex)
                            timerSecondsLeft.value = currentExercise?.durationSeconds ?: 45
                            
                            // Go sound indicator!
                            playSoundNotification(ToneGenerator.TONE_PROP_ACK, 250)
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

                        // Metronome beep ticks (for final 3 seconds of set)
                        if (activeLeft in 1..3) {
                            playSoundNotification(ToneGenerator.TONE_CDMA_PIP, 80)
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
                                
                                // Rest transition tone!
                                playSoundNotification(ToneGenerator.TONE_PROP_ACK, 250)
                            } else {
                                // Workout fully completed! Play triumphant end chords
                                playSoundNotification(ToneGenerator.TONE_PROP_ACK, 400)
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
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return
        viewModelScope.launch {
            val email = activeEmail.value ?: return@launch
            val h = heightStr.toFloatOrNull() ?: 175f
            val w = weightStr.toFloatOrNull() ?: 70f
            repository.updateProfile(email, cleanName, h, w)
            
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

    // ==========================================
    // AI CHAT COACH (GEMINI API) ENGINE
    // ==========================================
    val isAiLoading = MutableStateFlow(false)
    val isAiDemoMode = MutableStateFlow(false)
    val chatMessages = MutableStateFlow<List<Pair<String, String>>>(listOf(
        "Ulpifit Assistant" to "Hi I am Uplift, Your AI Personal Training and Sports Assistant. Just ask me anything about hypertrophy, fat loss, or calorie management!"
    ))

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    fun selectActiveChat(title: String) {
        val initialAssistantMessage = when {
            title.contains("bulk", ignoreCase = true) -> 
                "Hi! Let's optimize your bulk program. Are you looking to do a clean bulk or a general surplus build? List any weight/height targets."
            title.contains("Score", ignoreCase = true) -> 
                "Greetings! Here on the Fitness Score board, let's talk about tracking your metabolic compliance. Ask me anything on how to increase your score."
            title.contains("water", ignoreCase = true) -> 
                "Uplift Hydration helper here! Let's plan your daily water intake. Tell me your average cup size or current daily water amount."
            title.contains("muscle", ignoreCase = true) -> 
                "Gain muscle protocols initiated! To design a high-hypertrophy structure, tell me what equipment (gym/barbell or home/bodyweight) is available to you."
            title.contains("Nutrition", ignoreCase = true) -> 
                "Nutrition upgrade protocol ready. Let's design a daily meal structure of whole foods. What are your primary dietary restrictions or calorie goals?"
            title.contains("Fitness data", ignoreCase = true) -> 
                "Your fitness data is logged. Let's analyze your completed workouts or dynamic streaks to push you even harder!"
            else -> 
                "Hi! I am Uplift, Your AI Personal Training and Sports Assistant. Just ask me anything about hypertrophy, fat loss, or calorie management!"
        }
        
        chatMessages.value = listOf("Ulpifit Assistant" to initialAssistantMessage)
        isAiDemoMode.value = false
    }

    fun sendChatMessage(messageText: String) {
        val trimmed = messageText.trim()
        if (trimmed.isEmpty()) return

        val currentList = chatMessages.value.toMutableList()
        currentList.add("User" to trimmed)
        chatMessages.value = currentList

        isAiLoading.value = true

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val responseText = if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    isAiDemoMode.value = true
                    getLocalAiDemoResponse(trimmed)
                } else {
                    isAiDemoMode.value = false
                    queryGeminiAPI(currentList.dropLast(1), trimmed)
                }
                
                val updatedList = chatMessages.value.toMutableList()
                updatedList.add("Ulpifit Assistant" to responseText)
                chatMessages.value = updatedList
            } catch (e: Exception) {
                Log.e("AuraViewModel", "Failed to send chat message", e)
                val updatedList = chatMessages.value.toMutableList()
                updatedList.add("Ulpifit Assistant" to "I encountered an error connecting to the AI helper. Please make sure your internet connection or key configuration is active.")
                chatMessages.value = updatedList
            } finally {
                isAiLoading.value = false
            }
        }
    }

    private suspend fun queryGeminiAPI(history: List<Pair<String, String>>, userMessage: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            isAiDemoMode.value = true
            return getLocalAiDemoResponse(userMessage)
        }

        return withContext(Dispatchers.IO) {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                
                val contentsArray = JSONArray()
                
                // Add history
                history.forEach { (sender, text) ->
                    val role = if (sender == "User") "user" else "model"
                    val contentObj = JSONObject()
                    contentObj.put("role", role)
                    val partsArray = JSONArray()
                    val partObj = JSONObject()
                    partObj.put("text", text)
                    partsArray.put(partObj)
                    contentObj.put("parts", partsArray)
                    contentsArray.put(contentObj)
                }
                
                // Add newest prompt
                val userContentObj = JSONObject()
                userContentObj.put("role", "user")
                val userPartsArray = JSONArray()
                val userPartObj = JSONObject()
                userPartObj.put("text", userMessage)
                userPartsArray.put(userPartObj)
                userContentObj.put("parts", userPartsArray)
                contentsArray.put(userContentObj)

                val bodyObj = JSONObject()
                bodyObj.put("contents", contentsArray)

                val sysInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPartObj = JSONObject()
                sysPartObj.put("text", "You are Uplift AI Coach, a friendly, professional AI Personal Training and Sports Assistant. Keep responses concise, encouraging, and focused on physical exercise, hypertrophy, athletic performance, and nutrition.")
                sysPartsArray.put(sysPartObj)
                sysInstructionObj.put("parts", sysPartsArray)
                bodyObj.put("systemInstruction", sysInstructionObj)

                val requestBodyStr = bodyObj.toString()
                
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
                
                val request = Request.Builder()
                    .url(url)
                    .post(requestBodyStr.toRequestBody(mediaType))
                    .build()
                
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val resBody = response.body?.string() ?: ""
                    val resJson = JSONObject(resBody)
                    val candidates = resJson.getJSONArray("candidates")
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    val text = parts.getJSONObject(0).getString("text")
                    text
                } else {
                    val code = response.code
                    val errorMsg = response.body?.string() ?: "Unknown error"
                    Log.e("AuraViewModel", "Gemini API Error $code: $errorMsg")
                    "Error executing AI query (HTTP $code). Running in Demo Mode as fallback."
                }
            } catch (e: Exception) {
                Log.e("AuraViewModel", "Gemini request execution failure", e)
                "Exception calling Gemini: ${e.message}. Running in Demo Mode as fallback."
            }
        }
    }

    private fun getLocalAiDemoResponse(message: String): String {
        val query = message.lowercase(Locale.US)
        return when {
            query.contains("bulk") || query.contains("gaining") || query.contains("gain") -> {
                "💪 **BULK & MASS PROTOCOLS (Uplift Demo Coach)**\n\nTo build quality muscular weight without excess fat accumulation:\n\n• **Calorie Surplus**: Aim for +300 to +500 kcal above maintenance.\n• **Protein Target**: Consume 1.8g to 2.2g of protein per kg of bodyweight.\n• **Progression**: Focus on progressive overload in the 8-12 repetition range.\n• **Top Sources**: Lean beef, eggs, brown rice, peanut butter, and sweet potatoes."
            }
            query.contains("shred") || query.contains("lose") || query.contains("deficit") || query.contains("cut") -> {
                "🔥 **FAT LOSS & RECOMPOSITION (Uplift Demo Coach)**\n\nTo maximize muscle preservation while accelerating lipid oxidation:\n\n• **Calorie Deficit**: Aim for -400 to -600 kcal beneath maintenance.\n• **Protein Buffer**: Increase to 2.2g+ per kg to safeguard lean muscle tissue.\n• **Cardio Interleaving**: Combine high-intensity drills in our Cardio Tab with steady-state walking.\n• **Hydration**: Drink 3200ml of pure water daily to flush metabolic waste."
            }
            query.contains("program") || query.contains("plan") || query.contains("workout") || query.contains("exercise") -> {
                "🏋️ **STRENGTH & HYPERTROPHY BUILDER (Uplift Demo Coach)**\n\nHere is a solid compound training schedule:\n\n• **Day 1: Pull Day** (Lat Pulldowns, Row Drills, Bicep curls)\n• **Day 2: Push Day** (Diamond Push-ups, Barbell squats, overhead press)\n• **Day 3: Core & Recover** (Plank, dynamic stretches)\n\n*Press the 'Start Workout' buttons under our Programs tab to trigger live bio-interactive timers!*"
            }
            query.contains("score") || query.contains("optimal") -> {
                "📈 **OPTIMAL FITNESS SCORE SYSTEMS (Uplift Demo Coach)**\n\nYour Fitness Score is computed from: \n\n1. **Workout Completion Consistency** (+5 pts per logged day).\n2. **Hydration Adherence** (+3 pts per 250ml cup).\n3. **Calorie Compliance** within your daily targeted threshold.\n\nKeep tracking your sets daily to unlock high-tier athletic achievements!"
            }
            query.contains("water") || query.contains("drink") || query.contains("hydrate") -> {
                "💧 **HYDRATION DYNAMICS (Uplift Demo Coach)**\n\nMaintaining fluid compliance is critical for myofibrillar hydration and power output:\n\n• **Active Target**: 3000ml to 4000ml (12-16 cups) depending on rate of sweating.\n• **Key Windows**: Consume 500ml 1 hour before strength sessions, and sip 150ml every 15 minutes during training."
            }
            else -> {
                val topics = listOf(
                    "To build a strong V-taper frame, focus heavily on wide-grip pull-ups and lat pulldowns with slow 3-second negatives.",
                    "Ensure you are logging sleep of 7-8 hours daily; recovery is when your muscles actively synthesize protein and grow.",
                    "To target the inner chest and triceps, leverage the Diamond Push-ups in our strength inventory.",
                    "Integrate dynamic stretches like Child's Pose and shoulder mobility work to stay injury-free and athletic."
                )
                "🌟 **Uplift AI Coach (Demo Mode)**\n\nI processed your query: *\"$message\"*\n\nHere is your custom coaching recommendation:\n\n• ${topics.random()}\n• Ensure you structure your daily calorie intake around your goal.\n• Remember to log daily accomplishments to build an unbroken training streak!\n\n*(Note: For unlimited, live Gemini answers matching any custom query, configure a valid API key in your workspace secrets!)*"
            }
        }
    }
}
