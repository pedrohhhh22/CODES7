package com.appsdevs.popit

import androidx.compose.animation. core.EaseInOutSine
import androidx.compose. animation.core.LinearEasing
import androidx.compose.animation. core.RepeatMode
import androidx.compose.animation.core. animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx. compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation. background
import androidx. compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout. Box
import androidx.compose.foundation.layout. fillMaxSize
import androidx.compose.foundation. layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose. runtime.Composable
import androidx.compose. runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose. ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics. Brush
import androidx.compose.ui. graphics.Color
import androidx.compose.ui. draw.scale
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics. drawscope.Stroke
import androidx.compose. ui.graphics.drawscope.rotate
import androidx.compose.ui.unit. Dp
import androidx. compose.ui.unit.dp
import kotlin.math. cos
import kotlin.math.sin
import kotlin.random.Random

// ==================== GENERATED AVATARS DATA ====================

data class GeneratedAvatarData(
    val id: Int,
    val name: String,
    val isAnimated: Boolean = false
)

object GeneratedAvatars {
    val staticAvatars = listOf(
        GeneratedAvatarData(0, "Blue Orb"),
        GeneratedAvatarData(1, "Fire Spirit"),
        GeneratedAvatarData(2, "Nature Soul"),
        GeneratedAvatarData(3, "Purple Gem"),
        GeneratedAvatarData(4, "Golden Sun"),
        GeneratedAvatarData(5, "Ice Crystal"),
        GeneratedAvatarData(6, "Rose Heart"),
        GeneratedAvatarData(7, "Storm Cloud"),
        GeneratedAvatarData(8, "Emerald Eye"),
        GeneratedAvatarData(9, "Sunset Glow"),
        GeneratedAvatarData(10, "Ocean Wave"),
        GeneratedAvatarData(11, "Forest Spirit"),
        GeneratedAvatarData(12, "Lava Core"),
        GeneratedAvatarData(13, "Diamond Shine"),
        GeneratedAvatarData(14, "Neon Spark"),
        GeneratedAvatarData(15, "Mystic Moon"),
        GeneratedAvatarData(16, "Cherry Blossom"),
        GeneratedAvatarData(17, "Thunder Bolt"),
        GeneratedAvatarData(18, "Cosmic Dust"),
        GeneratedAvatarData(19, "Jade Stone")
    )

    val animatedAvatars = listOf(
        GeneratedAvatarData(100, "Pulsing Star", true),
        GeneratedAvatarData(101, "Spinning Galaxy", true),
        GeneratedAvatarData(102, "Breathing Fire", true),
        GeneratedAvatarData(103, "Flowing Water", true),
        GeneratedAvatarData(104, "Electric Pulse", true),
        GeneratedAvatarData(105, "Rainbow Cycle", true),
        GeneratedAvatarData(106, "Heartbeat", true),
        GeneratedAvatarData(107, "Orbiting Rings", true),
        GeneratedAvatarData(108, "Glowing Aura", true),
        GeneratedAvatarData(109, "Plasma Ball", true),
        GeneratedAvatarData(110, "Northern Glow", true),
        GeneratedAvatarData(111, "Flame Dance", true),
        GeneratedAvatarData(112, "Crystal Spin", true),
        GeneratedAvatarData(113, "Neon Wave", true),
        GeneratedAvatarData(114, "Starfield", true),
        GeneratedAvatarData(115, "Bubble Rise", true)
    )

    val allAvatars = staticAvatars + animatedAvatars

    fun getById(id: Int): GeneratedAvatarData = allAvatars. find { it.id == id } ?: staticAvatars[0]
    fun isAnimated(id:  Int): Boolean = getById(id).isAnimated
}

// ==================== MAIN AVATAR COMPOSABLE ====================

@Composable
fun GeneratedAvatar(
    avatarId:  Int,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp
) {
    val avatarData = GeneratedAvatars.getById(avatarId)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        when (avatarId) {
            // Static avatars
            0 -> BlueOrbAvatar()
            1 -> FireSpiritAvatar()
            2 -> NatureSoulAvatar()
            3 -> PurpleGemAvatar()
            4 -> GoldenSunAvatar()
            5 -> IceCrystalAvatar()
            6 -> RoseHeartAvatar()
            7 -> StormCloudAvatar()
            8 -> EmeraldEyeAvatar()
            9 -> SunsetGlowAvatar()
            10 -> OceanWaveAvatar()
            11 -> ForestSpiritAvatar()
            12 -> LavaCoreAvatar()
            13 -> DiamondShineAvatar()
            14 -> NeonSparkAvatar()
            15 -> MysticMoonAvatar()
            16 -> CherryBlossomAvatar()
            17 -> ThunderBoltAvatar()
            18 -> CosmicDustAvatar()
            19 -> JadeStoneAvatar()
            // Animated avatars
            100 -> PulsingStarAvatar()
            101 -> SpinningGalaxyAvatar()
            102 -> BreathingFireAvatar()
            103 -> FlowingWaterAvatar()
            104 -> ElectricPulseAvatar()
            105 -> RainbowCycleAvatar()
            106 -> HeartbeatAvatar()
            107 -> OrbitingRingsAvatar()
            108 -> GlowingAuraAvatar()
            109 -> PlasmaBallAvatar()
            110 -> NorthernGlowAvatar()
            111 -> FlameDanceAvatar()
            112 -> CrystalSpinAvatar()
            113 -> NeonWaveAvatar()
            114 -> StarfieldAvatar()
            115 -> BubbleRiseAvatar()
            else -> BlueOrbAvatar()
        }
    }
}

// ==================== STATIC AVATARS ====================

@Composable
private fun BlueOrbAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size. minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF64B5F6),
                    Color(0xFF1976D2),
                    Color(0xFF0D47A1)
                ),
                center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f)
            ),
            radius = radius,
            center = center
        )

        // Highlight
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.6f),
                    Color. Transparent
                ),
                center = Offset(center.x - radius * 0.3f, center. y - radius * 0.4f)
            ),
            radius = radius * 0.4f,
            center = Offset(center.x - radius * 0.3f, center.y - radius * 0.4f)
        )
    }
}

