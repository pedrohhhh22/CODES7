package com.appsdevs.popit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.first
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ==================== BADGE DATA MODELS ====================

enum class BadgeCategory {
    RECORDS,
    CHALLENGES,
    TOURNAMENTS,
    PROGRESS,
    SPECIAL
}

enum class BadgeTier(val level: Int) {
    LOCKED(0),
    BRONZE(1),
    SILVER(2),
    GOLD(3),
    DIAMOND(4),
    LEGENDARY(5)
}

enum class BadgeShape {
    ROYAL_MEDAL,      // Medalla real con corona
    WARRIOR_SHIELD,   // Escudo de guerrero
    CELESTIAL_STAR,   // Estrella celestial con rayos
    MYSTIC_HEXAGON,   // Hexágono místico
    IMPERIAL_CROWN,   // Corona imperial
    PHOENIX_FLAME,    // Llama de fénix
    DRAGON_EMBLEM,    // Emblema de dragón
    COSMIC_ORB        // Orbe cósmico
}

data class MedalBadge(
    val id: String,
    val name: String,
    val description: String,
    val category: BadgeCategory,
    val shape: BadgeShape,
    val tiers: List<MedalTierInfo> = emptyList()
)

data class MedalTierInfo(
    val tier: BadgeTier,
    val requirement: Int,
    val description: String
)

data class MedalProgress(
    val badge: MedalBadge,
    val currentTier:  BadgeTier,
    val currentValue: Int,
    val nextTierRequirement: Int?,
    val isMaxed: Boolean,
    val progressPercent: Float
)

// ==================== PREMIUM TIER COLORS ====================

data class MedalColors(
    val primary: Color,
    val secondary:  Color,
    val accent: Color,
    val glow: Color,
    val border: Color,
    val innerGlow: Color,
    val highlight: Color
)

fun getMedalColors(tier: BadgeTier): MedalColors {
    return when (tier) {
        BadgeTier.LOCKED -> MedalColors(
            primary = Color(0xFF2A2A2A),
            secondary = Color(0xFF1A1A1A),
            accent = Color(0xFF3A3A3A),
            glow = Color(0xFF222222),
            border = Color(0xFF444444),
            innerGlow = Color(0xFF333333),
            highlight = Color(0xFF4A4A4A)
        )
        BadgeTier.BRONZE -> MedalColors(
            primary = Color(0xFFCD7F32),
            secondary = Color(0xFF8B4513),
            accent = Color(0xFFE8A84C),
            glow = Color(0xFFCD7F32),
            border = Color(0xFFF4C794),
            innerGlow = Color(0xFFDAA06D),
            highlight = Color(0xFFFFD7A8)
        )
        BadgeTier.SILVER -> MedalColors(
            primary = Color(0xFFC0C0C0),
            secondary = Color(0xFF808080),
            accent = Color(0xFFE8E8E8),
            glow = Color(0xFFD4D4D4),
            border = Color(0xFFFFFFFF),
            innerGlow = Color(0xFFE0E0E0),
            highlight = Color(0xFFFFFFFF)
        )
        BadgeTier.GOLD -> MedalColors(
            primary = Color(0xFFFFD700),
            secondary = Color(0xFFFF8C00),
            accent = Color(0xFFFFF44F),
            glow = Color(0xFFFFD700),
            border = Color(0xFFFFE878),
            innerGlow = Color(0xFFFFEC8B),
            highlight = Color(0xFFFFFFF0)
        )
        BadgeTier.DIAMOND -> MedalColors(
            primary = Color(0xFF00D4FF),
            secondary = Color(0xFF0099CC),
            accent = Color(0xFFB8F4FF),
            glow = Color(0xFF00D4FF),
            border = Color(0xFF7DF9FF),
            innerGlow = Color(0xFF40E0D0),
            highlight = Color(0xFFE0FFFF)
        )
        BadgeTier.LEGENDARY -> MedalColors(
            primary = Color(0xFFFF00FF),
            secondary = Color(0xFF9400D3),
            accent = Color(0xFFFFD700),
            glow = Color(0xFFFF00FF),
            border = Color(0xFFFF69B4),
            innerGlow = Color(0xFFDA70D6),
            highlight = Color(0xFFFFE4FF)
        )
    }
}

// ==================== BADGE DEFINITIONS ====================

object MedalDefinitions {

    val SCORE_MASTER = MedalBadge(
        id = "score_master",
        name = "Score Master",
        description = "Master of classic mode",
        category = BadgeCategory.RECORDS,
        shape = BadgeShape.ROYAL_MEDAL,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 1000, "Score 1,000 points"),
            MedalTierInfo(BadgeTier.SILVER, 3000, "Score 3,000 points"),
            MedalTierInfo(BadgeTier.GOLD, 7500, "Score 7,500 points"),
            MedalTierInfo(BadgeTier.DIAMOND, 15000, "Score 15,000 points"),
            MedalTierInfo(BadgeTier.LEGENDARY, 30000, "Score 30,000 points")
        )
    )

    val BUBBLE_KING = MedalBadge(
        id = "bubble_king",
        name = "Bubble King",
        description = "Master of Bubble King challenge",
        category = BadgeCategory.CHALLENGES,
        shape = BadgeShape.IMPERIAL_CROWN,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 2000, "Score 2,000 in Bubble King"),
            MedalTierInfo(BadgeTier.SILVER, 3500, "Score 3,500 in Bubble King"),
            MedalTierInfo(BadgeTier.GOLD, 5000, "Complete Bubble King (5,000)"),
            MedalTierInfo(BadgeTier.DIAMOND, 5, "Complete Bubble King 5 times"),
            MedalTierInfo(BadgeTier.LEGENDARY, 15, "Complete Bubble King 15 times")
        )
    )

    val STREAK_FURY = MedalBadge(
        id = "streak_fury",
        name = "Perfect Streak",
        description = "Perfect streaks master",
        category = BadgeCategory.CHALLENGES,
        shape = BadgeShape.PHOENIX_FLAME,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 25, "Reach 25 streak"),
            MedalTierInfo(BadgeTier.SILVER, 50, "Reach 50 streak"),
            MedalTierInfo(BadgeTier.GOLD, 100, "Complete (100 streak)"),
            MedalTierInfo(BadgeTier.DIAMOND, 5, "Complete 5 times"),
            MedalTierInfo(BadgeTier.LEGENDARY, 15, "Complete 15 times")
        )
    )

    val TIME_MASTER = MedalBadge(
        id = "time_master",
        name = "Time Master",
        description = "Master of time challenge",
        category = BadgeCategory.CHALLENGES,
        shape = BadgeShape.MYSTIC_HEXAGON,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 12, "Pop 12 bubbles"),
            MedalTierInfo(BadgeTier.SILVER, 25, "Pop 25 bubbles"),
            MedalTierInfo(BadgeTier.GOLD, 50, "Complete (50 pops in 60s)"),
            MedalTierInfo(BadgeTier.DIAMOND, 5, "Complete 5 times"),
            MedalTierInfo(BadgeTier.LEGENDARY, 15, "Complete 15 times")
        )
    )

    val COMBO_MASTER = MedalBadge(
        id = "combo_master",
        name = "Combo Master",
        description = "Master of combos",
        category = BadgeCategory.CHALLENGES,
        shape = BadgeShape.CELESTIAL_STAR,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 15, "Reach 15 combo"),
            MedalTierInfo(BadgeTier.SILVER, 30, "Reach 30 combo"),
            MedalTierInfo(BadgeTier.GOLD, 50, "Complete (50 combo)"),
            MedalTierInfo(BadgeTier.DIAMOND, 5, "Complete 5 times"),
            MedalTierInfo(BadgeTier.LEGENDARY, 15, "Complete 15 times")
        )
    )

    val SPEED_DEMON = MedalBadge(
        id = "speed_demon",
        name = "Speed Demon",
        description = "Master of speed",
        category = BadgeCategory.CHALLENGES,
        shape = BadgeShape.PHOENIX_FLAME,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 25, "Pop 25 bubbles"),
            MedalTierInfo(BadgeTier.SILVER, 50, "Pop 50 bubbles"),
            MedalTierInfo(BadgeTier.GOLD, 100, "Complete (100 pops in 30s)"),
            MedalTierInfo(BadgeTier.DIAMOND, 5, "Complete 5 times"),
            MedalTierInfo(BadgeTier.LEGENDARY, 15, "Complete 15 times")
        )
    )

    val ENDURANCE_CHAMPION = MedalBadge(
        id = "endurance_champion",
        name = "Endurance Champion",
        description = "Master of endurance",
        category = BadgeCategory.CHALLENGES,
        shape = BadgeShape.WARRIOR_SHIELD,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 60, "Survive 60 seconds"),
            MedalTierInfo(BadgeTier.SILVER, 120, "Survive 120 seconds"),
            MedalTierInfo(BadgeTier.GOLD, 180, "Complete (180 seconds)"),
            MedalTierInfo(BadgeTier.DIAMOND, 5, "Complete 5 times"),
            MedalTierInfo(BadgeTier.LEGENDARY, 15, "Complete 15 times")
        )
    )

    val SHARPSHOOTER = MedalBadge(
        id = "sharpshooter",
        name = "Sharpshooter",
        description = "Lethal precision",
        category = BadgeCategory.RECORDS,
        shape = BadgeShape.CELESTIAL_STAR,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 70, "70% accuracy"),
            MedalTierInfo(BadgeTier.SILVER, 80, "80% accuracy"),
            MedalTierInfo(BadgeTier.GOLD, 90, "90% accuracy"),
            MedalTierInfo(BadgeTier.DIAMOND, 95, "95% accuracy"),
            MedalTierInfo(BadgeTier.LEGENDARY, 99, "99% accuracy")
        )
    )

    val CHALLENGER = MedalBadge(
        id = "challenger",
        name = "Challenger",
        description = "Conqueror of challenges",
        category = BadgeCategory.CHALLENGES,
        shape = BadgeShape.WARRIOR_SHIELD,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 1, "Complete 1 challenge"),
            MedalTierInfo(BadgeTier.SILVER, 3, "Complete 3 challenges"),
            MedalTierInfo(BadgeTier.GOLD, 6, "Complete all 6 challenges"),
            MedalTierInfo(BadgeTier.DIAMOND, 6, "Gold tier on all 6 challenges"),
            MedalTierInfo(BadgeTier.LEGENDARY, 60, "Complete all challenges 10+ times each")
        )
    )

    val TOURNAMENT_WARRIOR = MedalBadge(
        id = "tournament_warrior",
        name = "Tournament Warrior",
        description = "Tournament warrior",
        category = BadgeCategory.TOURNAMENTS,
        shape = BadgeShape.DRAGON_EMBLEM,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 1, "Participate in 1"),
            MedalTierInfo(BadgeTier.SILVER, 5, "Participate in 5"),
            MedalTierInfo(BadgeTier.GOLD, 15, "Participate in 15"),
            MedalTierInfo(BadgeTier.DIAMOND, 30, "Participate in 30"),
            MedalTierInfo(BadgeTier.LEGENDARY, 50, "Participate in 50")
        )
    )

    val CHAMPION = MedalBadge(
        id = "champion",
        name = "Champion",
        description = "Tournament champion",
        category = BadgeCategory.TOURNAMENTS,
        shape = BadgeShape.IMPERIAL_CROWN,
        tiers = listOf(
            MedalTierInfo(BadgeTier.GOLD, 1, "Win 1 tournament"),
            MedalTierInfo(BadgeTier.DIAMOND, 3, "Win 3 tournaments"),
            MedalTierInfo(BadgeTier.LEGENDARY, 10, "Win 10 tournaments")
        )
    )

    val PODIUM_STAR = MedalBadge(
        id = "podium_star",
        name = "Podium Star",
        description = "Always on the podium",
        category = BadgeCategory.TOURNAMENTS,
        shape = BadgeShape.CELESTIAL_STAR,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 1, "Top 3 once"),
            MedalTierInfo(BadgeTier.SILVER, 3, "Top 3 three times"),
            MedalTierInfo(BadgeTier.GOLD, 5, "Top 3 five times"),
            MedalTierInfo(BadgeTier.DIAMOND, 10, "Top 3 ten times"),
            MedalTierInfo(BadgeTier.LEGENDARY, 25, "Top 3 twenty-five times")
        )
    )

    val POP_LEGEND = MedalBadge(
        id = "pop_legend",
        name = "Pop Legend",
        description = "Legend of popping",
        category = BadgeCategory.PROGRESS,
        shape = BadgeShape.COSMIC_ORB,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 5000, "5,000 pops"),
            MedalTierInfo(BadgeTier.SILVER, 25000, "25,000 pops"),
            MedalTierInfo(BadgeTier.GOLD, 100000, "100,000 pops"),
            MedalTierInfo(BadgeTier.DIAMOND, 250000, "250,000 pops"),
            MedalTierInfo(BadgeTier.LEGENDARY, 1000000, "1,000,000 pops")
        )
    )

    val DEDICATED = MedalBadge(
        id = "dedicated",
        name = "Dedicated",
        description = "Dedicated player",
        category = BadgeCategory.PROGRESS,
        shape = BadgeShape.MYSTIC_HEXAGON,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 3, "3 consecutive days"),
            MedalTierInfo(BadgeTier.SILVER, 7, "7 consecutive days"),
            MedalTierInfo(BadgeTier.GOLD, 14, "14 consecutive days"),
            MedalTierInfo(BadgeTier.DIAMOND, 30, "30 consecutive days"),
            MedalTierInfo(BadgeTier.LEGENDARY, 100, "100 consecutive days")
        )
    )

    val COLLECTOR = MedalBadge(
        id = "collector",
        name = "Collector",
        description = "Item collector",
        category = BadgeCategory.PROGRESS,
        shape = BadgeShape.MYSTIC_HEXAGON,
        tiers = listOf(
            MedalTierInfo(BadgeTier.BRONZE, 5, "5 items"),
            MedalTierInfo(BadgeTier.SILVER, 10, "10 items"),
            MedalTierInfo(BadgeTier.GOLD, 20, "20 items"),
            MedalTierInfo(BadgeTier.DIAMOND, 30, "All items")
        )
    )

    val OG_PLAYER = MedalBadge(
        id = "og_player",
        name = "OG Player",
        description = "Original player",
        category = BadgeCategory.SPECIAL,
        shape = BadgeShape.DRAGON_EMBLEM,
        tiers = listOf(
            MedalTierInfo(BadgeTier.LEGENDARY, 1, "From the beginning")
        )
    )

    val ALL_MEDALS = listOf(
        SCORE_MASTER, BUBBLE_KING, STREAK_FURY, TIME_MASTER, COMBO_MASTER, SPEED_DEMON, ENDURANCE_CHAMPION, SHARPSHOOTER,
        CHALLENGER,
        TOURNAMENT_WARRIOR, CHAMPION, PODIUM_STAR,
        POP_LEGEND, DEDICATED, COLLECTOR,
        OG_PLAYER
    )
}

