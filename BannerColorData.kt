package com.appsdevs.popit

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core. FastOutSlowInEasing
import androidx. compose.animation.core.LinearEasing
import androidx.compose.animation. core.RepeatMode
import androidx.compose.animation. core.animateFloat
import androidx.compose.animation.core. animateFloatAsState
import androidx. compose.animation.core.infiniteRepeatable
import androidx.compose. animation.core.rememberInfiniteTransition
import androidx. compose.animation.core.spring
import androidx.compose.animation.core. tween
import androidx.compose.foundation. Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose. foundation.clickable
import androidx. compose.foundation.layout. Box
import androidx.compose.foundation.layout. fillMaxSize
import androidx.compose.foundation. layout.height
import androidx.compose.foundation.layout. offset
import androidx.compose.foundation.layout. padding
import androidx.compose.foundation.layout. size
import androidx. compose.foundation.layout.width
import androidx.compose.foundation. shape.CircleShape
import androidx.compose.foundation. shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime. Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime. remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose. ui.draw.scale
import androidx.compose.ui.geometry. CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry. Size
import androidx.compose.ui.graphics. Brush
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. graphics.Path
import androidx.compose.ui.graphics. drawscope. Stroke
import androidx. compose.ui.graphics.drawscope. rotate
import androidx.compose.ui.layout.ContentScale
import androidx. compose.ui.res.painterResource
import androidx.compose. ui.text.font.FontWeight
import androidx.compose.ui.unit. Dp
import androidx. compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ==================== BANNER DATA ====================

data class BannerColor(
    val id: Int,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val isAnimated: Boolean = false
)

object BannerColors {
    val staticColors = listOf(
        // Originales
        BannerColor(0, "Royal Purple", Color(0xFF6A1B9A), Color(0xFF9C27B0), Color(0xFFE1BEE7)),
        BannerColor(1, "Ocean Blue", Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFFBBDEFB)),
        BannerColor(2, "Forest Green", Color(0xFF1B5E20), Color(0xFF388E3C), Color(0xFFC8E6C9)),
        BannerColor(3, "Sunset Orange", Color(0xFFE65100), Color(0xFFFF6D00), Color(0xFFFFE0B2)),
        BannerColor(4, "Crimson Red", Color(0xFFB71C1C), Color(0xFFD32F2F), Color(0xFFFFCDD2)),
        BannerColor(5, "Golden Yellow", Color(0xFFF57F17), Color(0xFFFFD700), Color(0xFFFFF9C4)),
        BannerColor(6, "Midnight Black", Color(0xFF212121), Color(0xFF424242), Color(0xFF757575)),
        BannerColor(7, "Rose Pink", Color(0xFFAD1457), Color(0xFFE91E63), Color(0xFFF8BBD9)),
        BannerColor(8, "Teal Wave", Color(0xFF00695C), Color(0xFF00897B), Color(0xFFB2DFDB)),
        BannerColor(9, "Royal Gold", Color(0xFFFF8F00), Color(0xFFFFB300), Color(0xFFFFECB3)),
        // Nuevos estáticos
        BannerColor(10, "Lavender Dream", Color(0xFF7E57C2), Color(0xFFB39DDB), Color(0xFFEDE7F6)),
        BannerColor(11, "Mint Fresh", Color(0xFF26A69A), Color(0xFF80CBC4), Color(0xFFE0F2F1)),
        BannerColor(12, "Cherry Blossom", Color(0xFFEC407A), Color(0xFFF48FB1), Color(0xFFFCE4EC)),
        BannerColor(13, "Deep Ocean", Color(0xFF1A237E), Color(0xFF3949AB), Color(0xFFC5CAE9)),
        BannerColor(14, "Amber Glow", Color(0xFFFF6F00), Color(0xFFFFCA28), Color(0xFFFFF8E1)),
        BannerColor(15, "Slate Gray", Color(0xFF37474F), Color(0xFF607D8B), Color(0xFFCFD8DC)),
        BannerColor(16, "Coral Reef", Color(0xFFFF7043), Color(0xFFFF8A65), Color(0xFFFBE9E7)),
        BannerColor(17, "Indigo Night", Color(0xFF283593), Color(0xFF5C6BC0), Color(0xFFC5CAE9)),
        BannerColor(18, "Emerald City", Color(0xFF00695C), Color(0xFF4DB6AC), Color(0xFFB2DFDB)),
        BannerColor(19, "Burgundy Wine", Color(0xFF6D1B3C), Color(0xFF9C2858), Color(0xFFE8B4C8)),
        BannerColor(20, "Steel Blue", Color(0xFF455A64), Color(0xFF78909C), Color(0xFFECEFF1)),
        BannerColor(21, "Peach Sunset", Color(0xFFFF8A65), Color(0xFFFFAB91), Color(0xFFFFF3E0)),
        BannerColor(22, "Navy Commander", Color(0xFF0D1B2A), Color(0xFF1B3A5F), Color(0xFF415A77)),
        BannerColor(23, "Plum Velvet", Color(0xFF4A148C), Color(0xFF7B1FA2), Color(0xFFCE93D8)),
        BannerColor(24, "Bronze Medal", Color(0xFF795548), Color(0xFFA1887F), Color(0xFFD7CCC8)),
        BannerColor(25, "Ice Blue", Color(0xFFB3E5FC), Color(0xFF81D4FA), Color(0xFFE1F5FE)),
        BannerColor(26, "Olive Grove", Color(0xFF558B2F), Color(0xFF8BC34A), Color(0xFFDCEDC8)),
        BannerColor(27, "Magenta Pop", Color(0xFFC2185B), Color(0xFFE91E63), Color(0xFFF8BBD0)),
        BannerColor(28, "Charcoal Smoke", Color(0xFF263238), Color(0xFF455A64), Color(0xFF90A4AE)),
        BannerColor(29, "Turquoise Gem", Color(0xFF00838F), Color(0xFF00ACC1), Color(0xFFB2EBF2))
    )

    val animatedBanners = listOf(
        // Originales
        BannerColor(100, "Space Animation", Color(0xFF0D0D2B), Color(0xFF1A1A4A), Color(0xFF4A90D9), isAnimated = true),
        BannerColor(101, "Aurora Borealis", Color(0xFF1A237E), Color(0xFF4CAF50), Color(0xFF00BCD4), isAnimated = true),
        BannerColor(102, "Fire Storm", Color(0xFF8B0000), Color(0xFFFF4500), Color(0xFFFFD700), isAnimated = true),
        BannerColor(103, "Neon Pulse", Color(0xFF1A1A2E), Color(0xFFFF00FF), Color(0xFF00FFFF), isAnimated = true),
        BannerColor(104, "Galaxy Swirl", Color(0xFF1A0A2E), Color(0xFF4A148C), Color(0xFFE040FB), isAnimated = true),
        BannerColor(105, "Ocean Waves", Color(0xFF01579B), Color(0xFF0288D1), Color(0xFF4FC3F7), isAnimated = true),
        BannerColor(106, "Rainbow Flow", Color(0xFFFF0000), Color(0xFF00FF00), Color(0xFF0000FF), isAnimated = true),
        BannerColor(107, "Electric Storm", Color(0xFF1A1A2E), Color(0xFFFFEB3B), Color(0xFF2196F3), isAnimated = true),
        // Nuevos animados
        BannerColor(108, "Lava Flow", Color(0xFF4A0000), Color(0xFFFF3D00), Color(0xFFFFAB00), isAnimated = true),
        BannerColor(109, "Cyber Grid", Color(0xFF0A0A0A), Color(0xFF00FF41), Color(0xFF39FF14), isAnimated = true),
        BannerColor(110, "Plasma Sphere", Color(0xFF1A0033), Color(0xFF9C27B0), Color(0xFFE040FB), isAnimated = true),
        BannerColor(111, "Northern Lights", Color(0xFF001529), Color(0xFF00E676), Color(0xFF69F0AE), isAnimated = true),
        BannerColor(112, "Sunset Beach", Color(0xFFFF6B35), Color(0xFFFF8C42), Color(0xFFFFC947), isAnimated = true),
        BannerColor(113, "Matrix Rain", Color(0xFF000000), Color(0xFF00FF00), Color(0xFF003300), isAnimated = true),
        BannerColor(114, "Crystal Cave", Color(0xFF1A1A3E), Color(0xFF00BCD4), Color(0xFF80DEEA), isAnimated = true),
        BannerColor(115, "Toxic Waste", Color(0xFF1A2E1A), Color(0xFF76FF03), Color(0xFFB2FF59), isAnimated = true),
        BannerColor(116, "Blood Moon", Color(0xFF1A0A0A), Color(0xFFB71C1C), Color(0xFFFF5252), isAnimated = true),
        BannerColor(117, "Holographic", Color(0xFF2A2A3E), Color(0xFFFF00FF), Color(0xFF00FFFF), isAnimated = true),
        BannerColor(118, "Starfield", Color(0xFF000011), Color(0xFF1A1A4A), Color(0xFFFFFFFF), isAnimated = true),
        BannerColor(119, "Heartbeat", Color(0xFF1A0A1A), Color(0xFFE91E63), Color(0xFFF48FB1), isAnimated = true),
        BannerColor(120, "Ice Crystal", Color(0xFF0A1A2E), Color(0xFF4FC3F7), Color(0xFFE1F5FE), isAnimated = true),
        BannerColor(121, "Golden Particles", Color(0xFF1A1500), Color(0xFFFFD700), Color(0xFFFFF59D), isAnimated = true),
        BannerColor(122, "Vortex Portal", Color(0xFF0D0D1A), Color(0xFF7C4DFF), Color(0xFFB388FF), isAnimated = true),
        BannerColor(123, "Fireflies", Color(0xFF0A1A0A), Color(0xFFFFEB3B), Color(0xFFFFF176), isAnimated = true),
        BannerColor(124, "Nebula Cloud", Color(0xFF1A0A2E), Color(0xFFAA00FF), Color(0xFFEA80FC), isAnimated = true),
        BannerColor(125, "Thunder Strike", Color(0xFF0A0A1A), Color(0xFF2196F3), Color(0xFFFFEB3B), isAnimated = true),
        BannerColor(126, "Sakura Petals", Color(0xFF2E1A2A), Color(0xFFF8BBD0), Color(0xFFFFFFFF), isAnimated = true),
        BannerColor(127, "Underwater", Color(0xFF001F3F), Color(0xFF0074D9), Color(0xFF7FDBFF), isAnimated = true)
    )

    val availableColors = staticColors + animatedBanners

    fun getById(id: Int): BannerColor = availableColors.find { it.id == id } ?: staticColors[0]
    fun isAnimated(id:  Int): Boolean = getById(id).isAnimated
}

// ==================== MAIN BANNER COMPOSABLE ====================