@Composable
private fun FireSpiritAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFEB3B),
                    Color(0xFFFF9800),
                    Color(0xFFFF5722),
                    Color(0xFFD32F2F)
                ),
                center = Offset(center.x, center.y + radius * 0.2f)
            ),
            radius = radius,
            center = center
        )

        // Flame shapes
        for (i in 0..4) {
            val angle = Math.toRadians((i * 72 - 90).toDouble())
            val flameX = center.x + cos(angle).toFloat() * radius * 0.4f
            val flameY = center. y + sin(angle).toFloat() * radius * 0.4f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF59D),
                        Color(0xFFFFB74D).copy(alpha = 0.5f),
                        Color. Transparent
                    )
                ),
                radius = radius * 0.3f,
                center = Offset(flameX, flameY)
            )
        }
    }
}

@Composable
private fun NatureSoulAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFA5D6A7),
                    Color(0xFF66BB6A),
                    Color(0xFF2E7D32)
                )
            ),
            radius = radius,
            center = center
        )

        // Leaf pattern
        for (i in 0..5) {
            val angle = Math.toRadians((i * 60).toDouble())
            val leafPath = Path().apply {
                val startX = center.x
                val startY = center.y
                val endX = center.x + cos(angle).toFloat() * radius * 0.7f
                val endY = center.y + sin(angle).toFloat() * radius * 0.7f
                val controlX = center.x + cos(angle + 0.3).toFloat() * radius * 0.5f
                val controlY = center.y + sin(angle + 0.3).toFloat() * radius * 0.5f

                moveTo(startX, startY)
                quadraticBezierTo(controlX, controlY, endX, endY)
            }

            drawPath(
                path = leafPath,
                color = Color(0xFF81C784),
                style = Stroke(width = 3f)
            )
        }
    }
}

@Composable
private fun PurpleGemAvatar() {
    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFCE93D8),
                    Color(0xFF9C27B0),
                    Color(0xFF4A148C)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size. height)
            ),
            radius = radius,
            center = center
        )

        // Gem facets
        val facetPath = Path().apply {
            moveTo(center.x, center.y - radius * 0.6f)
            lineTo(center.x + radius * 0.5f, center.y)
            lineTo(center.x, center.y + radius * 0.6f)
            lineTo(center.x - radius * 0.5f, center.y)
            close()
        }

        drawPath(
            path = facetPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.4f),
                    Color.Transparent
                )
            )
        )

        // Sparkle
        drawCircle(
            color = Color. White.copy(alpha = 0.7f),
            radius = radius * 0.1f,
            center = Offset(center.x - radius * 0.2f, center. y - radius * 0.3f)
        )
    }
}

@Composable
private fun GoldenSunAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size. minDimension / 2

        // Sun rays
        for (i in 0.. 11) {
            val angle = Math.toRadians((i * 30).toDouble())
            val innerRadius = radius * 0.5f
            val outerRadius = radius * 0.95f

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD54F),
                        Color(0xFFFFB300)
                    )
                ),
                start = Offset(
                    center.x + cos(angle).toFloat() * innerRadius,
                    center.y + sin(angle).toFloat() * innerRadius
                ),
                end = Offset(
                    center.x + cos(angle).toFloat() * outerRadius,
                    center.y + sin(angle).toFloat() * outerRadius
                ),
                strokeWidth = if (i % 2 == 0) 8f else 4f
            )
        }

        // Sun center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF59D),
                    Color(0xFFFFD700),
                    Color(0xFFFF8F00)
                )
            ),
            radius = radius * 0.5f,
            center = center
        )
    }
}

@Composable
private fun IceCrystalAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size. minDimension / 2

        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFFE1F5FE),
                    Color(0xFF81D4FA),
                    Color(0xFF0288D1)
                )
            ),
            radius = radius,
            center = center
        )

        // Crystal pattern
        for (i in 0..5) {
            val angle = Math.toRadians((i * 60).toDouble())
            val endX = center.x + cos(angle).toFloat() * radius * 0.8f
            val endY = center.y + sin(angle).toFloat() * radius * 0.8f

            drawLine(
                color = Color. White.copy(alpha = 0.8f),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 2f
            )

            // Branches
            val branchStart = 0.5f
            val branchX = center.x + cos(angle).toFloat() * radius * branchStart
            val branchY = center.y + sin(angle).toFloat() * radius * branchStart

            for (j in listOf(-30, 30)) {
                val branchAngle = angle + Math.toRadians(j. toDouble())
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(branchX, branchY),
                    end = Offset(
                        branchX + cos(branchAngle).toFloat() * radius * 0.25f,
                        branchY + sin(branchAngle).toFloat() * radius * 0.25f
                    ),
                    strokeWidth = 1.5f
                )
            }
        }
    }
}

@Composable
private fun RoseHeartAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size. minDimension / 2

        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFFF8BBD0),
                    Color(0xFFE91E63),
                    Color(0xFFC2185B)
                )
            ),
            radius = radius,
            center = center
        )

        // Heart shape
        val heartSize = radius * 0.6f
        val heartPath = Path().apply {
            moveTo(center.x, center.y + heartSize * 0.3f)
            cubicTo(
                center.x - heartSize, center.y - heartSize * 0.3f,
                center.x - heartSize * 0.5f, center.y - heartSize * 0.8f,
                center.x, center. y - heartSize * 0.3f
            )
            cubicTo(
                center.x + heartSize * 0.5f, center. y - heartSize * 0.8f,
                center.x + heartSize, center.y - heartSize * 0.3f,
                center.x, center.y + heartSize * 0.3f
            )
        }

        drawPath(
            path = heartPath,
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFFFFCDD2),
                    Color(0xFFE91E63)
                ),
                center = Offset(center.x - heartSize * 0.2f, center. y - heartSize * 0.2f)
            )
        )
    }
}