// ==================== MEDAL MANAGER (CORREGIDO) ====================

class MedalManager(private val dataStore: DataStoreManager) {

    suspend fun getMedalProgress(medal: MedalBadge): MedalProgress {
        // Primero calcular el tier actual
        val currentTier = calculateCurrentTier(medal)

        // Obtener el valor a mostrar basado en el tier actual
        val currentValue = getCurrentValueForMedal(medal, currentTier)

        // Obtener el siguiente requisito
        val nextTierReq = getNextTierRequirement(medal, currentTier)
        val isMaxed = currentTier == medal.tiers.lastOrNull()?.tier

        // Calcular el progreso hacia el siguiente tier
        val progressPercent = calculateProgressPercent(medal, currentTier, nextTierReq, isMaxed)

        return MedalProgress(
            badge = medal,
            currentTier = currentTier,
            currentValue = currentValue,
            nextTierRequirement = nextTierReq,
            isMaxed = isMaxed,
            progressPercent = progressPercent
        )
    }

    /**
     * Obtiene el valor actual para mostrar, basado en el tier actual.
     * Para medallas duales:  si estamos en Gold o superior, mostramos completaciones.
     */
    private suspend fun getCurrentValueForMedal(medal: MedalBadge, currentTier: BadgeTier): Int {
        return when (medal.id) {
            "score_master" -> dataStore.highScoreFlow().first()

            "bubble_king" -> {
                // Si ya alcanzó Gold, mostrar completaciones; sino, mostrar score
                if (currentTier.level >= BadgeTier.GOLD.level) {
                    dataStore.bubbleKingCompletionsFlow().first()
                } else {
                    dataStore.highScoreBubbleKingFlow().first()
                }
            }

            "streak_fury" -> {
                if (currentTier.level >= BadgeTier.GOLD.level) {
                    dataStore.perfectStreakCompletionsFlow().first()
                } else {
                    dataStore.highScorePerfectStreakFlow().first()
                }
            }

            "time_master" -> {
                if (currentTier.level >= BadgeTier.GOLD.level) {
                    dataStore.timeMasterCompletionsFlow().first()
                } else {
                    dataStore.highScoreTimeMasterFlow().first()
                }
            }

            "combo_master" -> {
                if (currentTier.level >= BadgeTier.GOLD.level) {
                    dataStore.comboMasterCompletionsFlow().first()
                } else {
                    dataStore.highScoreComboMasterFlow().first()
                }
            }

            "speed_demon" -> {
                if (currentTier.level >= BadgeTier.GOLD.level) {
                    dataStore.speedDemonCompletionsFlow().first()
                } else {
                    dataStore.highScoreSpeedDemonFlow().first()
                }
            }

            "endurance_champion" -> {
                if (currentTier.level >= BadgeTier.GOLD.level) {
                    dataStore.enduranceChampionCompletionsFlow().first()
                } else {
                    dataStore.highScoreEnduranceChampionFlow().first()
                }
            }

            "sharpshooter" -> dataStore.bestClickPercentFlow().first()

            "challenger" -> {
                // Para challenger, el valor depende del tier
                when {
                    currentTier == BadgeTier.LEGENDARY -> 60 // Representa 10 completaciones * 6 desafíos
                    currentTier == BadgeTier.DIAMOND -> 6    // Todos los desafíos en Gold
                    else -> dataStore.challengesCompletedCountFlow().first()
                }
            }

            "tournament_warrior" -> dataStore.tournamentsParticipatedFlow().first()
            "champion" -> dataStore.tournamentWinsFlow().first()
            "podium_star" -> dataStore.tournamentPodiumsFlow().first()
            "pop_legend" -> dataStore.totalPopsFlow().first()
            "dedicated" -> dataStore.maxConsecutiveDaysFlow().first()
            "collector" -> getOwnedItemsCount()
            "og_player" -> if (dataStore.isOgPlayer()) 1 else 0
            else -> 0
        }
    }

    /**
     * Calcula el tier actual de forma centralizada.
     * Esta es la fuente de verdad para determinar el tier.
     */
    private suspend fun calculateCurrentTier(medal: MedalBadge): BadgeTier {
        return when (medal.id) {
            // Medallas con sistema dual (score para Bronze-Gold, completaciones para Diamond-Legendary)
            "bubble_king" -> calculateDualTier(
                highScore = dataStore.highScoreBubbleKingFlow().first(),
                completions = dataStore.bubbleKingCompletionsFlow().first(),
                goldThreshold = 5000,
                medal = medal
            )

            "streak_fury" -> calculateDualTier(
                highScore = dataStore.highScorePerfectStreakFlow().first(),
                completions = dataStore.perfectStreakCompletionsFlow().first(),
                goldThreshold = 100,
                medal = medal
            )

            "time_master" -> calculateDualTier(
                highScore = dataStore.highScoreTimeMasterFlow().first(),
                completions = dataStore.timeMasterCompletionsFlow().first(),
                goldThreshold = 50,
                medal = medal
            )

            "combo_master" -> calculateDualTier(
                highScore = dataStore.highScoreComboMasterFlow().first(),
                completions = dataStore.comboMasterCompletionsFlow().first(),
                goldThreshold = 50,
                medal = medal
            )

            "speed_demon" -> calculateDualTier(
                highScore = dataStore.highScoreSpeedDemonFlow().first(),
                completions = dataStore.speedDemonCompletionsFlow().first(),
                goldThreshold = 100,
                medal = medal
            )

            "endurance_champion" -> calculateDualTier(
                highScore = dataStore.highScoreEnduranceChampionFlow().first(),
                completions = dataStore.enduranceChampionCompletionsFlow().first(),
                goldThreshold = 180,
                medal = medal
            )

            // Challenger tiene lógica especial
            "challenger" -> calculateChallengerTier()

            // Medallas estándar (solo basadas en un valor)
            else -> calculateStandardTier(medal)
        }
    }