@Composable
fun HorizontalProfileBanner(
    bannerColorId: Int,
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height: Dp = 180.dp
) {
    val bannerColor = BannerColors.getById(bannerColorId)

    if (bannerColor. isAnimated) {
        when (bannerColorId) {
            100 -> SpaceAnimationBanner(modifier, width, height)
            101 -> AuroraBorealisBanner(modifier, width, height)
            102 -> FireStormBanner(modifier, width, height)
            103 -> NeonPulseBanner(modifier, width, height)
            104 -> GalaxySwirlBanner(modifier, width, height)
            105 -> OceanWavesBanner(modifier, width, height)
            106 -> RainbowFlowBanner(modifier, width, height)
            107 -> ElectricStormBanner(modifier, width, height)
            108 -> LavaFlowBanner(modifier, width, height)
            109 -> CyberGridBanner(modifier, width, height)
            110 -> PlasmaSphereBanner(modifier, width, height)
            111 -> NorthernLightsBanner(modifier, width, height)
            112 -> SunsetBeachBanner(modifier, width, height)
            113 -> MatrixRainBanner(modifier, width, height)
            114 -> CrystalCaveBanner(modifier, width, height)
            115 -> ToxicWasteBanner(modifier, width, height)
            116 -> BloodMoonBanner(modifier, width, height)
            117 -> HolographicBanner(modifier, width, height)
            118 -> StarfieldBanner(modifier, width, height)
            119 -> HeartbeatBanner(modifier, width, height)
            120 -> IceCrystalBanner(modifier, width, height)
            121 -> GoldenParticlesBanner(modifier, width, height)
            122 -> VortexPortalBanner(modifier, width, height)
            123 -> FirefliesBanner(modifier, width, height)
            124 -> NebulaCloudBanner(modifier, width, height)
            125 -> ThunderStrikeBanner(modifier, width, height)
            126 -> SakuraPetalsBanner(modifier, width, height)
            127 -> UnderwaterBanner(modifier, width, height)
            else -> SpaceAnimationBanner(modifier, width, height)
        }
    } else {
        StaticHorizontalBanner(bannerColor, modifier, width, height)
    }
}

// ==================== STATIC BANNER ====================

@Composable
private fun StaticHorizontalBanner(
    bannerColor:  BannerColor,
    modifier:  Modifier = Modifier,
    width:  Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "staticBanner")
    val shimmerOffset by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "shimmer"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    bannerColor.primaryColor,
                    bannerColor.secondaryColor,
                    bannerColor.primaryColor. copy(alpha = 0.9f)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        val shimmerX = canvasWidth * shimmerOffset
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color. Transparent,
                    Color.White. copy(alpha = 0.2f),
                    Color. Transparent
                ),
                startX = shimmerX - canvasWidth * 0.4f,
                endX = shimmerX + canvasWidth * 0.4f
            ),
            size = Size(canvasWidth, canvasHeight)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    bannerColor.accentColor. copy(alpha = 0.5f),
                    bannerColor.secondaryColor.copy(alpha = 0.3f),
                    bannerColor.accentColor.copy(alpha = 0.5f)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== SPACE ANIMATION BANNER ====================

private data class Star(
    val x: Float,
    val y:  Float,
    val size: Float,
    val speed: Float,
    val alpha: Float,
    val twinkleSpeed: Float
)

@Composable
fun SpaceAnimationBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height: Dp = 180.dp
) {
    val stars = remember {
        List(70) {
            Star(
                x = Random. nextFloat(),
                y = Random.nextFloat(),
                size = Random. nextFloat() * 3f + 1f,
                speed = Random. nextFloat() * 0.003f + 0.001f,
                alpha = Random.nextFloat() * 0.5f + 0.5f,
                twinkleSpeed = Random.nextFloat() * 1500f + 800f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "space")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val nebulaOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "nebula"
    )

    val shootingStarProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shooting"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF050510),
                    Color(0xFF0D0D2B),
                    Color(0xFF1A1A4A),
                    Color(0xFF0D0D2B)
                )
            ),
            cornerRadius = CornerRadius(24.dp. toPx())
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF4A90D9).copy(alpha = 0.25f * nebulaOffset),
                    Color(0xFF9B59B6).copy(alpha = 0.15f),
                    Color. Transparent
                )
            ),
            radius = canvasWidth * 0.5f,
            center = Offset(canvasWidth * (0.25f + nebulaOffset * 0.15f), canvasHeight * 0.4f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE91E63).copy(alpha = 0.2f * (1f - nebulaOffset)),
                    Color(0xFF9C27B0).copy(alpha = 0.1f),
                    Color.Transparent
                )
            ),
            radius = canvasWidth * 0.35f,
            center = Offset(canvasWidth * (0.75f - nebulaOffset * 0.1f), canvasHeight * 0.6f)
        )

        stars.forEach { star ->
            val twinkle = (sin(time / star.twinkleSpeed * Math.PI).toFloat() + 1f) / 2f
            val currentAlpha = star. alpha * (0.4f + twinkle * 0.6f)
            val adjustedX = (star.x + time * star.speed) % 1f

            drawCircle(
                color = Color.White.copy(alpha = currentAlpha),
                radius = star. size,
                center = Offset(adjustedX * canvasWidth, star.y * canvasHeight)
            )

            if (star.size > 2f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color. White.copy(alpha = currentAlpha * 0.4f),
                            Color. Transparent
                        )
                    ),
                    radius = star.size * 4f,
                    center = Offset(adjustedX * canvasWidth, star. y * canvasHeight)
                )
            }
        }

        if (shootingStarProgress < 0.5f) {
            val progress = shootingStarProgress / 0.5f
            val startX = canvasWidth * 0.05f
            val startY = canvasHeight * 0.15f
            val endX = canvasWidth * 0.95f
            val endY = canvasHeight * 0.85f

            val currentX = startX + (endX - startX) * progress
            val currentY = startY + (endY - startY) * progress

            val alpha = if (progress < 0.15f) progress / 0.15f
            else if (progress > 0.4f) (0.5f - progress) / 0.1f
            else 1f

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color. Transparent,
                        Color.White.copy(alpha = alpha * 0.6f),
                        Color.White.copy(alpha = alpha)
                    )
                ),
                start = Offset(currentX - 50f, currentY - 30f),
                end = Offset(currentX, currentY),
                strokeWidth = 2.5f
            )

            drawCircle(
                color = Color.White. copy(alpha = alpha),
                radius = 4f,
                center = Offset(currentX, currentY)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF4A90D9).copy(alpha = 0.7f),
                    Color(0xFF9B59B6).copy(alpha = 0.5f),
                    Color(0xFF4A90D9).copy(alpha = 0.7f)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2.5f)
        )
    }
}

// ==================== AURORA BOREALIS BANNER ====================

@Composable
fun AuroraBorealisBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave2"
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "colorShift"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A0A1A),
                    Color(0xFF151530),
                    Color(0xFF0A0A1A)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        val colorSets = listOf(
            listOf(Color(0xFF00FF88), Color(0xFF00FFFF)),
            listOf(Color(0xFF00FFFF), Color(0xFF8800FF)),
            listOf(Color(0xFF8800FF), Color(0xFF00FF88)),
            listOf(Color(0xFFFF00FF), Color(0xFF00FFFF))
        )

        for (i in 0.. 4) {
            val waveOffset = if (i % 2 == 0) wave1 else wave2
            val yOffset = canvasHeight * (0.15f + i * 0.18f + waveOffset * 0.1f)
            val colorIndex = ((i + (colorShift * 4).toInt()) % 4)

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color. Transparent,
                        colorSets[colorIndex][0].copy(alpha = 0.35f - i * 0.05f),
                        colorSets[colorIndex][1].copy(alpha = 0.25f - i * 0.04f),
                        Color.Transparent
                    )
                ),
                topLeft = Offset(0f, yOffset - 25f),
                size = Size(canvasWidth, 50f)
            )
        }

        for (i in 0..25) {
            val starX = ((i * 47 + 17) % canvasWidth. toInt()).toFloat()
            val starY = ((i * 31 + 11) % (canvasHeight * 0.5f).toInt()).toFloat()
            drawCircle(
                color = Color.White. copy(alpha = 0.8f),
                radius = if (i % 3 == 0) 1.5f else 1f,
                center = Offset(starX, starY)
            )
        }
    }
}

// ==================== FIRE STORM BANNER ====================

@Composable
fun FireStormBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fire")

    val flameWave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Restart),
        label = "flame"
    )

    val intensity by infiniteTransition.animateFloat(
        initialValue = 0.75f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(150, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "intensity"
    )

    Canvas(
        modifier = modifier
            . width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size. width
        val canvasHeight = size. height

        drawRoundRect(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF1A0000),
                    Color(0xFF2D0000),
                    Color(0xFF1A0000)
                )
            ),
            cornerRadius = CornerRadius(24.dp. toPx())
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color. Transparent,
                    Color(0xFFFF4500).copy(alpha = 0.35f * intensity),
                    Color(0xFFFFD700).copy(alpha = 0.5f * intensity)
                )
            ),
            topLeft = Offset(0f, canvasHeight * 0.25f),
            size = Size(canvasWidth, canvasHeight * 0.75f)
        )

        for (i in 0..18) {
            val baseX = (i * canvasWidth / 18)
            val animOffset = ((flameWave + i * 0.06f) % 1f)
            val particleY = canvasHeight * (1f - animOffset * 0.85f)
            val particleAlpha = (1f - animOffset) * intensity

            val colors = if (i % 2 == 0) {
                listOf(Color(0xFFFFD700), Color(0xFFFF4500), Color. Transparent)
            } else {
                listOf(Color(0xFFFF6600), Color(0xFFFF0000), Color.Transparent)
            }

            drawCircle(
                brush = Brush.radialGradient(colors = colors. map { it.copy(alpha = particleAlpha) }),
                radius = 20f * (1f - animOffset * 0.4f),
                center = Offset(baseX + sin(animOffset * 12f).toFloat() * 12f, particleY)
            )
        }

        drawRoundRect(
            brush = Brush. linearGradient(
                colors = listOf(Color(0xFFFF4500), Color(0xFFFFD700), Color(0xFFFF4500))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2.5f)
        )
    }
}

// ==================== NEON PULSE BANNER ====================