@Composable
private fun StormCloudAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF546E7A),
                    Color(0xFF37474F),
                    Color(0xFF263238)
                )
            ),
            radius = radius,
            center = center
        )

        // Cloud puffs
        val cloudCenters = listOf(
            Offset(center.x - radius * 0.3f, center.y - radius * 0.1f),
            Offset(center.x + radius * 0.2f, center.y - radius * 0.2f),
            Offset(center.x, center.y + radius * 0.1f),
            Offset(center. x - radius * 0.15f, center.y - radius * 0.3f)
        )

        cloudCenters.forEachIndexed { index, cloudCenter ->
            drawCircle(
                color = Color(0xFF78909C).copy(alpha = 0.7f),
                radius = radius * (0.25f + index * 0.05f),
                center = cloudCenter
            )
        }

        // Lightning bolt
        val boltPath = Path().apply {
            moveTo(center.x + radius * 0.1f, center.y)
            lineTo(center.x - radius * 0.05f, center.y + radius * 0.25f)
            lineTo(center.x + radius * 0.05f, center.y + radius * 0.25f)
            lineTo(center. x - radius * 0.1f, center. y + radius * 0.5f)
        }

        drawPath(
            path = boltPath,
            color = Color(0xFFFFEB3B),
            style = Stroke(width = 3f)
        )
    }
}

@Composable
private fun EmeraldEyeAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF81C784),
                    Color(0xFF4CAF50),
                    Color(0xFF1B5E20)
                )
            ),
            radius = radius,
            center = center
        )

        // Eye white
        drawOval(
            color = Color.White,
            topLeft = Offset(center.x - radius * 0.6f, center.y - radius * 0.3f),
            size = Size(radius * 1.2f, radius * 0.6f)
        )

        // Iris
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4CAF50),
                    Color(0xFF2E7D32),
                    Color(0xFF1B5E20)
                )
            ),
            radius = radius * 0.25f,
            center = center
        )

        // Pupil
        drawCircle(
            color = Color.Black,
            radius = radius * 0.12f,
            center = center
        )

        // Highlight
        drawCircle(
            color = Color.White. copy(alpha = 0.8f),
            radius = radius * 0.05f,
            center = Offset(center.x - radius * 0.08f, center.y - radius * 0.08f)
        )
    }
}

@Composable
private fun SunsetGlowAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFFFF7043),
                    Color(0xFFFF5722),
                    Color(0xFFE64A19),
                    Color(0xFF5D4037)
                )
            ),
            radius = radius,
            center = center
        )

        // Sun silhouette
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFCC80),
                    Color(0xFFFF9800)
                )
            ),
            radius = radius * 0.35f,
            center = Offset(center.x, center.y - radius * 0.1f)
        )

        // Horizon line
        drawRect(
            color = Color(0xFF3E2723).copy(alpha = 0.5f),
            topLeft = Offset(0f, center.y + radius * 0.3f),
            size = Size(size.width, radius * 0.7f)
        )
    }
}

@Composable
private fun OceanWaveAvatar() {
    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4FC3F7),
                    Color(0xFF0288D1),
                    Color(0xFF01579B)
                )
            ),
            radius = radius,
            center = center
        )

        // Waves
        for (i in 0.. 2) {
            val waveY = center.y + (i - 1) * radius * 0.3f
            val wavePath = Path().apply {
                moveTo(center.x - radius, waveY)
                for (x in 0.. 10) {
                    val xPos = center.x - radius + x * radius * 0.2f
                    val yOffset = sin(x * 0.8).toFloat() * radius * 0.1f
                    lineTo(xPos, waveY + yOffset)
                }
            }

            drawPath(
                path = wavePath,
                color = Color. White.copy(alpha = 0.4f - i * 0.1f),
                style = Stroke(width = 3f)
            )
        }
    }
}

@Composable
private fun ForestSpiritAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFC8E6C9),
                    Color(0xFF81C784),
                    Color(0xFF388E3C)
                )
            ),
            radius = radius,
            center = center
        )

        // Tree silhouette
        val treePath = Path().apply {
            moveTo(center.x, center.y - radius * 0.6f)
            lineTo(center.x - radius * 0.4f, center.y + radius * 0.2f)
            lineTo(center.x - radius * 0.15f, center.y + radius * 0.2f)
            lineTo(center.x - radius * 0.15f, center.y + radius * 0.5f)
            lineTo(center.x + radius * 0.15f, center.y + radius * 0.5f)
            lineTo(center.x + radius * 0.15f, center.y + radius * 0.2f)
            lineTo(center.x + radius * 0.4f, center.y + radius * 0.2f)
            close()
        }

        drawPath(
            path = treePath,
            color = Color(0xFF2E7D32)
        )

        // Glowing particles
        for (i in 0.. 5) {
            val particleX = center.x + cos(i * 1.2).toFloat() * radius * 0.5f
            val particleY = center. y + sin(i * 1.5).toFloat() * radius * 0.4f
            drawCircle(
                color = Color(0xFFFFF59D).copy(alpha = 0.7f),
                radius = 4f,
                center = Offset(particleX, particleY)
            )
        }
    }
}