    /**
     * Calcula el tier para medallas con sistema dual (score + completaciones)
     */
    private fun calculateDualTier(
        highScore: Int,
        completions: Int,
        goldThreshold: Int,
        medal: MedalBadge
    ): BadgeTier {
        var tier = BadgeTier.LOCKED

        // Primero evaluar tiers basados en score (Bronze, Silver, Gold)
        for (t in medal.tiers.filter { it.tier.level <= BadgeTier.GOLD.level }) {
            if (highScore >= t.requirement) {
                tier = t.tier
            }
        }

        // Solo si alcanzó Gold, evaluar tiers de completaciones (Diamond, Legendary)
        // IMPORTANTE: Solo avanza si tiene completaciones > 0
        if (tier == BadgeTier.GOLD && completions > 0) {
            for (t in medal.tiers.filter { it.tier.level > BadgeTier.GOLD.level }) {
                if (completions >= t.requirement) {
                    tier = t.tier
                }
            }
        }

        return tier
    }

    /**
     * Calcula el tier para la medalla Challenger
     */
    private suspend fun calculateChallengerTier(): BadgeTier {
        val completedCount = dataStore.challengesCompletedCountFlow().first()
        var tier = BadgeTier.LOCKED

        // Bronze: 1 challenge completed
        if (completedCount >= 1) tier = BadgeTier.BRONZE
        // Silver: 3 challenges completed
        if (completedCount >= 3) tier = BadgeTier.SILVER
        // Gold: all 6 challenges completed
        if (completedCount >= 6) tier = BadgeTier.GOLD

        // Diamond: all 6 challenges at Gold tier or better
        if (tier == BadgeTier.GOLD) {
            if (checkAllChallengesGoldOrBetter()) {
                tier = BadgeTier.DIAMOND
            }
        }

        // Legendary: all 6 challenges completed 10+ times each
        if (tier == BadgeTier.DIAMOND) {
            if (checkAllChallengesLegendary()) {
                tier = BadgeTier.LEGENDARY
            }
        }

        return tier
    }

    /**
     * Calcula el tier para medallas estándar (basadas en un solo valor)
     */
    private suspend fun calculateStandardTier(medal: MedalBadge): BadgeTier {
        val value = when (medal.id) {
            "score_master" -> dataStore.highScoreFlow().first()
            "sharpshooter" -> dataStore.bestClickPercentFlow().first()
            "tournament_warrior" -> dataStore.tournamentsParticipatedFlow().first()
            "champion" -> dataStore.tournamentWinsFlow().first()
            "podium_star" -> dataStore.tournamentPodiumsFlow().first()
            "pop_legend" -> dataStore.totalPopsFlow().first()
            "dedicated" -> dataStore.maxConsecutiveDaysFlow().first()
            "collector" -> getOwnedItemsCount()
            "og_player" -> if (dataStore.isOgPlayer()) 1 else 0
            else -> 0
        }

        var tier = BadgeTier.LOCKED
        for (t in medal.tiers) {
            if (value >= t.requirement) {
                tier = t.tier
            }
        }
        return tier
    }

    /**
     * Calcula el porcentaje de progreso hacia el siguiente tier
     */
    private suspend fun calculateProgressPercent(
        medal: MedalBadge,
        currentTier: BadgeTier,
        nextTierReq: Int?,
        isMaxed: Boolean
    ): Float {
        if (isMaxed) return 1f
        if (nextTierReq == null || nextTierReq <= 0) return 0f

        // Obtener el requisito del tier actual (o 0 si está bloqueado)
        val currentTierReq = if (currentTier == BadgeTier.LOCKED) {
            0
        } else {
            medal.tiers.find { it.tier == currentTier }?.requirement ?: 0
        }

        // Obtener el valor actual relevante para el progreso
        val currentProgressValue = getProgressValue(medal, currentTier, nextTierReq)

        // Calcular el progreso
        val range = nextTierReq - currentTierReq
        if (range <= 0) return 0f

        val progress = currentProgressValue - currentTierReq
        return (progress.toFloat() / range).coerceIn(0f, 1f)
    }

    /**
     * Obtiene el valor a usar para calcular el progreso.
     * Para medallas duales, determina si usar score o completaciones.
     */
    private suspend fun getProgressValue(
        medal: MedalBadge,
        currentTier: BadgeTier,
        nextTierReq: Int
    ): Int {
        // Determinar si el siguiente tier es basado en completaciones
        val nextTierInfo = medal.tiers.find { it.requirement == nextTierReq }
        val isNextTierCompletionBased = nextTierInfo?.tier?.level?.let { it > BadgeTier.GOLD.level } ?: false

        return when (medal.id) {
            "bubble_king" -> {
                if (isNextTierCompletionBased) {
                    dataStore.bubbleKingCompletionsFlow().first()
                } else {
                    dataStore.highScoreBubbleKingFlow().first()
                }
            }
            "streak_fury" -> {
                if (isNextTierCompletionBased) {
                    dataStore.perfectStreakCompletionsFlow().first()
                } else {
                    dataStore.highScorePerfectStreakFlow().first()
                }
            }
            "time_master" -> {
                if (isNextTierCompletionBased) {
                    dataStore.timeMasterCompletionsFlow().first()
                } else {
                    dataStore.highScoreTimeMasterFlow().first()
                }
            }
            "combo_master" -> {
                if (isNextTierCompletionBased) {
                    dataStore.comboMasterCompletionsFlow().first()
                } else {
                    dataStore.highScoreComboMasterFlow().first()
                }
            }
            "speed_demon" -> {
                if (isNextTierCompletionBased) {
                    dataStore.speedDemonCompletionsFlow().first()
                } else {
                    dataStore.highScoreSpeedDemonFlow().first()
                }
            }
            "endurance_champion" -> {
                if (isNextTierCompletionBased) {
                    dataStore.enduranceChampionCompletionsFlow().first()
                } else {
                    dataStore.highScoreEnduranceChampionFlow().first()
                }
            }
            "challenger" -> {
                // Para challenger depende del tier actual
                when {
                    currentTier.level >= BadgeTier.DIAMOND.level -> {
                        // Progreso hacia Legendary:  contar total de completaciones
                        getTotalChallengeCompletions()
                    }
                    currentTier == BadgeTier.GOLD -> {
                        // Progreso hacia Diamond: cuenta de challenges con gold
                        getGoldChallengesCount()
                    }
                    else -> dataStore.challengesCompletedCountFlow().first()
                }
            }
            else -> getCurrentValueForMedal(medal, currentTier)
        }
    }

    /**
     * Cuenta cuántos challenges tienen Gold o mejor
     */
    private suspend fun getGoldChallengesCount(): Int {
        var count = 0
        if (dataStore.highScoreBubbleKingFlow().first() >= 5000) count++
        if (dataStore.highScorePerfectStreakFlow().first() >= 100) count++
        if (dataStore.highScoreTimeMasterFlow().first() >= 50) count++
        if (dataStore.highScoreComboMasterFlow().first() >= 50) count++
        if (dataStore.highScoreSpeedDemonFlow().first() >= 100) count++
        if (dataStore.highScoreEnduranceChampionFlow().first() >= 180) count++
        return count
    }

    /**
     * Obtiene el total de completaciones de todos los challenges
     */
    private suspend fun getTotalChallengeCompletions(): Int {
        return dataStore.bubbleKingCompletionsFlow().first() +
                dataStore.perfectStreakCompletionsFlow().first() +
                dataStore.timeMasterCompletionsFlow().first() +
                dataStore.comboMasterCompletionsFlow().first() +
                dataStore.speedDemonCompletionsFlow().first() +
                dataStore.enduranceChampionCompletionsFlow().first()
    }

    private fun getNextTierRequirement(medal: MedalBadge, currentTier: BadgeTier): Int?  {
        if (currentTier == BadgeTier.LOCKED) {
            return medal.tiers.firstOrNull()?.requirement
        }
        val idx = medal.tiers.indexOfFirst { it.tier == currentTier }
        return if (idx >= 0 && idx < medal.tiers.size - 1) {
            medal.tiers[idx + 1].requirement
        } else {
            null
        }
    }

    // Check if all 6 challenges have at least Gold tier
    private suspend fun checkAllChallengesGoldOrBetter(): Boolean {
        val bubbleKing = dataStore.highScoreBubbleKingFlow().first() >= 5000
        val perfectStreak = dataStore.highScorePerfectStreakFlow().first() >= 100
        val timeMaster = dataStore.highScoreTimeMasterFlow().first() >= 50
        val comboMaster = dataStore.highScoreComboMasterFlow().first() >= 50
        val speedDemon = dataStore.highScoreSpeedDemonFlow().first() >= 100
        val endurance = dataStore.highScoreEnduranceChampionFlow().first() >= 180
        return bubbleKing && perfectStreak && timeMaster && comboMaster && speedDemon && endurance
    }

    // Check if all 6 challenges have been completed 10+ times (Legendary)
    private suspend fun checkAllChallengesLegendary(): Boolean {
        val bubbleKing = dataStore.bubbleKingCompletionsFlow().first() >= 10
        val perfectStreak = dataStore.perfectStreakCompletionsFlow().first() >= 10
        val timeMaster = dataStore.timeMasterCompletionsFlow().first() >= 10
        val comboMaster = dataStore.comboMasterCompletionsFlow().first() >= 10
        val speedDemon = dataStore.speedDemonCompletionsFlow().first() >= 10
        val endurance = dataStore.enduranceChampionCompletionsFlow().first() >= 10
        return bubbleKing && perfectStreak && timeMaster && comboMaster && speedDemon && endurance
    }

    private suspend fun getOwnedItemsCount(): Int {
        var count = 0
        // Backgrounds (1-21)
        for (i in 1..21) {
            if (dataStore.isBackgroundPurchasedFlow(i).first()) count++
        }
        // Bubbles (1-30)
        for (i in 1..30) {
            if (dataStore.isBubblePurchasedFlow(i).first()) count++
        }
        // MainMenus (1-20)
        for (i in 1..20) {
            if (dataStore.isMainMenuPurchasedFlow(i).first()) count++
        }
        return count
    }

