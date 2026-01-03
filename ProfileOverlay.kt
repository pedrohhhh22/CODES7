package com.appsdevs.popit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx. compose.animation.core. Animatable
import androidx.compose.animation. core.FastOutSlowInEasing
import androidx. compose.animation.core.tween
import androidx. compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose. foundation.clickable
import androidx. compose.foundation.gestures.detectTapGestures
import androidx.compose. foundation.layout. Arrangement
import androidx.compose.foundation. layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation. layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose. foundation.layout. Spacer
import androidx.compose.foundation. layout.fillMaxSize
import androidx.compose.foundation.layout. fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose. foundation.layout.offset
import androidx.compose.foundation.layout. padding
import androidx.compose.foundation.layout. size
import androidx. compose.foundation.layout.width
import androidx.compose.foundation. layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation. shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text. KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx. compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx. compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx. compose.material3.Text
import androidx. compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose. runtime.LaunchedEffect
import androidx. compose.runtime.collectAsState
import androidx.compose.runtime. getValue
import androidx. compose.runtime.mutableIntStateOf
import androidx. compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose. ui.Alignment
import androidx.compose. ui.Modifier
import androidx.compose. ui.draw.alpha
import androidx.compose.ui.draw. clip
import androidx. compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics. Brush
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. graphics.Shadow
import androidx.compose.ui.graphics. StrokeCap
import androidx. compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui. input.pointer.pointerInput
import androidx. compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui. text.TextStyle
import androidx. compose.ui.text.font.FontWeight
import androidx.compose.ui.text. input.ImeAction
import androidx.compose. ui.text.style.TextAlign
import androidx.compose.ui.unit. Dp
import androidx. compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window. DialogProperties
import androidx.compose.ui. zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==================== MAIN PROFILE OVERLAY ====================

