package com.appsdevs.popit

import android.content.pm.ActivityInfo
import android. os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core. Animatable
import androidx.compose.animation. core.EaseInOutSine
import androidx.compose.animation.core. RepeatMode
import androidx.compose.animation. core.animateFloat
import androidx.compose.animation.core. infiniteRepeatable
import androidx.compose. animation.core.rememberInfiniteTransition
import androidx. compose.animation.core.spring
import androidx.compose.animation.core. tween
import androidx. compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose. foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout. Arrangement
import androidx.compose.foundation. layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation. layout.Column
import androidx.compose.foundation.layout. ColumnScope
import androidx. compose.foundation.layout.Row
import androidx.compose.foundation.layout. Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx. compose.foundation.layout.fillMaxWidth
import androidx.compose. foundation.layout.height
import androidx.compose.foundation.layout. offset
import androidx.compose.foundation.layout. padding
import androidx. compose.foundation.layout.size
import androidx.compose.foundation. layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy. LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation. shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose. material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime. Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime. getValue
import androidx. compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx. compose.runtime.mutableIntStateOf
import androidx.compose.runtime. mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx. compose.runtime.mutableStateMapOf
import androidx. compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose. ui.Alignment
import androidx.compose. ui.Modifier
import androidx.compose. ui.draw.alpha
import androidx.compose.ui.draw. clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics. Brush
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. graphics.Shadow
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input. pointer.pointerInput
import androidx.compose.ui. layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform. LocalDensity
import androidx.compose. ui.res.painterResource
import androidx.compose. ui.text.TextStyle
import androidx. compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit. Dp
import androidx. compose.ui.unit.dp
import androidx.compose.ui.unit. sp
import androidx. compose.ui.window.Dialog
import androidx.compose.ui.window. DialogProperties
import androidx. core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.appsdevs.popit.ui.theme.PopITTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx. coroutines.delay
import kotlinx. coroutines.flow.first
import kotlinx. coroutines.launch
import java.util. Locale
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.random.Random

class TournamentActivity :  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        try {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller. hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } catch (_: Exception) {}

        enableEdgeToEdge()
        MusicController.initIfNeeded(applicationContext)
        SoundManager.init(applicationContext)

        setContent {
            PopITTheme {
                TournamentMainScreen(onClose = { finish() })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicController.stopGameMusic()
    }
}

// ==================== DATA CLASSES ====================

data class TournamentRankingEntry(
    val rank: Int,
    val name: String,
    val score:  Int,
    val avatarRes: Int,
    val oduserId: String,
    val updatedAt: Long = 0L,
    val generatedAvatarId: Int = -1,
    val bannerColorId: Int = 0,
    val highScore: Int = 0,
    val totalPops: Int = 0,
    val bestClickPercent:  Int = 0,
    val challengesCompleted: Int = 0,
    val level: Int = 1,
    val bestStreak: Int = 0,
    val maxConsecutiveDays: Int = 0
)

// ==================== HELPERS ====================

private fun formatTournamentCurrency(amount: Int): String {
    return when {
        amount >= 1_000_000 -> String.format(Locale.US, "%.1fM", amount / 1_000_000f)
        amount >= 10_000 -> String.format(Locale.US, "%.1fK", amount / 1_000f)
        else -> String.format(Locale.US, "%,d", amount)
    }
}

private fun formatRemainingTime(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val days = totalSec / (24 * 3600)
    val hours = (totalSec % (24 * 3600)) / 3600
    val mins = (totalSec % 3600) / 60
    val secs = totalSec % 60
    return when {
        days > 0 -> "${days}d ${hours}h"
        hours > 0 -> "${hours}h ${mins}m"
        mins > 0 -> "${mins}m ${secs}s"
        else -> "${secs}s"
    }
}

private fun formatLargeNumber(number:  Int): String {
    return when {
        number >= 1_000_000 -> String.format(Locale.US, "%.1fM", number / 1_000_000f)
        number >= 1_000 -> String.format(Locale.US, "%.1fK", number / 1_000f)
        else -> number.toString()
    }
}

// ==================== GAME STATE ====================

sealed class TournamentGameState {
    data object Lobby : TournamentGameState()
    data object Playing : TournamentGameState()
    data class GameOver(val finalScore: Int) : TournamentGameState()
}

// ==================== MAIN SCREEN ====================

@Composable
fun TournamentMainScreen(onClose: () -> Unit) {
    var gameState by remember { mutableStateOf<TournamentGameState>(TournamentGameState.Lobby) }

    when (gameState) {
        is TournamentGameState.Lobby -> {
            TournamentLobby(
                onPlay = { gameState = TournamentGameState.Playing },
                onClose = onClose
            )
        }
        is TournamentGameState.Playing -> {
            TournamentGameScreen(
                onGameOver = { score ->
                    gameState = TournamentGameState.GameOver(score)
                }
            )
        }
        is TournamentGameState. GameOver -> {
            val score = (gameState as TournamentGameState.GameOver).finalScore
            TournamentGameOverScreen(
                finalScore = score,
                onPlayAgain = { gameState = TournamentGameState.Playing },
                onBackToLobby = { gameState = TournamentGameState. Lobby }
            )
        }
    }
}