@Composable
fun NeonPulseBanner(
    modifier:  Modifier = Modifier,
    width:  Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "scan"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            color = Color(0xFF080812),
            cornerRadius = CornerRadius(24.dp. toPx())
        )

        val gridSpacing = 20f
        for (x in 0..(canvasWidth / gridSpacing).toInt()) {
            drawLine(
                color = Color(0xFF00FFFF).copy(alpha = 0.12f + pulse * 0.08f),
                start = Offset(x * gridSpacing, 0f),
                end = Offset(x * gridSpacing, canvasHeight),
                strokeWidth = 1f
            )
        }
        for (y in 0..(canvasHeight / gridSpacing).toInt()) {
            drawLine(
                color = Color(0xFFFF00FF).copy(alpha = 0.12f + pulse * 0.08f),
                start = Offset(0f, y * gridSpacing),
                end = Offset(canvasWidth, y * gridSpacing),
                strokeWidth = 1f
            )
        }

        val scanY = canvasHeight * scanLine
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(Color. Transparent, Color(0xFF00FFFF).copy(alpha = 0.9f), Color. Transparent)
            ),
            start = Offset(0f, scanY),
            end = Offset(canvasWidth, scanY),
            strokeWidth = 3f
        )

        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        for (i in 0..3) {
            val radius = 20f + i * 16f + pulse * 14f
            val alpha = (0.6f - i * 0.12f) * (1f - pulse * 0.25f)
            drawCircle(
                color = if (i % 2 == 0) Color(0xFFFF00FF).copy(alpha = alpha)
                else Color(0xFF00FFFF).copy(alpha = alpha),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.5f)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF00FF).copy(alpha = 0.6f + pulse * 0.4f),
                    Color(0xFF00FFFF).copy(alpha = 0.6f + pulse * 0.4f),
                    Color(0xFFFF00FF).copy(alpha = 0.6f + pulse * 0.4f)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 3f)
        )
    }
}

// ==================== GALAXY SWIRL BANNER ====================

@Composable
fun GalaxySwirlBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2A1A4A),
                    Color(0xFF1A0A2E),
                    Color(0xFF0A0515)
                ),
                center = Offset(centerX, centerY)
            ),
            cornerRadius = CornerRadius(24.dp. toPx())
        )

        for (arm in 0..2) {
            val armAngle = arm * 120f + rotation
            for (i in 0..30) {
                val distance = 12f + i * 5f * pulse
                val angle = Math.toRadians((armAngle + i * 12f).toDouble())
                val x = centerX + (cos(angle) * distance).toFloat()
                val y = centerY + (sin(angle) * distance * 0.55f).toFloat()

                val alpha = (1f - i / 35f) * 0.7f
                val starColor = when (arm) {
                    0 -> Color(0xFFE040FB)
                    1 -> Color(0xFF7C4DFF)
                    else -> Color(0xFF536DFE)
                }

                if (x in 0f..canvasWidth && y in 0f..canvasHeight) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(starColor. copy(alpha = alpha), Color.Transparent)
                        ),
                        radius = 10f - i * 0.2f,
                        center = Offset(x, y)
                    )
                }
            }
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.8f),
                    Color(0xFFE040FB).copy(alpha = 0.5f),
                    Color. Transparent
                )
            ),
            radius = 25f * pulse,
            center = Offset(centerX, centerY)
        )

        for (i in 0..40) {
            val starX = ((i * 53 + 17) % canvasWidth. toInt()).toFloat()
            val starY = ((i * 37 + 13) % canvasHeight.toInt()).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = 0.6f),
                radius = 1f,
                center = Offset(starX, starY)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFE040FB), Color(0xFF7C4DFF), Color(0xFFE040FB))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== OCEAN WAVES BANNER ====================

@Composable
fun OceanWavesBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ocean")

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = EaseInOutSine), RepeatMode. Reverse),
        label = "wave2"
    )

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0277BD),
                    Color(0xFF01579B),
                    Color(0xFF003366)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        val wavePath1 = Path().apply {
            moveTo(0f, canvasHeight * (0.45f + wave1 * 0.1f))
            for (x in 0.. canvasWidth. toInt() step 20) {
                val y = canvasHeight * (0.45f + sin(x * 0.02 + wave1 * Math.PI * 2).toFloat() * 0.08f)
                lineTo(x. toFloat(), y)
            }
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        drawPath(
            path = wavePath1,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4FC3F7).copy(alpha = 0.6f),
                    Color(0xFF0288D1).copy(alpha = 0.8f)
                )
            )
        )

        val wavePath2 = Path().apply {
            moveTo(0f, canvasHeight * (0.6f + wave2 * 0.08f))
            for (x in 0..canvasWidth. toInt() step 15) {
                val y = canvasHeight * (0.6f + sin(x * 0.025 + wave2 * Math.PI * 2 + 1).toFloat() * 0.06f)
                lineTo(x.toFloat(), y)
            }
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        drawPath(
            path = wavePath2,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF81D4FA).copy(alpha = 0.5f),
                    Color(0xFF03A9F4).copy(alpha = 0.7f)
                )
            )
        )

        val shimmerX = canvasWidth * shimmer
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.4f),
                    Color. Transparent
                )
            ),
            radius = 35f,
            center = Offset(shimmerX, canvasHeight * 0.3f)
        )

        for (i in 0..15) {
            val bubbleX = ((i * 67 + shimmer * 100) % canvasWidth.toInt()).toFloat()
            val bubbleY = canvasHeight * (0.65f + (i % 3) * 0.1f)
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = 3f,
                center = Offset(bubbleX, bubbleY)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1), Color(0xFF4FC3F7))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== RAINBOW FLOW BANNER ====================

@Composable
fun RainbowFlowBanner(
    modifier:  Modifier = Modifier,
    width:  Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rainbow")

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "colorShift"
    )

    val wave by infiniteTransition. animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val rainbowColors = listOf(
            Color(0xFFFF0000),
            Color(0xFFFF7F00),
            Color(0xFFFFFF00),
            Color(0xFF00FF00),
            Color(0xFF0000FF),
            Color(0xFF4B0082),
            Color(0xFF9400D3)
        )

        val stripeHeight = canvasHeight / 6
        for (i in 0..5) {
            val colorIndex = ((i + (colorShift * 7).toInt()) % 7)
            val nextColorIndex = ((colorIndex + 1) % 7)
            val yOffset = wave * 5f * if (i % 2 == 0) 1 else -1

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        rainbowColors[colorIndex],
                        rainbowColors[nextColorIndex],
                        rainbowColors[colorIndex]
                    )
                ),
                topLeft = Offset(0f, i * stripeHeight + yOffset),
                size = Size(canvasWidth, stripeHeight + 2f)
            )
        }

        for (i in 0..20) {
            val sparkX = ((i * 47 + colorShift * canvasWidth) % canvasWidth.toInt()).toFloat()
            val sparkY = ((i * 31) % canvasHeight.toInt()).toFloat()
            val sparkAlpha = (sin(colorShift * Math.PI * 4 + i).toFloat() + 1f) / 2f

            drawCircle(
                color = Color.White. copy(alpha = sparkAlpha * 0.8f),
                radius = 3f,
                center = Offset(sparkX, sparkY)
            )
        }

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.3f),
                    Color. Transparent,
                    Color. Black. copy(alpha = 0.1f)
                )
            ),
            size = Size(canvasWidth, canvasHeight)
        )

        drawRoundRect(
            color = Color.White. copy(alpha = 0.5f),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== ELECTRIC STORM BANNER ====================

@Composable
fun ElectricStormBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "electric")

    val flash by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(200, easing = LinearEasing), RepeatMode.Reverse),
        label = "flash"
    )

    val bolt by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "bolt"
    )

    val cloudMove by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "cloud"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A1A2E),
                    Color(0xFF2D2D44),
                    Color(0xFF1A1A2E)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        if (flash > 0.85f) {
            drawRoundRect(
                color = Color(0xFFFFEB3B).copy(alpha = 0.15f),
                cornerRadius = CornerRadius(24.dp.toPx())
            )
        }

        for (i in 0..4) {
            val cloudX = ((i * 80 + cloudMove * canvasWidth * 0.3f) % (canvasWidth + 60)) - 30
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3D3D5C).copy(alpha = 0.8f),
                        Color. Transparent
                    )
                ),
                radius = 45f + i * 12f,
                center = Offset(cloudX, 30f + i * 10f)
            )
        }

        if (bolt < 0.3f) {
            val boltPath = Path().apply {
                moveTo(canvasWidth * 0.5f, 0f)
                lineTo(canvasWidth * 0.45f, canvasHeight * 0.3f)
                lineTo(canvasWidth * 0.55f, canvasHeight * 0.35f)
                lineTo(canvasWidth * 0.4f, canvasHeight * 0.7f)
                lineTo(canvasWidth * 0.5f, canvasHeight * 0.65f)
                lineTo(canvasWidth * 0.35f, canvasHeight)
            }

            drawPath(
                path = boltPath,
                color = Color(0xFFFFEB3B).copy(alpha = 0.5f * (1f - bolt / 0.3f)),
                style = Stroke(width = 12f)
            )

            drawPath(
                path = boltPath,
                color = Color. White.copy(alpha = 1f - bolt / 0.3f),
                style = Stroke(width = 3f)
            )
        }

        if (bolt > 0.5f && bolt < 0.7f) {
            val boltPath2 = Path().apply {
                moveTo(canvasWidth * 0.75f, 0f)
                lineTo(canvasWidth * 0.7f, canvasHeight * 0.4f)
                lineTo(canvasWidth * 0.8f, canvasHeight * 0.45f)
                lineTo(canvasWidth * 0.65f, canvasHeight * 0.9f)
            }

            drawPath(
                path = boltPath2,
                color = Color(0xFF2196F3).copy(alpha = 0.6f * (1f - (bolt - 0.5f) / 0.2f)),
                style = Stroke(width = 8f)
            )

            drawPath(
                path = boltPath2,
                color = Color.White.copy(alpha = 0.9f * (1f - (bolt - 0.5f) / 0.2f)),
                style = Stroke(width = 2f)
            )
        }

        for (i in 0..12) {
            val particleX = ((i * 53 + bolt * 200) % canvasWidth.toInt()).toFloat()
            val particleY = ((i * 37) % canvasHeight. toInt()).toFloat()
            val particleAlpha = if ((i + (bolt * 10).toInt()) % 3 == 0) 0.8f else 0.3f

            drawCircle(
                color = Color(0xFFFFEB3B).copy(alpha = particleAlpha),
                radius = 2f,
                center = Offset(particleX, particleY)
            )
        }

        drawRoundRect(
            brush = Brush. linearGradient(
                colors = listOf(
                    Color(0xFFFFEB3B).copy(alpha = 0.5f + flash * 0.3f),
                    Color(0xFF2196F3).copy(alpha = 0.5f),
                    Color(0xFFFFEB3B).copy(alpha = 0.5f + flash * 0.3f)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== LAVA FLOW BANNER ====================

@Composable
fun LavaFlowBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lava")

    val flow by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "flow"
    )

    val bubble by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bubble"
    )

    Canvas(
        modifier = modifier
            . width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size. width
        val canvasHeight = size. height

        drawRoundRect(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF1A0500),
                    Color(0xFF3D0A00),
                    Color(0xFF4A0000)
                )
            ),
            cornerRadius = CornerRadius(24.dp. toPx())
        )

        // Lava rivers
        for (i in 0..4) {
            val riverY = canvasHeight * (0.2f + i * 0.18f)
            val waveOffset = sin((flow + i * 0.3f) * Math.PI * 2).toFloat() * 15f

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFFF3D00).copy(alpha = 0.3f),
                        Color(0xFFFF6D00).copy(alpha = 0.7f),
                        Color(0xFFFFAB00).copy(alpha = 0.5f),
                        Color(0xFFFF3D00).copy(alpha = 0.3f)
                    ),
                    startX = flow * canvasWidth * 2 - canvasWidth,
                    endX = flow * canvasWidth * 2
                ),
                topLeft = Offset(0f, riverY + waveOffset),
                size = Size(canvasWidth, 25f)
            )
        }

        // Lava bubbles
        for (i in 0..12) {
            val bubbleX = ((i * 73 + flow * 150) % canvasWidth.toInt()).toFloat()
            val bubbleY = canvasHeight * (0.4f + (i % 4) * 0.15f)
            val bubbleSize = 8f + (bubble * 4f) * if (i % 2 == 0) 1f else -0.5f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFAB00),
                        Color(0xFFFF6D00),
                        Color(0xFFFF3D00).copy(alpha = 0.5f)
                    )
                ),
                radius = bubbleSize. coerceAtLeast(4f),
                center = Offset(bubbleX, bubbleY - bubble * 20f)
            )
        }

        // Hot glow at bottom
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color. Transparent,
                    Color(0xFFFF6D00).copy(alpha = 0.4f),
                    Color(0xFFFFAB00).copy(alpha = 0.6f)
                )
            ),
            topLeft = Offset(0f, canvasHeight * 0.6f),
            size = Size(canvasWidth, canvasHeight * 0.4f)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF3D00), Color(0xFFFFAB00), Color(0xFFFF3D00))
            ),
            cornerRadius = CornerRadius(24.dp. toPx()),
            style = Stroke(width = 2.5f)
        )
    }
}