@Composable
private fun LavaCoreAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size. height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFEB3B),
                    Color(0xFFFF9800),
                    Color(0xFFFF5722),
                    Color(0xFF8B0000)
                )
            ),
            radius = radius,
            center = center
        )

        // Lava cracks
        for (i in 0..7) {
            val angle = Math.toRadians((i * 45 + 22.5).toDouble())
            val crackPath = Path().apply {
                moveTo(center.x, center.y)
                val midX = center.x + cos(angle).toFloat() * radius * 0.4f
                val midY = center.y + sin(angle).toFloat() * radius * 0.4f
                val endX = center.x + cos(angle + 0.2).toFloat() * radius * 0.8f
                val endY = center.y + sin(angle + 0.2).toFloat() * radius * 0.8f
                lineTo(midX, midY)
                lineTo(endX, endY)
            }

            drawPath(
                path = crackPath,
                color = Color(0xFFFFEB3B).copy(alpha = 0.8f),
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
private fun DiamondShineAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size. height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFE3F2FD),
                    Color(0xFFBBDEFB),
                    Color(0xFF90CAF9),
                    Color(0xFFE3F2FD)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            radius = radius,
            center = center
        )

        // Diamond shape
        val diamondPath = Path().apply {
            moveTo(center. x, center.y - radius * 0.6f)
            lineTo(center.x + radius * 0.5f, center.y)
            lineTo(center.x, center.y + radius * 0.6f)
            lineTo(center.x - radius * 0.5f, center.y)
            close()
        }

        drawPath(
            path = diamondPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.9f),
                    Color(0xFF64B5F6).copy(alpha = 0.6f),
                    Color. White.copy(alpha = 0.7f)
                )
            )
        )

        drawPath(
            path = diamondPath,
            color = Color(0xFF1976D2).copy(alpha = 0.5f),
            style = Stroke(width = 2f)
        )

        // Sparkles
        val sparklePositions = listOf(
            Offset(center.x - radius * 0.25f, center.y - radius * 0.2f),
            Offset(center.x + radius * 0.2f, center.y + radius * 0.15f),
            Offset(center.x, center.y - radius * 0.35f)
        )

        sparklePositions.forEach { pos ->
            drawCircle(
                color = Color. White,
                radius = 3f,
                center = pos
            )
        }
    }
}

@Composable
private fun NeonSparkAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            color = Color(0xFF1A1A2E),
            radius = radius,
            center = center
        )

        // Neon rings
        val colors = listOf(
            Color(0xFFFF00FF),
            Color(0xFF00FFFF),
            Color(0xFFFFFF00)
        )

        colors.forEachIndexed { index, color ->
            drawCircle(
                color = color. copy(alpha = 0.8f),
                radius = radius * (0.7f - index * 0.15f),
                center = center,
                style = Stroke(width = 3f)
            )
        }

        // Central spark
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White,
                    Color(0xFF00FFFF),
                    Color. Transparent
                )
            ),
            radius = radius * 0.25f,
            center = center
        )
    }
}

@Composable
private fun MysticMoonAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size. minDimension / 2

        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFF1A237E),
                    Color(0xFF0D1B4A),
                    Color(0xFF050A1A)
                )
            ),
            radius = radius,
            center = center
        )

        // Moon
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF8E1),
                    Color(0xFFFFECB3),
                    Color(0xFFCFD8DC)
                ),
                center = Offset(center.x - radius * 0.1f, center.y - radius * 0.1f)
            ),
            radius = radius * 0.4f,
            center = center
        )

        // Moon craters
        drawCircle(
            color = Color(0xFFBDBDBD).copy(alpha = 0.4f),
            radius = radius * 0.08f,
            center = Offset(center.x - radius * 0.1f, center.y + radius * 0.05f)
        )
        drawCircle(
            color = Color(0xFFBDBDBD).copy(alpha = 0.3f),
            radius = radius * 0.05f,
            center = Offset(center.x + radius * 0.1f, center.y - radius * 0.1f)
        )

        // Stars
        for (i in 0..8) {
            val starX = center.x + cos(i * 0.8).toFloat() * radius * 0.75f
            val starY = center.y + sin(i * 1.1).toFloat() * radius * 0.7f
            drawCircle(
                color = Color. White.copy(alpha = 0.8f),
                radius = if (i % 2 == 0) 2f else 1.5f,
                center = Offset(starX, starY)
            )
        }
    }
}

@Composable
private fun CherryBlossomAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFCE4EC),
                    Color(0xFFF8BBD0),
                    Color(0xFFF48FB1)
                )
            ),
            radius = radius,
            center = center
        )

        // Flower petals
        for (i in 0..4) {
            val angle = Math.toRadians((i * 72 - 90).toDouble())
            val petalPath = Path().apply {
                moveTo(center.x, center.y)
                val controlX1 = center.x + cos(angle - 0.3).toFloat() * radius * 0.4f
                val controlY1 = center.y + sin(angle - 0.3).toFloat() * radius * 0.4f
                val endX = center.x + cos(angle).toFloat() * radius * 0.6f
                val endY = center.y + sin(angle).toFloat() * radius * 0.6f
                val controlX2 = center.x + cos(angle + 0.3).toFloat() * radius * 0.4f
                val controlY2 = center.y + sin(angle + 0.3).toFloat() * radius * 0.4f

                quadraticBezierTo(controlX1, controlY1, endX, endY)
                quadraticBezierTo(controlX2, controlY2, center.x, center. y)
            }

            drawPath(
                path = petalPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color. White.copy(alpha = 0.9f),
                        Color(0xFFF8BBD0)
                    ),
                    center = center
                )
            )
        }

        // Center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFEB3B),
                    Color(0xFFFFC107)
                )
            ),
            radius = radius * 0.15f,
            center = center
        )
    }
}

@Composable
private fun ThunderBoltAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF37474F),
                    Color(0xFF263238),
                    Color(0xFF1A1A2E)
                )
            ),
            radius = radius,
            center = center
        )

        // Lightning bolt
        val boltPath = Path().apply {
            moveTo(center.x + radius * 0.1f, center.y - radius * 0.6f)
            lineTo(center.x - radius * 0.15f, center.y - radius * 0.05f)
            lineTo(center.x + radius * 0.1f, center.y)
            lineTo(center.x - radius * 0.1f, center.y + radius * 0.6f)
            lineTo(center.x + radius * 0.15f, center. y + radius * 0.05f)
            lineTo(center.x - radius * 0.1f, center.y)
            close()
        }

        // Glow
        drawPath(
            path = boltPath,
            color = Color(0xFFFFEB3B).copy(alpha = 0.5f),
            style = Stroke(width = 8f)
        )

        // Core
        drawPath(
            path = boltPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFF59D),
                    Color(0xFFFFEB3B),
                    Color(0xFFFFC107)
                )
            )
        )
    }
}