@Composable
fun ProfileOverlayCoil(
    profileImageUrl: String?,
    pencilDrawable: Int,
    onRequestClose: () -> Unit,
    onSave: ((selectedDrawableResId: Int) -> Unit)? = null,
    onProfileUpdated: (() -> Unit)? = null
) {
    val avatarSizeDefault = 120.dp
    val bannerWidthDefault = 340.dp
    val bannerHeightDefault = 180.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(5f)
    ) {
        val transformOrigin = TransformOrigin(0.5f, 0.2f)

        val scaleAnim = remember { Animatable(0f) }
        val cornerPercentAnim = remember { Animatable(50f) }
        val scope = rememberCoroutineScope()

        var contentVisible by remember { mutableStateOf(false) }
        var showBannerSelector by remember { mutableStateOf(false) }
        var showAvatarSelector by remember { mutableStateOf(false) }
        var showAllMedals by remember { mutableStateOf(false) }

        val ctx = LocalContext.current
        val ds = remember { DataStoreManager(ctx) }
        val storedProfileName by ds.profileNameFlow().collectAsState(initial = "")
        val storedBannerColor by ds.bannerColorFlow().collectAsState(initial = 0)
        val storedAvatarId by ds.generatedAvatarIdFlow().collectAsState(initial = 0)

        var currentBannerColor by remember(storedBannerColor) {
            mutableIntStateOf(storedBannerColor)
        }

        var currentAvatarId by remember(storedAvatarId) {
            mutableIntStateOf(storedAvatarId)
        }

        val highScore by ds.highScoreFlow().collectAsState(initial = 0)
        val bestClickPct by ds.bestClickPercentFlow().collectAsState(initial = 0)
        val challengesCompletedCount by ds.challengesCompletedCountFlow().collectAsState(initial = 0)
        val totalPops by ds.totalPopsFlow().collectAsState(initial = 0)
        val claimedRewards by ds.claimedLevelRewardsFlow().collectAsState(initial = emptySet())

        // NEW: Best streak stats
        val bestStreak by ds.highScorePerfectStreakFlow().collectAsState(initial = 0)
        val maxConsecutiveDays by ds.maxConsecutiveDaysFlow().collectAsState(initial = 0)

        var editingName by remember { mutableStateOf(false) }
        var nameField by remember { mutableStateOf(storedProfileName) }

        LaunchedEffect(storedProfileName) { nameField = storedProfileName }

        LaunchedEffect(Unit) {
            ds.updateConsecutiveDays()
            ds.ensureFirstInstallDate()
        }

        val snackbarHostState = remember { SnackbarHostState() }
        val scrollState = rememberScrollState()

        LaunchedEffect(Unit) {
            scaleAnim.snapTo(0f)
            cornerPercentAnim.snapTo(50f)
            val jobScale = scope.launch {
                scaleAnim.animateTo(
                    1f,
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                )
            }
            val jobCorner = scope.launch {
                cornerPercentAnim.animateTo(
                    0f,
                    animationSpec = tween(durationMillis = 280, delayMillis = 50, easing = FastOutSlowInEasing)
                )
            }
            jobScale.join()
            jobCorner.join()
            delay(30)
            contentVisible = true
        }

        Box(
            modifier = Modifier
                . fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .pointerInput(Unit) { detectTapGestures { } }
        )

        Box(
            modifier = Modifier
                . fillMaxSize()
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                    this.transformOrigin = transformOrigin
                }
                .clip(RoundedCornerShape(percent = cornerPercentAnim. value. toInt()))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E),
                            Color(0xFF0F3460)
                        )
                    )
                )
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    . padding(top = 8.dp)
                    .zIndex(10f)
            )

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                    initialScale = 0.96f,
                    animationSpec = tween(200)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(20.dp)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileHeader(
                        onClose = {
                            scope.launch {
                                contentVisible = false
                                delay(80)
                                cornerPercentAnim. animateTo(
                                    50f,
                                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                                )
                                scaleAnim. animateTo(
                                    0f,
                                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                                )
                                onRequestClose()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileBannerWithAvatar(
                        generatedAvatarId = currentAvatarId,
                        bannerColorId = currentBannerColor,
                        avatarSize = avatarSizeDefault,
                        bannerWidth = bannerWidthDefault,
                        bannerHeight = bannerHeightDefault,
                        showEditButton = true,
                        pencilDrawable = pencilDrawable,
                        onEditClick = { showAvatarSelector = true },
                        onBannerClick = { showBannerSelector = true }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Tap banner to customize",
                            fontSize = 10.sp,
                            color = Color.White. copy(alpha = 0.4f)
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = Color.White. copy(alpha = 0.3f)
                        )
                        Text(
                            text = "Tap avatar to change",
                            fontSize = 10.sp,
                            color = Color.White. copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier. height(16.dp))

                    ProfileNameSection(
                        storedProfileName = storedProfileName,
                        nameField = nameField,
                        onNameFieldChange = { nameField = it },
                        editingName = editingName,
                        onEditingNameChange = { editingName = it },
                        pencilDrawable = pencilDrawable,
                        ds = ds,
                        scope = scope,
                        onProfileUpdated = onProfileUpdated
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PremiumMedalsShowcase(
                        onViewAllClick = { showAllMedals = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // UPDATED: Statistics section with best streak
                    StatisticsSection(
                        highScore = highScore,
                        totalPops = totalPops,
                        bestClickPct = bestClickPct,
                        bestStreak = bestStreak,
                        maxConsecutiveDays = maxConsecutiveDays,
                        challengesCompletedCount = challengesCompletedCount,
                        claimedRewards = claimedRewards,
                        ds = ds
                    )

                    Spacer(modifier = Modifier. height(24.dp))
                }
            }
        }

        if (showBannerSelector) {
            BannerColorSelector(
                currentColorId = currentBannerColor,
                onColorSelected = { newColorId ->
                    currentBannerColor = newColorId
                    scope.launch {
                        ds.saveBannerColor(newColorId)
                        onProfileUpdated?. invoke()
                    }
                },
                onDismiss = { showBannerSelector = false }
            )
        }

        if (showAvatarSelector) {
            AvatarSelectorDialog(
                currentAvatarId = currentAvatarId,
                onAvatarSelected = { newAvatarId ->
                    currentAvatarId = newAvatarId
                    scope.launch {
                        ds.saveGeneratedAvatarId(newAvatarId)
                        onProfileUpdated?. invoke()
                    }
                },
                onDismiss = { showAvatarSelector = false }
            )
        }

        if (showAllMedals) {
            AllMedalsDialog(
                onDismiss = { showAllMedals = false }
            )
        }
    }
}

// ==================== PROFILE HEADER ====================

@Composable
private fun ProfileHeader(onClose: () -> Unit) {
    Row(
        modifier = Modifier. fillMaxWidth(),
        horizontalArrangement = Arrangement. SpaceBetween,
        verticalAlignment = Alignment. CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "👤", fontSize = 26.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color. Black. copy(alpha = 0.5f),
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }

        TextButton(
            onClick = onClose,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White. copy(alpha = 0.1f))
        ) {
            Text(
                text = "✕ Close",
                color = Color.White. copy(alpha = 0.9f),
                fontWeight = FontWeight. Medium,
                fontSize = 14.sp
            )
        }
    }
}