// ==================== CYBER GRID BANNER ====================

@Composable
fun CyberGridBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber")

    val gridPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "gridPulse"
    )

    val dataPulse by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Restart),
        label = "dataPulse"
    )

    Canvas(
        modifier = modifier
            . width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size. width
        val canvasHeight = size. height

        drawRoundRect(
            color = Color(0xFF0A0A0A),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Perspective grid
        val gridSpacing = 30f
        for (i in 0..(canvasWidth / gridSpacing).toInt() + 1) {
            val x = i * gridSpacing
            val pulseAlpha = if (((i + (gridPulse * 20).toInt()) % 5) == 0) 0.8f else 0.2f
            drawLine(
                color = Color(0xFF00FF41).copy(alpha = pulseAlpha),
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = if (pulseAlpha > 0.5f) 2f else 1f
            )
        }

        for (i in 0..(canvasHeight / gridSpacing).toInt() + 1) {
            val y = i * gridSpacing
            val pulseAlpha = if (((i + (gridPulse * 15).toInt()) % 4) == 0) 0.8f else 0.2f
            drawLine(
                color = Color(0xFF00FF41).copy(alpha = pulseAlpha),
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = if (pulseAlpha > 0.5f) 2f else 1f
            )
        }

        // Data packets moving
        for (i in 0.. 8) {
            val packetX = ((dataPulse * canvasWidth * 3 + i * 80) % canvasWidth)
            val packetY = (i * 37 % canvasHeight. toInt()).toFloat()

            drawRect(
                color = Color(0xFF39FF14),
                topLeft = Offset(packetX, packetY),
                size = Size(15f, 4f)
            )
        }

        // Central node
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF00FF41).copy(alpha = 0.8f),
                    Color(0xFF00FF41).copy(alpha = 0.3f),
                    Color. Transparent
                )
            ),
            radius = 40f + gridPulse * 15f,
            center = Offset(centerX, centerY)
        )

        drawCircle(
            color = Color(0xFF00FF41),
            radius = 8f,
            center = Offset(centerX, centerY)
        )

        drawRoundRect(
            color = Color(0xFF00FF41).copy(alpha = 0.7f),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== PLASMA SPHERE BANNER ====================

@Composable
fun PlasmaSphereBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "plasma")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            . width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size. width
        val canvasHeight = size. height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2A0A4A),
                    Color(0xFF1A0033),
                    Color(0xFF0A0015)
                ),
                center = Offset(centerX, centerY)
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Plasma tendrils
        for (i in 0..11) {
            val angle = Math.toRadians((rotation + i * 30.0))
            val length = 60f + pulse * 20f
            val endX = centerX + (cos(angle) * length).toFloat()
            val endY = centerY + (sin(angle) * length * 0.6f).toFloat()

            val tendrilColor = when (i % 3) {
                0 -> Color(0xFFE040FB)
                1 -> Color(0xFF9C27B0)
                else -> Color(0xFFAA00FF)
            }

            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color. White,
                        tendrilColor,
                        tendrilColor.copy(alpha = 0.3f)
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY)
                ),
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = 3f
            )

            // Spark at end
            drawCircle(
                color = Color. White.copy(alpha = pulse),
                radius = 4f,
                center = Offset(endX, endY)
            )
        }

        // Central orb
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White,
                    Color(0xFFE040FB),
                    Color(0xFF9C27B0).copy(alpha = 0.5f)
                )
            ),
            radius = 25f * pulse,
            center = Offset(centerX, centerY)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFE040FB), Color(0xFF9C27B0), Color(0xFFE040FB))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== NORTHERN LIGHTS BANNER ====================

@Composable
fun NorthernLightsBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "northern")

    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave2"
    )

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Night sky
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF000A14),
                    Color(0xFF001529),
                    Color(0xFF001F3D)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Aurora curtains
        for (i in 0.. 5) {
            val curtainPath = Path().apply {
                moveTo(0f, canvasHeight * (0.2f + i * 0.12f))
                for (x in 0..canvasWidth. toInt() step 10) {
                    val waveVal = if (i % 2 == 0) wave1 else wave2
                    val y = canvasHeight * (0.2f + i * 0.12f) +
                            sin(x * 0.015 + waveVal * Math.PI * 2 + i).toFloat() * 20f
                    lineTo(x. toFloat(), y)
                }
                lineTo(canvasWidth, canvasHeight)
                lineTo(0f, canvasHeight)
                close()
            }

            val curtainColor = when (i % 3) {
                0 -> Color(0xFF00E676)
                1 -> Color(0xFF69F0AE)
                else -> Color(0xFF00BFA5)
            }

            drawPath(
                path = curtainPath,
                brush = Brush. verticalGradient(
                    colors = listOf(
                        curtainColor. copy(alpha = 0.4f - i * 0.05f),
                        curtainColor.copy(alpha = 0.2f - i * 0.03f),
                        Color. Transparent
                    )
                )
            )
        }

        // Stars
        for (i in 0..30) {
            val starX = ((i * 43 + 17) % canvasWidth. toInt()).toFloat()
            val starY = ((i * 29 + 11) % (canvasHeight * 0.5f).toInt()).toFloat()
            val twinkle = (sin(shimmer * Math.PI * 2 + i).toFloat() + 1f) / 2f

            drawCircle(
                color = Color. White.copy(alpha = 0.5f + twinkle * 0.5f),
                radius = if (i % 5 == 0) 2f else 1f,
                center = Offset(starX, starY)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF00E676), Color(0xFF69F0AE), Color(0xFF00E676))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== SUNSET BEACH BANNER ====================

@Composable
fun SunsetBeachBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sunset")

    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sunPulse"
    )

    val wave by infiniteTransition. animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave"
    )

    val reflection by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "reflection"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Sky gradient
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFF6B35),
                    Color(0xFFFF8C42),
                    Color(0xFFFFC947),
                    Color(0xFFFFE4A0)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Sun
        val sunY = canvasHeight * 0.35f
        val sunRadius = 35f * sunPulse

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF59D),
                    Color(0xFFFFEB3B),
                    Color(0xFFFF8C42).copy(alpha = 0.5f)
                )
            ),
            radius = sunRadius + 15f,
            center = Offset(canvasWidth / 2, sunY)
        )

        drawCircle(
            color = Color(0xFFFFF59D),
            radius = sunRadius,
            center = Offset(canvasWidth / 2, sunY)
        )

        // Water
        val waterPath = Path().apply {
            moveTo(0f, canvasHeight * 0.55f)
            for (x in 0..canvasWidth. toInt() step 15) {
                val y = canvasHeight * 0.55f + sin(x * 0.02 + wave * Math. PI * 2).toFloat() * 5f
                lineTo(x.toFloat(), y)
            }
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        drawPath(
            path = waterPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFF6B35).copy(alpha = 0.6f),
                    Color(0xFF0277BD).copy(alpha = 0.8f),
                    Color(0xFF01579B)
                )
            )
        )

        // Sun reflection on water
        for (i in 0..8) {
            val refX = canvasWidth / 2 + sin(reflection * Math.PI * 2 + i).toFloat() * 20f
            val refY = canvasHeight * (0.6f + i * 0.045f)

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color. Transparent,
                        Color(0xFFFFC947).copy(alpha = 0.6f - i * 0.05f),
                        Color. Transparent
                    )
                ),
                topLeft = Offset(refX - 30f, refY),
                size = Size(60f, 4f)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF6B35), Color(0xFFFFC947), Color(0xFFFF6B35))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== MATRIX RAIN BANNER ====================