@Composable
private fun CosmicDustAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size. height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2A1A4A),
                    Color(0xFF1A0A2E),
                    Color(0xFF0A0515)
                )
            ),
            radius = radius,
            center = center
        )

        // Cosmic dust particles
        for (i in 0..40) {
            val particleX = center. x + cos(i * 0.4).toFloat() * radius * (0.3f + (i % 5) * 0.12f)
            val particleY = center.y + sin(i * 0.6).toFloat() * radius * (0.3f + (i % 4) * 0.13f)

            val particleColor = when (i % 4) {
                0 -> Color(0xFFE040FB)
                1 -> Color(0xFF7C4DFF)
                2 -> Color(0xFF536DFE)
                else -> Color. White
            }

            drawCircle(
                color = particleColor. copy(alpha = 0.7f),
                radius = if (i % 3 == 0) 3f else 2f,
                center = Offset(particleX, particleY)
            )
        }

        // Central nebula
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE040FB).copy(alpha = 0.4f),
                    Color. Transparent
                )
            ),
            radius = radius * 0.4f,
            center = center
        )
    }
}

@Composable
private fun JadeStoneAvatar() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size. height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFA5D6A7),
                    Color(0xFF66BB6A),
                    Color(0xFF43A047),
                    Color(0xFF2E7D32)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            radius = radius,
            center = center
        )

        // Jade veins
        for (i in 0.. 3) {
            val veinPath = Path().apply {
                val startX = center.x - radius * 0.6f + i * radius * 0.3f
                val startY = center.y - radius * 0.5f
                moveTo(startX, startY)
                quadraticBezierTo(
                    center.x + (i - 1.5f) * radius * 0.2f,
                    center.y,
                    startX + radius * 0.2f,
                    center.y + radius * 0.6f
                )
            }

            drawPath(
                path = veinPath,
                color = Color(0xFF1B5E20).copy(alpha = 0.3f),
                style = Stroke(width = 2f)
            )
        }

        // Shine
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.5f),
                    Color. Transparent
                )
            ),
            radius = radius * 0.3f,
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.3f)
        )
    }
}

// ==================== ANIMATED AVATARS ====================

@Composable
private fun PulsingStarAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsingStar")

    val pulse by infiniteTransition. animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    val glow by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size. height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A1A2E),
                    Color(0xFF0D0D1A)
                )
            ),
            radius = radius,
            center = center
        )

        // Outer glow
        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFFFFD700).copy(alpha = glow * 0.4f),
                    Color. Transparent
                )
            ),
            radius = radius * pulse,
            center = center
        )

        // Star points
        for (i in 0.. 4) {
            val angle = Math.toRadians((i * 72 - 90).toDouble())
            val innerAngle = Math.toRadians((i * 72 - 90 + 36).toDouble())
            val outerRadius = radius * 0.5f * pulse
            val innerRadius = radius * 0.25f * pulse

            val starPath = Path().apply {
                moveTo(
                    center.x + cos(angle).toFloat() * outerRadius,
                    center.y + sin(angle).toFloat() * outerRadius
                )
                lineTo(
                    center.x + cos(innerAngle).toFloat() * innerRadius,
                    center. y + sin(innerAngle).toFloat() * innerRadius
                )
            }

            drawPath(
                path = starPath,
                color = Color(0xFFFFD700),
                style = Stroke(width = 3f)
            )
        }

        // Center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White,
                    Color(0xFFFFD700)
                )
            ),
            radius = radius * 0.15f * pulse,
            center = center
        )
    }
}

@Composable
private fun SpinningGalaxyAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "spinningGalaxy")

    val rotation by infiniteTransition. animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A0A2E),
                    Color(0xFF0A0515)
                )
            ),
            radius = radius,
            center = center
        )

        rotate(degrees = rotation, pivot = center) {
            // Spiral arms
            for (arm in 0..2) {
                val armAngle = arm * 120f
                for (i in 0..20) {
                    val distance = 10f + i * 4f
                    val angle = Math.toRadians((armAngle + i * 15f).toDouble())
                    val x = center.x + cos(angle).toFloat() * distance
                    val y = center.y + sin(angle).toFloat() * distance * 0.6f

                    val starColor = when (arm) {
                        0 -> Color(0xFFE040FB)
                        1 -> Color(0xFF7C4DFF)
                        else -> Color(0xFF536DFE)
                    }

                    if (x in 0f..size.width && y in 0f..size.height) {
                        drawCircle(
                            color = starColor.copy(alpha = 1f - i / 25f),
                            radius = 3f - i * 0.1f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        // Central core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White,
                    Color(0xFFE040FB),
                    Color. Transparent
                )
            ),
            radius = radius * 0.2f,
            center = center
        )
    }
}

@Composable
private fun BreathingFireAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "breathingFire")

    val breath by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "breath"
    )

    val flicker by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(200, easing = LinearEasing), RepeatMode.Reverse),
        label = "flicker"
    )

    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFEB3B).copy(alpha = breath),
                    Color(0xFFFF9800),
                    Color(0xFFFF5722),
                    Color(0xFF8B0000)
                ),
                center = Offset(center.x, center.y + radius * 0.3f)
            ),
            radius = radius,
            center = center
        )

        // Flickering flames
        for (i in 0..5) {
            val flameX = center.x + (i - 2.5f) * radius * 0.25f
            val flameHeight = radius * (0.4f + breath * 0.2f + flicker * 0.1f * if (i % 2 == 0) 1 else -1)

            val flamePath = Path().apply {
                moveTo(flameX - radius * 0.08f, center.y + radius * 0.3f)
                quadraticBezierTo(
                    flameX, center.y - flameHeight,
                    flameX + radius * 0.08f, center. y + radius * 0.3f
                )
            }

            drawPath(
                path = flamePath,
                brush = Brush. verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF59D),
                        Color(0xFFFF9800).copy(alpha = 0.7f)
                    )
                )
            )
        }
    }
}