    suspend fun getAllMedalsProgress(): List<MedalProgress> {
        return MedalDefinitions.ALL_MEDALS.map { getMedalProgress(it) }
    }

    suspend fun getUnlockedCount(): Int {
        return getAllMedalsProgress().count { it.currentTier != BadgeTier.LOCKED }
    }
}
// ==================== ADVANCED DRAWING FUNCTIONS ====================

private fun DrawScope.drawMedalShape(
    shape: BadgeShape,
    colors: MedalColors,
    tier: BadgeTier,
    animProgress: Float,
    pulseScale: Float,
    glowIntensity: Float
) {
    val cx = size.width / 2
    val cy = size.height / 2
    val baseRadius = size.minDimension / 2 - 12f
    val isLegendary = tier == BadgeTier.LEGENDARY
    val isDiamond = tier == BadgeTier.DIAMOND
    val isGold = tier == BadgeTier.GOLD

    // Outer glow effect based on tier
    if (tier != BadgeTier.LOCKED) {
        val glowRadius = baseRadius * (1.2f + glowIntensity * 0.15f)
        val glowAlpha = when (tier) {
            BadgeTier.LEGENDARY -> 0.6f + glowIntensity * 0.3f
            BadgeTier.DIAMOND -> 0.4f + glowIntensity * 0.2f
            BadgeTier.GOLD -> 0.3f + glowIntensity * 0.15f
            else -> 0.2f + glowIntensity * 0.1f
        }
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.glow.copy(alpha = glowAlpha),
                    colors.glow.copy(alpha = glowAlpha * 0.5f),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = glowRadius
            ),
            radius = glowRadius,
            center = Offset(cx, cy)
        )
    }

    when (shape) {
        BadgeShape.ROYAL_MEDAL -> drawRoyalMedal(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
        BadgeShape.WARRIOR_SHIELD -> drawWarriorShield(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
        BadgeShape.CELESTIAL_STAR -> drawCelestialStar(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
        BadgeShape.MYSTIC_HEXAGON -> drawMysticHexagon(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
        BadgeShape.IMPERIAL_CROWN -> drawImperialCrown(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
        BadgeShape.PHOENIX_FLAME -> drawPhoenixFlame(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
        BadgeShape.DRAGON_EMBLEM -> drawDragonEmblem(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
        BadgeShape.COSMIC_ORB -> drawCosmicOrb(cx, cy, baseRadius, colors, tier, animProgress, pulseScale)
    }

    // Legendary fire ring
    if (isLegendary) {
        drawLegendaryFireRing(cx, cy, baseRadius, colors, animProgress)
    }

    // Diamond sparkles
    if (isDiamond) {
        drawDiamondSparkles(cx, cy, baseRadius, colors, animProgress)
    }

    // Gold shimmer
    if (isGold) {
        drawGoldShimmer(cx, cy, baseRadius, colors, animProgress)
    }
}

private fun DrawScope.drawRoyalMedal(
    cx: Float, cy: Float, radius: Float,
    colors: MedalColors, tier: BadgeTier, anim: Float, pulse: Float
) {
    val r = radius * pulse

    // Ribbon at top
    val ribbonPath = Path().apply {
        moveTo(cx - r * 0.3f, cy - r * 0.85f)
        lineTo(cx - r * 0.5f, cy - r * 1.3f)
        lineTo(cx - r * 0.2f, cy - r * 1.1f)
        lineTo(cx, cy - r * 1.3f)
        lineTo(cx + r * 0.2f, cy - r * 1.1f)
        lineTo(cx + r * 0.5f, cy - r * 1.3f)
        lineTo(cx + r * 0.3f, cy - r * 0.85f)
        close()
    }
    drawPath(
        path = ribbonPath,
        brush = Brush.linearGradient(
            colors = listOf(colors.secondary, colors.primary, colors.secondary),
            start = Offset(cx - r * 0.5f, cy - r * 1.3f),
            end = Offset(cx + r * 0.5f, cy - r * 1.3f)
        )
    )

    // Outer decorative ring with notches
    for (i in 0 until 24) {
        val angle = (PI * 2 * i / 24).toFloat()
        val notchOuter = r * 1.05f
        val notchInner = r * 0.95f
        drawLine(
            color = colors.border.copy(alpha = 0.6f),
            start = Offset(cx + notchInner * cos(angle), cy + notchInner * sin(angle)),
            end = Offset(cx + notchOuter * cos(angle), cy + notchOuter * sin(angle)),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }

    // Main medal body with 3D effect
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colors.accent, colors.primary, colors.secondary),
            center = Offset(cx - r * 0.25f, cy - r * 0.25f),
            radius = r * 1.8f
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // Inner ring
    drawCircle(
        color = colors.border,
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = 4f)
    )

    // Second inner ring
    drawCircle(
        color = colors.highlight.copy(alpha = 0.5f),
        radius = r * 0.85f,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )

    // Center emblem - star pattern
    val starPoints = 8
    val innerStarRadius = r * 0.25f
    val outerStarRadius = r * 0.5f
    val starPath = Path().apply {
        for (i in 0 until starPoints * 2) {
            val angle = (PI / 2 + PI * i / starPoints).toFloat()
            val rad = if (i % 2 == 0) outerStarRadius else innerStarRadius
            val x = cx + rad * cos(angle)
            val y = cy + rad * sin(angle)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    rotate(degrees = anim * 360f * 0.1f, pivot = Offset(cx, cy)) {
        drawPath(
            path = starPath,
            brush = Brush.sweepGradient(
                colors = listOf(colors.highlight, colors.accent, colors.primary, colors.accent, colors.highlight),
                center = Offset(cx, cy)
            )
        )
        drawPath(
            path = starPath,
            color = colors.border.copy(alpha = 0.8f),
            style = Stroke(width = 1.5f)
        )
    }

    // Center jewel
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, colors.highlight, colors.accent),
            center = Offset(cx - r * 0.05f, cy - r * 0.05f),
            radius = r * 0.2f
        ),
        radius = r * 0.12f,
        center = Offset(cx, cy)
    )

    // Light reflection
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
            start = Offset(cx - r * 0.4f, cy - r * 0.4f),
            end = Offset(cx + r * 0.2f, cy + r * 0.2f)
        ),
        radius = r * 0.7f,
        center = Offset(cx - r * 0.15f, cy - r * 0.15f)
    )
}

private fun DrawScope.drawWarriorShield(
    cx: Float, cy: Float, radius: Float,
    colors: MedalColors, tier: BadgeTier, anim: Float, pulse: Float
) {
    val r = radius * pulse

    // Shield body path
    val shieldPath = Path().apply {
        moveTo(cx, cy - r * 0.95f)
        // Top curve
        cubicTo(
            cx + r * 0.5f, cy - r * 0.9f,
            cx + r * 0.9f, cy - r * 0.6f,
            cx + r * 0.95f, cy - r * 0.2f
        )
        // Right side
        lineTo(cx + r * 0.9f, cy + r * 0.3f)
        // Bottom right curve
        cubicTo(
            cx + r * 0.7f, cy + r * 0.7f,
            cx + r * 0.3f, cy + r * 0.95f,
            cx, cy + r * 1.05f
        )
        // Bottom left curve
        cubicTo(
            cx - r * 0.3f, cy + r * 0.95f,
            cx - r * 0.7f, cy + r * 0.7f,
            cx - r * 0.9f, cy + r * 0.3f
        )
        // Left side
        lineTo(cx - r * 0.95f, cy - r * 0.2f)
        // Top left curve
        cubicTo(
            cx - r * 0.9f, cy - r * 0.6f,
            cx - r * 0.5f, cy - r * 0.9f,
            cx, cy - r * 0.95f
        )
        close()
    }

    // Shield gradient fill
    drawPath(
        path = shieldPath,
        brush = Brush.linearGradient(
            colors = listOf(colors.accent, colors.primary, colors.secondary, colors.primary),
            start = Offset(cx - r, cy - r),
            end = Offset(cx + r, cy + r)
        )
    )

    // Inner shield detail
    val innerShieldPath = Path().apply {
        moveTo(cx, cy - r * 0.7f)
        cubicTo(cx + r * 0.35f, cy - r * 0.65f, cx + r * 0.65f, cy - r * 0.4f, cx + r * 0.7f, cy - r * 0.1f)
        lineTo(cx + r * 0.65f, cy + r * 0.2f)
        cubicTo(cx + r * 0.5f, cy + r * 0.5f, cx + r * 0.2f, cy + r * 0.7f, cx, cy + r * 0.8f)
        cubicTo(cx - r * 0.2f, cy + r * 0.7f, cx - r * 0.5f, cy + r * 0.5f, cx - r * 0.65f, cy + r * 0.2f)
        lineTo(cx - r * 0.7f, cy - r * 0.1f)
        cubicTo(cx - r * 0.65f, cy - r * 0.4f, cx - r * 0.35f, cy - r * 0.65f, cx, cy - r * 0.7f)
        close()
    }

    drawPath(
        path = innerShieldPath,
        brush = Brush.radialGradient(
            colors = listOf(colors.highlight.copy(alpha = 0.3f), colors.primary.copy(alpha = 0.1f)),
            center = Offset(cx, cy),
            radius = r * 0.8f
        )
    )

    // Central cross/sword emblem
    val swordWidth = r * 0.08f
    val swordPath = Path().apply {
        // Vertical blade
        moveTo(cx - swordWidth, cy - r * 0.5f)
        lineTo(cx + swordWidth, cy - r * 0.5f)
        lineTo(cx + swordWidth, cy + r * 0.4f)
        lineTo(cx, cy + r * 0.6f)
        lineTo(cx - swordWidth, cy + r * 0.4f)
        close()
    }
    drawPath(
        path = swordPath,
        brush = Brush.linearGradient(
            colors = listOf(colors.highlight, colors.accent, colors.highlight),
            start = Offset(cx - swordWidth, cy),
            end = Offset(cx + swordWidth, cy)
        )
    )

    // Crossguard
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(colors.secondary, colors.accent, colors.secondary)
        ),
        start = Offset(cx - r * 0.3f, cy - r * 0.2f),
        end = Offset(cx + r * 0.3f, cy - r * 0.2f),
        strokeWidth = r * 0.1f,
        cap = StrokeCap.Round
    )

    // Border
    drawPath(
        path = shieldPath,
        color = colors.border,
        style = Stroke(width = 4f)
    )

    // Rivets
    val rivetPositions = listOf(
        Offset(cx, cy - r * 0.85f),
        Offset(cx + r * 0.7f, cy - r * 0.4f),
        Offset(cx - r * 0.7f, cy - r * 0.4f),
        Offset(cx + r * 0.6f, cy + r * 0.4f),
        Offset(cx - r * 0.6f, cy + r * 0.4f)
    )
    rivetPositions.forEach { pos ->
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.highlight, colors.border),
                center = pos,
                radius = r * 0.06f
            ),
            radius = r * 0.05f,
            center = pos
        )
    }
}