// ==================== PROFILE BANNER WITH AVATAR ====================

@Composable
fun ProfileBannerWithAvatar(
    generatedAvatarId: Int,
    bannerColorId: Int,
    avatarSize:  Dp = 120.dp,
    bannerWidth: Dp = 340.dp,
    bannerHeight:  Dp = 180.dp,
    showEditButton: Boolean = false,
    pencilDrawable:  Int = 0,
    onEditClick: () -> Unit = {},
    onBannerClick: () -> Unit = {}
) {
    val avatarInsideBanner = avatarSize * 0.40f
    val avatarOutsideBanner = avatarSize * 0.60f
    val totalHeight = bannerHeight + avatarOutsideBanner

    Box(
        modifier = Modifier
            .width(bannerWidth)
            .height(totalHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                . align(Alignment.TopCenter)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onBannerClick() }
        ) {
            HorizontalProfileBanner(
                bannerColorId = bannerColorId,
                width = bannerWidth,
                height = bannerHeight
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    . padding(8.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎨", fontSize = 14.sp)
            }
        }

        Box(
            modifier = Modifier
                . align(Alignment.TopCenter)
                .offset(y = bannerHeight - avatarInsideBanner)
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize + 8.dp)
                    .align(Alignment.Center)
                    . clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4CAF50).copy(alpha = 0.4f),
                                Color. Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(4.dp, Color(0xFF1A1A2E), CircleShape)
                    .border(2.dp, Color(0xFF4CAF50), CircleShape)
                    .clickable { onEditClick() }
            ) {
                GeneratedAvatar(
                    avatarId = generatedAvatarId,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (showEditButton && pencilDrawable != 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment. BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
                            )
                        )
                        .border(2.dp, Color. White, CircleShape)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = pencilDrawable),
                        contentDescription = "edit",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ==================== PREMIUM MEDALS SHOWCASE ====================

