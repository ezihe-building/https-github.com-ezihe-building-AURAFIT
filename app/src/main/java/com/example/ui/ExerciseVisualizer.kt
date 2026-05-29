package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import java.util.Locale
import kotlin.math.*

@Composable
fun ExerciseVisualizer(
    exerciseName: String,
    modifier: Modifier = Modifier,
    isResting: Boolean = false,
    gender: String = "Male"
) {
    val infiniteTransition = rememberInfiniteTransition(label = "exercise_oscillator")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "oscillator_phase"
    )

    // Secondary faster phase for rapid cardio
    val fastPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fast_oscillator"
    )

    val cleanName = exerciseName.lowercase(Locale.US)
    val isMale = gender.lowercase(Locale.US) == "male"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp) // slightly increased height for grander display
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CosmicSlate.copy(alpha = 0.75f),
                        ObsidianBlack.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f

            // Draw clean technical futuristic grids in background (smooth parallax movement)
            val gridSpacing = 24.dp.toPx()
            val gridAlpha = 0.05f
            var x = 0f
            while (x < width) {
                drawLine(
                    color = FrostWhite.copy(alpha = gridAlpha),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
                x += gridSpacing
            }
            var y = 0f
            while (y < height) {
                drawLine(
                    color = FrostWhite.copy(alpha = gridAlpha),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
                y += gridSpacing
            }

            // Draw ground reference line for exercises
            val floorY = height * 0.8f
            drawLine(
                color = FrostWhite.copy(alpha = 0.15f),
                start = Offset(width * 0.05f, floorY),
                end = Offset(width * 0.95f, floorY),
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
            )

            if (isResting) {
                // RESTING STATE / MINDFUL BREATHING: Pulsing expansion sphere and relaxing dynamic nodes
                val pulse = (sin(phase) + 1f) / 2f // 0f to 1f
                val radius = 45.dp.toPx() + (35.dp.toPx() * pulse)
                val color = AuraAestheticPurple

                // Shadow aura glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.25f * (1f - pulse)), Color.Transparent),
                        center = Offset(centerX, centerY),
                        radius = radius * 1.8f
                    ),
                    center = Offset(centerX, centerY),
                    radius = radius * 1.8f
                )

                // Outer animated ring
                drawCircle(
                    color = color.copy(alpha = 0.4f),
                    center = Offset(centerX, centerY),
                    radius = radius,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 15f), phase * 10f))
                )

                // Inner core
                drawCircle(
                    color = color.copy(alpha = 0.8f),
                    center = Offset(centerX, centerY),
                    radius = radius * 0.6f,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Inner pulsing core star
                drawCircle(
                    color = AuraNeonCyan,
                    center = Offset(centerX, centerY),
                    radius = radius * 0.2f * (1.2f - pulse)
                )

                // Drifting recovery dust particles
                for (i in 0..5) {
                    val angleOffset = i * (2 * PI / 6)
                    val orbitRadius = radius * 1.3f
                    val particleX = centerX + orbitRadius * cos(phase + angleOffset).toFloat()
                    val particleY = centerY + orbitRadius * sin(phase * 0.5f + angleOffset).toFloat()
                    drawCircle(
                        color = AuraNeonCyan.copy(alpha = 0.6f),
                        center = Offset(particleX, particleY),
                        radius = 3.5f.dp.toPx()
                    )
                }
                return@Canvas
            }

            // ===================================
            // ATHLETIC PROGRESSION FORMULA (More Responsive!)
            // Slow controlled eccentric phase (down), brief peak squeeze pause, explosive concentric rise (up)!
            // ===================================
            val normalizedPhase = (phase / (2 * PI).toFloat())
            val athleticProg = if (normalizedPhase < 0.55f) {
                // 1. Slow eccentric lowering: 0.0 to 1.0
                val t = normalizedPhase / 0.55f
                t * t * (3 - 2 * t) // smooth Hermite ease-in-out
            } else if (normalizedPhase < 0.65f) {
                // 2. High-tension peak contraction hold at the bottom (10% duration)
                1.0f
            } else {
                // 3. Explosive rapid concentric lift-off back to 0.0: 1.0 down to 0.0
                val t = (normalizedPhase - 0.65f) / 0.35f
                1.0f - (t * t) // explosive quadratic acceleration
            }

            // Helper function to draw sleek, organic human-like muscular limbs (Sartorius, Biceps, Calf silhouettes)
            fun drawHumanLimb(
                start: Offset,
                end: Offset,
                isThigh: Boolean = false,
                isShank: Boolean = false,
                isArm: Boolean = false,
                torsoJoint: Boolean = false
            ) {
                // Establish specific human muscle thickness dimensions
                val thickness = when {
                    isThigh -> if (isMale) 22f else 18f
                    isShank -> if (isMale) 15f else 12f
                    isArm -> if (isMale) 13f else 10f
                    else -> 12f
                }

                // Athletic Skin and Active-wear tones
                val primaryColor = if (isMale) AuraNeonCyan else AuraAestheticPurple
                val skinColor = if (isMale) Color(0xFFFBE9E7) else Color(0xFFFFF1F0)
                val clothingColor = if (isMale) CosmicSlate else ObsidianBlack

                // Gimmick muscle stress shadow glow
                drawLine(
                    color = primaryColor.copy(alpha = 0.15f),
                    start = start,
                    end = end,
                    strokeWidth = thickness * 2.5f,
                    cap = StrokeCap.Round
                )

                // 1. Draw base fleshed limb
                drawLine(
                    color = skinColor,
                    start = start,
                    end = end,
                    strokeWidth = thickness,
                    cap = StrokeCap.Round
                )

                // 2. Draw fitness wear overlay (Shorts for thighs, long sleeves / sports wristbands)
                if (isThigh) {
                    // Draw compression workout shorts covering 65% of upper thigh
                    val shortsEnd = Offset(
                        start.x + (end.x - start.x) * 0.65f,
                        start.y + (end.y - start.y) * 0.65f
                    )
                    drawLine(
                        color = clothingColor,
                        start = start,
                        end = shortsEnd,
                        strokeWidth = thickness + 3f,
                        cap = StrokeCap.Round
                    )
                    // Neon hem band on shorts
                    drawLine(
                        color = primaryColor,
                        start = shortsEnd,
                        end = Offset(
                            start.x + (end.x - start.x) * 0.68f,
                            start.y + (end.y - start.y) * 0.68f
                        ),
                        strokeWidth = thickness + 4f,
                        cap = StrokeCap.Round
                    )
                }

                if (isArm && torsoJoint) {
                    // Draw tank top shoulder joint sleeve covering 35% of upper arm
                    val sleeveEnd = Offset(
                        start.x + (end.x - start.x) * 0.35f,
                        start.y + (end.y - start.y) * 0.35f
                    )
                    drawLine(
                        color = clothingColor,
                        start = start,
                        end = sleeveEnd,
                        strokeWidth = thickness + 3f,
                        cap = StrokeCap.Round
                    )
                }

                // 3. Central light specula high-fidelity reflection path for cylindrical 3D visual contour
                drawLine(
                    color = Color.White.copy(alpha = 0.4f),
                    start = start,
                    end = end,
                    strokeWidth = thickness * 0.18f,
                    cap = StrokeCap.Round
                )
            }

            // Helper to draw realistic athletic sneakers
            fun drawAthleticSneaker(ankle: Offset, toeDirectionX: Float) {
                val shoeLength = 16.dp.toPx()
                val shoeHeight = 7.dp.toPx()
                val primaryColor = if (isMale) AuraNeonCyan else AuraAestheticPurple

                val sneakerPath = Path().apply {
                    moveTo(ankle.x - shoeLength * 0.3f, ankle.y)
                    lineTo(ankle.x + toeDirectionX * shoeLength, ankle.y + shoeHeight * 0.3f)
                    lineTo(ankle.x + toeDirectionX * shoeLength * 0.8f, ankle.y + shoeHeight)
                    lineTo(ankle.x - shoeLength * 0.5f, ankle.y + shoeHeight * 0.8f)
                    close()
                }
                drawPath(
                    path = sneakerPath,
                    brush = Brush.verticalGradient(listOf(primaryColor, CosmicSlate))
                )
                // Glowing white sole
                drawLine(
                    color = FrostWhite,
                    start = Offset(ankle.x - shoeLength * 0.5f, ankle.y + shoeHeight * 0.8f),
                    end = Offset(ankle.x + toeDirectionX * shoeLength, ankle.y + shoeHeight * 0.3f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Square
                )
            }

            // Helper to draw clean stylized heads, hair and features
            fun drawCharacterHead(headX: Float, headY: Float) {
                val headRadius = 10.dp.toPx()
                val primaryColor = if (isMale) AuraNeonCyan else AuraAestheticPurple
                val skinColor = if (isMale) Color(0xFFFBE9E7) else Color(0xFFFFF1F0)

                // Face sphere
                drawCircle(color = skinColor, radius = headRadius, center = Offset(headX, headY))

                // High tech futuristic visors / biometric headband
                val visorStart = Offset(headX - headRadius * 0.8f, headY - headRadius * 0.2f)
                val visorEnd = Offset(headX + headRadius * 0.5f, headY - headRadius * 0.1f)
                drawLine(
                    color = primaryColor,
                    start = visorStart,
                    end = visorEnd,
                    strokeWidth = 3.5f.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Visor shine
                drawLine(
                    color = Color.White,
                    start = visorStart,
                    end = Offset(visorStart.x + (visorEnd.x - visorStart.x) * 0.4f, visorStart.y + (visorEnd.y - visorStart.y) * 0.4f),
                    strokeWidth = 1.2f.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Dynamic gender-specific characters hair styling
                if (!isMale) {
                    // Female character: Beautiful high-bound dynamic pony tail sways with active motion amplitude
                    val ponytailPath = Path().apply {
                        moveTo(headX - headRadius * 0.5f, headY - headRadius * 0.6f)
                        val swayAmplitude = 5.dp.toPx()
                        // Ponytail reacts to the speed of movement: higher speed = higher swing sway lag
                        val swayX = -12.dp.toPx() + (cos(phase * 1.5f) * swayAmplitude)
                        val swayY = 8.dp.toPx() + (sin(phase * 1.5f) * swayAmplitude)
                        
                        quadraticTo(
                            headX - headRadius * 0.9f + swayX / 2f, headY - headRadius * 0.1f + swayY / 2f,
                            headX - headRadius * 1.1f + swayX, headY + headRadius * 0.4f + swayY
                        )
                    }
                    drawPath(
                        path = ponytailPath,
                        color = Color(0xFF1E293B), // sporty obsidian sleek dark hair
                        style = Stroke(width = 5.5f.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // High bun-tie lock indicator
                    drawCircle(
                        color = AuraAestheticPurple,
                        radius = 2.5f.dp.toPx(),
                        center = Offset(headX - headRadius * 0.6f, headY - headRadius * 0.6f)
                    )
                } else {
                    // Male character: Modern sporty cropped side-undercut high top hair
                    val hairPath = Path().apply {
                        moveTo(headX - headRadius, headY - headRadius * 0.5f)
                        lineTo(headX - headRadius * 0.2f, headY - headRadius * 1.3f)
                        lineTo(headX + headRadius * 0.8f, headY - headRadius * 0.9f)
                        lineTo(headX + headRadius * 0.6f, headY - headRadius * 0.3f)
                        lineTo(headX - headRadius, headY - headRadius * 0.3f)
                        close()
                    }
                    drawPath(
                        path = hairPath,
                        color = Color(0xFF1B1B1D) // obsidian jet carbon fiber hair
                    )
                }
            }

            // ==========================================
            // RENDER EXERCISES ACTIVELY
            // ==========================================
            when {
                // 1. PUSH-UPS / PLANK / BURPEES / MOUNTAIN CLIMBERS (Side Profile View)
                cleanName.contains("push-up") || cleanName.contains("plank") || cleanName.contains("burpee") || cleanName.contains("mountain") -> {
                    val isPushUp = cleanName.contains("push-up") || cleanName.contains("burpee")
                    val amplitude = if (cleanName.contains("pike")) 0.4f else 1.0f
                    // Apply athletic progressive dip
                    val dip = if (isPushUp) athleticProg * amplitude else 0f

                    // Coordinates layout
                    val feetX = width * 0.76f
                    val feetY = floorY - 6.dp.toPx()

                    val handX = width * 0.24f
                    val handY = floorY

                    val hipX = width * 0.56f
                    val hipYDefault = floorY - 58.dp.toPx()
                    val hipY = hipYDefault + (22.dp.toPx() * dip)

                    val shoulderX = width * 0.34f
                    val shoulderYDefault = floorY - 84.dp.toPx()
                    val shoulderY = shoulderYDefault + (42.dp.toPx() * dip)

                    val headX = shoulderX - 18.dp.toPx()
                    val headY = shoulderY - 10.dp.toPx()

                    // Knee & Elbow Calculations
                    val kneeX = (feetX + hipX) / 2f
                    val kneeY = (feetY + hipY) / 2f

                    // Elbow bends backward in realistic athletic alignment
                    val elbowX = ((shoulderX + handX) / 2f) + (16.dp.toPx() * dip)
                    val elbowY = ((shoulderY + handY) / 2f) - (24.dp.toPx() * dip)

                    // Active working muscle infrared glow region (Chest, Core, Shoulders)
                    val primeColor = if (isMale) AuraNeonCyan else AuraAestheticPurple
                    val intensity = 0.12f + (0.22f * dip)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AuraAccentCoral.copy(alpha = intensity), Color.Transparent),
                            center = Offset(shoulderX + 5.dp.toPx(), shoulderY + 5.dp.toPx()),
                            radius = 58.dp.toPx()
                        ),
                        center = Offset(shoulderX + 5.dp.toPx(), shoulderY + 5.dp.toPx()),
                        radius = 58.dp.toPx()
                    )

                    // --- DRAW TORSO BODY (Side Profile active sports outfit segment) ---
                    val wearColor = if (isMale) CosmicSlate else ObsidianBlack
                    val torsoHeight = if (isMale) 18.dp.toPx() else 14.dp.toPx()
                    val torsoPath = Path().apply {
                        moveTo(shoulderX, shoulderY - torsoHeight / 2)
                        lineTo(shoulderX, shoulderY + torsoHeight / 2)
                        lineTo(hipX, hipY + torsoHeight * 0.4f)
                        lineTo(hipX, hipY - torsoHeight * 0.4f)
                        close()
                    }
                    drawPath(
                        path = torsoPath,
                        brush = Brush.verticalGradient(listOf(wearColor, primeColor.copy(alpha = 0.4f)))
                    )

                    // --- DRAW LIMBS ---
                    drawHumanLimb(Offset(feetX, feetY), Offset(kneeX, kneeY), isShank = true)
                    drawHumanLimb(Offset(kneeX, kneeY), Offset(hipX, hipY), isThigh = true)
                    drawHumanLimb(Offset(shoulderX, shoulderY), Offset(elbowX, elbowY), isArm = true, torsoJoint = true)
                    drawHumanLimb(Offset(elbowX, elbowY), Offset(handX, handY), isArm = true)

                    // --- DRAW JOINTS AS HIGH-TECH DATA SIGNALS ---
                    val joints = listOf(
                        Offset(kneeX, kneeY),
                        Offset(hipX, hipY),
                        Offset(shoulderX, shoulderY),
                        Offset(elbowX, elbowY)
                    )
                    joints.forEach { jt ->
                        drawCircle(color = primeColor, radius = 5.5f.dp.toPx(), center = jt)
                        drawCircle(color = FrostWhite, radius = 2.dp.toPx(), center = jt)
                    }

                    // Sneaker and athletic gloves
                    drawAthleticSneaker(Offset(feetX, feetY), -1.2f)
                    drawCircle(color = wearColor, radius = 4.5f.dp.toPx(), center = Offset(handX, handY))
                    drawCharacterHead(headX, headY)
                }

                // 2. SQUATS / LUNGES (Frontal Semi-Isometric view)
                cleanName.contains("squat") || cleanName.contains("lunge") -> {
                    // Squat depth driven by high fidelity responsive loop
                    val squatDepth = athleticProg

                    val leftFeetX = centerX - 44.dp.toPx()
                    val rightFeetX = centerX + 44.dp.toPx()
                    val feetY = floorY - 5.dp.toPx()

                    val standingHipY = centerY + 14.dp.toPx()
                    val bottomHipY = centerY + 62.dp.toPx()
                    val hipY = standingHipY + ((bottomHipY - standingHipY) * squatDepth)

                    val leftHipX = centerX - 18.dp.toPx()
                    val rightHipX = centerX + 18.dp.toPx()

                    // Thigh spread
                    val leftKneeX = (leftFeetX + leftHipX) / 2f - (12.dp.toPx() * squatDepth)
                    val rightKneeX = (rightFeetX + rightHipX) / 2f + (12.dp.toPx() * squatDepth)
                    val kneeY = (feetY + hipY) / 2f + (8.dp.toPx() * squatDepth)

                    val spineLength = 58.dp.toPx()
                    val shoulderY = hipY - spineLength
                    val leftShoulderX = centerX - 22.dp.toPx()
                    val rightShoulderX = centerX + 22.dp.toPx()

                    val headY = shoulderY - 22.dp.toPx()

                    // Hands raise forward smoothly as support during lowering
                    val lHandX = leftShoulderX - 28.dp.toPx()
                    val lHandY = shoulderY - (28.dp.toPx() * squatDepth)
                    val rHandX = rightShoulderX + 28.dp.toPx()
                    val rHandY = shoulderY - (28.dp.toPx() * squatDepth)

                    // Target Quad workout heat zone glow
                    val primeColor = if (isMale) AuraNeonCyan else AuraAestheticPurple
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AuraAccentCoral.copy(alpha = 0.12f + 0.22f * squatDepth), Color.Transparent),
                            center = Offset(centerX, (hipY + kneeY) / 2f),
                            radius = 68.dp.toPx()
                        ),
                        center = Offset(centerX, (hipY + kneeY) / 2f),
                        radius = 68.dp.toPx()
                    )

                    // --- DRAW FRONTAL ATHLETIC MODEL TORSO ---
                    val wearColor = if (isMale) CosmicSlate else ObsidianBlack
                    val torsoPath = Path().apply {
                        if (isMale) {
                            moveTo(leftShoulderX, shoulderY)
                            lineTo(rightShoulderX, shoulderY)
                            lineTo(rightHipX, hipY)
                            lineTo(leftHipX, hipY)
                        } else {
                            moveTo(leftShoulderX, shoulderY)
                            lineTo(rightShoulderX, shoulderY)
                            quadraticTo(centerX + 6.dp.toPx(), (shoulderY + hipY) / 2, rightHipX, hipY)
                            lineTo(leftHipX, hipY)
                            quadraticTo(centerX - 6.dp.toPx(), (shoulderY + hipY) / 2, leftShoulderX, shoulderY)
                        }
                        close()
                    }
                    drawPath(
                        path = torsoPath,
                        brush = Brush.verticalGradient(listOf(wearColor, primeColor.copy(alpha = 0.35f)))
                    )

                    // --- DRAW LIMBS ---
                    drawHumanLimb(Offset(leftFeetX, feetY), Offset(leftKneeX, kneeY), isShank = true)
                    drawHumanLimb(Offset(rightFeetX, feetY), Offset(rightKneeX, kneeY), isShank = true)
                    drawHumanLimb(Offset(leftKneeX, kneeY), Offset(leftHipX, hipY), isThigh = true)
                    drawHumanLimb(Offset(rightKneeX, kneeY), Offset(rightHipX, hipY), isThigh = true)

                    drawHumanLimb(Offset(leftShoulderX, shoulderY), Offset(lHandX, lHandY), isArm = true, torsoJoint = true)
                    drawHumanLimb(Offset(rightShoulderX, shoulderY), Offset(rHandX, rHandY), isArm = true, torsoJoint = true)

                    // --- TRACKER NODES ---
                    listOf(
                        Offset(leftKneeX, kneeY), Offset(rightKneeX, kneeY),
                        Offset(leftHipX, hipY), Offset(rightHipX, hipY),
                        Offset(leftShoulderX, shoulderY), Offset(rightShoulderX, shoulderY)
                    ).forEach { jt ->
                        drawCircle(color = primeColor, radius = 5.dp.toPx(), center = jt)
                    }

                    drawAthleticSneaker(Offset(leftFeetX, feetY), -1f)
                    drawAthleticSneaker(Offset(rightFeetX, feetY), 1f)
                    drawCharacterHead(centerX, headY)
                }

                // 3. JUMPING JACKS / SPRINTING / HIGH KNEES (High Frequency Elastic Movement)
                cleanName.contains("jack") || cleanName.contains("sprint") || cleanName.contains("high knee") || cleanName.contains("cardio") -> {
                    // Cardio is very responsive and springy
                    val pace = (sin(fastPhase) + 1f) / 2f
                    val bounceY = abs(sin(fastPhase * 2f)) * 14f // bouncy elastic coordinate shift

                    val feetWidth = 14.dp.toPx() + (60.dp.toPx() * pace)
                    val leftFeetX = centerX - feetWidth
                    val rightFeetX = centerX + feetWidth
                    val feetY = floorY - 5.dp.toPx() - bounceY

                    val hipY = centerY + 18.dp.toPx() - bounceY
                    val leftHipX = centerX - 15.dp.toPx()
                    val rightHipX = centerX + 15.dp.toPx()

                    val shoulderY = hipY - 60.dp.toPx()
                    val leftShoulderX = centerX - 22.dp.toPx()
                    val rightShoulderX = centerX + 22.dp.toPx()

                    val headY = shoulderY - 20.dp.toPx()

                    // Hands swing from hip side to hands above head
                    val armAngleRad = (-75f + 190f * pace) * PI / 180f
                    val armLength = 40.dp.toPx()

                    val leftHandX = leftShoulderX - (armLength * cos(armAngleRad)).toFloat()
                    val leftHandY = shoulderY - (armLength * sin(armAngleRad)).toFloat()

                    val rightHandX = rightShoulderX + (armLength * cos(armAngleRad)).toFloat()
                    val rightHandY = shoulderY - (armLength * sin(armAngleRad)).toFloat()

                    val primeColor = if (isMale) AuraNeonCyan else AuraAestheticPurple
                    val wearColor = if (isMale) CosmicSlate else ObsidianBlack

                    // High intensity Cardio Lungs Glow Aura
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AuraNeonCyan.copy(alpha = 0.15f + 0.12f * pace), Color.Transparent),
                            center = Offset(centerX, shoulderY + 15.dp.toPx()),
                            radius = 65.dp.toPx()
                        ),
                        center = Offset(centerX, shoulderY + 15.dp.toPx()),
                        radius = 65.dp.toPx()
                    )

                    // Draw Torso
                    val torsoPath = Path().apply {
                        if (isMale) {
                            moveTo(leftShoulderX, shoulderY)
                            lineTo(rightShoulderX, shoulderY)
                            lineTo(rightHipX, hipY)
                            lineTo(leftHipX, hipY)
                        } else {
                            moveTo(leftShoulderX, shoulderY)
                            lineTo(rightShoulderX, shoulderY)
                            quadraticTo(centerX + 5.dp.toPx(), (shoulderY + hipY) / 2, rightHipX, hipY)
                            lineTo(leftHipX, hipY)
                            quadraticTo(centerX - 5.dp.toPx(), (shoulderY + hipY) / 2, leftShoulderX, shoulderY)
                        }
                        close()
                    }
                    drawPath(
                        path = torsoPath,
                        brush = Brush.verticalGradient(listOf(wearColor, primeColor.copy(alpha = 0.35f)))
                    )

                    // Render Fleshed bones
                    drawHumanLimb(Offset(leftFeetX, feetY), Offset(leftHipX, hipY), isThigh = true)
                    drawHumanLimb(Offset(rightFeetX, feetY), Offset(rightHipX, hipY), isThigh = true)
                    drawHumanLimb(Offset(leftShoulderX, shoulderY), Offset(leftHandX, leftHandY), isArm = true, torsoJoint = true)
                    drawHumanLimb(Offset(rightShoulderX, shoulderY), Offset(rightHandX, rightHandY), isArm = true, torsoJoint = true)

                    // Draw connections
                    listOf(
                        Offset(leftFeetX, feetY), Offset(rightFeetX, feetY),
                        Offset(leftHipX, hipY), Offset(rightHipX, hipY),
                        Offset(leftShoulderX, shoulderY), Offset(rightShoulderX, shoulderY),
                        Offset(leftHandX, leftHandY), Offset(rightHandX, rightHandY)
                    ).forEach { jt ->
                        drawCircle(color = primeColor, radius = 5.dp.toPx(), center = jt)
                    }

                    drawAthleticSneaker(Offset(leftFeetX, feetY), -1f)
                    drawAthleticSneaker(Offset(rightFeetX, feetY), 1f)
                    drawCharacterHead(centerX, headY)

                    // Draw dynamic motion speed trails (makes it look 120fps ultra-fast responsive)
                    drawArc(
                        color = primeColor.copy(alpha = 0.18f),
                        startAngle = 110f,
                        sweepAngle = 140f,
                        useCenter = false,
                        topLeft = Offset(centerX - 82.dp.toPx(), shoulderY - 32.dp.toPx()),
                        size = Size(164.dp.toPx(), 104.dp.toPx()),
                        style = Stroke(width = 2.5f.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
                    )
                }

                // 4. CRUNCHES / RUSSIAN TWISTS / SITS (Sleek Horizontal Core Flex)
                cleanName.contains("crunch") || cleanName.contains("twist") || cleanName.contains("abs") || cleanName.contains("sit-up") || cleanName.contains("leg raise") -> {
                    // Torso hinge folds dynamically based on athletic responsive progression
                    val flex = athleticProg

                    val hipX = centerX + 18.dp.toPx()
                    val hipY = floorY - 14.dp.toPx()

                    // Legs bent
                    val footX = hipX - 60.dp.toPx()
                    val footY = floorY - 5.dp.toPx()

                    val kneeX = (hipX + footX) / 2f - 8.dp.toPx()
                    val kneeY = floorY - 34.dp.toPx()

                    val spineLength = 60.dp.toPx()
                    val startAngleRad = 12f * PI / 180f
                    val maxAngleRad = 64f * PI / 180f
                    val currentAngleRad = startAngleRad + ((maxAngleRad - startAngleRad) * flex)

                    val shoulderX = hipX + (spineLength * cos(currentAngleRad)).toFloat()
                    val shoulderY = hipY - (spineLength * sin(currentAngleRad)).toFloat()

                    val headX = shoulderX + (18.dp.toPx() * cos(currentAngleRad + 0.1f)).toFloat()
                    val headY = shoulderY - (18.dp.toPx() * sin(currentAngleRad + 0.1f)).toFloat()

                    // Hands behind neck
                    val elbowX = shoulderX - 12.dp.toPx()
                    val elbowY = shoulderY - 8.dp.toPx()

                    val primeColor = if (isMale) AuraNeonCyan else AuraAestheticPurple
                    val wearColor = if (isMale) CosmicSlate else ObsidianBlack

                    // Target Abs Contraction Warmth Spot
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(AuraAccentCoral.copy(alpha = 0.15f + 0.22f * flex), Color.Transparent),
                            center = Offset((hipX + shoulderX) / 2f, (hipY + shoulderY) / 2f),
                            radius = 52.dp.toPx()
                        ),
                        center = Offset((hipX + shoulderX) / 2f, (hipY + shoulderY) / 2f),
                        radius = 52.dp.toPx()
                    )

                    // Core Torso compression wear
                    val torsoThickness = if (isMale) 16.dp.toPx() else 12.dp.toPx()
                    val torsoPath = Path().apply {
                        moveTo(shoulderX - torsoThickness / 2, shoulderY)
                        lineTo(shoulderX + torsoThickness / 2, shoulderY)
                        lineTo(hipX + torsoThickness * 0.4f, hipY)
                        lineTo(hipX - torsoThickness * 0.4f, hipY)
                        close()
                    }
                    drawPath(
                        path = torsoPath,
                        brush = Brush.verticalGradient(listOf(wearColor, primeColor.copy(alpha = 0.4f)))
                    )

                    // Draw limbs out
                    drawHumanLimb(Offset(footX, footY), Offset(kneeX, kneeY), isShank = true)
                    drawHumanLimb(Offset(kneeX, kneeY), Offset(hipX, hipY), isThigh = true)
                    drawHumanLimb(Offset(shoulderX, shoulderY), Offset(elbowX, elbowY), isArm = true, torsoJoint = true)
                    drawHumanLimb(Offset(elbowX, elbowY), Offset(shoulderX + 5.dp.toPx(), shoulderY - 5.dp.toPx()), isArm = true)

                    listOf(
                        Offset(kneeX, kneeY), Offset(hipX, hipY),
                        Offset(shoulderX, shoulderY)
                    ).forEach { jt ->
                        drawCircle(color = primeColor, radius = 5.dp.toPx(), center = jt)
                    }

                    drawAthleticSneaker(Offset(footX, footY), -1f)
                    drawCharacterHead(headX, headY)
                }

                // 5. STRETCHING / RECOVERY (Harmonic meditative settle)
                else -> {
                    val stretchStretch = (sin(phase) + 1f) / 2f

                    val footFrontX = centerX - 36.dp.toPx()
                    val footFrontY = floorY - 5.dp.toPx()

                    val footBackX = centerX + 56.dp.toPx()
                    val footBackY = floorY - 12.dp.toPx()

                    val hipX = centerX + 12.dp.toPx()
                    val hipY = floorY - 34.dp.toPx() + (5.dp.toPx() * stretchStretch)

                    val shoulderX = centerX - 18.dp.toPx()
                    val shoulderY = hipY - 56.dp.toPx()

                    val headX = shoulderX - 8.dp.toPx()
                    val headY = shoulderY - 18.dp.toPx()

                    val rHandX = shoulderX
                    val rHandY = shoulderY - 45.dp.toPx()

                    val lHandX = shoulderX - 5.dp.toPx()
                    val lHandY = floorY - 10.dp.toPx()

                    val primeColor = if (isMale) AuraNeonCyan else AuraAestheticPurple
                    val wearColor = if (isMale) CosmicSlate else ObsidianBlack

                    // Deep meditative peace glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primeColor.copy(alpha = 0.14f), Color.Transparent),
                            center = Offset(hipX, hipY),
                            radius = 64.dp.toPx()
                        ),
                        center = Offset(hipX, hipY),
                        radius = 64.dp.toPx()
                    )

                    // Draw Torso connection
                    val torsoThickness = if (isMale) 15.dp.toPx() else 11.dp.toPx()
                    drawLine(
                        color = wearColor,
                        start = Offset(hipX, hipY),
                        end = Offset(shoulderX, shoulderY),
                        strokeWidth = torsoThickness,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = primeColor.copy(alpha = 0.5f),
                        start = Offset(hipX, hipY),
                        end = Offset(shoulderX, shoulderY),
                        strokeWidth = torsoThickness * 0.4f,
                        cap = StrokeCap.Round
                    )

                    // Draw limbs stretch
                    drawHumanLimb(Offset(footFrontX, footFrontY), Offset(hipX, hipY), isThigh = true)
                    drawHumanLimb(Offset(footBackX, footBackY), Offset(hipX, hipY), isThigh = true)
                    drawHumanLimb(Offset(shoulderX, shoulderY), Offset(lHandX, lHandY), isArm = true, torsoJoint = true)
                    drawHumanLimb(Offset(shoulderX, shoulderY), Offset(rHandX, rHandY), isArm = true, torsoJoint = true)

                    listOf(
                        Offset(footFrontX, footFrontY), Offset(footBackX, footBackY),
                        Offset(hipX, hipY), Offset(shoulderX, shoulderY),
                        Offset(lHandX, lHandY), Offset(rHandX, rHandY)
                    ).forEach { p ->
                        drawCircle(color = primeColor, radius = 5.dp.toPx(), center = p)
                    }

                    drawCharacterHead(headX, headY)
                }
            }
        }
    }
}
