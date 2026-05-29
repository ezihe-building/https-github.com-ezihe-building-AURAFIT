package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AuraRepository(context: Context) {
    private val db = AuraDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val profileDao = db.profileDao()
    private val streakDao = db.streakDao()
    private val workoutDao = db.workoutDao()
    private val notificationDao = db.notificationDao()
    private val settingDao = db.settingDao()

    // ==========================================
    // DATE HELPERS
    // ==========================================
    fun getTodayString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    fun getYesterdayString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    // ==========================================
    // REAL AUTHENTICATION
    // ==========================================
    fun getActiveSessionFlow(): Flow<UserSessionEntity?> = userDao.getActiveSessionFlow()

    suspend fun getActiveSession(): UserSessionEntity? = userDao.getActiveSession()

    suspend fun signUp(email: String, passwordRaw: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                return@withContext Result.failure(Exception("An account with this email already exists."))
            }

            val hash = hashPassword(passwordRaw)
            val newUser = UserEntity(email = email, passwordHash = hash, isVerified = true)
            userDao.insertUser(newUser)

            // Auto-initialize Default Settings
            settingDao.insertOrUpdateSettings(SettingEntity(email = email))
            // Auto-initialize empty streak
            streakDao.insertOrUpdateStreak(StreakEntity(email = email))

            // Optional Supabase Background Sign Up Sync
            if (SupabaseConfig.isConfigured) {
                try {
                    val authBody = mapOf("email" to email, "password" to passwordRaw)
                    SupabaseClient.getInstance().auth.signUp(authBody)
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Supabase signup failed, local registered successfully", e)
                }
            }

            // Write active session
            userDao.setSession(UserSessionEntity(loggedInUserEmail = email, isLoggedIn = true))
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logIn(email: String, passwordRaw: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByEmail(email)
            if (user == null) {
                return@withContext Result.failure(Exception("Invalid email or password."))
            }

            val valid = verifyPassword(passwordRaw, user.passwordHash)
            if (!valid) {
                return@withContext Result.failure(Exception("Invalid email or password."))
            }

            // Optional Supabase Background Login Sync
            if (SupabaseConfig.isConfigured) {
                try {
                    val authBody = mapOf("email" to email, "password" to passwordRaw)
                    val res = SupabaseClient.getInstance().auth.logIn(body = authBody)
                    if (res.isSuccessful) {
                        val bodyMap = res.body()
                        val token = (bodyMap?.get("access_token") as? String)
                        SupabaseSessionManager.authToken = token
                        SupabaseSessionManager.userEmail = email
                        Log.d("SupabaseSync", "Authenticated with Supabase")
                    }
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Supabase Auth Sync failed, fallback to offline state", e)
                }
            }

            // Ensure settings and streak exist for user
            if (settingDao.getSettingsByEmail(email) == null) {
                settingDao.insertOrUpdateSettings(SettingEntity(email = email))
            }
            if (streakDao.getStreakByEmail(email) == null) {
                streakDao.insertOrUpdateStreak(StreakEntity(email = email))
            }

            // Write session
            userDao.setSession(UserSessionEntity(loggedInUserEmail = email, isLoggedIn = true))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByEmail(email)
            if (user == null) {
                return@withContext Result.failure(Exception("No user found with this email."))
            }

            // Generative recovery token
            val randomToken = (100000..999999).random().toString()
            userDao.updateUser(user.copy(resetToken = randomToken))

            // Optional Supabase link
            if (SupabaseConfig.isConfigured) {
                try {
                    SupabaseClient.getInstance().auth.recoverPassword(mapOf("email" to email))
                } catch (e: Exception) {
                    Log.w("SupabaseSync", "Supabase recovery failed, offline code generated", e)
                }
            }

            Result.success(randomToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(email: String, token: String, newPasswordRaw: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByEmail(email)
            if (user == null) {
                return@withContext Result.failure(Exception("User not found."))
            }

            if (user.resetToken != token) {
                return@withContext Result.failure(Exception("Invalid verification token."))
            }

            val newHash = hashPassword(newPasswordRaw)
            userDao.updateUser(user.copy(passwordHash = newHash, resetToken = null))
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.clearAllSessions()
        SupabaseSessionManager.authToken = null
        SupabaseSessionManager.userEmail = null
    }

    private fun hashPassword(password: String): String {
        // Simple secure-checksum simulation for demo reliability
        return "aura_" + password.hashCode().toString() + "_fit"
    }

    private fun verifyPassword(password: String, hash: String): Boolean {
        return hash == hashPassword(password)
    }

    // ==========================================
    // ONBOARDING PROFILE
    // ==========================================
    fun getProfileFlow(email: String): Flow<ProfileEntity?> = profileDao.getProfileFlow(email)

    suspend fun getProfileByEmail(email: String): ProfileEntity? = withContext(Dispatchers.IO) {
        profileDao.getProfileByEmail(email)
    }

    suspend fun saveProfile(profile: ProfileEntity) = withContext(Dispatchers.IO) {
        profileDao.insertProfile(profile)

        // Send confirmation notification
        val welcomeNudge = NotificationLogEntity(
            userEmail = profile.email,
            title = "AuraFit Activated!",
            message = "Welcome ${profile.name}! Your tailored ${profile.disciplinePlan} is live.",
            type = "motivation"
        )
        notificationDao.insertNotification(welcomeNudge)

        // Supabase database post profile sync
        if (SupabaseConfig.isConfigured) {
            try {
                val profileMap = mapOf(
                    "email" to profile.email,
                    "name" to profile.name,
                    "age" to profile.age,
                    "gender" to profile.gender,
                    "height" to profile.height,
                    "weight" to profile.weight,
                    "fitnessGoal" to profile.fitnessGoal,
                    "experienceLevel" to profile.experienceLevel,
                    "dailyWorkoutTime" to profile.dailyWorkoutTime,
                    "disciplinePlan" to profile.disciplinePlan
                )
                SupabaseClient.getInstance().db.upsertProfile(profileMap)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Supabase profile sync failed", e)
            }
        }
    }

    suspend fun updateProfile(email: String, name: String, height: Float, weight: Float) = withContext(Dispatchers.IO) {
        val current = profileDao.getProfileByEmail(email)
        if (current != null) {
            val updated = current.copy(name = name, height = height, weight = weight)
            profileDao.insertProfile(updated)
            if (SupabaseConfig.isConfigured) {
                try {
                    val profileMap = mapOf(
                        "email" to updated.email,
                        "name" to updated.name,
                        "height" to updated.height,
                        "weight" to updated.weight
                    )
                    SupabaseClient.getInstance().db.upsertProfile(profileMap)
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Supabase profile update sync failed", e)
                }
            }
        }
    }

    // ==========================================
    // REAL STREAK ENGINE & CALCULATIONS
    // ==========================================
    fun getStreakFlow(email: String): Flow<StreakEntity?> = streakDao.getStreakFlow(email)

    suspend fun valuateStreakIntegrity(email: String) = withContext(Dispatchers.IO) {
        val streak = streakDao.getStreakByEmail(email) ?: return@withContext
        val today = getTodayString()
        val yesterday = getYesterdayString()

        val lastDate = streak.lastCompletedWorkoutDate ?: return@withContext

        // If last completed date was neither today nor yesterday, streak is broken!
        if (lastDate != today && lastDate != yesterday) {
            val brokenStreak = streak.copy(currentStreak = 0)
            streakDao.insertOrUpdateStreak(brokenStreak)
            
            // Log inactivity notification
            notificationDao.insertNotification(NotificationLogEntity(
                userEmail = email,
                title = "Streak Broken!",
                message = "You missed your exercise window. Let's restart today and stay disciplined!",
                type = "streak"
            ))

            if (SupabaseConfig.isConfigured) {
                try {
                    val streakMap = mapOf(
                        "email" to email,
                        "currentStreak" to 0,
                        "longestStreak" to streak.longestStreak,
                        "lastCompletedWorkoutDate" to lastDate
                    )
                    SupabaseClient.getInstance().db.upsertStreak(streakMap)
                } catch (e: Exception) {
                    Log.e("SupabaseSync", "Supabase streak reset sync failed", e)
                }
            }
        }
    }

    suspend fun completeWorkout(email: String, workoutName: String, category: String, durationSec: Int, caloriesBurned: Int) = withContext(Dispatchers.IO) {
        // 1. Log completed workout
        val workout = CompletedWorkoutEntity(
            userEmail = email,
            workoutName = workoutName,
            category = category,
            durationSeconds = durationSec,
            caloriesBurned = caloriesBurned
        )
        workoutDao.insertCompletedWorkout(workout)

        // 2. Compute true Streak Logic
        val todayString = getTodayString()
        val yesterdayString = getYesterdayString()
        val streak = streakDao.getStreakByEmail(email) ?: StreakEntity(email = email)

        var newCurrent = streak.currentStreak
        var newLongest = streak.longestStreak
        val lastDate = streak.lastCompletedWorkoutDate

        when (lastDate) {
            todayString -> {
                // Already did a workout today. Completed count goes up but streak stays the same (increases ONLY ONCE per calendar day!)
                Log.d("StreakEngine", "Workout already done today! No streak increment.")
            }
            yesterdayString -> {
                // Consecutive streak!
                newCurrent += 1
                if (newCurrent > newLongest) {
                    newLongest = newCurrent
                }
                Log.d("StreakEngine", "Yesterday matched, streak increments to $newCurrent")

                // Reward milestone check
                checkAndIssueStreakMilestones(email, newCurrent)
            }
            null -> {
                // First workout ever!
                newCurrent = 1
                newLongest = 1
                Log.d("StreakEngine", "No previous workouts, streak starts at 1")
                
                notificationDao.insertNotification(NotificationLogEntity(
                    userEmail = email,
                    title = "Your First Streak!",
                    message = "Awesome start! Secure your line by completing another tomorrow.",
                    type = "streak"
                ))
            }
            else -> {
                // Streak was broken and is restarted today!
                newCurrent = 1
                Log.d("StreakEngine", "Streak broken. Restarting today.")
                
                notificationDao.insertNotification(NotificationLogEntity(
                    userEmail = email,
                    title = "Streak Restarted!",
                    message = "Discipline consists of starting again. Keep pushing forward!",
                    type = "streak"
                ))
            }
        }

        val updatedStreak = StreakEntity(
            email = email,
            currentStreak = newCurrent,
            longestStreak = newLongest,
            lastCompletedWorkoutDate = todayString
        )
        streakDao.insertOrUpdateStreak(updatedStreak)

        // 3. Supabase Upload sync!
        if (SupabaseConfig.isConfigured) {
            try {
                // Upsert streak
                val streakMap = mapOf(
                    "email" to email,
                    "currentStreak" to newCurrent,
                    "longestStreak" to newLongest,
                    "lastCompletedWorkoutDate" to todayString
                )
                SupabaseClient.getInstance().db.upsertStreak(streakMap)

                // Archive completed workout
                val wMap = mapOf(
                    "userEmail" to email,
                    "workoutName" to workoutName,
                    "category" to category,
                    "durationSeconds" to durationSec,
                    "caloriesBurned" to caloriesBurned,
                    "completedAtTimestamp" to System.currentTimeMillis()
                )
                SupabaseClient.getInstance().db.uploadCompletedWorkout(wMap)
            } catch (e: Exception) {
                Log.e("SupabaseSync", "Supabase workout update sync failed", e)
            }
        }
    }

    private suspend fun checkAndIssueStreakMilestones(email: String, curStreak: Int) {
        val rewardMessage = when (curStreak) {
            3 -> "Bronze Shield Achieved! 3 Days of absolute willpower."
            7 -> "Silver Shield Achieved! 7 Days - A pure week of consistent efforts."
            15 -> "Gold Crown Achieved! 15 Days of unbreakable daily tracking."
            30 -> "Diamond Flame Unlocked! 30 Days of legendary consistency."
            else -> null
        }
        if (rewardMessage != null) {
            notificationDao.insertNotification(NotificationLogEntity(
                userEmail = email,
                title = "Streak Reward Unlocked!",
                message = rewardMessage,
                type = "streak"
            ))
        }
    }

    // ==========================================
    // PHYSICAL STATS FLOWS
    // ==========================================
    fun getWorkoutHistoryFlow(email: String): Flow<List<CompletedWorkoutEntity>> = workoutDao.getAllHistoryFlow(email)
    fun getCompletedCountFlow(email: String): Flow<Int> = workoutDao.getCompletedWorkoutsCountFlow(email)
    fun getCumulativeDurationFlow(email: String): Flow<Int?> = workoutDao.getCumulativeDurationFlow(email)
    fun getCumulativeCaloriesFlow(email: String): Flow<Int?> = workoutDao.getCumulativeCaloriesFlow(email)

    // ==========================================
    // NOTIFICATIONS SYSTEM
    // ==========================================
    fun getNotificationsFlow(email: String): Flow<List<NotificationLogEntity>> = notificationDao.getNotificationsFlow(email)
    suspend fun clearNotifications(email: String) = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead(email)
    }
    suspend fun triggerCustomNotification(email: String, title: String, message: String, type: String) = withContext(Dispatchers.IO) {
        notificationDao.insertNotification(NotificationLogEntity(
            userEmail = email,
            title = title,
            message = message,
            type = type
        ))
    }

    // ==========================================
    // SYSTEM SETTINGS
    // ==========================================
    fun getSettingsFlow(email: String): Flow<SettingEntity?> = settingDao.getSettingsFlow(email)
    suspend fun updateSettings(settings: SettingEntity) = withContext(Dispatchers.IO) {
        settingDao.insertOrUpdateSettings(settings)
    }
}