@Composable
private fun PremiumMedalsShowcase(
    onViewAllClick:  () -> Unit
) {
    val context = LocalContext. current
    val dataStore = remember { DataStoreManager(context) }
    val medalManager = remember { MedalManager(dataStore) }

    var allMedals by remember { mutableStateOf<List<MedalProgress>>(emptyList()) }
    var selectedMedal by remember { mutableStateOf<MedalProgress?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        allMedals = medalManager.getAllMedalsProgress()
        isLoading = false
    }

    val featuredMedals = allMedals
        .filter { it.currentTier != BadgeTier. LOCKED }
        .sortedByDescending { it. currentTier. level }
        .take(3)

    val unlockedCount = allMedals.count { it. currentTier != BadgeTier. LOCKED }
    val totalMedals = allMedals.size

    Card(
        modifier = Modifier
            . fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults. cardColors(
            containerColor = Color(0xFF0A0A14)
        ),
        elevation = CardDefaults. cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                . fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF0A0A14)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏅", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MEDALS",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$unlockedCount / $totalMedals unlocked",
                        fontSize = 13.sp,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Medium
                    )
                }

                TextButton(
                    onClick = onViewAllClick,
                    modifier = Modifier
                        . clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFF6D00).copy(alpha = 0.15f))
                ) {
                    Text(
                        text = "View All →",
                        color = Color(0xFFFF6D00),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        . height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Loading medals...",
                        color = Color.White. copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else if (featuredMedals.isEmpty()) {
                Box(
                    modifier = Modifier
                        . fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔒", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Play games to unlock medals! ",
                            color = Color.White. copy(alpha = 0.6f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'View All' to see requirements",
                            color = Color(0xFFFF6D00).copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // FIXED: All medals same size, no scaling for first one
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    featuredMedals. forEach { medal ->
                        Box(
                            modifier = Modifier. width(100.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            PremiumMedal(
                                progress = medal,
                                size = 80.dp, // Fixed size for all
                                onClick = { selectedMedal = medal },
                                showName = true,
                                showTierBadge = true
                            )
                        }
                    }

                    repeat((3 - featuredMedals.size).coerceAtLeast(0)) {
                        Box(
                            modifier = Modifier. width(100.dp),
                            contentAlignment = Alignment. TopCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    . clip(CircleShape)
                                    .background(Color. White.copy(alpha = 0.03f))
                                    .border(
                                        width = 2.dp,
                                        color = Color.White. copy(alpha = 0.08f),
                                        shape = CircleShape
                                    )
                                    .clickable { onViewAllClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "? ",
                                    fontSize = 28.sp,
                                    color = Color.White. copy(alpha = 0.2f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val nextToUnlock = allMedals
                .filter { it.currentTier == BadgeTier. LOCKED }
                .minByOrNull { medal ->
                    medal. badge.tiers.firstOrNull()?.requirement ?: Int.MAX_VALUE
                }

            nextToUnlock?.let { medal ->
                Card(
                    modifier = Modifier. fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color. White.copy(alpha = 0.05f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            . padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PremiumMedal(
                            progress = medal,
                            size = 50.dp,
                            showName = false,
                            showTierBadge = false
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Next:  ",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = medal.badge.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight. Bold,
                                    color = Color. White
                                )
                            }

                            val req = medal.badge. tiers.firstOrNull()?.requirement ?: 0
                            val progress = if (req > 0) {
                                (medal.currentValue. toFloat() / req).coerceIn(0f, 1f)
                            } else 0f

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    . clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFFF6D00),
                                trackColor = Color.White.copy(alpha = 0.1f),
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement. SpaceBetween
                            ) {
                                Text(
                                    text = "${medal.currentValue} / $req",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFF6D00),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedMedal?. let { medal ->
        MedalDetailDialog(
            progress = medal,
            onDismiss = { selectedMedal = null }
        )
    }
}

// ==================== PROFILE NAME SECTION ====================

@Composable
private fun ProfileNameSection(
    storedProfileName: String,
    nameField:  String,
    onNameFieldChange: (String) -> Unit,
    editingName:  Boolean,
    onEditingNameChange: (Boolean) -> Unit,
    pencilDrawable: Int,
    ds: DataStoreManager,
    scope: kotlinx.coroutines.CoroutineScope,
    onProfileUpdated: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            . fillMaxWidth(0.92f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                . fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (! editingName) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Username",
                        fontSize = 11.sp,
                        color = Color.White. copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (storedProfileName.isNotBlank()) storedProfileName else "Tap to set name",
                        fontSize = 20.sp,
                        fontWeight = FontWeight. Bold,
                        color = if (storedProfileName.isNotBlank()) Color.White else Color.White.copy(alpha = 0.4f),
                        textAlign = TextAlign. Center
                    )
                }

                IconButton(
                    onClick = { onEditingNameChange(true) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFF6D00).copy(alpha = 0.3f),
                                    Color(0xFFFF6D00).copy(alpha = 0.15f)
                                )
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(id = pencilDrawable),
                        contentDescription = "edit_name",
                        modifier = Modifier. size(20.dp)
                    )
                }
            } else {
                OutlinedTextField(
                    value = nameField,
                    onValueChange = onNameFieldChange,
                    modifier = Modifier. weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    placeholder = {
                        Text(
                            "Enter your name",
                            color = Color.White. copy(alpha = 0.4f)
                        )
                    },
                    keyboardOptions = KeyboardOptions. Default.copy(imeAction = ImeAction. Done),
                    keyboardActions = KeyboardActions(onDone = {
                        scope.launch {
                            ds.saveProfileName(nameField)
                            onProfileUpdated?.invoke()
                            onEditingNameChange(false)
                        }
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF6D00),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color(0xFFFF6D00)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier. width(10.dp))

                Button(
                    onClick = {
                        scope.launch {
                            ds.saveProfileName(nameField)
                            onProfileUpdated?.invoke()
                            onEditingNameChange(false)
                        }
                    },
                    colors = ButtonDefaults. buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text(
                        text = "✓",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==================== STATISTICS SECTION (UPDATED WITH 6 STATS) ====================

@Composable
private fun StatisticsSection(
    highScore: Int,
    totalPops: Int,
    bestClickPct:  Int,
    bestStreak: Int,
    maxConsecutiveDays: Int,
    challengesCompletedCount:  Int,
    claimedRewards:  Set<Int>,
    ds:  DataStoreManager
) {
    val scope = rememberCoroutineScope()
    var showLevelRewards by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val currentClaimedRewards by ds.claimedLevelRewardsFlow().collectAsState(initial = claimedRewards)

    Card(
        modifier = Modifier
            . fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A0A14)
        ),
        elevation = CardDefaults. cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                . fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A2E),
                            Color(0xFF0A0A14)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📊", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Statistics",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Row 1: Best Score & Total Pops
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement. spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "🏆",
                    label = "Best Score",
                    value = formatNumber(highScore),
                    color = Color(0xFFFFD700),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "🫧",
                    label = "Total Pops",
                    value = formatNumber(totalPops),
                    color = Color(0xFF00BFFF),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Best Accuracy & Best Streak
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement. spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "🎯",
                    label = "Best Accuracy",
                    value = if (bestClickPct > 0) "$bestClickPct%" else "—",
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "🔥",
                    label = "Best Streak",
                    value = if (bestStreak > 0) formatNumber(bestStreak) else "—",
                    color = Color(0xFFFF6D00),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 3: Challenges & Days Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = "⭐",
                    label = "Challenges",
                    value = "$challengesCompletedCount",
                    color = Color(0xFFE91E63),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = "📅",
                    label = "Days Streak",
                    value = if (maxConsecutiveDays > 0) "$maxConsecutiveDays" else "—",
                    color = Color(0xFF9C27B0),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier. height(18.dp))

            HorizontalDivider(
                color = Color. White. copy(alpha = 0.08f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Level progress section
            val level = calculateLevel(totalPops)
            val levelProgress = calculateLevelProgress(totalPops)
            val unclaimedCount = (1.. level).count { it !in currentClaimedRewards }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLevelRewards = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A1A4A).copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFF6D00), Color(0xFFFFD700))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$level",
                            fontSize = 24.sp,
                            fontWeight = FontWeight. ExtraBold,
                            color = Color. White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Player Level",
                                fontSize = 15.sp,
                                fontWeight = FontWeight. Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier. width(10.dp))
                            if (unclaimedCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFF6D00))
                                        . padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "🎁 $unclaimedCount",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight. Bold,
                                        color = Color. White
                                    )
                                }
                            } else {
                                Text(
                                    text = "🎁 Tap for rewards",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { levelProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFFFF6D00),
                            trackColor = Color.White.copy(alpha = 0.1f),
                            strokeCap = StrokeCap. Round
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${(levelProgress * 100).toInt()}% to Level ${level + 1}",
                            fontSize = 11.sp,
                            color = Color. White.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

    if (showLevelRewards) {
        LevelRewardsDialog(
            currentLevel = calculateLevel(totalPops),
            totalPops = totalPops,
            claimedRewards = currentClaimedRewards,
            ds = ds,
            onDismiss = { showLevelRewards = false },
            onRewardClaimed = { refreshTrigger++ }
        )
    }
}

@Composable
private fun StatCard(
    icon: String,
    label: String,
    value:  String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier. height(85.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color. copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                . fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color. White.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight. Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}

// ==================== LEVEL REWARDS DIALOG ====================

@Composable
fun LevelRewardsDialog(
    currentLevel: Int,
    totalPops:  Int,
    claimedRewards:  Set<Int>,
    ds: DataStoreManager,
    onDismiss:  () -> Unit,
    onRewardClaimed: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val rewards = ds.getLevelRewards()
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                . fillMaxWidth(0.95f)
                .fillMaxSize(0.88f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0A14)),
            elevation = CardDefaults. cardElevation(defaultElevation = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2A1A4A), Color(0xFF1A1A2E), Color(0xFF0A0A14))
                        )
                    )
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement. SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎁", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Level Rewards",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color. White
                            )
                            Text(
                                text = "Current Level: $currentLevel",
                                fontSize = 14.sp,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            . clip(CircleShape)
                            .background(Color. White.copy(alpha = 0.1f))
                    ) {
                        Text("✕", fontSize = 22.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        . weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    rewards.forEach { reward ->
                        val isUnlocked = currentLevel >= reward.level
                        val isClaimed = reward.level in claimedRewards
                        val isSpecial = reward.level == 7

                        LevelRewardItem(
                            reward = reward,
                            isUnlocked = isUnlocked,
                            isClaimed = isClaimed,
                            isSpecial = isSpecial,
                            onClaim = {
                                scope.launch {
                                    val success = ds.claimLevelReward(reward.level)
                                    if (success) {
                                        onRewardClaimed()
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun LevelRewardItem(
    reward: DataStoreManager.LevelReward,
    isUnlocked:  Boolean,
    isClaimed: Boolean,
    isSpecial: Boolean,
    onClaim: () -> Unit
) {
    val borderColor = when {
        isSpecial && isUnlocked && ! isClaimed -> Color(0xFFFFD700)
        isUnlocked && ! isClaimed -> Color(0xFF4CAF50)
        isClaimed -> Color(0xFF555555)
        else -> Color(0xFF333333)
    }

    val bgColor = when {
        isSpecial && isUnlocked && !isClaimed -> Color(0xFFFFD700).copy(alpha = 0.12f)
        isUnlocked && ! isClaimed -> Color(0xFF4CAF50).copy(alpha = 0.08f)
        else -> Color(0xFF0F0F1A).copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSpecial && ! isClaimed) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults. cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                . fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        brush = if (isSpecial) {
                            Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFF6D00)))
                        } else {
                            Brush.linearGradient(listOf(Color(0xFF4CAF50), Color(0xFF2196F3)))
                        }
                    )
                    .alpha(if (isUnlocked) 1f else 0.35f),
                contentAlignment = Alignment. Center
            ) {
                Text(
                    text = "${reward.level}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isSpecial) "⭐ SPECIAL LEVEL ⭐" else "Level ${reward.level}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSpecial) Color(0xFFFFD700) else Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (reward.coins > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.coin),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${reward.coins}",
                                fontSize = 13.sp,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (reward.lux > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.gemgame),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${reward.lux}",
                                fontSize = 13.sp,
                                color = Color(0xFF00BFFF),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (isSpecial) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = "🫧", fontSize = 16.sp)
                        Text(text = "🖼️", fontSize = 16.sp)
                        Text(text = "🏠", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Exclusive Bubble, Background & Menu! ",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD700).copy(alpha = 0.8f)
                    )
                }

                if (reward.description.isNotEmpty() && ! isSpecial) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reward. description,
                        fontSize = 11.sp,
                        color = Color. White.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier. width(10.dp))

            when {
                isClaimed -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF555555))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "✓",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color. White
                        )
                    }
                }
                isUnlocked -> {
                    Button(
                        onClick = onClaim,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpecial) Color(0xFFFFD700) else Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            text = "CLAIM",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSpecial) Color. Black else Color.White
                        )
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2A2A2A))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(text = "🔒", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// ==================== HELPER FUNCTIONS ====================

private fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> {
            val value = number / 1_000_000f
            String.format(java.util.Locale.US, "%. 1fM", value)
        }
        number >= 1_000 -> {
            val value = number / 1_000f
            String.format(java.util. Locale.US, "%.1fK", value)
        }
        else -> number.toString()
    }
}