@Composable
fun TournamentLobby(onPlay: () -> Unit, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val ds = remember { DataStoreManager(ctx) }
    val scope = rememberCoroutineScope()

    val myName by ds.profileNameFlow().collectAsState(initial = "")
    val myAvatar by ds.profileDrawableFlow().collectAsState(initial = 0)
    val myGeneratedAvatar by ds.generatedAvatarIdFlow().collectAsState(initial = 0)
    val myUserId by ds.userIdFlow().collectAsState(initial = "")
    val myBannerColor by ds.bannerColorFlow().collectAsState(initial = 0)

    // Usar Firebase leaderboard en tiempo real
    val firebaseLeaderboard by ds.getFirebaseLeaderboardFlow().collectAsState(initial = emptyList())

    val pendingReward by ds.getPendingRewardFlow().collectAsState(initial = null)

    var showRewardDialog by remember { mutableStateOf(false) }
    var showRewardsInfoSheet by remember { mutableStateOf(false) }
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isLoading by remember { mutableStateOf(true) }

    // Timer para actualizar el tiempo
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTimeMs = System.currentTimeMillis()
        }
    }

    // Sincronizar con Firebase al iniciar
    LaunchedEffect(Unit) {
        try {
            ds.ensureUserId()
            // Sincronizar epoch con Firebase
            ds.syncTournamentEpochWithFirebase()
            // NUEVO: Forzar sincronización del perfil al entrar
            ds. forceSyncProfileToFirebase()
            isLoading = false
        } catch (e: Exception) {
            e.printStackTrace()
            isLoading = false
        }
    }

    // NUEVO: Actualizar Firebase cuando cambia el perfil local
    LaunchedEffect(myName, myAvatar, myGeneratedAvatar, myBannerColor) {
        if (! isLoading && myUserId. isNotBlank()) {
            // Pequeño delay para evitar múltiples llamadas
            delay(300L)
            ds.updateProfileInFirebase()
        }
    }

    // Verificar rewards pendientes
    LaunchedEffect(isLoading) {
        if (! isLoading) {
            val epoch = ds.tournamentEpochMillisFlow().first()
            if (epoch > 0L) {
                ds.checkAndPrepareTournamentRewards(epoch, System.currentTimeMillis())
            }
        }
    }

    LaunchedEffect(pendingReward) {
        if (pendingReward != null) {
            delay(500L)
            showRewardDialog = true
        }
    }

    val epochFlowVal by ds.tournamentEpochMillisFlow().collectAsState(initial = currentTimeMs)
    val currentEpoch = if (epochFlowVal == 0L) currentTimeMs else epochFlowVal

    val activeMs = ds.getTournamentActiveMs()
    val totalMs = ds. getTournamentDurationMs()
    val elapsedMs = (currentTimeMs - currentEpoch).coerceAtLeast(0L)

    val isActive = elapsedMs < activeMs
    val isTournamentEnded = elapsedMs >= activeMs && elapsedMs < totalMs
    val isCycleComplete = elapsedMs >= totalMs

    val remainingMs = when {
        isActive -> activeMs - elapsedMs
        isTournamentEnded -> totalMs - elapsedMs
        else -> 0L
    }

    // Convertir Firebase entries a TournamentRankingEntry
    // MEJORADO: Usar datos locales para el usuario actual para reflejar cambios inmediatos
    val entries = firebaseLeaderboard.mapIndexed { idx, e ->
        val isCurrentUser = e.oduserId == myUserId
        TournamentRankingEntry(
            rank = idx + 1,
            name = if (isCurrentUser && myName.isNotBlank()) myName else e.name,
            score = e.score,
            avatarRes = if (isCurrentUser && myAvatar != 0) myAvatar else e.avatarRes,
            oduserId = e. oduserId,
            updatedAt = e. updatedAt,
            generatedAvatarId = if (isCurrentUser) myGeneratedAvatar else e.generatedAvatarId,
            bannerColorId = if (isCurrentUser) myBannerColor else e.bannerColorId,
            highScore = e.highScore,
            totalPops = e.totalPops,
            bestClickPercent = e. bestClickPercent,
            challengesCompleted = e.challengesCompleted,
            level = e. level,
            bestStreak = e. bestStreak,
            maxConsecutiveDays = e. maxConsecutiveDays
        )
    }

    val myEntry = entries.find { it.oduserId == myUserId }
    val myRank = myEntry?.rank ?: (entries.size + 1)

    var selectedPlayer by remember { mutableStateOf<TournamentRankingEntry?>(null) }

    // ...  resto del código igual ...

    val infiniteTransition = rememberInfiniteTransition(label = "lobbyEffects")
    val headerGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode. Reverse),
        label = "headerGlow"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            // Mostrar loading mientras sincroniza
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🏆", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading Tournament...",
                        fontSize = 18.sp,
                        color = Color. White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                        )
                    )
                    .padding(16.dp)
            ) {
                TournamentHeader(
                    isActive = isActive,
                    isTournamentEnded = isTournamentEnded,
                    remainingMs = remainingMs,
                    headerGlow = headerGlow,
                    isTestMode = DataStoreManager. TOURNAMENT_TEST_MODE,
                    onClose = onClose
                )

                Spacer(modifier = Modifier.height(16.dp))

                TournamentPlayButton(
                    isActive = isActive,
                    isTournamentEnded = isTournamentEnded,
                    isCycleComplete = isCycleComplete,
                    pendingReward = pendingReward,
                    onPlay = onPlay,
                    onClaimReward = { showRewardDialog = true },
                    onStartNewTournament = {
                        scope.launch {
                            ds.startNewTournamentWithFirebase()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        . clickable { showRewardsInfoSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF7B1FA2).copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            . padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "🎁", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Rewards & Prizes",
                            fontSize = 14.sp,
                            fontWeight = FontWeight. Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "→", fontSize = 16.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                YourRankCard(
                    myName = myName,
                    myAvatar = myAvatar,
                    myGeneratedAvatar = myGeneratedAvatar,
                    myEntry = myEntry,
                    myRank = myRank
                )

                Spacer(modifier = Modifier.height(12.dp))

                LeaderboardCard(
                    entries = entries,
                    myUserId = myUserId,
                    onPlayerClick = { selectedPlayer = it }
                )
            }
        }

        selectedPlayer?.let { player ->
            PlayerProfileDialog(player = player, onDismiss = { selectedPlayer = null })
        }

        if (showRewardsInfoSheet) {
            RewardsInfoDialog(
                rewardTiers = ds.getTournamentRewardTiers(),
                onDismiss = { showRewardsInfoSheet = false }
            )
        }

        if (showRewardDialog && pendingReward != null) {
            TournamentRewardClaimDialog(
                reward = pendingReward!! ,
                rewardTier = ds.getRewardForRank(pendingReward!!.rank),
                onClaim = {
                    scope.launch {
                        ds.claimPendingTournamentReward()
                        showRewardDialog = false
                    }
                },
                onDismiss = { showRewardDialog = false }
            )
        }
    }
}