private fun DrawScope.drawCelestialStar(
    cx: Float, cy: Float, radius: Float,
    colors: MedalColors, tier: BadgeTier, anim: Float, pulse: Float
) {
    val r = radius * pulse
    val points = 6
    val outerRadius = r
    val innerRadius = r * 0.45f
    val midRadius = r * 0.7f

    // Outer rays
    for (i in 0 until 12) {
        val angle = (PI * 2 * i / 12 + anim * PI * 0.5f).toFloat()
        val rayLength = if (i % 2 == 0) r * 1.2f else r * 1.05f
        val rayWidth = if (i % 2 == 0) 3f else 2f
        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(colors.glow, colors.glow.copy(alpha = 0f)),
                start = Offset(cx + r * 0.5f * cos(angle), cy + r * 0.5f * sin(angle)),
                end = Offset(cx + rayLength * cos(angle), cy + rayLength * sin(angle))
            ),
            start = Offset(cx + r * 0.5f * cos(angle), cy + r * 0.5f * sin(angle)),
            end = Offset(cx + rayLength * cos(angle), cy + rayLength * sin(angle)),
            strokeWidth = rayWidth,
            cap = StrokeCap.Round
        )
    }

    // Main 6-pointed star
    val starPath = Path().apply {
        for (i in 0 until points * 2) {
            val angle = (-PI / 2 + PI * i / points).toFloat()
            val rad = if (i % 2 == 0) outerRadius else innerRadius
            val x = cx + rad * cos(angle)
            val y = cy + rad * sin(angle)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    // Star gradient fill
    drawPath(
        path = starPath,
        brush = Brush.sweepGradient(
            colors = listOf(
                colors.accent, colors.primary, colors.accent,
                colors.primary, colors.accent, colors.primary, colors.accent
            ),
            center = Offset(cx, cy)
        )
    )

    // Inner layer
    val innerStarPath = Path().apply {
        for (i in 0 until points * 2) {
            val angle = (-PI / 2 + PI * i / points).toFloat()
            val rad = if (i % 2 == 0) midRadius else innerRadius * 0.7f
            val x = cx + rad * cos(angle)
            val y = cy + rad * sin(angle)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }

    drawPath(
        path = innerStarPath,
        brush = Brush.radialGradient(
            colors = listOf(colors.highlight, colors.accent, colors.primary),
            center = Offset(cx - r * 0.15f, cy - r * 0.15f),
            radius = midRadius
        )
    )

    // Center circle with concentric rings
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, colors.highlight, colors.accent),
            center = Offset(cx, cy),
            radius = r * 0.25f
        ),
        radius = r * 0.22f,
        center = Offset(cx, cy)
    )

    drawCircle(
        color = colors.border,
        radius = r * 0.22f,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )

    // Border
    drawPath(
        path = starPath,
        color = colors.border,
        style = Stroke(width = 3f)
    )
}

private fun DrawScope.drawMysticHexagon(
    cx:  Float, cy: Float, radius: Float,
    colors: MedalColors, tier: BadgeTier, anim: Float, pulse: Float
) {
    val r = radius * pulse

    // Create hexagon path
    fun hexPath(rad: Float, rotation: Float = 0f): Path {
        return Path().apply {
            for (i in 0 until 6) {
                val angle = (PI / 6 + PI / 3 * i + rotation).toFloat()
                val x = cx + rad * cos(angle)
                val y = cy + rad * sin(angle)
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
    }

    // Outer hexagon
    val outerHex = hexPath(r)
    drawPath(
        path = outerHex,
        brush = Brush.linearGradient(
            colors = listOf(colors.primary, colors.accent, colors.primary),
            start = Offset(cx - r, cy - r),
            end = Offset(cx + r, cy + r)
        )
    )

    // Rotating middle hexagon
    rotate(degrees = anim * 30f, pivot = Offset(cx, cy)) {
        val midHex = hexPath(r * 0.75f, PI.toFloat() / 6)
        drawPath(
            path = midHex,
            brush = Brush.sweepGradient(
                colors = listOf(colors.secondary, colors.primary, colors.accent, colors.primary, colors.secondary),
                center = Offset(cx, cy)
            )
        )
        drawPath(
            path = midHex,
            color = colors.border.copy(alpha = 0.5f),
            style = Stroke(width = 2f)
        )
    }

    // Inner hexagon
    val innerHex = hexPath(r * 0.5f)
    drawPath(
        path = innerHex,
        brush = Brush.radialGradient(
            colors = listOf(colors.highlight, colors.accent, colors.primary),
            center = Offset(cx - r * 0.1f, cy - r * 0.1f),
            radius = r * 0.6f
        )
    )

    // Mystical runes on edges
    for (i in 0 until 6) {
        val angle = (PI / 6 + PI / 3 * i).toFloat()
        val runeX = cx + r * 0.85f * cos(angle)
        val runeY = cy + r * 0.85f * sin(angle)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.highlight, colors.glow.copy(alpha = 0f)),
                center = Offset(runeX, runeY),
                radius = r * 0.12f
            ),
            radius = r * 0.08f,
            center = Offset(runeX, runeY)
        )
    }

    // Center eye/jewel
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, colors.highlight, colors.glow),
            center = Offset(cx - r * 0.03f, cy - r * 0.03f),
            radius = r * 0.2f
        ),
        radius = r * 0.15f,
        center = Offset(cx, cy)
    )

    // Border
    drawPath(
        path = outerHex,
        color = colors.border,
        style = Stroke(width = 4f)
    )
}

private fun DrawScope.drawImperialCrown(
    cx: Float, cy: Float, radius:  Float,
    colors: MedalColors, tier: BadgeTier, anim: Float, pulse:  Float
) {
    val r = radius * pulse

    // Crown base (band)
    val bandTop = cy + r * 0.2f
    val bandBottom = cy + r * 0.5f
    val bandPath = Path().apply {
        moveTo(cx - r * 0.85f, bandBottom)
        lineTo(cx - r * 0.9f, bandTop)
        lineTo(cx + r * 0.9f, bandTop)
        lineTo(cx + r * 0.85f, bandBottom)
        close()
    }
    drawPath(
        path = bandPath,
        brush = Brush.linearGradient(
            colors = listOf(colors.secondary, colors.primary, colors.secondary),
            start = Offset(cx - r, bandTop),
            end = Offset(cx + r, bandTop)
        )
    )

    // Crown peaks
    val crownPath = Path().apply {
        moveTo(cx - r * 0.9f, bandTop)
        // Left peak
        lineTo(cx - r * 0.75f, cy - r * 0.3f)
        lineTo(cx - r * 0.55f, bandTop * 0.8f + cy * 0.2f)
        // Left-center peak
        lineTo(cx - r * 0.35f, cy - r * 0.6f)
        lineTo(cx - r * 0.15f, bandTop * 0.7f + cy * 0.3f)
        // Center peak (tallest)
        lineTo(cx, cy - r * 0.95f)
        lineTo(cx + r * 0.15f, bandTop * 0.7f + cy * 0.3f)
        // Right-center peak
        lineTo(cx + r * 0.35f, cy - r * 0.6f)
        lineTo(cx + r * 0.55f, bandTop * 0.8f + cy * 0.2f)
        // Right peak
        lineTo(cx + r * 0.75f, cy - r * 0.3f)
        lineTo(cx + r * 0.9f, bandTop)
        close()
    }

    drawPath(
        path = crownPath,
        brush = Brush.linearGradient(
            colors = listOf(colors.accent, colors.primary, colors.accent, colors.primary, colors.accent),
            start = Offset(cx, cy - r),
            end = Offset(cx, bandTop)
        )
    )

    // Peak jewels
    val jewelPositions = listOf(
        Offset(cx - r * 0.75f, cy - r * 0.25f),
        Offset(cx - r * 0.35f, cy - r * 0.55f),
        Offset(cx, cy - r * 0.85f),
        Offset(cx + r * 0.35f, cy - r * 0.55f),
        Offset(cx + r * 0.75f, cy - r * 0.25f)
    )

    val jewelColors = listOf(Color.Red, Color.Blue, Color.White, Color.Green, Color.Red)

    jewelPositions.forEachIndexed { index, pos ->
        val shimmer = sin(anim * PI.toFloat() * 2 + index).coerceIn(0f, 1f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    jewelColors[index].copy(alpha = 0.8f + shimmer * 0.2f),
                    jewelColors[index].copy(alpha = 0.6f)
                ),
                center = Offset(pos.x - r * 0.02f, pos.y - r * 0.02f),
                radius = r * 0.12f
            ),
            radius = r * 0.08f,
            center = pos
        )
    }

    // Band decoration
    for (i in 0 until 7) {
        val x = cx - r * 0.7f + i * r * 0.233f
        val y = (bandTop + bandBottom) / 2
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.highlight, colors.border),
                center = Offset(x, y),
                radius = r * 0.08f
            ),
            radius = r * 0.05f,
            center = Offset(x, y)
        )
    }

    // Borders
    drawPath(path = crownPath, color = colors.border, style = Stroke(width = 3f))
    drawPath(path = bandPath, color = colors.border, style = Stroke(width = 3f))
}

