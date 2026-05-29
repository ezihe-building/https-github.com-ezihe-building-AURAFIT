package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ==========================================
// 1. ENTITIES
// ==========================================

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val isVerified: Boolean = false,
    val resetToken: String? = null
)

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
    @PrimaryKey val id: Int = 1, // Single active session
    val loggedInUserEmail: String?,
    val isLoggedIn: Boolean,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "onboarding_profiles")
data class ProfileEntity(
    @PrimaryKey val email: String,
    val name: String,
    val age: Int,
    val gender: String, // Male, Female
    val height: Float,  // in cm
    val weight: Float,  // in kg
    val fitnessGoal: String, // "Weight Loss", "Weight Gain", "Maintenance", "Austerity", "Athleticism"
    val experienceLevel: String, // "Beginner", "Advanced"
    val dailyWorkoutTime: Int, // in minutes
    val disciplinePlan: String // "60 Day Plan", "150 Day Plan", "365 Day Plan", "2 Year Discipline Plan"
)

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val email: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedWorkoutDate: String? = null // YYYY-MM-DD
)

@Entity(tableName = "completed_workouts")
data class CompletedWorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val workoutName: String,
    val category: String, // "Cardio", "Strength", "Stretching"
    val durationSeconds: Int,
    val caloriesBurned: Int,
    val completedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "streak", "workout", "motivation"
    val isRead: Boolean = false
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val email: String,
    val isDarkMode: Boolean = true,
    val areRemindersEnabled: Boolean = true,
    val workoutReminders: Boolean = true,
    val streakReminders: Boolean = true,
    val motivateReminders: Boolean = true,
    val notificationFrequency: String = "Daily", // "Daily", "Weekly", "Quiet"
    val isPrivacyEnabled: Boolean = false
)

// ==========================================
// 2. DAOS
// ==========================================

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM user_sessions WHERE id = 1 LIMIT 1")
    fun getActiveSessionFlow(): Flow<UserSessionEntity?>

    @Query("SELECT * FROM user_sessions WHERE id = 1 LIMIT 1")
    suspend fun getActiveSession(): UserSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSession(session: UserSessionEntity)

    @Query("DELETE FROM user_sessions")
    suspend fun clearAllSessions()
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM onboarding_profiles WHERE email = :email LIMIT 1")
    fun getProfileFlow(email: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM onboarding_profiles WHERE email = :email LIMIT 1")
    suspend fun getProfileByEmail(email: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity)
}

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE email = :email LIMIT 1")
    fun getStreakFlow(email: String): Flow<StreakEntity?>

    @Query("SELECT * FROM streaks WHERE email = :email LIMIT 1")
    suspend fun getStreakByEmail(email: String): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStreak(streak: StreakEntity)
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM completed_workouts WHERE userEmail = :email ORDER BY completedAtTimestamp DESC")
    fun getAllHistoryFlow(email: String): Flow<List<CompletedWorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedWorkout(workout: CompletedWorkoutEntity)

    @Query("SELECT COUNT(*) FROM completed_workouts WHERE userEmail = :email")
    fun getCompletedWorkoutsCountFlow(email: String): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM completed_workouts WHERE userEmail = :email")
    fun getCumulativeDurationFlow(email: String): Flow<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM completed_workouts WHERE userEmail = :email")
    fun getCumulativeCaloriesFlow(email: String): Flow<Int?>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getNotificationsFlow(email: String): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationLogEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE userEmail = :email")
    suspend fun markAllAsRead(email: String)
}

@Dao
interface SettingDao {
    @Query("SELECT * FROM settings WHERE email = :email LIMIT 1")
    fun getSettingsFlow(email: String): Flow<SettingEntity?>

    @Query("SELECT * FROM settings WHERE email = :email LIMIT 1")
    suspend fun getSettingsByEmail(email: String): SettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: SettingEntity)
}

// ==========================================
// 3. DATABASE HOLDER
// ==========================================

@Database(
    entities = [
        UserEntity::class,
        UserSessionEntity::class,
        ProfileEntity::class,
        StreakEntity::class,
        CompletedWorkoutEntity::class,
        NotificationLogEntity::class,
        SettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun streakDao(): StreakDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun notificationDao(): NotificationDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AuraDatabase? = null

        fun getDatabase(context: Context): AuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuraDatabase::class.java,
                    "aurafit_secure_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