@Composable
fun MatrixRainBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix")

    val rain by infiniteTransition. animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Restart),
        label = "rain"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            color = Color(0xFF000000),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Matrix rain columns
        val columnWidth = 15f
        val columns = (canvasWidth / columnWidth).toInt()

        for (col in 0.. columns) {
            val x = col * columnWidth
            val speed = 0.5f + (col % 5) * 0.15f
            val offset = ((rain * speed + col * 0.1f) % 1f)

            // Trail of characters
            for (i in 0..12) {
                val charY = (offset * canvasHeight * 1.5f - i * 15f) % (canvasHeight + 50f) - 25f
                val alpha = (1f - i / 12f) * 0.9f

                if (charY in 0f..canvasHeight) {
                    val color = if (i == 0) Color(0xFFAAFFAA) else Color(0xFF00FF00)
                    drawCircle(
                        color = color. copy(alpha = alpha),
                        radius = 3f,
                        center = Offset(x, charY)
                    )
                }
            }
        }

        // Glow effect
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF00FF00).copy(alpha = 0.1f),
                    Color. Transparent,
                    Color(0xFF003300).copy(alpha = 0.2f)
                )
            ),
            size = Size(canvasWidth, canvasHeight)
        )

        drawRoundRect(
            color = Color(0xFF00FF00).copy(alpha = 0.6f),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== CRYSTAL CAVE BANNER ====================

@Composable
fun CrystalCaveBanner(
    modifier:  Modifier = Modifier,
    width:  Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crystal")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "shimmer"
    )

    val glow by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Cave background
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A1A3E),
                    Color(0xFF0D0D2A),
                    Color(0xFF050515)
                ),
                center = Offset(canvasWidth / 2, canvasHeight / 2)
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Crystal formations
        val crystalPositions = listOf(
            Triple(0.15f, 0.8f, 0.4f),
            Triple(0.3f, 0.9f, 0.6f),
            Triple(0.5f, 0.75f, 0.5f),
            Triple(0.7f, 0.85f, 0.7f),
            Triple(0.85f, 0.7f, 0.45f),
            Triple(0.2f, 0.2f, 0.35f),
            Triple(0.6f, 0.15f, 0.4f),
            Triple(0.8f, 0.25f, 0.3f)
        )

        crystalPositions.forEachIndexed { index, (xRatio, yRatio, heightRatio) ->
            val baseX = canvasWidth * xRatio
            val baseY = canvasHeight * yRatio
            val crystalHeight = canvasHeight * heightRatio
            val isTop = yRatio < 0.5f

            val crystalPath = Path().apply {
                if (isTop) {
                    moveTo(baseX - 12f, 0f)
                    lineTo(baseX, crystalHeight)
                    lineTo(baseX + 12f, 0f)
                } else {
                    moveTo(baseX - 12f, canvasHeight)
                    lineTo(baseX, baseY - crystalHeight)
                    lineTo(baseX + 12f, canvasHeight)
                }
                close()
            }

            val crystalColor = when (index % 3) {
                0 -> Color(0xFF00BCD4)
                1 -> Color(0xFF80DEEA)
                else -> Color(0xFF4DD0E1)
            }

            // Crystal glow
            drawPath(
                path = crystalPath,
                brush = Brush.verticalGradient(
                    colors = if (isTop) {
                        listOf(crystalColor.copy(alpha = 0.3f), crystalColor.copy(alpha = glow * 0.7f))
                    } else {
                        listOf(crystalColor.copy(alpha = glow * 0.7f), crystalColor.copy(alpha = 0.3f))
                    }
                )
            )

            // Crystal highlight
            val shimmerAlpha = if ((index + (shimmer * 8).toInt()) % 3 == 0) 0.8f else 0.3f
            drawLine(
                color = Color. White.copy(alpha = shimmerAlpha),
                start = Offset(baseX, if (isTop) 5f else canvasHeight - 5f),
                end = Offset(baseX, if (isTop) crystalHeight * 0.6f else baseY - crystalHeight * 0.6f),
                strokeWidth = 2f
            )
        }

        // Ambient particles
        for (i in 0..20) {
            val particleX = ((i * 53 + shimmer * 100) % canvasWidth. toInt()).toFloat()
            val particleY = ((i * 37) % canvasHeight. toInt()).toFloat()
            val particleAlpha = (sin(shimmer * Math.PI * 2 + i).toFloat() + 1f) / 2f * 0.6f

            drawCircle(
                color = Color(0xFF80DEEA).copy(alpha = particleAlpha),
                radius = 2f,
                center = Offset(particleX, particleY)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF00BCD4), Color(0xFF80DEEA), Color(0xFF00BCD4))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== TOXIC WASTE BANNER ====================

@Composable
fun ToxicWasteBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "toxic")

    val bubble by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "bubble"
    )

    val glow by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "glow"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Dark industrial background
        drawRoundRect(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF0A1A0A),
                    Color(0xFF1A2E1A),
                    Color(0xFF0D1F0D)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Toxic pool
        val poolPath = Path().apply {
            moveTo(0f, canvasHeight * 0.5f)
            for (x in 0..canvasWidth. toInt() step 20) {
                val y = canvasHeight * 0.5f + sin(x * 0.03 + bubble * Math. PI * 2).toFloat() * 8f
                lineTo(x.toFloat(), y)
            }
            lineTo(canvasWidth, canvasHeight)
            lineTo(0f, canvasHeight)
            close()
        }

        drawPath(
            path = poolPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF76FF03).copy(alpha = 0.7f * glow),
                    Color(0xFFB2FF59).copy(alpha = 0.5f),
                    Color(0xFF64DD17).copy(alpha = 0.8f)
                )
            )
        )

        // Bubbles rising
        for (i in 0..15) {
            val bubbleX = (i * 47 % canvasWidth.toInt()).toFloat()
            val bubbleProgress = (bubble + i * 0.08f) % 1f
            val bubbleY = canvasHeight * (1f - bubbleProgress * 0.6f)
            val bubbleSize = 6f + (i % 4) * 3f
            val bubbleAlpha = if (bubbleProgress > 0.8f) (1f - bubbleProgress) * 5f else 0.7f

            if (bubbleY > canvasHeight * 0.4f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFB2FF59).copy(alpha = bubbleAlpha),
                            Color(0xFF76FF03).copy(alpha = bubbleAlpha * 0.5f),
                            Color. Transparent
                        )
                    ),
                    radius = bubbleSize,
                    center = Offset(bubbleX, bubbleY)
                )
            }
        }

        // Warning symbols glow
        val warningGlow = glow * 0.4f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFB2FF59).copy(alpha = warningGlow),
                    Color. Transparent
                )
            ),
            radius = 50f,
            center = Offset(canvasWidth * 0.2f, canvasHeight * 0.3f)
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF76FF03).copy(alpha = warningGlow),
                    Color. Transparent
                )
            ),
            radius = 40f,
            center = Offset(canvasWidth * 0.8f, canvasHeight * 0.35f)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF76FF03), Color(0xFFB2FF59), Color(0xFF76FF03))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2.5f)
        )
    }
}

// ==================== BLOOD MOON BANNER ====================

@Composable
fun BloodMoonBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bloodMoon")

    val moonPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(4000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "moonPulse"
    )

    val cloudMove by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "cloudMove"
    )

    val batFly by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "batFly"
    )

    Canvas(
        modifier = modifier
            . width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size. width
        val canvasHeight = size. height

        // Dark night sky
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A0505),
                    Color(0xFF1A0A0A),
                    Color(0xFF0D0505)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Blood moon
        val moonX = canvasWidth * 0.7f
        val moonY = canvasHeight * 0.35f
        val moonRadius = 40f * moonPulse

        // Moon glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFB71C1C).copy(alpha = 0.4f),
                    Color(0xFFB71C1C).copy(alpha = 0.1f),
                    Color. Transparent
                )
            ),
            radius = moonRadius * 2f,
            center = Offset(moonX, moonY)
        )

        // Moon surface
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFF5252),
                    Color(0xFFD32F2F),
                    Color(0xFFB71C1C)
                )
            ),
            radius = moonRadius,
            center = Offset(moonX, moonY)
        )

        // Moon craters
        drawCircle(
            color = Color(0xFF8B0000).copy(alpha = 0.5f),
            radius = 8f,
            center = Offset(moonX - 10f, moonY - 5f)
        )
        drawCircle(
            color = Color(0xFF8B0000).copy(alpha = 0.4f),
            radius = 5f,
            center = Offset(moonX + 12f, moonY + 8f)
        )

        // Clouds
        for (i in 0..3) {
            val cloudX = ((cloudMove * canvasWidth + i * 120) % (canvasWidth + 100)) - 50f
            val cloudY = 20f + i * 25f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A0A0A).copy(alpha = 0.9f),
                        Color. Transparent
                    )
                ),
                radius = 40f + i * 10f,
                center = Offset(cloudX, cloudY)
            )
        }

        // Bats silhouettes
        for (i in 0..2) {
            val batX = ((batFly * canvasWidth * 1.5f + i * 100) % (canvasWidth + 50)) - 25f
            val batY = 30f + sin(batFly * Math. PI * 4 + i).toFloat() * 15f + i * 20f

            // Simple bat shape
            val batPath = Path().apply {
                moveTo(batX, batY)
                lineTo(batX - 8f, batY - 4f)
                lineTo(batX - 12f, batY + 2f)
                lineTo(batX, batY + 4f)
                lineTo(batX + 12f, batY + 2f)
                lineTo(batX + 8f, batY - 4f)
                close()
            }

            drawPath(
                path = batPath,
                color = Color(0xFF0A0505)
            )
        }

        // Stars
        for (i in 0..20) {
            val starX = ((i * 47 + 23) % canvasWidth. toInt()).toFloat()
            val starY = ((i * 31 + 11) % canvasHeight.toInt()).toFloat()
            if (starY < canvasHeight * 0.7f) {
                drawCircle(
                    color = Color. White.copy(alpha = 0.4f + (i % 3) * 0.2f),
                    radius = 1f,
                    center = Offset(starX, starY)
                )
            }
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFB71C1C), Color(0xFFFF5252), Color(0xFFB71C1C))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== HOLOGRAPHIC BANNER ====================