private fun calculateLevel(totalPops:  Int): Int {
    val thresholds = listOf(
        0, 100, 500, 1000, 2500, 5000, 10000, 25000, 50000, 100000,
        150000, 200000, 300000, 400000, 500000, 650000, 800000, 1000000, 1250000, 1500000
    )
    for (i in thresholds. indices. reversed()) {
        if (totalPops >= thresholds[i]) {
            return i + 1
        }
    }
    return 1
}

private fun calculateLevelProgress(totalPops: Int): Float {
    val thresholds = listOf(
        0, 100, 500, 1000, 2500, 5000, 10000, 25000, 50000, 100000,
        150000, 200000, 300000, 400000, 500000, 650000, 800000, 1000000, 1250000, 1500000
    )
    for (i in thresholds. indices) {
        if (i == thresholds.lastIndex) {
            return 1f
        }
        if (totalPops < thresholds[i + 1]) {
            val prev = thresholds[i]
            val next = thresholds[i + 1]
            return (totalPops - prev).toFloat() / (next - prev)
        }
    }
    return 0f
}

// ==================== LEGACY SUPPORT ====================

@Composable
fun ProfileOverlay(
    profileDrawable: Int,
    pencilDrawable: Int,
    onRequestClose: () -> Unit,
    onSave: ((selectedDrawableResId: Int) -> Unit)? = null,
    onProfileUpdated: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val profileUri = "android.resource://${ctx.packageName}/$profileDrawable"
    ProfileOverlayCoil(
        profileImageUrl = profileUri,
        pencilDrawable = pencilDrawable,
        onRequestClose = onRequestClose,
        onSave = onSave,
        onProfileUpdated = onProfileUpdated
    )
}