@Composable
private fun FlowingWaterAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "flowingWater")

    val wave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )

    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4FC3F7),
                    Color(0xFF0288D1),
                    Color(0xFF01579B)
                )
            ),
            radius = radius,
            center = center
        )

        // Waves
        for (i in 0.. 3) {
            val waveY = center.y + (i - 1.5f) * radius * 0.25f
            val wavePath = Path().apply {
                moveTo(center.x - radius, waveY)
                for (x in 0..20) {
                    val xPos = center.x - radius + x * radius * 0.1f
                    val phase = wave * Math.PI * 2 + x * 0.5 + i
                    val yOffset = sin(phase).toFloat() * radius * 0.08f
                    lineTo(xPos, waveY + yOffset)
                }
            }

            drawPath(
                path = wavePath,
                color = Color. White.copy(alpha = 0.4f - i * 0.08f),
                style = Stroke(width = 2f)
            )
        }

        // Bubbles
        for (i in 0.. 5) {
            val bubbleX = center.x + cos(i * 1.3).toFloat() * radius * 0.4f
            val bubbleY = center. y + ((wave + i * 0.15f) % 1f - 0.5f) * radius * 1.5f

            if (bubbleY > center.y - radius * 0.8f && bubbleY < center.y + radius * 0.8f) {
                drawCircle(
                    color = Color. White.copy(alpha = 0.5f),
                    radius = 4f,
                    center = Offset(bubbleX, bubbleY)
                )
            }
        }
    }
}

@Composable
private fun ElectricPulseAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "electricPulse")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse"
    )

    val flash by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Reverse),
        label = "flash"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            color = Color(0xFF1A1A2E),
            radius = radius,
            center = center
        )

        // Electric arcs
        for (i in 0.. 5) {
            val startAngle = Math.toRadians((i * 60 + pulse * 360).toDouble())
            val endAngle = Math.toRadians((i * 60 + 30 + pulse * 360).toDouble())

            val arcPath = Path().apply {
                moveTo(
                    center.x + cos(startAngle).toFloat() * radius * 0.3f,
                    center.y + sin(startAngle).toFloat() * radius * 0.3f
                )
                quadraticBezierTo(
                    center.x + cos(startAngle + 0.26).toFloat() * radius * 0.6f,
                    center.y + sin(startAngle + 0.26).toFloat() * radius * 0.6f,
                    center.x + cos(endAngle).toFloat() * radius * 0.7f,
                    center.y + sin(endAngle).toFloat() * radius * 0.7f
                )
            }

            val arcAlpha = if ((i + (pulse * 6).toInt()) % 2 == 0) 0.9f else 0.4f

            drawPath(
                path = arcPath,
                color = Color(0xFF00FFFF).copy(alpha = arcAlpha),
                style = Stroke(width = 2f)
            )
        }

        // Central orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.5f + flash * 0.5f),
                    Color(0xFF00FFFF).copy(alpha = 0.3f),
                    Color. Transparent
                )
            ),
            radius = radius * 0.3f,
            center = center
        )

        // Pulsing rings
        for (i in 0..2) {
            val ringRadius = radius * ((pulse + i * 0.33f) % 1f)
            val ringAlpha = 1f - (pulse + i * 0.33f) % 1f

            drawCircle(
                color = Color(0xFF00FFFF).copy(alpha = ringAlpha * 0.5f),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
private fun RainbowCycleAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "rainbowCycle")

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "colorShift"
    )

    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        val rainbowColors = listOf(
            Color(0xFFFF0000),
            Color(0xFFFF7F00),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF0000FF),
            Color(0xFF4B0082),
            Color(0xFF9400D3)
        )

        // Rainbow rings
        for (i in 0..6) {
            val colorIndex = ((i + (colorShift * 7).toInt()) % 7)
            val ringRadius = radius * (1f - i * 0.12f)

            drawCircle(
                color = rainbowColors[colorIndex],
                radius = ringRadius,
                center = center,
                style = Stroke(width = radius * 0.1f)
            )
        }

        // White center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color. White.copy(alpha = 0.7f)
                )
            ),
            radius = radius * 0.2f,
            center = center
        )
    }
}

@Composable
private fun HeartbeatAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")

    val beat by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "beat"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A0A1A),
                    Color(0xFF0D050D)
                )
            ),
            radius = radius,
            center = center
        )

        // Heart glow
        val heartSize = radius * 0.5f * beat

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE91E63).copy(alpha = 0.4f * beat),
                    Color. Transparent
                )
            ),
            radius = heartSize * 1.5f,
            center = center
        )

        // Heart shape
        val heartPath = Path().apply {
            moveTo(center.x, center.y + heartSize * 0.4f)
            cubicTo(
                center.x - heartSize, center.y - heartSize * 0.2f,
                center.x - heartSize * 0.5f, center.y - heartSize * 0.8f,
                center.x, center.y - heartSize * 0.2f
            )
            cubicTo(
                center.x + heartSize * 0.5f, center.y - heartSize * 0.8f,
                center.x + heartSize, center.y - heartSize * 0.2f,
                center.x, center.y + heartSize * 0.4f
            )
        }

        drawPath(
            path = heartPath,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF48FB1),
                    Color(0xFFE91E63),
                    Color(0xFFC2185B)
                ),
                center = Offset(center.x - heartSize * 0.2f, center.y - heartSize * 0.2f)
            )
        )
    }
}

@Composable
private fun OrbitingRingsAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbitingRings")

    val rotation by infiniteTransition. animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A237E),
                    Color(0xFF0D1B4A)
                )
            ),
            radius = radius,
            center = center
        )

        // Orbiting rings
        val ringColors = listOf(
            Color(0xFF64B5F6),
            Color(0xFFE040FB),
            Color(0xFF69F0AE)
        )

        ringColors.forEachIndexed { index, color ->
            rotate(degrees = rotation + index * 60f, pivot = center) {
                drawOval(
                    color = color. copy(alpha = 0.7f),
                    topLeft = Offset(center.x - radius * 0.7f, center.y - radius * 0.3f),
                    size = Size(radius * 1.4f, radius * 0.6f),
                    style = Stroke(width = 2f)
                )
            }
        }

        // Central planet
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4FC3F7),
                    Color(0xFF0288D1)
                ),
                center = Offset(center.x - radius * 0.1f, center.y - radius * 0.1f)
            ),
            radius = radius * 0.25f,
            center = center
        )
    }
}