@Composable
fun HolographicBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "holographic")

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "colorShift"
    )

    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "scanLine"
    )

    val glitch by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(150, easing = LinearEasing), RepeatMode.Reverse),
        label = "glitch"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Base
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1A1A2E),
                    Color(0xFF2A2A3E),
                    Color(0xFF1A1A2E)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Holographic stripes
        val stripeCount = 20
        for (i in 0 until stripeCount) {
            val y = (i. toFloat() / stripeCount) * canvasHeight
            val colorPhase = (colorShift + i * 0.05f) % 1f

            val stripeColor = when {
                colorPhase < 0.33f -> Color(0xFFFF00FF).copy(alpha = 0.3f)
                colorPhase < 0.66f -> Color(0xFF00FFFF).copy(alpha = 0.3f)
                else -> Color(0xFFFFFF00).copy(alpha = 0.3f)
            }

            drawRect(
                color = stripeColor,
                topLeft = Offset(0f, y),
                size = Size(canvasWidth, canvasHeight / stripeCount + 1)
            )
        }

        // Scan line
        val scanY = scanLine * canvasHeight
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color. Transparent,
                    Color. White.copy(alpha = 0.4f),
                    Color. Transparent
                )
            ),
            topLeft = Offset(0f, scanY - 10f),
            size = Size(canvasWidth, 20f)
        )

        // Glitch effect
        if (glitch > 0.9f) {
            val glitchOffset = (glitch - 0.9f) * 100f
            drawRect(
                color = Color(0xFFFF00FF).copy(alpha = 0.3f),
                topLeft = Offset(glitchOffset, canvasHeight * 0.3f),
                size = Size(canvasWidth * 0.3f, 10f)
            )
            drawRect(
                color = Color(0xFF00FFFF).copy(alpha = 0.3f),
                topLeft = Offset(-glitchOffset, canvasHeight * 0.6f),
                size = Size(canvasWidth * 0.4f, 8f)
            )
        }

        // Rainbow shimmer overlay
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF00FF).copy(alpha = 0.1f),
                    Color(0xFF00FFFF).copy(alpha = 0.1f),
                    Color(0xFFFFFF00).copy(alpha = 0.1f),
                    Color(0xFFFF00FF).copy(alpha = 0.1f)
                ),
                start = Offset(colorShift * canvasWidth, 0f),
                end = Offset(colorShift * canvasWidth + canvasWidth, canvasHeight)
            ),
            size = Size(canvasWidth, canvasHeight)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF00FF), Color(0xFF00FFFF), Color(0xFFFF00FF))
            ),
            cornerRadius = CornerRadius(24.dp. toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== STARFIELD BANNER ====================

@Composable
fun StarfieldBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "starfield")

    val warp by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "warp"
    )

    val starData = remember {
        List(100) {
            Triple(
                Random.nextFloat(),  // angle
                Random.nextFloat(),  // distance
                Random.nextFloat() * 2f + 1f  // speed
            )
        }
    }

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Deep space
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0A0A1A),
                    Color(0xFF000011),
                    Color(0xFF000005)
                ),
                center = Offset(centerX, centerY)
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Warp stars
        starData.forEach { (angleRatio, distRatio, speed) ->
            val angle = angleRatio * Math.PI * 2
            val baseDistance = distRatio * 0.3f
            val warpedDistance = (baseDistance + warp * speed * 0.5f) % 1f
            val distance = warpedDistance * (canvasWidth * 0.6f)

            val x = centerX + (cos(angle) * distance).toFloat()
            val y = centerY + (sin(angle) * distance * 0.5f).toFloat()

            if (x in 0f..canvasWidth && y in 0f..canvasHeight) {
                val streakLength = warpedDistance * 15f
                val alpha = warpedDistance. coerceIn(0.1f, 1f)

                // Star streak
                val startX = centerX + (cos(angle) * (distance - streakLength)).toFloat()
                val startY = centerY + (sin(angle) * (distance - streakLength) * 0.5f).toFloat()

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color. Transparent,
                            Color.White. copy(alpha = alpha * 0.5f),
                            Color.White.copy(alpha = alpha)
                        ),
                        start = Offset(startX, startY),
                        end = Offset(x, y)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(x, y),
                    strokeWidth = 1.5f
                )

                // Star point
                drawCircle(
                    color = Color. White.copy(alpha = alpha),
                    radius = 2f,
                    center = Offset(x, y)
                )
            }
        }

        // Central glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color. White.copy(alpha = 0.3f),
                    Color(0xFF4A90D9).copy(alpha = 0.1f),
                    Color. Transparent
                )
            ),
            radius = 30f,
            center = Offset(centerX, centerY)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4A90D9), Color. White, Color(0xFF4A90D9))
            ),
            cornerRadius = CornerRadius(24.dp. toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== HEARTBEAT BANNER ====================

@Composable
fun HeartbeatBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")

    val beat by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Restart),
        label = "beat"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2

        // Background
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF1A0A1A),
                    Color(0xFF0D050D),
                    Color(0xFF1A0A1A)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Grid lines
        for (i in 0.. 8) {
            val y = (i. toFloat() / 8) * canvasHeight
            drawLine(
                color = Color(0xFFE91E63).copy(alpha = 0.1f),
                start = Offset(0f, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f
            )
        }
        for (i in 0..16) {
            val x = (i. toFloat() / 16) * canvasWidth
            drawLine(
                color = Color(0xFFE91E63).copy(alpha = 0.1f),
                start = Offset(x, 0f),
                end = Offset(x, canvasHeight),
                strokeWidth = 1f
            )
        }

        // EKG line
        val ekgPath = Path().apply {
            moveTo(0f, centerY)

            val scrollOffset = beat * canvasWidth

            for (x in 0.. canvasWidth. toInt() step 5) {
                val normalizedX = ((x + scrollOffset) % canvasWidth) / canvasWidth

                val y = when {
                    normalizedX in 0.4f..0.42f -> centerY - 40f
                    normalizedX in 0.42f..0.44f -> centerY + 50f
                    normalizedX in 0.44f..0.48f -> centerY - 30f
                    normalizedX in 0.48f..0.52f -> centerY + 20f
                    else -> centerY + sin(normalizedX * 20).toFloat() * 3f
                }

                lineTo(x.toFloat(), y)
            }
        }

        // Glow
        drawPath(
            path = ekgPath,
            color = Color(0xFFE91E63).copy(alpha = 0.3f),
            style = Stroke(width = 6f)
        )

        // Main line
        drawPath(
            path = ekgPath,
            color = Color(0xFFE91E63),
            style = Stroke(width = 2f)
        )

        // Heart icon
        val heartX = canvasWidth * 0.85f
        val heartY = canvasHeight * 0.25f
        val heartSize = 20f * pulse

        val heartPath = Path().apply {
            moveTo(heartX, heartY + heartSize * 0.3f)
            cubicTo(
                heartX - heartSize, heartY - heartSize * 0.5f,
                heartX - heartSize * 0.5f, heartY - heartSize,
                heartX, heartY - heartSize * 0.3f
            )
            cubicTo(
                heartX + heartSize * 0.5f, heartY - heartSize,
                heartX + heartSize, heartY - heartSize * 0.5f,
                heartX, heartY + heartSize * 0.3f
            )
        }

        drawPath(
            path = heartPath,
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFF48FB1), Color(0xFFE91E63))
            )
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFE91E63), Color(0xFFF48FB1), Color(0xFFE91E63))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== ICE CRYSTAL BANNER ====================

@Composable
fun IceCrystalBanner(
    modifier:  Modifier = Modifier,
    width:  Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ice")

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer"
    )

    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sparkle"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Icy background
        drawRoundRect(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF0A1A2E),
                    Color(0xFF1A3A5E),
                    Color(0xFF0A2A4E)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Frost patterns
        for (i in 0..5) {
            val centerX = ((i * 67 + 30) % canvasWidth.toInt()).toFloat()
            val centerY = ((i * 43 + 20) % canvasHeight.toInt()).toFloat()

            // Six-pointed snowflake
            for (j in 0..5) {
                val angle = Math.toRadians(j * 60.0 + shimmer * 30)
                val length = 25f + sparkle * 10f
                val endX = centerX + (cos(angle) * length).toFloat()
                val endY = centerY + (sin(angle) * length).toFloat()

                drawLine(
                    brush = Brush. linearGradient(
                        colors = listOf(
                            Color. White.copy(alpha = 0.8f),
                            Color(0xFF4FC3F7).copy(alpha = 0.4f)
                        ),
                        start = Offset(centerX, centerY),
                        end = Offset(endX, endY)
                    ),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 2f
                )

                // Branch
                val branchAngle1 = angle + Math.toRadians(30.0)
                val branchAngle2 = angle - Math.toRadians(30.0)
                val branchLength = length * 0.4f
                val branchStartX = centerX + (cos(angle) * length * 0.6f).toFloat()
                val branchStartY = centerY + (sin(angle) * length * 0.6f).toFloat()

                drawLine(
                    color = Color(0xFF4FC3F7).copy(alpha = 0.5f),
                    start = Offset(branchStartX, branchStartY),
                    end = Offset(
                        branchStartX + (cos(branchAngle1) * branchLength).toFloat(),
                        branchStartY + (sin(branchAngle1) * branchLength).toFloat()
                    ),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color(0xFF4FC3F7).copy(alpha = 0.5f),
                    start = Offset(branchStartX, branchStartY),
                    end = Offset(
                        branchStartX + (cos(branchAngle2) * branchLength).toFloat(),
                        branchStartY + (sin(branchAngle2) * branchLength).toFloat()
                    ),
                    strokeWidth = 1f
                )
            }
        }

        // Sparkles
        for (i in 0.. 30) {
            val sparkX = ((i * 53 + shimmer * 100) % canvasWidth.toInt()).toFloat()
            val sparkY = ((i * 37) % canvasHeight. toInt()).toFloat()
            val sparkAlpha = (sin(sparkle * Math.PI * 2 + i).toFloat() + 1f) / 2f

            drawCircle(
                color = Color.White.copy(alpha = sparkAlpha * 0.8f),
                radius = if (i % 3 == 0) 3f else 1.5f,
                center = Offset(sparkX, sparkY)
            )
        }

        // Shimmer overlay
        val shimmerX = shimmer * canvasWidth * 1.5f - canvasWidth * 0.25f
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color. Transparent,
                    Color.White.copy(alpha = 0.15f),
                    Color. Transparent
                ),
                startX = shimmerX,
                endX = shimmerX + canvasWidth * 0.3f
            ),
            size = Size(canvasWidth, canvasHeight)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF4FC3F7), Color(0xFFE1F5FE), Color(0xFF4FC3F7))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== GOLDEN PARTICLES BANNER ====================

@Composable
fun GoldenParticlesBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "golden")

    val float by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "float"
    )

    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode. Reverse),
        label = "shimmer"
    )

    val particles = remember {
        List(50) {
            Triple(
                Random. nextFloat(),  // x position
                Random.nextFloat(),  // y position base
                Random.nextFloat() * 0.5f + 0.5f  // speed
            )
        }
    }

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Rich dark background
        drawRoundRect(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF1A1500),
                    Color(0xFF2A2000),
                    Color(0xFF1A1500)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Floating golden particles
        particles.forEach { (xRatio, yBase, speed) ->
            val x = xRatio * canvasWidth
            val y = ((yBase + float * speed) % 1.2f - 0.1f) * canvasHeight
            val size = 4f + yBase * 4f
            val alpha = (1f - (y / canvasHeight).coerceIn(0f, 1f)) * 0.8f

            if (y in -10f..canvasHeight + 10f) {
                // Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = alpha * 0.5f),
                            Color. Transparent
                        )
                    ),
                    radius = size * 3f,
                    center = Offset(x, y)
                )

                // Particle
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF59D),
                            Color(0xFFFFD700)
                        )
                    ),
                    radius = size,
                    center = Offset(x, y)
                )
            }
        }

        // Shimmer rays
        for (i in 0..4) {
            val rayX = canvasWidth * (0.2f + i * 0.15f)
            val rayAlpha = (sin(shimmer * Math.PI + i).toFloat() + 1f) / 2f * 0.3f

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFD700).copy(alpha = rayAlpha),
                        Color. Transparent
                    )
                ),
                topLeft = Offset(rayX - 5f, 0f),
                size = Size(10f, canvasHeight * 0.7f)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFD700), Color(0xFFFFF59D), Color(0xFFFFD700))
            ),
            cornerRadius = CornerRadius(24.dp. toPx()),
            style = Stroke(width = 2.5f)
        )
    }
}

// ==================== VORTEX PORTAL BANNER ====================