private fun DrawScope.drawPhoenixFlame(
    cx:  Float, cy: Float, radius: Float,
    colors: MedalColors, tier: BadgeTier, anim: Float, pulse: Float
) {
    val r = radius * pulse
    val waveOffset = sin(anim * PI.toFloat() * 2) * r * 0.08f
    val waveOffset2 = sin(anim * PI.toFloat() * 2 + PI.toFloat() / 3) * r * 0.06f

    // Outer flame (largest)
    val outerFlamePath = Path().apply {
        moveTo(cx, cy + r * 0.9f)
        // Left outer curve
        cubicTo(
            cx - r * 0.5f, cy + r * 0.7f,
            cx - r * 0.9f, cy + r * 0.2f,
            cx - r * 0.7f + waveOffset, cy - r * 0.3f
        )
        // Left peak
        cubicTo(
            cx - r * 0.5f, cy - r * 0.6f,
            cx - r * 0.4f + waveOffset2, cy - r * 0.8f,
            cx - r * 0.25f, cy - r * 0.95f
        )
        // Center peak (tallest)
        cubicTo(
            cx - r * 0.1f, cy - r * 0.7f,
            cx, cy - r * 0.5f,
            cx, cy - r * 1.1f + waveOffset
        )
        cubicTo(
            cx, cy - r * 0.5f,
            cx + r * 0.1f, cy - r * 0.7f,
            cx + r * 0.25f, cy - r * 0.95f
        )
        // Right peak
        cubicTo(
            cx + r * 0.4f - waveOffset2, cy - r * 0.8f,
            cx + r * 0.5f, cy - r * 0.6f,
            cx + r * 0.7f - waveOffset, cy - r * 0.3f
        )
        // Right outer curve
        cubicTo(
            cx + r * 0.9f, cy + r * 0.2f,
            cx + r * 0.5f, cy + r * 0.7f,
            cx, cy + r * 0.9f
        )
        close()
    }

    drawPath(
        path = outerFlamePath,
        brush = Brush.verticalGradient(
            colors = listOf(
                colors.accent,
                colors.primary,
                colors.secondary,
                colors.secondary.copy(alpha = 0.8f)
            ),
            startY = cy - r * 1.1f,
            endY = cy + r * 0.9f
        )
    )

    // Middle flame
    val middleFlamePath = Path().apply {
        moveTo(cx, cy + r * 0.6f)
        cubicTo(
            cx - r * 0.35f, cy + r * 0.4f,
            cx - r * 0.5f, cy,
            cx - r * 0.35f - waveOffset2, cy - r * 0.4f
        )
        cubicTo(
            cx - r * 0.2f, cy - r * 0.6f,
            cx - r * 0.1f, cy - r * 0.5f,
            cx, cy - r * 0.8f - waveOffset * 0.5f
        )
        cubicTo(
            cx + r * 0.1f, cy - r * 0.5f,
            cx + r * 0.2f, cy - r * 0.6f,
            cx + r * 0.35f + waveOffset2, cy - r * 0.4f
        )
        cubicTo(
            cx + r * 0.5f, cy,
            cx + r * 0.35f, cy + r * 0.4f,
            cx, cy + r * 0.6f
        )
        close()
    }

    drawPath(
        path = middleFlamePath,
        brush = Brush.verticalGradient(
            colors = listOf(colors.highlight, colors.accent, colors.primary),
            startY = cy - r * 0.8f,
            endY = cy + r * 0.6f
        )
    )

    // Inner flame (core)
    val innerFlamePath = Path().apply {
        moveTo(cx, cy + r * 0.35f)
        cubicTo(
            cx - r * 0.2f, cy + r * 0.2f,
            cx - r * 0.25f, cy - r * 0.1f,
            cx - r * 0.15f + waveOffset, cy - r * 0.35f
        )
        cubicTo(
            cx - r * 0.05f, cy - r * 0.45f,
            cx, cy - r * 0.4f,
            cx, cy - r * 0.55f - waveOffset2
        )
        cubicTo(
            cx, cy - r * 0.4f,
            cx + r * 0.05f, cy - r * 0.45f,
            cx + r * 0.15f - waveOffset, cy - r * 0.35f
        )
        cubicTo(
            cx + r * 0.25f, cy - r * 0.1f,
            cx + r * 0.2f, cy + r * 0.2f,
            cx, cy + r * 0.35f
        )
        close()
    }

    drawPath(
        path = innerFlamePath,
        brush = Brush.radialGradient(
            colors = listOf(Color.White, colors.highlight, colors.accent),
            center = Offset(cx, cy - r * 0.2f),
            radius = r * 0.4f
        )
    )

    // Ember particles
    val particleCount = 8
    for (i in 0 until particleCount) {
        val particleAnim = (anim + i * 0.125f) % 1f
        val particleY = cy + r * 0.5f - particleAnim * r * 1.5f
        val particleX = cx + sin(particleAnim * PI.toFloat() * 4 + i) * r * 0.3f
        val particleAlpha = (1f - particleAnim).coerceIn(0f, 1f)
        val particleSize = r * 0.04f * (1f - particleAnim * 0.5f)

        if (particleY < cy + r * 0.5f && particleY > cy - r * 1.2f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.highlight.copy(alpha = particleAlpha),
                        colors.accent.copy(alpha = particleAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(particleX, particleY),
                    radius = particleSize * 2
                ),
                radius = particleSize * 2,
                center = Offset(particleX, particleY)
            )
        }
    }

    // Border glow
    drawPath(
        path = outerFlamePath,
        color = colors.border.copy(alpha = 0.6f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawDragonEmblem(
    cx: Float, cy:  Float, radius: Float,
    colors:  MedalColors, tier: BadgeTier, anim:  Float, pulse: Float
) {
    val r = radius * pulse

    // Background circle
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(colors.primary, colors.secondary, Color(0xFF0A0A0A)),
            center = Offset(cx, cy),
            radius = r
        ),
        radius = r,
        center = Offset(cx, cy)
    )

    // Outer ring with scale pattern
    for (i in 0 until 16) {
        val angle = (PI * 2 * i / 16).toFloat()
        val scaleSize = r * 0.12f
        val scaleX = cx + r * 0.85f * cos(angle)
        val scaleY = cy + r * 0.85f * sin(angle)

        val scalePath = Path().apply {
            moveTo(scaleX, scaleY - scaleSize * 0.6f)
            lineTo(scaleX + scaleSize * 0.5f, scaleY + scaleSize * 0.4f)
            lineTo(scaleX - scaleSize * 0.5f, scaleY + scaleSize * 0.4f)
            close()
        }

        rotate(degrees = (angle * 180 / PI).toFloat() + 90f, pivot = Offset(scaleX, scaleY)) {
            drawPath(
                path = scalePath,
                brush = Brush.linearGradient(
                    colors = listOf(colors.accent, colors.primary),
                    start = Offset(scaleX, scaleY - scaleSize),
                    end = Offset(scaleX, scaleY + scaleSize)
                )
            )
        }
    }

    // Dragon silhouette (simplified S-curve)
    val dragonPath = Path().apply {
        // Head
        moveTo(cx + r * 0.4f, cy - r * 0.5f)
        cubicTo(
            cx + r * 0.5f, cy - r * 0.6f,
            cx + r * 0.55f, cy - r * 0.45f,
            cx + r * 0.45f, cy - r * 0.35f
        )
        // Snout
        lineTo(cx + r * 0.6f, cy - r * 0.3f)
        lineTo(cx + r * 0.5f, cy - r * 0.25f)
        // Neck curve
        cubicTo(
            cx + r * 0.4f, cy - r * 0.15f,
            cx + r * 0.2f, cy,
            cx + r * 0.1f, cy + r * 0.15f
        )
        // Body curve
        cubicTo(
            cx - r * 0.1f, cy + r * 0.35f,
            cx - r * 0.35f, cy + r * 0.4f,
            cx - r * 0.45f, cy + r * 0.25f
        )
        // Tail
        cubicTo(
            cx - r * 0.55f, cy + r * 0.1f,
            cx - r * 0.5f, cy - r * 0.1f,
            cx - r * 0.35f, cy - r * 0.2f
        )
        lineTo(cx - r * 0.55f, cy - r * 0.35f)
        // Back to body
        cubicTo(
            cx - r * 0.4f, cy - r * 0.25f,
            cx - r * 0.3f, cy - r * 0.1f,
            cx - r * 0.2f, cy + r * 0.05f
        )
        // Wing
        lineTo(cx - r * 0.1f, cy - r * 0.3f)
        lineTo(cx + r * 0.15f, cy - r * 0.15f)
        // Back to head
        cubicTo(
            cx + r * 0.25f, cy - r * 0.25f,
            cx + r * 0.35f, cy - r * 0.4f,
            cx + r * 0.4f, cy - r * 0.5f
        )
        close()
    }

    drawPath(
        path = dragonPath,
        brush = Brush.linearGradient(
            colors = listOf(colors.accent, colors.highlight, colors.accent),
            start = Offset(cx - r * 0.5f, cy - r * 0.5f),
            end = Offset(cx + r * 0.5f, cy + r * 0.5f)
        )
    )

    // Dragon eye
    val eyeX = cx + r * 0.42f
    val eyeY = cy - r * 0.42f
    val eyeGlow = 0.5f + sin(anim * PI.toFloat() * 2) * 0.3f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Red.copy(alpha = eyeGlow),
                Color.Red.copy(alpha = eyeGlow * 0.3f),
                Color.Transparent
            ),
            center = Offset(eyeX, eyeY),
            radius = r * 0.15f
        ),
        radius = r * 0.12f,
        center = Offset(eyeX, eyeY)
    )

    drawCircle(
        color = Color.Red,
        radius = r * 0.04f,
        center = Offset(eyeX, eyeY)
    )

    drawCircle(
        color = Color.White,
        radius = r * 0.015f,
        center = Offset(eyeX - r * 0.01f, eyeY - r * 0.01f)
    )

    // Outer border
    drawCircle(
        color = colors.border,
        radius = r,
        center = Offset(cx, cy),
        style = Stroke(width = 4f)
    )

    // Inner decorative ring
    drawCircle(
        color = colors.accent.copy(alpha = 0.5f),
        radius = r * 0.7f,
        center = Offset(cx, cy),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawCosmicOrb(
    cx: Float, cy:  Float, radius: Float,
    colors:  MedalColors, tier: BadgeTier, anim:  Float, pulse: Float
) {
    val r = radius * pulse

    // Outer cosmic ring
    rotate(degrees = anim * 360f * 0.2f, pivot = Offset(cx, cy)) {
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    colors.glow.copy(alpha = 0.8f),
                    colors.primary.copy(alpha = 0.3f),
                    colors.glow.copy(alpha = 0.8f),
                    colors.primary.copy(alpha = 0.3f),
                    colors.glow.copy(alpha = 0.8f)
                ),
                center = Offset(cx, cy)
            ),
            radius = r * 1.05f,
            center = Offset(cx, cy),
            style = Stroke(width = r * 0.08f)
        )
    }

    // Main orb body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                colors.highlight,
                colors.accent,
                colors.primary,
                colors.secondary
            ),
            center = Offset(cx - r * 0.25f, cy - r * 0.25f),
            radius = r * 1.5f
        ),
        radius = r * 0.9f,
        center = Offset(cx, cy)
    )

    // Swirling nebula effect
    for (i in 0 until 3) {
        val swirl = (anim + i * 0.33f) % 1f
        val swirlAngle = swirl * PI.toFloat() * 4
        val swirlRadius = r * 0.4f + sin(swirl * PI.toFloat() * 2) * r * 0.2f

        val nebulaPath = Path().apply {
            val startAngle = swirlAngle
            val endAngle = swirlAngle + PI.toFloat() * 0.8f
            moveTo(
                cx + swirlRadius * 0.3f * cos(startAngle),
                cy + swirlRadius * 0.3f * sin(startAngle)
            )
            for (j in 0..20) {
                val t = j / 20f
                val angle = startAngle + (endAngle - startAngle) * t
                val rad = swirlRadius * (0.3f + t * 0.7f)
                lineTo(cx + rad * cos(angle), cy + rad * sin(angle))
            }
        }

        drawPath(
            path = nebulaPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    colors.highlight.copy(alpha = 0.6f - i * 0.15f),
                    colors.accent.copy(alpha = 0.3f - i * 0.08f),
                    Color.Transparent
                )
            ),
            style = Stroke(width = r * 0.08f - i * r * 0.02f, cap = StrokeCap.Round)
        )
    }

    // Star particles inside
    val starCount = 12
    for (i in 0 until starCount) {
        val starAnim = (anim * 2 + i * 0.0833f) % 1f
        val starAngle = (PI * 2 * i / starCount).toFloat()
        val starDist = r * 0.3f + sin(starAnim * PI.toFloat() * 2) * r * 0.25f
        val starX = cx + starDist * cos(starAngle + anim * PI.toFloat())
        val starY = cy + starDist * sin(starAngle + anim * PI.toFloat())
        val starSize = r * 0.03f + sin(starAnim * PI.toFloat() * 4) * r * 0.02f
        val starAlpha = 0.4f + sin(starAnim * PI.toFloat() * 2) * 0.4f

        drawCircle(
            color = colors.highlight.copy(alpha = starAlpha),
            radius = starSize,
            center = Offset(starX, starY)
        )
    }

    // Center core
    val coreGlow = 0.6f + sin(anim * PI.toFloat() * 3) * 0.3f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                colors.highlight.copy(alpha = coreGlow),
                colors.accent.copy(alpha = coreGlow * 0.5f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = r * 0.35f
        ),
        radius = r * 0.3f,
        center = Offset(cx, cy)
    )

    // Light reflection
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f),
                Color.Transparent
            ),
            center = Offset(cx - r * 0.3f, cy - r * 0.3f),
            radius = r * 0.35f
        ),
        radius = r * 0.25f,
        center = Offset(cx - r * 0.25f, cy - r * 0.25f)
    )

    // Outer border
    drawCircle(
        color = colors.border,
        radius = r * 0.9f,
        center = Offset(cx, cy),
        style = Stroke(width = 3f)
    )
}

