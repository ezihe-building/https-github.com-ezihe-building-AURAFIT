package com.example.data

import androidx.compose.ui.graphics.vector.ImageVector

data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val category: String, // "Cardio", "Strength", "Stretching"
    val level: String,     // "Beginner", "Advanced"
    val isHome: Boolean,
    val isGym: Boolean,
    val isMaleRecommended: Boolean,
    val isFemaleRecommended: Boolean,
    val durationSeconds: Int,
    val burnedCaloriesPerMin: Float,
    val targetMuscles: String
)

object WorkoutPreset {
    val exercises = listOf(
        // === CARDIO WORKOUTS ===
        Exercise(
            id = "c1",
            name = "High Knee Sprints",
            description = "Drive knees up to hip height rapidly, engaging the core and pumping the arms to maximize heart rate.",
            category = "Cardio", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 10.5f, targetMuscles = "Quads, Calf, Abs"
        ),
        Exercise(
            id = "c2",
            name = "Jumping Jacks",
            description = "Classic full-body kinetic cardiorespiratory movement that builds aerobic efficiency.",
            category = "Cardio", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 8.0f, targetMuscles = "Full Body"
        ),
        Exercise(
            id = "c3",
            name = "Burpees",
            description = "From standing, drop into a plank, perform a push-up, jump back to feet and explode upwards.",
            category = "Cardio", level = "Advanced", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 14.2f, targetMuscles = "Chest, Shoulders, Quads, Core"
        ),
        Exercise(
            id = "c4",
            name = "Mountain Climbers",
            description = "Maintain plank alignment while driving alternative knees to chest at a rapid pace.",
            category = "Cardio", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 9.8f, targetMuscles = "Core, Shoulders, Hip Flexors"
        ),
        Exercise(
            id = "c5",
            name = "Box Jumps",
            description = "Jump explosively onto a sturdy box/platform, soft landing in a squat, stand fully and step down.",
            category = "Cardio", level = "Advanced", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = false, durationSeconds = 40,
            burnedCaloriesPerMin = 11.5f, targetMuscles = "Glutes, Hamstrings, Quads, Calves"
        ),
        Exercise(
            id = "c6",
            name = "Jump Rope Double Unders",
            description = "Rope passes twice beneath your feet per jump, developing exceptional calf strength and timing.",
            category = "Cardio", level = "Advanced", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 50,
            burnedCaloriesPerMin = 13.0f, targetMuscles = "Calves, Forearms, Shoulders"
        ),
        Exercise(
            id = "c7",
            name = "Fast Feet Drill",
            description = "Stay low in an athletic stance, moving feet rapidly in place representing lateral Agility.",
            category = "Cardio", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 9.0f, targetMuscles = "Calves, Quads, Glutes"
        ),
        Exercise(
            id = "c8",
            name = "Shadow Boxing",
            description = "Throw continuous punches, pivot, and weave in a sports stance focusing on cardio pacing.",
            category = "Cardio", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 90,
            burnedCaloriesPerMin = 8.5f, targetMuscles = "Shoulders, Lats, Core"
        ),

        // === STRENGTH TRAINING WORKOUTS ===
        Exercise(
            id = "s1",
            name = "Diamond Push-Ups",
            description = "Position hands close together forming a diamond, lowering chest to hands to target the triceps.",
            category = "Strength", level = "Advanced", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = false, durationSeconds = 45,
            burnedCaloriesPerMin = 8.2f, targetMuscles = "Triceps, Inner Chest, Anterior Deltoids"
        ),
        Exercise(
            id = "s2",
            name = "Barbell Squats",
            description = "Rest barbell across traps, lower hips below parallel keeping spine neutral, drive upwards.",
            category = "Strength", level = "Advanced", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 10.0f, targetMuscles = "Quads, Glutes, Spinal Erectors"
        ),
        Exercise(
            id = "s3",
            name = "Dumbbell Lunge Walks",
            description = "Step forward with weighted dumbbells, holding shoulders back, creating 90-degree angles in knees.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 7.5f, targetMuscles = "Quads, Glutes, Hamstrings"
        ),
        Exercise(
            id = "s4",
            name = "Barbell Deadlift",
            description = "Hinge hip back, grip barbell, contract upper back, and raise weight by driving floor away until lock.",
            category = "Strength", level = "Advanced", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 12.0f, targetMuscles = "Hamstrings, Glutes, Spinal Erectors, Traps"
        ),
        Exercise(
            id = "s5",
            name = "Standard Bodyweight Squats",
            description = "Descend with heels planted and chest proud, pushing knees outward dynamically.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 50,
            burnedCaloriesPerMin = 6.0f, targetMuscles = "Quads, Glutes"
        ),
        Exercise(
            id = "s6",
            name = "Military Press",
            description = "Press barbell vertically overhead from shoulder level while locking out core completely.",
            category = "Strength", level = "Advanced", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = false, durationSeconds = 40,
            burnedCaloriesPerMin = 9.0f, targetMuscles = "Shoulders, Triceps, Upper Chest"
        ),
        Exercise(
            id = "s7",
            name = "Pull-Ups",
            description = "Hang fully extended, engage lats, and pull body upward until your chin clears the bar cleanly.",
            category = "Strength", level = "Advanced", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = false, durationSeconds = 45,
            burnedCaloriesPerMin = 9.5f, targetMuscles = "Latissimus Dorsi, Biceps, Forearms"
        ),
        Exercise(
            id = "s8",
            name = "Dumbbell Bicep Curl",
            description = "Stand tall, keeping elbows strictly tucked at your flanks, curl dumbbells rotating wrists upward.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 5.0f, targetMuscles = "Biceps, Brachialis"
        ),
        Exercise(
            id = "s9",
            name = "Glute Bridges",
            description = "Lie on back with knees bent, squeeze glutes to press hips towards the sky, building rear-chain strength.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = false, isFemaleRecommended = true, durationSeconds = 50,
            burnedCaloriesPerMin = 5.5f, targetMuscles = "Glutes, Lower Back"
        ),
        Exercise(
            id = "s10",
            name = "Kettlebell Swings",
            description = "Hinge hips back and drive them forward explosively, swinging kettlebell up to shoulder height.",
            category = "Strength", level = "Intermediate", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 50,
            burnedCaloriesPerMin = 11.0f, targetMuscles = "Hamstrings, Glutes, Core"
        ),
        Exercise(
            id = "s11",
            name = "Goblet Squat",
            description = "Hold a weight at your chest like a chalice and perform deep squats with perfect safety.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 8.0f, targetMuscles = "Quads, Hip Adductors"
        ),
        Exercise(
            id = "s12",
            name = "Pike Push-Ups",
            description = "Invert your hips into a V-shape, lowering your head to the floor to develop deltoid strength.",
            category = "Strength", level = "Advanced", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 7.8f, targetMuscles = "Anterior Deltoids, Trapped Core"
        ),

        // === STRETCHING / RECOVERY ===
        Exercise(
            id = "st1",
            name = "Cobra Stretch",
            description = "Lie prone, press palms down fully and arch spine upward to stretch abs and release spinal compression.",
            category = "Stretching", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 2.5f, targetMuscles = "Abdominals, Lower Back"
        ),
        Exercise(
            id = "st2",
            name = "Child's Pose",
            description = "Settle hips back back toward heels, reaching your arms long ahead to open lats and shoulders.",
            category = "Stretching", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 90,
            burnedCaloriesPerMin = 2.0f, targetMuscles = "Shoulders, Spine, Glutes"
        ),
        Exercise(
            id = "st3",
            name = "Downward Dog",
            description = "Form a tall inverted V-shape, pressing heels flat downward to deeply stretch calves and hamstrings.",
            category = "Stretching", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 3.0f, targetMuscles = "Hamstrings, Calves, Shoulders"
        ),
        Exercise(
            id = "st4",
            name = "Pigeon Pose",
            description = "Fold front leg at 90 degrees with rear leg straight back, melting over the thigh to release hips.",
            category = "Stretching", level = "Advanced", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 2.8f, targetMuscles = "Deep Glutes, Piriformis, Hip Flexors"
        ),
        Exercise(
            id = "st5",
            name = "World's Greatest Stretch",
            description = "Deep lunge with rotation, opening chest and spinal muscles dynamically.",
            category = "Stretching", level = "Advanced", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 75,
            burnedCaloriesPerMin = 4.2f, targetMuscles = "Spine, Quads, Hip Flexors"
        ),
        Exercise(
            id = "st6",
            name = "Hamstring Scoop",
            description = "Extend one heel forward, hinge back hips, and sweep arms downward in an active hamstring sweep.",
            category = "Stretching", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 50,
            burnedCaloriesPerMin = 3.2f, targetMuscles = "Hamstrings, Lower Back"
        ),
        Exercise(
            id = "st7",
            name = "Standing Quad Stretch",
            description = "Balance on one leg, pulling opposite heel to glutes keeping thighs locked parallel.",
            category = "Stretching", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 40,
            burnedCaloriesPerMin = 2.2f, targetMuscles = "Quadriceps"
        ),
        Exercise(
            id = "st8",
            name = "Chest Opener Wall Stretch",
            description = "Place forearm on wall, rotating body outward slowly to release minor/major pectoral tightness.",
            category = "Stretching", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 2.0f, targetMuscles = "Pectorals, Anterior Deltoids"
        ),

        // === HOME FIT EXTRA PACK ===
        Exercise(
            id = "h1",
            name = "Plank Hold",
            description = "Position weight on elbows and toes, keeping body in flat steel alignment without sagging hips.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 4.5f, targetMuscles = "Core, Shoulders, Glutes"
        ),
        Exercise(
            id = "h2",
            name = "Russian Twists",
            description = "Sit balanced on hips, rotate shoulders side-to-side kissing fingertips to floor.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 6.5f, targetMuscles = "Obliques, Abs"
        ),
        Exercise(
            id = "h3",
            name = "Flutter Kicks",
            description = "Lie flat with hands supporting tailbone, alternate kick low legs rapidly keeping core engaged.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 40,
            burnedCaloriesPerMin = 6.0f, targetMuscles = "Lower Abs, Hip Flexors"
        ),
        Exercise(
            id = "h4",
            name = "Wall Sit",
            description = "Slide back flat against a wall until knees bend at 90 degrees, holding state of static quad fire.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 5.2f, targetMuscles = "Quadriceps, Calves"
        ),
        Exercise(
            id = "h5",
            name = "Tricep Chair Dips",
            description = "Grip edge of sturdy chair with fingers forward, flexing elbows to lower body until triceps are tested.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 6.0f, targetMuscles = "Triceps, Deltoids"
        ),
        Exercise(
            id = "h6",
            name = "Single-Leg Calf Raises",
            description = "Elevate body cleanly on one foot's toes, holding wall support, pushing calf growth.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 4.0f, targetMuscles = "Calves, Ankles"
        ),
        Exercise(
            id = "h7",
            name = "Bicycle Crunches",
            description = "Alternate touching elbow to opposite knee in a bicycle rhythm to lock obliques.",
            category = "Strength", level = "Intermediate", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 7.5f, targetMuscles = "Rectus Abdominis, Obliques"
        ),
        Exercise(
            id = "h8",
            name = "Supermans",
            description = "Lie face down, simultaneously lift arms, chest, and legs off floor, squeezing rear alignment.",
            category = "Strength", level = "Beginner", isHome = true, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 4.0f, targetMuscles = "Spinal Erectors, Glutes"
        ),

        // === GYM CLASSICS ===
        Exercise(
            id = "g1",
            name = "Incline Dumbbell Press",
            description = "Press dumbbells upward from 30-degree incline, sculpting upper chest region.",
            category = "Strength", level = "Intermediate", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 50,
            burnedCaloriesPerMin = 8.5f, targetMuscles = "Upper Clavicular Pectorals, Deltoids"
        ),
        Exercise(
            id = "g2",
            name = "Cable Lat Pulldown",
            description = "Pull cable bar down smoothly until it brushes upper chest, squeezing rear lats.",
            category = "Strength", level = "Beginner", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 7.0f, targetMuscles = "Latissimus Dorsi, Rhomboids, Lower Traps"
        ),
        Exercise(
            id = "g3",
            name = "Leg Press Machine",
            description = "Position feet on steel sled, release safety keys, and bend knees to 90 degrees before driving weight up.",
            category = "Strength", level = "Beginner", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 60,
            burnedCaloriesPerMin = 9.0f, targetMuscles = "Quadriceps, Glutes, Hamstrings"
        ),
        Exercise(
            id = "g4",
            name = "Lying Hamstring Curl",
            description = "In curl machine, grip handles and pull padded roller down to Glutes with complete lower leg force.",
            category = "Strength", level = "Beginner", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 45,
            burnedCaloriesPerMin = 6.0f, targetMuscles = "Hamstrings, Gastrocnemius"
        ),
        Exercise(
            id = "g5",
            name = "Seated Cable Row",
            description = "Pull weight attachment to belly button keeping posture perfectly upright and shoulders back.",
            category = "Strength", level = "Beginner", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 50,
            burnedCaloriesPerMin = 7.5f, targetMuscles = "Middle Traps, Rhomboids, Rear Delts"
        ),
        Exercise(
            id = "g6",
            name = "Dumbbell Lateral Raise",
            description = "Raise weights slowly outward to side, maintaining micro-flex in elbow to target cap deltoids.",
            category = "Strength", level = "Beginner", isHome = false, isGym = true,
            isMaleRecommended = true, isFemaleRecommended = true, durationSeconds = 40,
            burnedCaloriesPerMin = 5.2f, targetMuscles = "Lateral Deltoids"
        )
    )

    fun getRecommendedExercises(gender: String, experience: String, fitnessGoal: String): List<Exercise> {
        return exercises.filter { exercise ->
            // Filter based on suitability recommendations
            val isGenderOk = if (gender.lowercase() == "male") exercise.isMaleRecommended else exercise.isFemaleRecommended
            // Filter target categories
            val isGoalOk = when (fitnessGoal.lowercase()) {
                "cardio" -> exercise.category == "Cardio" || exercise.category == "Stretching"
                "strength", "muscle gain" -> exercise.category == "Strength"
                else -> true
            }
            isGenderOk && (exercise.level.lowercase() == experience.lowercase() || exercise.level == "Beginner" || isGoalOk)
        }.take(15) // take top 15 matches to create a compact personalized plan
    }
}