@Composable
fun VortexPortalBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vortex")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2
        val centerY = canvasHeight / 2

        // Dark void
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF1A0A3A),
                    Color(0xFF0D0D1A),
                    Color(0xFF050510)
                ),
                center = Offset(centerX, centerY)
            ),
            cornerRadius = CornerRadius(24.dp. toPx())
        )

        // Vortex rings
        for (i in 0..7) {
            val ringRadius = (30f + i * 18f) * pulse
            val ringRotation = rotation + i * 15f
            val alpha = (1f - i / 8f) * 0.6f

            rotate(degrees = ringRotation, pivot = Offset(centerX, centerY)) {
                drawCircle(
                    brush = Brush. sweepGradient(
                        colors = listOf(
                            Color(0xFF7C4DFF).copy(alpha = alpha),
                            Color(0xFFB388FF).copy(alpha = alpha * 0.5f),
                            Color. Transparent,
                            Color(0xFF7C4DFF).copy(alpha = alpha)
                        ),
                        center = Offset(centerX, centerY)
                    ),
                    radius = ringRadius,
                    center = Offset(centerX, centerY),
                    style = Stroke(width = 3f - i * 0.3f)
                )
            }
        }

        // Central void
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF000000),
                    Color(0xFF1A0A3A),
                    Color(0xFF7C4DFF).copy(alpha = 0.3f)
                )
            ),
            radius = 25f * pulse,
            center = Offset(centerX, centerY)
        )

        // Energy particles spiraling in
        for (i in 0..15) {             val angle = Math.toRadians((rotation * 2 + i * 24.0))
            val distance = 80f - (rotation / 360f * 60f + i * 3f) % 60f
            val x = centerX + (cos(angle) * distance).toFloat()
            val y = centerY + (sin(angle) * distance * 0.5f).toFloat()

            if (distance > 20f) {
                drawCircle(
                    color = Color(0xFFB388FF).copy(alpha = 0.8f),
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF7C4DFF), Color(0xFFB388FF), Color(0xFF7C4DFF))
            ),
            cornerRadius = CornerRadius(24.dp. toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== FIREFLIES BANNER ====================

@Composable
fun FirefliesBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fireflies")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "time"
    )

    val fireflies = remember {
        List(25) {
            listOf(
                Random.nextFloat(),  // base x
                Random.nextFloat(),  // base y
                Random. nextFloat() * 0.02f + 0.005f,  // speed x
                Random.nextFloat() * 0.015f + 0.005f,  // speed y
                Random. nextFloat() * 2000f + 1000f  // blink speed
            )
        }
    }

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Night forest background
        drawRoundRect(
            brush = Brush. verticalGradient(
                colors = listOf(
                    Color(0xFF0A1A0A),
                    Color(0xFF0D2A0D),
                    Color(0xFF051505)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Silhouette trees
        for (i in 0..6) {
            val treeX = i * canvasWidth / 6
            val treeHeight = canvasHeight * (0.4f + (i % 3) * 0.15f)

            val treePath = Path().apply {
                moveTo(treeX, canvasHeight)
                lineTo(treeX - 15f, canvasHeight)
                lineTo(treeX - 8f, canvasHeight - treeHeight * 0.3f)
                lineTo(treeX - 20f, canvasHeight - treeHeight * 0.3f)
                lineTo(treeX - 10f, canvasHeight - treeHeight * 0.6f)
                lineTo(treeX - 18f, canvasHeight - treeHeight * 0.6f)
                lineTo(treeX, canvasHeight - treeHeight)
                lineTo(treeX + 18f, canvasHeight - treeHeight * 0.6f)
                lineTo(treeX + 10f, canvasHeight - treeHeight * 0.6f)
                lineTo(treeX + 20f, canvasHeight - treeHeight * 0.3f)
                lineTo(treeX + 8f, canvasHeight - treeHeight * 0.3f)
                lineTo(treeX + 15f, canvasHeight)
                close()
            }

            drawPath(
                path = treePath,
                color = Color(0xFF051505)
            )
        }

        // Fireflies
        fireflies.forEach { data ->
            val baseX = data[0]
            val baseY = data[1]
            val speedX = data[2]
            val speedY = data[3]
            val blinkSpeed = data[4]

            val x = (baseX + sin(time * speedX).toFloat() * 0.15f) * canvasWidth
            val y = (baseY + cos(time * speedY).toFloat() * 0.1f) * canvasHeight
            val blink = (sin(time / blinkSpeed * Math.PI).toFloat() + 1f) / 2f

            if (blink > 0.3f) {
                // Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFEB3B).copy(alpha = blink * 0.6f),
                            Color(0xFFFFF176).copy(alpha = blink * 0.2f),
                            Color. Transparent
                        )
                    ),
                    radius = 15f * blink,
                    center = Offset(x, y)
                )

                // Core
                drawCircle(
                    color = Color(0xFFFFF59D).copy(alpha = blink),
                    radius = 3f,
                    center = Offset(x, y)
                )
            }
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFEB3B), Color(0xFF8BC34A), Color(0xFFFFEB3B))
            ),
            cornerRadius = CornerRadius(24.dp. toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== NEBULA CLOUD BANNER ====================

@Composable
fun NebulaCloudBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nebula")

    val drift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "drift"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "colorShift"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Deep space
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF2A0A4A),
                    Color(0xFF1A0A2E),
                    Color(0xFF0A0515)
                ),
                center = Offset(canvasWidth / 2, canvasHeight / 2)
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Nebula clouds
        val cloudCenters = listOf(
            Offset(canvasWidth * 0.3f, canvasHeight * 0.4f),
            Offset(canvasWidth * 0.7f, canvasHeight * 0.6f),
            Offset(canvasWidth * 0.5f, canvasHeight * 0.3f),
            Offset(canvasWidth * 0.2f, canvasHeight * 0.7f),
            Offset(canvasWidth * 0.8f, canvasHeight * 0.35f)
        )

        cloudCenters.forEachIndexed { index, center ->
            val offsetX = drift * 20f * if (index % 2 == 0) 1 else -1
            val offsetY = drift * 10f * if (index % 3 == 0) -1 else 1
            val adjustedCenter = Offset(center.x + offsetX, center.y + offsetY)

            val cloudColor = when ((index + (colorShift * 5).toInt()) % 5) {
                0 -> Color(0xFFAA00FF)
                1 -> Color(0xFFE040FB)
                2 -> Color(0xFF7C4DFF)
                3 -> Color(0xFFEA80FC)
                else -> Color(0xFFCE93D8)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        cloudColor. copy(alpha = 0.4f * pulse),
                        cloudColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = adjustedCenter
                ),
                radius = 70f + index * 15f,
                center = adjustedCenter
            )
        }

        // Stars
        for (i in 0..50) {
            val starX = ((i * 47 + 17) % canvasWidth. toInt()).toFloat()
            val starY = ((i * 31 + 13) % canvasHeight.toInt()).toFloat()
            val twinkle = (sin(colorShift * Math.PI * 2 + i).toFloat() + 1f) / 2f

            drawCircle(
                color = Color. White.copy(alpha = 0.4f + twinkle * 0.4f),
                radius = if (i % 7 == 0) 2f else 1f,
                center = Offset(starX, starY)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFAA00FF), Color(0xFFEA80FC), Color(0xFFAA00FF))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== THUNDER STRIKE BANNER ====================

@Composable
fun ThunderStrikeBanner(
    modifier: Modifier = Modifier,
    width: Dp = 340.dp,
    height:  Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "thunder")

    val flash by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Reverse),
        label = "flash"
    )

    val strike by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "strike"
    )

    val rumble by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "rumble"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Storm sky
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0A0A1A),
                    Color(0xFF1A1A3A),
                    Color(0xFF0A0A2A)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Flash effect
        if (strike < 0.15f) {
            val flashIntensity = (0.15f - strike) / 0.15f
            drawRoundRect(
                color = Color. White.copy(alpha = flashIntensity * 0.4f),
                cornerRadius = CornerRadius(24.dp.toPx())
            )
        }

        // Storm clouds
        for (i in 0..5) {
            val cloudX = (i * canvasWidth / 4) + rumble * 5f * if (i % 2 == 0) 1 else -1
            val cloudY = 20f + (i % 3) * 15f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2A2A4A),
                        Color(0xFF1A1A3A).copy(alpha = 0.5f),
                        Color. Transparent
                    )
                ),
                radius = 50f + i * 10f,
                center = Offset(cloudX, cloudY)
            )
        }

        // Main lightning bolt
        if (strike < 0.25f) {
            val boltAlpha = (0.25f - strike) / 0.25f

            val mainBolt = Path().apply {
                moveTo(canvasWidth * 0.5f, 0f)
                lineTo(canvasWidth * 0.45f, canvasHeight * 0.25f)
                lineTo(canvasWidth * 0.52f, canvasHeight * 0.27f)
                lineTo(canvasWidth * 0.42f, canvasHeight * 0.5f)
                lineTo(canvasWidth * 0.48f, canvasHeight * 0.52f)
                lineTo(canvasWidth * 0.35f, canvasHeight * 0.85f)
            }

            // Glow
            drawPath(
                path = mainBolt,
                color = Color(0xFF2196F3).copy(alpha = boltAlpha * 0.5f),
                style = Stroke(width = 15f)
            )

            // Core
            drawPath(
                path = mainBolt,
                color = Color. White.copy(alpha = boltAlpha),
                style = Stroke(width = 4f)
            )

            // Branch
            val branch = Path().apply {
                moveTo(canvasWidth * 0.42f, canvasHeight * 0.5f)
                lineTo(canvasWidth * 0.55f, canvasHeight * 0.65f)
                lineTo(canvasWidth * 0.6f, canvasHeight * 0.9f)
            }

            drawPath(
                path = branch,
                color = Color(0xFFFFEB3B).copy(alpha = boltAlpha * 0.7f),
                style = Stroke(width = 8f)
            )

            drawPath(
                path = branch,
                color = Color.White. copy(alpha = boltAlpha * 0.9f),
                style = Stroke(width = 2f)
            )
        }

        // Second strike
        if (strike > 0.5f && strike < 0.65f) {
            val boltAlpha = 1f - ((strike - 0.5f) / 0.15f)

            val secondBolt = Path().apply {
                moveTo(canvasWidth * 0.75f, 0f)
                lineTo(canvasWidth * 0.72f, canvasHeight * 0.3f)
                lineTo(canvasWidth * 0.78f, canvasHeight * 0.35f)
                lineTo(canvasWidth * 0.68f, canvasHeight * 0.7f)
            }

            drawPath(
                path = secondBolt,
                color = Color(0xFFFFEB3B).copy(alpha = boltAlpha * 0.4f),
                style = Stroke(width = 10f)
            )

            drawPath(
                path = secondBolt,
                color = Color.White.copy(alpha = boltAlpha),
                style = Stroke(width = 3f)
            )
        }

        // Rain
        for (i in 0..30) {
            val rainX = ((i * 37 + strike * 50) % canvasWidth. toInt()).toFloat()
            val rainY = ((i * 53 + strike * canvasHeight) % canvasHeight.toInt()).toFloat()

            drawLine(
                color = Color(0xFF4FC3F7).copy(alpha = 0.4f),
                start = Offset(rainX, rainY),
                end = Offset(rainX - 3f, rainY + 10f),
                strokeWidth = 1f
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF2196F3), Color(0xFFFFEB3B), Color(0xFF2196F3))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== SAKURA PETALS BANNER ====================