// ==================== HEADER ====================

@Composable
private fun TournamentHeader(
    isActive: Boolean,
    isTournamentEnded: Boolean,
    remainingMs:  Long,
    headerGlow: Float,
    isTestMode: Boolean,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier. fillMaxWidth(),
        horizontalArrangement = Arrangement. SpaceBetween,
        verticalAlignment = Alignment. CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "🏆",
                fontSize = 32.sp,
                modifier = Modifier.scale(headerGlow)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TOURNAMENT",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0xFFFFD700).copy(alpha = 0.5f),
                                offset = Offset(0f, 0f),
                                blurRadius = 12f
                            )
                        )
                    )
                    if (isTestMode) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFF5722))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "TEST",
                                fontSize = 8.sp,
                                fontWeight = FontWeight. Bold,
                                color = Color. White
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isActive -> Color(0xFF4CAF50)
                                    isTournamentEnded -> Color(0xFFFFD700)
                                    else -> Color(0xFF666666)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isActive -> "Active • ${formatRemainingTime(remainingMs)} left"
                            isTournamentEnded -> "Ended • ${formatRemainingTime(remainingMs)} to claim"
                            else -> "Starting new tournament..."
                        },
                        fontSize = 12.sp,
                        color = when {
                            isActive -> Color(0xFF4CAF50)
                            isTournamentEnded -> Color(0xFFFFD700)
                            else -> Color. White. copy(alpha = 0.7f)
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                . size(44.dp)
                .clip(CircleShape)
                .background(Color. White. copy(alpha = 0.1f))
                .border(1.dp, Color. White. copy(alpha = 0.2f), CircleShape)
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✕", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ==================== PLAY BUTTON ====================

@Composable
private fun TournamentPlayButton(
    isActive: Boolean,
    isTournamentEnded: Boolean,
    isCycleComplete: Boolean,
    pendingReward: DataStoreManager.PendingTournamentReward?,
    onPlay:  () -> Unit,
    onClaimReward: () -> Unit,
    onStartNewTournament:  () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "buttonPulse")
    val buttonPulse by infiniteTransition. animateFloat(
        initialValue = 1f, targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(1000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "pulse"
    )

    when {
        pendingReward != null -> {
            Button(
                onClick = onClaimReward,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .scale(buttonPulse),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                elevation = ButtonDefaults. buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎁", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "CLAIM YOUR REWARD! ",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Rank #${pendingReward.rank} • ${formatTournamentCurrency(pendingReward. coins)} coins",
                            fontSize = 12.sp,
                            color = Color. Black. copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        isActive -> {
            Button(
                onClick = onPlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎮", fontSize = 24.sp)
                    Spacer(modifier = Modifier. width(12.dp))
                    Text(
                        text = "PLAY TOURNAMENT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        isTournamentEnded -> {
            Card(
                modifier = Modifier. fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF666666))
            ) {
                Box(
                    modifier = Modifier. fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏳", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Tournament Ended - Waiting for rewards",
                            fontSize = 16.sp,
                            fontWeight = FontWeight. Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
        isCycleComplete -> {
            Button(
                onClick = onStartNewTournament,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔄", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "START NEW TOURNAMENT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==================== YOUR RANK CARD ====================

@Composable
private fun YourRankCard(
    myName: String,
    myAvatar:  Int,
    myGeneratedAvatar: Int,
    myEntry: TournamentRankingEntry?,
    myRank: Int
) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults. cardColors(containerColor = Color(0xFF0F0F1A).copy(alpha = 0.9f)),
        elevation = CardDefaults. cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.1f),
                            Color. Transparent,
                            Color(0xFFFFD700).copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFF6D00), Color(0xFFFFD700))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (myGeneratedAvatar > 0) {
                    GeneratedAvatar(
                        avatarId = myGeneratedAvatar,
                        modifier = Modifier.size(50.dp).clip(CircleShape)
                    )
                } else {
                    val avatarToShow = if (myAvatar != 0) myAvatar else R.drawable.profileuser0
                    Image(
                        painter = painterResource(id = avatarToShow),
                        contentDescription = null,
                        contentScale = ContentScale. Crop,
                        modifier = Modifier.size(50.dp).clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = myName. ifBlank { "You" },
                    color = Color.White,
                    fontWeight = FontWeight. Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Your Ranking",
                    color = Color.White. copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (myEntry != null) "#$myRank" else "--",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight. ExtraBold,
                    fontSize = 28.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFFFD700).copy(alpha = 0.5f),
                            offset = Offset(0f, 0f),
                            blurRadius = 8f
                        )
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatTournamentCurrency(myEntry?.score ?: 0),
                        color = Color. White,
                        fontWeight = FontWeight. Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ==================== LEADERBOARD CARD ====================

@Composable
private fun ColumnScope.LeaderboardCard(
    entries:  List<TournamentRankingEntry>,
    myUserId: String,
    onPlayerClick: (TournamentRankingEntry) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults. cardColors(containerColor = Color(0xFF0F0F1A).copy(alpha = 0.9f)),
        elevation = CardDefaults. cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.weight(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🏆", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Leaderboard",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Text(
                    text = "${entries.size} players",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color. White. copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🎮", fontSize = 48.sp)
                        Spacer(modifier = Modifier. height(12.dp))
                        Text(
                            text = "No scores yet",
                            color = Color.White. copy(alpha = 0.7f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Be the first to play! ",
                            color = Color.White. copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    itemsIndexed(entries. take(100)) { _, item ->
                        TournamentRankingRow(
                            item = item,
                            isCurrentUser = item.oduserId == myUserId,
                            onClick = { onPlayerClick(item) }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

// ==================== RANKING ROW ====================

@Composable
private fun TournamentRankingRow(
    item: TournamentRankingEntry,
    isCurrentUser: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rowGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "glow"
    )

    val rankColor = when (item.rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFF1A1A2E)
    }

    Row(
        modifier = Modifier
            . fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isCurrentUser) Color(0xFFFFD700).copy(alpha = glowAlpha * 0.25f)
                else Color. White.copy(alpha = 0.05f)
            )
            .then(
                if (isCurrentUser) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xFFFFD700).copy(alpha = glowAlpha),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else Modifier
            )
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(rankColor),
            contentAlignment = Alignment.Center
        ) {
            if (item.rank <= 3) {
                Text(
                    text = when (item.rank) {
                        1 -> "🥇"
                        2 -> "🥈"
                        3 -> "🥉"
                        else -> "#${item.rank}"
                    },
                    fontSize = 18.sp
                )
            } else {
                Text(
                    text = "#${item.rank}",
                    color = Color.White,
                    fontWeight = FontWeight. Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                . size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (item.generatedAvatarId >= 0) {
                GeneratedAvatar(
                    avatarId = item. generatedAvatarId,
                    modifier = Modifier. size(36.dp).clip(CircleShape)
                )
            } else {
                val avatarToDisplay = if (item. avatarRes != 0) item.avatarRes else R.drawable.profileuser0
                Image(
                    painter = painterResource(id = avatarToDisplay),
                    contentDescription = null,
                    contentScale = ContentScale. Crop,
                    modifier = Modifier. size(36.dp).clip(CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.name. ifBlank { "Player" },
                    color = if (isCurrentUser) Color(0xFFFFD700) else Color.White,
                    fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                if (isCurrentUser) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "YOU",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
            Text(
                text = "Lvl ${item.level}",
                fontSize = 10.sp,
                color = Color. White.copy(alpha = 0.5f)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "⭐", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatTournamentCurrency(item.score),
                color = Color(0xFFFFD700),
                fontWeight = FontWeight. Bold,
                fontSize = 14.sp
            )
        }
    }
}

// ==================== PLAYER PROFILE DIALOG ====================

@Composable
private fun PlayerProfileDialog(
    player: TournamentRankingEntry,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    var playerMedals by remember { mutableStateOf<List<MedalProgress>>(emptyList()) }
    var isLoadingMedals by remember { mutableStateOf(true) }

    // Load medals for this player (simulated based on their stats)
    LaunchedEffect(player) {
        playerMedals = listOf(
            createMedalProgressFromValue(MedalDefinitions. SCORE_MASTER, player.highScore),
            createMedalProgressFromValue(MedalDefinitions.POP_LEGEND, player.totalPops),
            createMedalProgressFromValue(MedalDefinitions.SHARPSHOOTER, player.bestClickPercent),
            createMedalProgressFromValue(MedalDefinitions.CHALLENGER, player.challengesCompleted)
        ).filter { it.currentTier != BadgeTier.LOCKED }
            .sortedByDescending { it. currentTier. level }
            .take(3)

        isLoadingMedals = false
    }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                . fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            elevation = CardDefaults. cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    . verticalScroll(scrollState)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2A1A4A), Color(0xFF1A1A2E))
                        )
                    )
            ) {
                // Close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        . padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", fontSize = 18.sp, color = Color. White. copy(alpha = 0.7f))
                    }
                }

                // Banner + Avatar
                Box(
                    modifier = Modifier
                        . fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        HorizontalProfileBanner(
                            bannerColorId = player.bannerColorId,
                            width = 360.dp,
                            height = 140.dp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment. BottomCenter)
                            .offset(y = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A1A2E))
                                .border(4.dp, Color(0xFF2A2A4A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (player.generatedAvatarId >= 0) {
                                GeneratedAvatar(
                                    avatarId = player.generatedAvatarId,
                                    modifier = Modifier.size(92.dp).clip(CircleShape)
                                )
                            } else {
                                val avatarToDisplay = if (player.avatarRes != 0) player.avatarRes else R.drawable.profileuser0
                                Image(
                                    painter = painterResource(id = avatarToDisplay),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier. size(92.dp).clip(CircleShape)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment. BottomEnd)
                                . offset(x = (-5).dp, y = (-5).dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFFF6D00), Color(0xFFFFD700))
                                    )
                                )
                                .border(2.dp, Color. White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${player.level}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight. ExtraBold,
                                color = Color. White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Player Name
                Text(
                    text = player.name. ifBlank { "Player" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Level text
                Text(
                    text = "LEVEL ${player.level}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD700),
                    letterSpacing = 2.sp,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFFFD700).copy(alpha = 0.5f),
                            offset = Offset(0f, 0f),
                            blurRadius = 8f
                        )
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ===== MEDALS PREVIEW SECTION =====
                if (! isLoadingMedals && playerMedals. isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            . padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                . padding(16.dp),
                            horizontalAlignment = Alignment. CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏅", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Top Medals",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color. White
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier. fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                playerMedals.forEach { medal ->
                                    Box(
                                        modifier = Modifier. width(80.dp),
                                        contentAlignment = Alignment. TopCenter
                                    ) {
                                        PremiumMedal(
                                            progress = medal,
                                            size = 60.dp,
                                            showName = true,
                                            showTierBadge = true
                                        )
                                    }
                                }

                                // Fill empty slots
                                repeat((3 - playerMedals.size).coerceAtLeast(0)) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            . background(Color.White.copy(alpha = 0.05f)),
                                        contentAlignment = Alignment. Center
                                    ) {
                                        Text(
                                            text = "? ",
                                            fontSize = 20.sp,
                                            color = Color.White.copy(alpha = 0.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Stats Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        . padding(horizontal = 16.dp)
                ) {
                    // Record Card
                    ProfileStatCard(
                        icon = "🏆",
                        title = "Personal Record",
                        value = formatLargeNumber(player. highScore),
                        valueColor = Color(0xFFFFD700)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid - Row 1
                    Row(
                        modifier = Modifier. fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatMiniCard(
                            icon = "🫧",
                            title = "Total Pops",
                            value = formatLargeNumber(player.totalPops),
                            valueColor = Color(0xFF00BFFF),
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatMiniCard(
                            icon = "🎯",
                            title = "Best Accuracy",
                            value = if (player.bestClickPercent > 0) "${player.bestClickPercent}%" else "—",
                            valueColor = Color(0xFF4CAF50),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid - Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatMiniCard(
                            icon = "🔥",
                            title = "Best Streak",
                            value = if (player.bestStreak > 0) formatLargeNumber(player.bestStreak) else "—",
                            valueColor = Color(0xFFFF6D00),
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatMiniCard(
                            icon = "📅",
                            title = "Days Streak",
                            value = if (player. maxConsecutiveDays > 0) "${player.maxConsecutiveDays}" else "—",
                            valueColor = Color(0xFF9C27B0),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid - Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatMiniCard(
                            icon = "⭐",
                            title = "Challenges",
                            value = "${player.challengesCompleted}",
                            valueColor = Color(0xFFE91E63),
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatMiniCard(
                            icon = "📊",
                            title = "Level",
                            value = "${player.level}",
                            valueColor = Color(0xFFE040FB),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier. height(24.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        . height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6D00))
                ) {
                    Text(
                        text = "Close",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier. height(16.dp))
            }
        }
    }
}

// Helper function to create medal progress from a value
private fun createMedalProgressFromValue(badge: MedalBadge, value: Int): MedalProgress {
    var currentTier = BadgeTier. LOCKED
    for (t in badge.tiers) {
        if (value >= t.requirement) currentTier = t. tier
    }

    val nextTierReq = badge.tiers
        .firstOrNull { it. tier. level > currentTier.level }
        ?.requirement

    val isMaxed = currentTier == badge.tiers.lastOrNull()?.tier

    val progressPercent = if (nextTierReq != null && nextTierReq > 0) {
        val currentTierReq = badge.tiers. find { it.tier == currentTier }?. requirement ?: 0
        ((value - currentTierReq).toFloat() / (nextTierReq - currentTierReq)).coerceIn(0f, 1f)
    } else if (isMaxed) 1f else 0f

    return MedalProgress(
        badge = badge,
        currentTier = currentTier,
        currentValue = value,
        nextTierRequirement = nextTierReq,
        isMaxed = isMaxed,
        progressPercent = progressPercent
    )
}

@Composable
private fun ProfileStatCard(
    icon: String,
    title:  String,
    value: String,
    valueColor: Color
) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults. cardColors(containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                . fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.White. copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = valueColor,
                    style = TextStyle(
                        shadow = Shadow(
                            color = valueColor.copy(alpha = 0.5f),
                            offset = Offset(0f, 0f),
                            blurRadius = 8f
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileStatMiniCard(
    icon: String,
    title: String,
    value: String,
    valueColor:  Color,
    modifier:  Modifier = Modifier
) {
    Card(
        modifier = modifier. height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = valueColor. copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                . fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier. height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color.White. copy(alpha = 0.6f),
                textAlign = TextAlign. Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

// ==================== REWARDS INFO DIALOG ====================

@Composable
private fun RewardsInfoDialog(
    rewardTiers: List<TournamentRewardTier>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f).fillMaxSize(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            elevation = CardDefaults. cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2A1A4A), Color(0xFF1A1A2E))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement. SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tournament Rewards",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Compete for amazing prizes!",
                                fontSize = 12.sp,
                                color = Color. White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color. White. copy(alpha = 0.1f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", fontSize = 18.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier. height(20.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(rewardTiers) { index, tier ->
                        RewardTierCard(tier = tier, isTop = index == 0)
                    }
                }

                Spacer(modifier = Modifier. height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier. padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Rewards are automatically available when the tournament ends.  Don't forget to claim them!",
                            fontSize = 12.sp,
                            color = Color. White.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier. height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text(text = "Got it!", fontSize = 16.sp, fontWeight = FontWeight. Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun RewardTierCard(tier: TournamentRewardTier, isTop: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "tierGlow")
    val glowAlpha by infiniteTransition. animateFloat(
        initialValue = 0.3f,
        targetValue = if (isTop) 0.8f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val tierColor = when {
        tier.rankRange. first == 1 -> Color(0xFFFFD700)
        tier.rankRange.first <= 3 -> Color(0xFFC0C0C0)
        tier.rankRange.first <= 10 -> Color(0xFFCD7F32)
        tier.rankRange.first <= 25 -> Color(0xFF9C27B0)
        tier.rankRange.first <= 50 -> Color(0xFF2196F3)
        else -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isTop) {
                    Modifier. border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700).copy(alpha = glowAlpha),
                                Color(0xFFFF6D00).copy(alpha = glowAlpha)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = tierColor. copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tierColor. copy(alpha = 0.3f))
                    .border(2.dp, tierColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = tier.emoji, fontSize = 20.sp)
                    val rankText = if (tier.rankRange.first == tier.rankRange.last) {
                        "#${tier.rankRange.first}"
                    } else {
                        "#${tier.rankRange.first}-${tier.rankRange.last}"
                    }
                    Text(
                        text = rankText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight. Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier. width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tier.label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight. Bold,
                    color = tierColor
                )
                Text(
                    text = "Rank ${tier.rankRange. first}${if (tier.rankRange.first != tier.rankRange. last) " - ${tier.rankRange.last}" else ""}",
                    fontSize = 12.sp,
                    color = Color. White.copy(alpha = 0.6f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatTournamentCurrency(tier.coins),
                        fontSize = 14.sp,
                        fontWeight = FontWeight. Bold,
                        color = Color(0xFFFFD700)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.gemgame),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${tier.lux}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1FA2)
                    )
                }
            }
        }
    }
}

// ==================== REWARD CLAIM DIALOG ====================

@Composable
private fun TournamentRewardClaimDialog(
    reward: DataStoreManager.PendingTournamentReward,
    rewardTier: TournamentRewardTier?,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { alpha.animateTo(1f, tween(300)) }
        launch { scale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 300f)) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                . fillMaxSize()
                .background(Color. Black.copy(alpha = 0.8f * alpha.value)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
                    .scale(scale.value)
                    .alpha(alpha.value),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                elevation = CardDefaults. cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        . background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.2f),
                                    Color(0xFF1A1A2E)
                                )
                            )
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment. CenterHorizontally
                ) {
                    Text(text = "🎉", fontSize = 56.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "CONGRATULATIONS! ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700),
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(0xFFFFD700).copy(alpha = 0.5f),
                                offset = Offset(0f, 0f),
                                blurRadius = 16f
                            )
                        )
                    )

                    Spacer(modifier = Modifier. height(8.dp))

                    Text(
                        text = "Tournament Finished",
                        fontSize = 16.sp,
                        color = Color. White. copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700).copy(alpha = 0.4f),
                                        Color(0xFFFF6D00).copy(alpha = 0.2f)
                                    )
                                )
                            )
                            .border(
                                width = 4.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFF6D00))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment. Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = rewardTier?.emoji ?: "🏆",
                                fontSize = 32.sp
                            )
                            Text(
                                text = "#${reward.rank}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color. White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = rewardTier?.label ?: "Player",
                        fontSize = 18.sp,
                        fontWeight = FontWeight. Bold,
                        color = Color(0xFFFFD700)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                . padding(20.dp),
                            horizontalAlignment = Alignment. CenterHorizontally
                        ) {
                            Text(
                                text = "YOUR REWARDS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color. White.copy(alpha = 0.6f),
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement. SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD700).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment. Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R. drawable.coin),
                                            contentDescription = null,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "+${formatTournamentCurrency(reward.coins)}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFFFD700)
                                    )
                                    Text(
                                        text = "Coins",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            . clip(CircleShape)
                                            .background(Color(0xFF7B1FA2).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment. Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.gemgame),
                                            contentDescription = null,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier. height(8.dp))
                                    Text(
                                        text = "+${reward.lux}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF7B1FA2)
                                    )
                                    Text(
                                        text = "LUX",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onClaim,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        elevation = ButtonDefaults. buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(text = "🎁", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "CLAIM REWARDS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color. White
                        )
                    }
                }
            }
        }
    }
}