// ==================== TIER SPECIAL EFFECTS ====================

private fun DrawScope.drawLegendaryFireRing(
    cx:  Float, cy: Float, radius: Float,
    colors: MedalColors, anim: Float
) {
    val particleCount = 20
    for (i in 0 until particleCount) {
        val angle = (2 * PI * i / particleCount + anim * PI * 2).toFloat()
        val dist = radius * 1.15f + sin(anim * PI.toFloat() * 6 + i * 0.5f) * radius * 0.1f
        val px = cx + dist * cos(angle)
        val py = cy + dist * sin(angle)

        // Flame particle
        val flameHeight = radius * 0.15f + sin(anim * PI.toFloat() * 4 + i) * radius * 0.08f
        val flameWidth = radius * 0.06f

        val flamePath = Path().apply {
            moveTo(px, py + flameHeight * 0.5f)
            quadraticBezierTo(px - flameWidth, py, px, py - flameHeight * 0.5f)
            quadraticBezierTo(px + flameWidth, py, px, py + flameHeight * 0.5f)
            close()
        }

        rotate(degrees = (angle * 180 / PI).toFloat() + 90f, pivot = Offset(px, py)) {
            drawPath(
                path = flamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.accent,
                        colors.glow.copy(alpha = 0.8f),
                        colors.glow.copy(alpha = 0f)
                    ),
                    startY = py - flameHeight,
                    endY = py + flameHeight
                )
            )
        }
    }

    // Outer energy ring
    for (i in 0 until 8) {
        val angle = (PI / 4 * i + anim * PI * 1.5f).toFloat()
        val startDist = radius * 1.0f
        val endDist = radius * 1.35f

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    colors.glow.copy(alpha = 0.9f),
                    colors.accent.copy(alpha = 0.5f),
                    colors.glow.copy(alpha = 0f)
                ),
                start = Offset(cx + startDist * cos(angle), cy + startDist * sin(angle)),
                end = Offset(cx + endDist * cos(angle), cy + endDist * sin(angle))
            ),
            start = Offset(cx + startDist * cos(angle), cy + startDist * sin(angle)),
            end = Offset(cx + endDist * cos(angle), cy + endDist * sin(angle)),
            strokeWidth = 4f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawDiamondSparkles(
    cx: Float, cy: Float, radius:  Float,
    colors: MedalColors, anim: Float
) {
    val sparkleCount = 12
    for (i in 0 until sparkleCount) {
        val sparkleAnim = (anim * 3 + i * 0.0833f) % 1f
        val angle = (PI * 2 * i / sparkleCount).toFloat()
        val dist = radius * (0.9f + sparkleAnim * 0.4f)
        val sx = cx + dist * cos(angle)
        val sy = cy + dist * sin(angle)
        val sparkleAlpha = (1f - sparkleAnim).coerceIn(0f, 1f) * 0.8f
        val sparkleSize = radius * 0.08f * (1f - sparkleAnim * 0.5f)

        // 4-pointed sparkle
        val sparklePath = Path().apply {
            moveTo(sx, sy - sparkleSize)
            lineTo(sx + sparkleSize * 0.3f, sy)
            lineTo(sx, sy + sparkleSize)
            lineTo(sx - sparkleSize * 0.3f, sy)
            close()
            moveTo(sx - sparkleSize, sy)
            lineTo(sx, sy + sparkleSize * 0.3f)
            lineTo(sx + sparkleSize, sy)
            lineTo(sx, sy - sparkleSize * 0.3f)
            close()
        }

        drawPath(
            path = sparklePath,
            color = colors.highlight.copy(alpha = sparkleAlpha)
        )
    }

    // Shimmer ring
    rotate(degrees = -anim * 180f, pivot = Offset(cx, cy)) {
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    colors.highlight.copy(alpha = 0.6f),
                    Color.Transparent,
                    colors.highlight.copy(alpha = 0.6f),
                    Color.Transparent
                ),
                center = Offset(cx, cy)
            ),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(cx - radius * 1.1f, cy - radius * 1.1f),
            size = Size(radius * 2.2f, radius * 2.2f),
            style = Stroke(width = 3f)
        )
    }
}

private fun DrawScope.drawGoldShimmer(
    cx: Float, cy:  Float, radius: Float,
    colors:  MedalColors, anim: Float
) {
    // Traveling light beam
    val beamAngle = anim * PI.toFloat() * 2
    val beamStartX = cx + radius * 1.2f * cos(beamAngle)
    val beamStartY = cy + radius * 1.2f * sin(beamAngle)
    val beamEndX = cx - radius * 1.2f * cos(beamAngle)
    val beamEndY = cy - radius * 1.2f * sin(beamAngle)

    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                colors.highlight.copy(alpha = 0.4f),
                colors.accent.copy(alpha = 0.6f),
                colors.highlight.copy(alpha = 0.4f),
                Color.Transparent
            ),
            start = Offset(beamStartX, beamStartY),
            end = Offset(beamEndX, beamEndY)
        ),
        start = Offset(beamStartX, beamStartY),
        end = Offset(beamEndX, beamEndY),
        strokeWidth = radius * 0.15f,
        cap = StrokeCap.Round
    )

    // Subtle sparkle points
    for (i in 0 until 6) {
        val sparklePhase = (anim * 2 + i * 0.167f) % 1f
        val sparkleAlpha = sin(sparklePhase * PI.toFloat()).coerceIn(0f, 1f) * 0.7f
        val angle = (PI * 2 * i / 6).toFloat()
        val dist = radius * 0.75f
        val sx = cx + dist * cos(angle)
        val sy = cy + dist * sin(angle)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    colors.highlight.copy(alpha = sparkleAlpha),
                    Color.Transparent
                ),
                center = Offset(sx, sy),
                radius = radius * 0.12f
            ),
            radius = radius * 0.1f,
            center = Offset(sx, sy)
        )
    }
}

// ==================== PREMIUM MEDAL COMPOSABLE ====================