@Composable
private fun GlowingAuraAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "glowingAura")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    val colorPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "colorPhase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Aura layers
        for (i in 0..4) {
            val layerRadius = radius * (0.4f + i * 0.15f) * pulse
            val hue = (colorPhase + i * 0.1f) % 1f

            val auraColor = when {
                hue < 0.33f -> Color(0xFF7C4DFF)
                hue < 0.66f -> Color(0xFFE040FB)
                else -> Color(0xFF00BCD4)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        auraColor. copy(alpha = 0.4f - i * 0.08f),
                        Color. Transparent
                    )
                ),
                radius = layerRadius,
                center = center
            )
        }

        // Core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFE040FB)
                )
            ),
            radius = radius * 0.2f,
            center = center
        )
    }
}

@Composable
private fun PlasmaBallAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "plasmaBall")

    val arcRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "arcRotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2A0A4A),
                    Color(0xFF1A0033)
                )
            ),
            radius = radius,
            center = center
        )

        // Plasma arcs
        for (i in 0.. 7) {
            val startAngle = Math.toRadians((arcRotation + i * 45).toDouble())
            val arcLength = radius * 0.6f * pulse

            val arcPath = Path().apply {
                moveTo(center. x, center.y)
                val controlAngle = startAngle + 0.3
                val endAngle = startAngle + 0.5
                quadraticBezierTo(
                    center.x + cos(controlAngle).toFloat() * arcLength * 0.7f,
                    center.y + sin(controlAngle).toFloat() * arcLength * 0.7f,
                    center.x + cos(endAngle).toFloat() * arcLength,
                    center. y + sin(endAngle).toFloat() * arcLength
                )
            }

            drawPath(
                path = arcPath,
                color = Color(0xFFE040FB).copy(alpha = 0.8f),
                style = Stroke(width = 2f)
            )

            // Arc tip
            drawCircle(
                color = Color. White.copy(alpha = pulse),
                radius = 3f,
                center = Offset(
                    center.x + cos(startAngle + 0.5).toFloat() * arcLength,
                    center. y + sin(startAngle + 0.5).toFloat() * arcLength
                )
            )
        }

        // Central orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White,
                    Color(0xFFE040FB),
                    Color(0xFF9C27B0)
                )
            ),
            radius = radius * 0.15f * pulse,
            center = center
        )
    }
}

@Composable
private fun NorthernGlowAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "northernGlow")

    val wave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave"
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "colorShift"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF001529),
                    Color(0xFF000A14)
                )
            ),
            radius = radius,
            center = center
        )

        // Aurora waves
        for (i in 0..4) {
            val waveY = center.y - radius * 0.3f + i * radius * 0.15f
            val waveOffset = wave * 10f * if (i % 2 == 0) 1 else -1

            val auroraColor = when ((i + (colorShift * 3).toInt()) % 3) {
                0 -> Color(0xFF00E676)
                1 -> Color(0xFF69F0AE)
                else -> Color(0xFF00BFA5)
            }

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color. Transparent,
                        auroraColor. copy(alpha = 0.4f - i * 0.07f),
                        Color. Transparent
                    )
                ),
                topLeft = Offset(0f, waveY + waveOffset),
                size = Size(size.width, radius * 0.2f)
            )
        }

        // Stars
        for (i in 0..10) {
            val starX = center.x + cos(i * 0.7).toFloat() * radius * 0.7f
            val starY = center.y + sin(i * 0.9).toFloat() * radius * 0.6f - radius * 0.2f

            drawCircle(
                color = Color. White.copy(alpha = 0.7f),
                radius = 1.5f,
                center = Offset(starX, starY)
            )
        }
    }
}

@Composable
private fun FlameDanceAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "flameDance")

    val dance by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "dance"
    )

    Canvas(modifier = Modifier. fillMaxSize()) {
        val center = Offset(size. width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A0000),
                    Color(0xFF1A0000)
                )
            ),
            radius = radius,
            center = center
        )

        // Dancing flames
        for (i in 0.. 6) {
            val flameX = center.x + (i - 3) * radius * 0.2f
            val phase = (dance + i * 0.15f) % 1f
            val flameHeight = radius * (0.5f + sin(phase * Math.PI * 2).toFloat() * 0.15f)
            val sway = sin((phase + i * 0.2f) * Math.PI * 4).toFloat() * radius * 0.08f

            val flamePath = Path().apply {
                moveTo(flameX - radius * 0.06f, center.y + radius * 0.4f)
                quadraticBezierTo(
                    flameX + sway, center.y - flameHeight + radius * 0.4f,
                    flameX + radius * 0.06f, center.y + radius * 0.4f
                )
            }

            val flameColor = if (i % 2 == 0) {
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF59D), Color(0xFFFF9800))
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFCC80), Color(0xFFFF5722))
                )
            }

            drawPath(
                path = flamePath,
                brush = flameColor
            )
        }
    }
}