// ==================== TOURNAMENT GAME SCREEN ====================

@Composable
fun TournamentGameScreen(onGameOver: (Int) -> Unit) {
    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }

    val equippedBubble by dataStore. equippedBubbleFlow().collectAsState(initial = 0)
    val bubbleRes = when (equippedBubble) {
        1 -> R.drawable.goldenbubble
        2 -> R.drawable.rainbowbubble
        3 -> R.drawable.greenbubble
        4 -> R. drawable.pinkbubble
        5 -> R.drawable.cyberpunkbubble
        6 -> R.drawable.oceanbubble
        7 -> R. drawable.animebubble1
        8 -> R.drawable.spacebubble
        9 -> R.drawable.levelbubble
        else -> R.drawable. bubble
    }
    val bubblePainter = painterResource(id = bubbleRes)

    val bubbles = remember { mutableStateListOf<Bubble>() }
    val popEffects = remember { mutableStateListOf<PopEffect>() }
    val floatingTexts = remember { mutableStateListOf<FloatingText>() }
    val particles = remember { mutableStateListOf<Particle>() }
    val comboEffects = remember { mutableStateListOf<ComboEffect>() }
    val streakMilestones = remember { mutableStateListOf<StreakMilestone>() }

    val spawnJobs = remember { mutableStateMapOf<Int, Job>() }
    val spawnStart = remember { mutableStateMapOf<Int, Long>() }
    val remainingMap = remember { mutableStateMapOf<Int, Long>() }

    var effectNextId by remember { mutableIntStateOf(0) }
    var bubbleSeq by remember { mutableIntStateOf(0) }

    val baseSpawn = 1200L
    val baseLifespan = 2000L
    val minSpawn = 200L
    val minLifespan = 400L

    var difficultyMultiplier by remember { mutableDoubleStateOf(1.0) }
    var spawnIntervalMs by remember { mutableLongStateOf(baseSpawn) }
    var bubbleLifespanMs by remember { mutableLongStateOf(baseLifespan) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }

    var totalPopped by remember { mutableIntStateOf(0) }
    var pointsBase by remember { mutableIntStateOf(0) }

    var currentStreak by remember { mutableIntStateOf(0) }
    var bestStreak by remember { mutableIntStateOf(0) }
    var lastStreakMilestone by remember { mutableIntStateOf(0) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val topBarHeight = 80.dp
    val topBarPadding = 12.dp
    val totalTopReserved = topBarHeight + topBarPadding

    val maxMultiplierBySpawn = baseSpawn. toDouble() / minSpawn.toDouble()
    val maxMultiplierByLifespan = baseLifespan.toDouble() / minLifespan.toDouble()
    val maxAllowedMultiplier = minOf(maxMultiplierBySpawn, maxMultiplierByLifespan)

    var showCountdown by remember { mutableStateOf(true) }
    var countdownValue by remember { mutableIntStateOf(3) }
    var showPlayLabel by remember { mutableStateOf(false) }

    var prevDifficultyMultiplier by remember { mutableDoubleStateOf(difficultyMultiplier) }
    var diffText by remember { mutableStateOf("") }
    var showDiffIndicator by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        MusicController.startGameMusic()
        onDispose { MusicController.stopGameMusic() }
    }

    fun scheduleTimeoutForBubble(bubble: Bubble, delayMs: Long) {
        spawnJobs. remove(bubble.id)?. cancel()
        spawnStart[bubble.id] = System.currentTimeMillis()
        val job = coroutineScope.launch {
            try {
                if (delayMs > 0) delay(delayMs)
                if (bubbles.any { it.id == bubble.id }) {
                    gameOver = true
                    running = false
                }
            } catch (_: CancellationException) { }
        }
        spawnJobs[bubble.id] = job
    }

    fun createParticles(centerX:  Dp, centerY:  Dp, count: Int = 8) {
        val colors = listOf(Color(0xFFFFD700), Color(0xFFFF6B00), Color(0xFF00FF88), Color(0xFF00BFFF), Color. White)
        repeat(count) { i ->
            val angle = (2 * PI * i / count).toFloat()
            val particle = Particle(
                id = effectNextId++, x = centerX, y = centerY, angle = angle,
                speed = Random.nextFloat() * 0.5f + 0.5f, color = colors. random(),
                size = (Random.nextFloat() * 4f + 4f).dp
            )
            particles.add(particle)
            coroutineScope.launch { delay(500); particles.removeAll { it.id == particle.id } }
        }
    }

    fun checkStreakMilestone(streak: Int) {
        val milestones = listOf(10, 20, 30, 50)
        if (streak in milestones && streak > lastStreakMilestone) {
            lastStreakMilestone = streak
            streakMilestones.add(StreakMilestone(id = effectNextId++, streak = streak))
        }
    }

    LaunchedEffect(Unit) {
        delay(120L)
        for (i in 3 downTo 1) { countdownValue = i; SoundManager.playCountdown(); delay(1000L) }
        showPlayLabel = true; SoundManager.playGo(); delay(450L)
        showCountdown = false; showPlayLabel = false; running = true
    }

    LaunchedEffect(difficultyMultiplier) {
        spawnIntervalMs = (baseSpawn / difficultyMultiplier).toLong().coerceAtLeast(minSpawn)
        bubbleLifespanMs = (baseLifespan / difficultyMultiplier).toLong().coerceAtLeast(minLifespan)
    }

    LaunchedEffect(difficultyMultiplier) {
        if (difficultyMultiplier != prevDifficultyMultiplier && prevDifficultyMultiplier != 1.0) {
            val change = (difficultyMultiplier - prevDifficultyMultiplier) / prevDifficultyMultiplier * 100.0
            diffText = String.format(Locale.US, "+%.0f%% Speed!", change)
            showDiffIndicator = true
            prevDifficultyMultiplier = difficultyMultiplier
            delay(1000L)
            showDiffIndicator = false
        } else { prevDifficultyMultiplier = difficultyMultiplier }
    }

    LaunchedEffect(running) {
        if (! running) return@LaunchedEffect
        while (running && ! gameOver) {
            delay(1000L)
            elapsedSeconds += 1
            if (elapsedSeconds % 30 == 0) {
                val next = difficultyMultiplier * 1.25
                difficultyMultiplier = if (next > maxAllowedMultiplier) maxAllowedMultiplier else next
            }
        }
    }

    var maxWidth by remember { mutableStateOf(0.dp) }
    var maxHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(running) {
        if (!running) return@LaunchedEffect
        while (running && !gameOver) {
            val bubbleSizeDp = 72.dp
            val xDp = (Random.nextFloat() * (maxWidth - bubbleSizeDp).value).dp
            val yRange = maxHeight - bubbleSizeDp - totalTopReserved
            val yDp = (totalTopReserved. value + Random.nextFloat() * yRange. value).dp

            val bubble = Bubble(
                id = bubbleSeq++, x = xDp, y = yDp, size = bubbleSizeDp,
                lifespanMs = bubbleLifespanMs, spawnedAtMillis = System.currentTimeMillis()
            )

            bubbles.add(bubble)
            scheduleTimeoutForBubble(bubble, bubbleLifespanMs)
            delay(spawnIntervalMs)
        }
    }

    LaunchedEffect(gameOver) {
        if (gameOver) {
            val myUserId = dataStore.userIdFlow().first()
            val myName = dataStore.profileNameFlow().first()
            val myAvatar = dataStore. profileDrawableFlow().first()
            val myGeneratedAvatar = dataStore. generatedAvatarIdFlow().first()

            val nameToUse = myName. ifBlank { "Player" }
            val avatarToUse = if (myAvatar != 0) myAvatar else R.drawable.profileuser0
            val userIdToUse = myUserId.ifBlank { dataStore.ensureUserId(); dataStore.userIdFlow().first() }

            // Save best streak if improved
            val currentBestStreak = dataStore.highScorePerfectStreakFlow().first()
            if (bestStreak > currentBestStreak) {
                dataStore.saveHighScorePerfectStreak(bestStreak)
            }

            // Guardar localmente también (como backup)
            dataStore.submitTournamentScoreWithUserId(
                oduserId = userIdToUse,
                name = nameToUse,
                avatarRes = avatarToUse,
                score = pointsBase,
                nowMillis = System. currentTimeMillis(),
                generatedAvatarId = myGeneratedAvatar
            )

            // *** ENVIAR A FIREBASE ***
            dataStore.submitScoreToFirebase(pointsBase)

            onGameOver(pointsBase)
        }
    }

    BoxWithConstraints(modifier = Modifier. fillMaxSize()) {
        maxWidth = this.maxWidth
        maxHeight = this. maxHeight

        val equippedBg by dataStore.equippedBackgroundFlow().collectAsState(initial = 0)
        val bgRes = when (equippedBg) {
            1 -> R.drawable. background1; 2 -> R.drawable.background2; 3 -> R.drawable.background3
            4 -> R.drawable.background4; 5 -> R. drawable.background5; 6 -> R.drawable.background6
            7 -> R.drawable.background7; 8 -> R.drawable. background8; 9 -> R.drawable.background9
            10 -> R.drawable.background10; 11 -> R. drawable.background11
            else -> 0
        }

        if (bgRes != 0) {
            Image(painter = painterResource(id = bgRes), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else { DefaultGradientBackground() }

        GameTopBar(points = pointsBase, elapsedSeconds = elapsedSeconds, streak = currentStreak, modifier = Modifier.padding(top = 70.dp))

        Box(
            modifier = Modifier
                . fillMaxSize()
                .pointerInput(bubbles. toList(), gameOver, running) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            for (change in event.changes) {
                                try {
                                    if (change.changedToUp()) {
                                        if (! running || gameOver) { change.consume(); continue }

                                        val tap = change.position
                                        var hitBubble:  Bubble? = null

                                        for (b in bubbles. toList().asReversed()) {
                                            val bx = with(density) { b.x.toPx() }
                                            val by = with(density) { b.y.toPx() }
                                            val bs = with(density) { b.size. toPx() }
                                            if (tap.x in bx..(bx + bs) && tap.y in by..(by + bs)) { hitBubble = b; break }
                                        }

                                        if (hitBubble != null) {
                                            if (bubbles.removeAll { it.id == hitBubble.id }) {
                                                spawnJobs.remove(hitBubble.id)?.cancel()
                                                spawnStart. remove(hitBubble.id)
                                                remainingMap. remove(hitBubble.id)

                                                SoundManager.playBubblePop()
                                                totalPopped++
                                                currentStreak++
                                                if (currentStreak > bestStreak) bestStreak = currentStreak
                                                checkStreakMilestone(currentStreak)

                                                val mult = when { currentStreak >= 50 -> 3.0; currentStreak >= 30 -> 2.5; currentStreak >= 20 -> 2.0; currentStreak >= 10 -> 1.5; currentStreak >= 5 -> 1.25; else -> 1.0 }
                                                val pts = (10 * mult).roundToInt()
                                                pointsBase += pts

                                                coroutineScope.launch { dataStore.addTotalPops(1) }

                                                val popId = effectNextId++
                                                val effSize = hitBubble.size * 1.25f
                                                popEffects.add(PopEffect(popId, hitBubble.x + (hitBubble. size - effSize) / 2f, hitBubble.y + (hitBubble.size - effSize) / 2f, effSize))

                                                val txtId = effectNextId++
                                                floatingTexts.add(FloatingText(txtId, hitBubble.x + hitBubble.size / 4, hitBubble. y, "+$pts", if (mult > 1.0) Color(0xFFFFD700) else Color(0xFF00FF88), if (mult > 1.0) 22. sp else 18.sp))

                                                createParticles(hitBubble.x + hitBubble.size / 2, hitBubble. y + hitBubble.size / 2, if (currentStreak >= 10) 12 else 8)

                                                if (currentStreak >= 3 && currentStreak % 3 == 0) { comboEffects.add(ComboEffect(effectNextId++, currentStreak, hitBubble. x, hitBubble. y)) }

                                                coroutineScope.launch { delay(600); popEffects.removeAll { it.id == popId } }
                                                coroutineScope.launch { delay(850); floatingTexts.removeAll { it.id == txtId } }
                                            }
                                        } else {
                                            if (currentStreak > 0) SoundManager.playLostStreak()
                                            currentStreak = 0
                                        }
                                        change.consume()
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    }
                }
        ) {
            bubbles.toList().forEach { b -> key(b.id) { BubbleView(b, bubblePainter) } }
            popEffects.toList().forEach { e -> key(e. id) { PopEffectView(e) { popEffects.removeAll { it.id == e.id } } } }
            particles.toList().forEach { p -> key(p.id) { ParticleView(p) } }
            floatingTexts.toList().forEach { f -> key(f. id) { FloatingTextEffect(f) { floatingTexts.removeAll { it.id == f.id } } } }
            comboEffects. toList().forEach { c -> key(c.id) { ComboEffectView(c) { comboEffects. removeAll { it. id == c.id } } } }

            Box(Modifier.fillMaxSize().padding(top = 150.dp), Alignment.TopCenter) {
                streakMilestones.toList().forEach { m -> key(m. id) { StreakMilestoneEffect(m) { streakMilestones.removeAll { it.id == m.id } } } }
            }

            Box(Modifier.fillMaxSize(), Alignment.TopCenter) {
                DifficultyCenterIndicator(diffText, showDiffIndicator, Modifier.padding(top = 130.dp))
            }

            if (showCountdown) CountdownOverlay(countdownValue, showPlayLabel)
        }
    }
}

// ==================== GAME OVER SCREEN ====================

@Composable
fun TournamentGameOverScreen(finalScore: Int, onPlayAgain: () -> Unit, onBackToLobby: () -> Unit) {
    val overlayAlpha = remember { Animatable(0f) }
    val cardScale = remember { Animatable(0.8f) }
    val cardAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        SoundManager.playCoinEarn()
        launch { overlayAlpha. animateTo(1f, tween(300)) }
        delay(150)
        launch { cardAlpha.animateTo(1f, tween(300)) }
        launch { cardScale.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = 300f)) }
    }

    Box(
        modifier = Modifier. fillMaxSize().background(Color(0xCC000000).copy(alpha = overlayAlpha.value)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(cardScale.value).alpha(cardAlpha.value)
        ) {
            Text(text = "🏆", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "TOURNAMENT SCORE",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color. White,
                style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.7f), Offset(2f, 2f), 4f))
            )

            Spacer(modifier = Modifier. height(20.dp))

            Card(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(0.9f),
                colors = CardDefaults. cardColors(containerColor = Color(0xFF1A1A2E).copy(alpha = 0.95f)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(brush = Brush.horizontalGradient(colors = listOf(Color(0xFFFFD700).copy(alpha = 0.2f), Color(0xFFFF6B00).copy(alpha = 0.2f))))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "YOUR SCORE", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White. copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$finalScore",
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD700),
                                style = TextStyle(shadow = Shadow(color = Color(0xFFFFD700).copy(alpha = 0.5f), offset = Offset(0f, 0f), blurRadius = 16f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Text(text = "✅", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Score submitted to leaderboard!", fontSize = 13.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier. height(28.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth(0.75f).height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(text = "🎮", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "PLAY AGAIN", fontSize = 18.sp, fontWeight = FontWeight. Bold, color = Color. White)
            }

            Spacer(modifier = Modifier. height(12.dp))

            Button(
                onClick = onBackToLobby,
                modifier = Modifier.fillMaxWidth(0.75f).height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(text = "📊", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "VIEW LEADERBOARD", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color. White)
            }
        }
    }
}