@Composable
fun PremiumMedal(
    progress: MedalProgress,
    size:  Dp = 120.dp,
    onClick: (() -> Unit)? = null,
    showName: Boolean = true,
    showTierBadge: Boolean = true
) {
    val colors = getMedalColors(progress.currentTier)
    val tier = progress.currentTier
    val isLegendary = tier == BadgeTier.LEGENDARY
    val isDiamond = tier == BadgeTier.DIAMOND
    val isGold = tier == BadgeTier.GOLD
    val isUnlocked = tier != BadgeTier.LOCKED

    val infiniteTransition = rememberInfiniteTransition(label = "medalAnim")

    // Animation speed increases with tier
    val animDuration = when (tier) {
        BadgeTier.LEGENDARY -> 1500
        BadgeTier.DIAMOND -> 2000
        BadgeTier.GOLD -> 2500
        BadgeTier.SILVER -> 3000
        BadgeTier.BRONZE -> 3500
        BadgeTier.LOCKED -> 5000
    }

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animProgress"
    )

    // Pulse intensity increases with tier
    val pulseTarget = when (tier) {
        BadgeTier.LEGENDARY -> 1.12f
        BadgeTier.DIAMOND -> 1.08f
        BadgeTier.GOLD -> 1.05f
        BadgeTier.SILVER -> 1.03f
        BadgeTier.BRONZE -> 1.02f
        BadgeTier.LOCKED -> 1f
    }

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseTarget,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val glowIntensity by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val rotationDegrees by infiniteTransition.animateFloat(
        initialValue = if (isLegendary) -5f else 0f,
        targetValue = if (isLegendary) 5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Box(
            modifier = Modifier.size(size * 1.3f),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow layer
            if (isUnlocked) {
                Box(
                    modifier = Modifier.size(size * 1.5f)
                        .alpha(0.3f + glowIntensity * 0.3f)
                        .blur(25.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(colors.glow, Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
            }

            // Medal canvas
            Canvas(
                modifier = Modifier.size(size)
                    .scale(pulseScale)
                    .rotate(rotationDegrees)
                    .alpha(if (isUnlocked) 1f else 0.35f)
            ) {
                drawMedalShape(
                    shape = progress.badge.shape,
                    colors = colors,
                    tier = tier,
                    animProgress = animProgress,
                    pulseScale = 1f,
                    glowIntensity = glowIntensity
                )
            }

            // Lock overlay for locked medals
            if (! isUnlocked) {
                Box(
                    modifier = Modifier.size(size * 0.45f)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF1A1A1A), Color(0xFF0A0A0A))
                            )
                        )
                        .border(2.dp, Color(0xFF333333), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔒",
                        fontSize = (size.value * 0.18f).sp
                    )
                }
            }

            // Tier badge
            if (showTierBadge && isUnlocked) {
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .offset(x = (-size.value * 0.05f).dp, y = (-size.value * 0.05f).dp)
                        .size(size * 0.32f)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(colors.primary, colors.secondary)
                            )
                        )
                        .border(2.dp, colors.border, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (tier) {
                            BadgeTier.BRONZE -> "I"
                            BadgeTier.SILVER -> "II"
                            BadgeTier.GOLD -> "III"
                            BadgeTier.DIAMOND -> "IV"
                            BadgeTier.LEGENDARY -> "★"
                            else -> ""
                        },
                        color = Color.White,
                        fontSize = (size.value * 0.11f).sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        if (showName) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = progress.badge.name,
                fontSize = (size.value * 0.11f).sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) colors.primary else Color.White.copy(alpha = 0.4f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            if (isUnlocked) {
                Text(
                    text = tier.name,
                    fontSize = (size.value * 0.08f).sp,
                    color = colors.primary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==================== PROFILE FEATURED MEDALS ====================

@Composable
fun ProfileFeaturedMedals(
    modifier: Modifier = Modifier,
    maxDisplay: Int = 3,
    medalSize: Dp = 85.dp,
    onViewAllClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val medalManager = remember { MedalManager(dataStore) }

    var topMedals by remember { mutableStateOf<List<MedalProgress>>(emptyList()) }
    var totalUnlocked by remember { mutableIntStateOf(0) }
    var totalMedals by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val allMedals = medalManager.getAllMedalsProgress()
        topMedals = allMedals.filter { it.currentTier != BadgeTier.LOCKED }
            .sortedByDescending { it.currentTier.level }
            .take(maxDisplay)
        totalUnlocked = allMedals.count { it.currentTier != BadgeTier.LOCKED }
        totalMedals = allMedals.size
        isLoading = false
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🏅", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Medals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "$totalUnlocked / $totalMedals",
                        fontSize = 12.sp,
                        color = Color(0xFFFFD700)
                    )
                }
            }

            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All →",
                    fontSize = 12.sp,
                    color = Color(0xFFFF6D00),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", color = Color.White.copy(alpha = 0.5f))
            }
        } else if (topMedals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔒", fontSize = 40.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Play to unlock medals! ",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            // Featured medals - displayed prominently
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                topMedals.forEachIndexed { index, medal ->
                    val scale = if (index == 0 && topMedals.size > 1) 1.15f else 1f
                    Box(
                        modifier = Modifier.scale(scale)
                    ) {
                        PremiumMedal(
                            progress = medal,
                            size = medalSize,
                            onClick = onViewAllClick,
                            showName = true,
                            showTierBadge = true
                        )
                    }
                }

                // Placeholder slots
                repeat((maxDisplay - topMedals.size).coerceAtLeast(0)) {
                    Box(
                        modifier = Modifier.size(medalSize)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.03f))
                            .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                            .clickable { onViewAllClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "? ",
                            fontSize = (medalSize.value * 0.3f).sp,
                            color = Color.White.copy(alpha = 0.2f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==================== MEDAL DETAIL DIALOG ====================

@Composable
fun MedalDetailDialog(
    progress: MedalProgress,
    onDismiss: () -> Unit
) {
    val colors = getMedalColors(progress.currentTier)
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.92f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A14)),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colors.primary.copy(alpha = 0.12f),
                                Color(0xFF0A0A14)
                            )
                        )
                    )
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("✕", fontSize = 22.sp, color = Color.White)
                    }
                }

                // Large medal display
                PremiumMedal(
                    progress = progress,
                    size = 180.dp,
                    showName = false,
                    showTierBadge = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Name
                Text(
                    text = progress.badge.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (progress.currentTier != BadgeTier.LOCKED) colors.primary else Color.White
                )

                // Description
                Text(
                    text = progress.badge.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                // Current tier badge
                if (progress.currentTier != BadgeTier.LOCKED) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(colors.primary.copy(alpha = 0.3f), colors.secondary.copy(alpha = 0.2f))
                                )
                            )
                            .border(1.dp, colors.border.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${progress.currentTier.name} TIER",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Tier progression header
                Text(
                    text = "TIER PROGRESSION",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Tier list
                progress.badge.tiers.forEach { tierInfo ->
                    val tierColors = getMedalColors(tierInfo.tier)
                    val isCompleted = tierInfo.tier.level <= progress.currentTier.level
                    val isCurrent = tierInfo.tier == progress.currentTier

                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent)
                                tierColors.primary.copy(alpha = 0.15f)
                            else
                                Color.White.copy(alpha = 0.02f)
                        ),
                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(
                            2.dp, tierColors.primary
                        ) else null
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tier indicator
                            Box(
                                modifier = Modifier.size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = if (isCompleted)
                                            Brush.linearGradient(listOf(tierColors.primary, tierColors.secondary))
                                        else
                                            Brush.linearGradient(listOf(Color(0xFF2A2A2A), Color(0xFF1A1A1A)))
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = if (isCompleted) tierColors.border else Color(0xFF444444),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (tierInfo.tier) {
                                        BadgeTier.BRONZE -> "I"
                                        BadgeTier.SILVER -> "II"
                                        BadgeTier.GOLD -> "III"
                                        BadgeTier.DIAMOND -> "IV"
                                        BadgeTier.LEGENDARY -> "★"
                                        else -> "?"
                                    },
                                    color = if (isCompleted) Color.White else Color.White.copy(alpha = 0.4f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tierInfo.tier.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) tierColors.primary else Color.White.copy(alpha = 0.4f)
                                )
                                Text(
                                    text = tierInfo.description,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }

                            if (isCompleted) {
                                Box(
                                    modifier = Modifier.size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text(
                                    text = "${tierInfo.requirement}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }

                // Progress bar for next tier
                if (! progress.isMaxed && progress.nextTierRequirement != null) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF6D00).copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progress to Next Tier",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${(progress.progressPercent * 100).toInt()}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF6D00)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { progress.progressPercent },
                                modifier = Modifier.fillMaxWidth()
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = Color(0xFFFF6D00),
                                trackColor = Color.White.copy(alpha = 0.1f),
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${progress.currentValue} / ${progress.nextTierRequirement}",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }

                // Maxed out badge
                if (progress.isMaxed) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFF6D00))
                                )
                            )
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "🏆 MAXED OUT! ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ==================== ALL MEDALS DIALOG ====================

@Composable
fun AllMedalsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val medalManager = remember { MedalManager(dataStore) }

    var allMedals by remember { mutableStateOf<List<MedalProgress>>(emptyList()) }
    var selectedMedal by remember { mutableStateOf<MedalProgress?>(null) }
    var selectedCategory by remember { mutableStateOf<BadgeCategory?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        allMedals = medalManager.getAllMedalsProgress()
        isLoading = false
    }

    val filteredMedals = if (selectedCategory != null) {
        allMedals.filter { it.badge.category == selectedCategory }
    } else {
        allMedals
    }

    val unlockedCount = allMedals.count { it.currentTier != BadgeTier.LOCKED }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f)
                .fillMaxSize(0.9f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A14)),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A1A2E), Color(0xFF0A0A14))
                        )
                    )
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🏅 ALL MEDALS",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "$unlockedCount / ${allMedals.size} unlocked",
                            fontSize = 14.sp,
                            color = Color(0xFFFFD700)
                        )
                    }

                    TextButton(onClick = onDismiss) {
                        Text("✕", fontSize = 24.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category filters
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip(
                        text = "All",
                        emoji = "🎖️",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                    BadgeCategory.entries.forEach { category ->
                        CategoryChip(
                            text = category.name.lowercase().replaceFirstChar { it.uppercase() },
                            emoji = when (category) {
                                BadgeCategory.RECORDS -> "🎯"
                                BadgeCategory.CHALLENGES -> "⚔️"
                                BadgeCategory.TOURNAMENTS -> "🏆"
                                BadgeCategory.PROGRESS -> "📈"
                                BadgeCategory.SPECIAL -> "⭐"
                            },
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Medals grid
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading...", color = Color.White.copy(alpha = 0.5f))
                    }
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        filteredMedals.chunked(3).forEach { rowMedals ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowMedals.forEach { medal ->
                                    PremiumMedal(
                                        progress = medal,
                                        size = 95.dp,
                                        onClick = { selectedMedal = medal },
                                        showName = true,
                                        showTierBadge = true
                                    )
                                }
                                repeat(3 - rowMedals.size) {
                                    Spacer(modifier = Modifier.size(95.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    selectedMedal?.let { medal ->
        MedalDetailDialog(
            progress = medal,
            onDismiss = { selectedMedal = null }
        )
    }
}

@Composable
private fun CategoryChip(
    text: String,
    emoji:  String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) Color(0xFFFF6D00).copy(alpha = 0.25f)
                else Color.White.copy(alpha = 0.05f)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFFF6D00) else Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFFFF6D00) else Color.White.copy(alpha = 0.7f)
            )
        }
    }
}