@Composable
private fun CrystalSpinAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "crystalSpin")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "shimmer"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFF1A1A3E),
                    Color(0xFF0D0D2A)
                )
            ),
            radius = radius,
            center = center
        )

        rotate(degrees = rotation, pivot = center) {
            // Crystal facets
            for (i in 0..5) {
                val angle = Math.toRadians((i * 60).toDouble())
                val nextAngle = Math. toRadians(((i + 1) * 60).toDouble())

                val crystalPath = Path().apply {
                    moveTo(center.x, center.y)
                    lineTo(
                        center.x + cos(angle).toFloat() * radius * 0.7f,
                        center.y + sin(angle).toFloat() * radius * 0.7f
                    )
                    lineTo(
                        center.x + cos(nextAngle).toFloat() * radius * 0.7f,
                        center.y + sin(nextAngle).toFloat() * radius * 0.7f
                    )
                    close()
                }

                val facetColor = if (i % 2 == 0) {
                    Color(0xFF00BCD4).copy(alpha = 0.6f + shimmer * 0.2f)
                } else {
                    Color(0xFF80DEEA).copy(alpha = 0.4f + shimmer * 0.3f)
                }

                drawPath(
                    path = crystalPath,
                    color = facetColor
                )

                drawPath(
                    path = crystalPath,
                    color = Color.White.copy(alpha = 0.3f),
                    style = Stroke(width = 1f)
                )
            }
        }

        // Central glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = shimmer),
                    Color(0xFF00BCD4).copy(alpha = 0.3f),
                    Color. Transparent
                )
            ),
            radius = radius * 0.25f,
            center = center
        )
    }
}

@Composable
private fun NeonWaveAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "neonWave")

    val wave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "wave"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size. minDimension / 2

        drawCircle(
            color = Color(0xFF0A0A0A),
            radius = radius,
            center = center
        )

        // Neon wave lines
        val waveColors = listOf(
            Color(0xFFFF00FF),
            Color(0xFF00FFFF),
            Color(0xFFFFFF00)
        )

        waveColors.forEachIndexed { index, color ->
            val waveY = center.y + (index - 1) * radius * 0.25f
            val wavePath = Path().apply {
                moveTo(center.x - radius, waveY)
                for (x in 0..20) {
                    val xPos = center.x - radius + x * radius * 0.1f
                    val phase = wave * Math.PI * 2 + x * 0.5 + index
                    val yOffset = sin(phase).toFloat() * radius * 0.12f
                    lineTo(xPos, waveY + yOffset)
                }
            }

            // Glow
            drawPath(
                path = wavePath,
                color = color.copy(alpha = 0.3f),
                style = Stroke(width = 6f)
            )

            // Core
            drawPath(
                path = wavePath,
                color = color,
                style = Stroke(width = 2f)
            )
        }
    }
}

@Composable
private fun StarfieldAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "starfield")

    val warp by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "warp"
    )

    val stars = remember {
        List(40) {
            Triple(
                Random. nextFloat() * 360f,  // angle
                Random.nextFloat(),  // distance ratio
                Random.nextFloat() * 0.5f + 0.5f  // speed
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size. height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush. radialGradient(
                colors = listOf(
                    Color(0xFF0A0A1A),
                    Color(0xFF000005)
                )
            ),
            radius = radius,
            center = center
        )

        // Warp stars
        stars.forEach { (angleDeg, distRatio, speed) ->
            val angle = Math.toRadians(angleDeg. toDouble())
            val distance = ((distRatio + warp * speed) % 1f) * radius * 0.9f

            if (distance > radius * 0.1f) {
                val x = center.x + cos(angle).toFloat() * distance
                val y = center.y + sin(angle).toFloat() * distance

                val streakLength = distance * 0.15f
                val startX = center.x + cos(angle).toFloat() * (distance - streakLength)
                val startY = center.y + sin(angle).toFloat() * (distance - streakLength)

                val alpha = (distance / radius).coerceIn(0.2f, 1f)

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color. Transparent,
                            Color. White.copy(alpha = alpha)
                        ),
                        start = Offset(startX, startY),
                        end = Offset(x, y)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(x, y),
                    strokeWidth = 1.5f
                )
            }
        }

        // Central glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.5f),
                    Color. Transparent
                )
            ),
            radius = radius * 0.15f,
            center = center
        )
    }
}

@Composable
private fun BubbleRiseAvatar() {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleRise")

    val rise by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "rise"
    )

    val bubbles = remember {
        List(15) {
            Triple(
                Random. nextFloat(),  // x position
                Random.nextFloat(),  // start y offset
                Random.nextFloat() * 8f + 4f  // size
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4FC3F7),
                    Color(0xFF0288D1),
                    Color(0xFF01579B)
                )
            ),
            radius = radius,
            center = center
        )

        // Rising bubbles
        bubbles.forEach { (xRatio, yOffset, bubbleSize) ->
            val x = center.x - radius * 0.7f + xRatio * radius * 1.4f
            val progress = (rise + yOffset) % 1f
            val y = center.y + radius * 0.8f - progress * radius * 1.8f

            if (y > center.y - radius * 0.9f && y < center.y + radius * 0.9f) {
                val wobble = sin(progress * Math. PI * 4).toFloat() * 5f
                val alpha = if (progress > 0.8f) (1f - progress) * 5f else 0.7f

                // Bubble
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color. White.copy(alpha = alpha * 0.3f),
                            Color(0xFF81D4FA).copy(alpha = alpha * 0.5f),
                            Color. Transparent
                        )
                    ),
                    radius = bubbleSize,
                    center = Offset(x + wobble, y)
                )

                // Highlight
                drawCircle(
                    color = Color.White. copy(alpha = alpha * 0.7f),
                    radius = bubbleSize * 0.3f,
                    center = Offset(x + wobble - bubbleSize * 0.3f, y - bubbleSize * 0.3f)
                )
            }
        }
    }
}

// ==================== AVATAR THUMBNAIL ====================

@Composable
fun GeneratedAvatarThumbnail(
    avatarId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier:  Modifier = Modifier
) {
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = androidx.compose.animation. core.spring(dampingRatio = 0.6f),
        label = "scale"
    )

    Box(
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color. White. copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        GeneratedAvatar(
            avatarId = avatarId,
            modifier = Modifier.fillMaxSize()
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment. BottomEnd)
                    . size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
                    .border(1.dp, Color. White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    "✓",
                    fontSize = 10.sp,
                    color = Color. White,
                    fontWeight = androidx.compose.ui. text.font.FontWeight.Bold
                )
            }
        }
    }
}