@Composable
fun SakuraPetalsBanner(
    modifier:  Modifier = Modifier,
    width:  Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sakura")

    val fall by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "fall"
    )

    val sway by infiniteTransition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "sway"
    )

    val petals = remember {
        List(30) {
            listOf(
                Random.nextFloat(),  // x position
                Random.nextFloat(),  // y offset
                Random.nextFloat() * 0.5f + 0.5f,  // fall speed
                Random. nextFloat() * 20f + 10f,  // sway amplitude
                Random.nextFloat() * 360f  // rotation
            )
        }
    }

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Soft pink sky
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF4A2040),
                    Color(0xFF2E1A2A),
                    Color(0xFF1A1020)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Distant cherry tree silhouette
        val treePath = Path().apply {
            moveTo(canvasWidth * 0.15f, canvasHeight)
            lineTo(canvasWidth * 0.12f, canvasHeight * 0.6f)
            quadraticBezierTo(
                canvasWidth * 0.05f, canvasHeight * 0.4f,
                canvasWidth * 0.15f, canvasHeight * 0.35f
            )
            quadraticBezierTo(
                canvasWidth * 0.25f, canvasHeight * 0.25f,
                canvasWidth * 0.35f, canvasHeight * 0.3f
            )
            quadraticBezierTo(
                canvasWidth * 0.3f, canvasHeight * 0.5f,
                canvasWidth * 0.2f, canvasHeight * 0.6f
            )
            lineTo(canvasWidth * 0.18f, canvasHeight)
            close()
        }

        drawPath(
            path = treePath,
            color = Color(0xFF1A0A15)
        )

        // Blossoms on tree
        for (i in 0.. 15) {
            val blossomX = canvasWidth * (0.1f + Random(i).nextFloat() * 0.2f)
            val blossomY = canvasHeight * (0.25f + Random(i * 2).nextFloat() * 0.25f)

            drawCircle(
                brush = Brush. radialGradient(
                    colors = listOf(
                        Color(0xFFF8BBD0).copy(alpha = 0.6f),
                        Color(0xFFE91E63).copy(alpha = 0.2f),
                        Color. Transparent
                    )
                ),
                radius = 12f + (i % 3) * 5f,
                center = Offset(blossomX, blossomY)
            )
        }

        // Falling petals
        petals.forEach { data ->
            val baseX = data[0]
            val yOffset = data[1]
            val speed = data[2]
            val swayAmp = data[3]
            val baseRotation = data[4]

            val y = ((yOffset + fall * speed) % 1.3f - 0.15f) * canvasHeight
            val x = baseX * canvasWidth + sin((fall * speed + baseX) * Math.PI * 2).toFloat() * swayAmp * sway

            if (y in -20f.. canvasHeight + 20f && x in -20f..canvasWidth + 20f) {
                val rotation = baseRotation + fall * 180f

                rotate(degrees = rotation, pivot = Offset(x, y)) {
                    // Petal shape (ellipse)
                    drawOval(
                        brush = Brush. radialGradient(
                            colors = listOf(
                                Color(0xFFFCE4EC),
                                Color(0xFFF8BBD0)
                            ),
                            center = Offset(x, y)
                        ),
                        topLeft = Offset(x - 6f, y - 3f),
                        size = Size(12f, 6f)
                    )
                }
            }
        }

        // Soft glow overlay
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF8BBD0).copy(alpha = 0.1f),
                    Color. Transparent,
                    Color(0xFFE91E63).copy(alpha = 0.05f)
                )
            ),
            size = Size(canvasWidth, canvasHeight)
        )

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFF8BBD0), Color(0xFFFFFFFF), Color(0xFFF8BBD0))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== UNDERWATER BANNER ====================

@Composable
fun UnderwaterBanner(
    modifier:  Modifier = Modifier,
    width:  Dp = 340.dp,
    height: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "underwater")

    val wave by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "wave"
    )

    val bubble by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "bubble"
    )

    val lightRay by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "lightRay"
    )

    Canvas(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(24.dp))
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Deep water gradient
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0077B6),
                    Color(0xFF023E8A),
                    Color(0xFF001F3F)
                )
            ),
            cornerRadius = CornerRadius(24.dp.toPx())
        )

        // Light rays from surface
        for (i in 0..4) {
            val rayX = canvasWidth * (0.15f + i * 0.18f) + lightRay * 20f
            val rayAlpha = 0.15f + lightRay * 0.1f

            val rayPath = Path().apply {
                moveTo(rayX - 10f, 0f)
                lineTo(rayX + 20f, 0f)
                lineTo(rayX + 50f + i * 10f, canvasHeight)
                lineTo(rayX - 30f - i * 10f, canvasHeight)
                close()
            }

            drawPath(
                path = rayPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7FDBFF).copy(alpha = rayAlpha),
                        Color. Transparent
                    )
                )
            )
        }

        // Seaweed
        for (i in 0..6) {
            val seaweedX = i * canvasWidth / 6 + 20f
            val seaweedHeight = canvasHeight * (0.3f + (i % 3) * 0.15f)
            val swayOffset = sin(wave * Math.PI * 2 + i).toFloat() * 10f

            val seaweedPath = Path().apply {
                moveTo(seaweedX, canvasHeight)
                quadraticBezierTo(
                    seaweedX + swayOffset, canvasHeight - seaweedHeight * 0.5f,
                    seaweedX + swayOffset * 1.5f, canvasHeight - seaweedHeight
                )
                quadraticBezierTo(
                    seaweedX + swayOffset * 0.8f, canvasHeight - seaweedHeight * 0.5f,
                    seaweedX + 8f, canvasHeight
                )
                close()
            }

            drawPath(
                path = seaweedPath,
                brush = Brush. verticalGradient(
                    colors = listOf(
                        Color(0xFF2D6A4F),
                        Color(0xFF1B4332)
                    )
                )
            )
        }

        // Bubbles
        for (i in 0.. 20) {
            val bubbleX = (i * 47 % canvasWidth. toInt()).toFloat() + sin(bubble * Math. PI + i).toFloat() * 5f
            val bubbleProgress = (bubble + i * 0.05f) % 1f
            val bubbleY = canvasHeight * (1f - bubbleProgress)
            val bubbleSize = 4f + (i % 4) * 2f
            val bubbleAlpha = if (bubbleProgress > 0.9f) (1f - bubbleProgress) * 10f else 0.6f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color. White.copy(alpha = bubbleAlpha * 0.3f),
                        Color(0xFF7FDBFF).copy(alpha = bubbleAlpha * 0.5f),
                        Color. Transparent
                    )
                ),
                radius = bubbleSize,
                center = Offset(bubbleX, bubbleY)
            )

            // Bubble highlight
            drawCircle(
                color = Color.White. copy(alpha = bubbleAlpha * 0.6f),
                radius = bubbleSize * 0.3f,
                center = Offset(bubbleX - bubbleSize * 0.3f, bubbleY - bubbleSize * 0.3f)
            )
        }

        // Small fish silhouettes
        for (i in 0..3) {
            val fishX = ((bubble * canvasWidth * 1.5f + i * 100) % (canvasWidth + 50)) - 25f
            val fishY = canvasHeight * (0.3f + i * 0.15f) + sin(wave * Math. PI + i).toFloat() * 10f

            val fishPath = Path().apply {
                moveTo(fishX, fishY)
                quadraticBezierTo(fishX + 10f, fishY - 5f, fishX + 20f, fishY)
                quadraticBezierTo(fishX + 10f, fishY + 5f, fishX, fishY)
                // Tail
                moveTo(fishX, fishY)
                lineTo(fishX - 8f, fishY - 4f)
                lineTo(fishX - 8f, fishY + 4f)
                close()
            }

            drawPath(
                path = fishPath,
                color = Color(0xFF0077B6).copy(alpha = 0.6f)
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF0077B6), Color(0xFF7FDBFF), Color(0xFF0077B6))
            ),
            cornerRadius = CornerRadius(24.dp.toPx()),
            style = Stroke(width = 2f)
        )
    }
}

// ==================== BANNER THUMBNAIL ====================

@Composable
fun BannerThumbnail(
    bannerColorId: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier:  Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "scale"
    )

    Box(
        modifier = modifier
            .width(100.dp)
            .height(55.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 3. dp else 1.dp,
                color = if (isSelected) Color(0xFFFFD700) else Color.White. copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        HorizontalProfileBanner(
            bannerColorId = bannerColorId,
            width = 100.dp,
            height = 55.dp
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFD700)),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", fontSize = 11.sp, color = Color. Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== PROFILE AVATAR WITH BANNER ====================

@Composable
fun ProfileAvatarWithBanner(
    avatarDrawableRes: Int?,
    bannerColorId: Int,
    avatarSize: Dp = 110.dp,
    bannerWidth: Dp = 340.dp,
    bannerHeight:  Dp = 180.dp,
    showEditButton: Boolean = false,
    pencilDrawable: Int = 0,
    onEditClick: () -> Unit = {},
    onBannerClick: () -> Unit = {},
    generatedAvatarId: Int = -1
) {
    // Avatar:  40% dentro del banner, 60% fuera (subido más)
    val avatarInsideBanner = avatarSize * 0.40f
    val avatarOutsideBanner = avatarSize * 0.60f
    val totalHeight = bannerHeight + avatarOutsideBanner

    Box(
        modifier = Modifier
            . width(bannerWidth)
            .height(totalHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        // Banner
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .clickable { onBannerClick() }
        ) {
            HorizontalProfileBanner(
                bannerColorId = bannerColorId,
                width = bannerWidth,
                height = bannerHeight
            )
        }

        // Avatar
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = bannerHeight - avatarInsideBanner)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1E1E2E),
                modifier = Modifier
                    .size(avatarSize)
                    .border(3.dp, Color(0xFF2A2A4A), CircleShape)
            ) {
                if (generatedAvatarId >= 0) {
                    // Usar avatar generado
                    GeneratedAvatar(
                        avatarId = generatedAvatarId,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (avatarDrawableRes != null && avatarDrawableRes != 0) {
                    Image(
                        painter = painterResource(id = avatarDrawableRes),
                        contentDescription = "avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF2A2A4A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = (avatarSize. value * 0.4f).sp)
                    }
                }
            }

            // Edit button
            if (showEditButton && pencilDrawable != 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        . offset(x = 0. dp, y = 0.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .border(2.dp, Color. White, CircleShape)
                        . clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = pencilDrawable),
                        contentDescription = "edit",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}