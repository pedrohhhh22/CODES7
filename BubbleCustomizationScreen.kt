package com.appsdevs.popit

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose. animation.core.animateFloat
import androidx.compose.animation. core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx. compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation. core.tween
import androidx. compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation. clickable
import androidx.compose. foundation.layout. Arrangement
import androidx.compose. foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout. Row
import androidx.compose.foundation. layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose. foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose. foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation. layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation. shape.RoundedCornerShape
import androidx.compose.foundation. verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx. compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose. material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose. material3.Text
import androidx. compose.runtime.Composable
import androidx. compose.runtime.LaunchedEffect
import androidx.compose.runtime. collectAsState
import androidx.compose. runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose. runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx. compose.runtime.rememberCoroutineScope
import androidx.compose. runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui. draw.clip
import androidx.compose. ui.draw.scale
import androidx.compose.ui. geometry.Offset
import androidx. compose.ui.graphics.Brush
import androidx.compose.ui. graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui. graphics.graphicsLayer
import androidx.compose. ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui. text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose. ui.unit.sp
import kotlinx.coroutines. Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ==================== BUBBLE CUSTOMIZATION SCREEN ====================

@Composable
fun BubbleCustomizationScreen() {
    val ctx = LocalContext.current
    val ds = remember { DataStoreManager(ctx) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val equippedBubbleFlow by ds.equippedBubbleFlow().collectAsState(initial = 0)

    // Collect purchase states for all bubbles dynamically
    val bubbleIds = listOf(1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
    val purchasedStates = remember { mutableMapOf<Int, Boolean>() }

    bubbleIds.forEach { id ->
        val isPurchased by ds.isBubblePurchasedFlow(id).collectAsState(initial = false)
        LaunchedEffect(isPurchased) {
            purchasedStates[id] = isPurchased
        }
    }

    // Level 7 exclusive bubble (ID 10)
    val isLevel7BubbleUnlocked by ds.isLevel7BubbleUnlockedFlow().collectAsState(initial = false)
    LaunchedEffect(isLevel7BubbleUnlocked) {
        if (isLevel7BubbleUnlocked) purchasedStates[10] = true
    }

    // Get current player level
    val totalPops by ds.totalPopsFlow().collectAsState(initial = 0)
    val currentLevel = ds.calculateLevelFromPops(totalPops)

    var equippedBubbleLocal by remember { mutableIntStateOf(equippedBubbleFlow) }
    LaunchedEffect(equippedBubbleFlow) { equippedBubbleLocal = equippedBubbleFlow }

    var selectedBubble by remember { mutableIntStateOf(if (equippedBubbleLocal > 0) equippedBubbleLocal else 0) }
    LaunchedEffect(equippedBubbleLocal) {
        if (selectedBubble == 0) selectedBubble = equippedBubbleLocal
    }

    // ==================== PRECIOS CORREGIDOS ====================
    // COINS:  Common, Rare (estáticos 1-5)
    // LUX: Epic, Legendary (estáticos 6-8) + TODOS los generados (11-30)
    // ID 10: Level 7 unlock - sin precio
    //
    // Precios por rareza:
    // COMMON (coins): 500
    // RARE (coins): 1000
    // EPIC (lux): 50
    // LEGENDARY (lux): 100
    // EXCLUSIVE:  Level unlock o 150 lux (generado)
    val prices = remember {
        mapOf(
            // Estáticos con COINS
            1 to 1000,  // RARE (Golden)
            2 to 100,   // LEGENDARY (Rainbow) - Lux
            3 to 500,   // COMMON (Green)
            4 to 500,   // COMMON (Pink)
            5 to 100,   // LEGENDARY (Cyberpunk) - Lux
            // Estáticos con LUX
            6 to 50,    // RARE (Ocean) - Lux
            7 to 50,    // EPIC (Anime) - Lux
            8 to 50,    // EPIC (Space) - Lux
            // Level unlock
            10 to 0,    // EXCLUSIVE - Level 7 unlock
            // Generados con LUX (todos)
            11 to 50,   // EPIC (Fire)
            12 to 50,   // EPIC (Ice)
            13 to 50,   // EPIC (Electric)
            14 to 40,   // RARE (Nature)
            15 to 100,  // LEGENDARY (Galaxy)
            16 to 50,   // EPIC (Lava)
            17 to 100,  // LEGENDARY (Crystal)
            18 to 40,   // RARE (Sunset)
            19 to 50,   // EPIC (Midnight)
            20 to 40,   // RARE (Cherry Blossom)
            21 to 50,   // EPIC (Toxic)
            22 to 40,   // RARE (Water)
            23 to 100,  // LEGENDARY (Diamond)
            24 to 50,   // EPIC (Neon)
            25 to 100,  // LEGENDARY (Aurora)
            26 to 100,  // LEGENDARY (Rainbow Swirl)
            27 to 40,   // RARE (Smoke)
            28 to 40,   // RARE (Candy)
            29 to 50,   // EPIC (Metal)
            30 to 100   // LEGENDARY (Plasma)
        )
    }

    var showConfirm by remember { mutableStateOf(false) }
    var pendingBuyId by remember { mutableIntStateOf(0) }

    var showLevelLockDialog by remember { mutableStateOf(false) }
    var levelLockTargetId by remember { mutableIntStateOf(0) }

    fun isPurchased(id: Int): Boolean = purchasedStates[id] ?: false
    fun setPurchased(id: Int, value: Boolean) {
        purchasedStates[id] = value
    }

    fun isLevelLockedItem(id: Int): Boolean = id == 10 && !isLevel7BubbleUnlocked

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        val isWide = maxWidth > 760. dp

        Column(modifier = Modifier.fillMaxWidth()) {
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(0.55f)
                            .height(580.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        BubblePreviewPanel(
                            selectedBubble = selectedBubble,
                            selectedName = getBubbleName(selectedBubble),
                            isPurchased = isPurchased(selectedBubble),
                            isEquipped = equippedBubbleLocal == selectedBubble,
                            price = prices[selectedBubble] ?: 0,
                            isLuxPriced = isLuxPricedBubble(selectedBubble),
                            isLevelLocked = isLevelLockedItem(selectedBubble),
                            requiredLevel = if (selectedBubble == 10) 7 else 0,
                            currentLevel = currentLevel,
                            onBuyOrEquip = {
                                if (selectedBubble == 10) {
                                    if (isLevel7BubbleUnlocked) {
                                        handleBuyOrEquip(
                                            selectedBubble = selectedBubble,
                                            isPurchased = isPurchased(selectedBubble),
                                            isEquipped = equippedBubbleLocal == selectedBubble,
                                            onEquip = { id ->
                                                equippedBubbleLocal = id
                                                coroutineScope.launch {
                                                    withContext(Dispatchers.IO) { ds.equipBubble(id) }
                                                    snackbarHostState.showSnackbar("✅ Equipped!")
                                                }
                                            },
                                            onUnequip = {
                                                equippedBubbleLocal = 0
                                                coroutineScope.launch {
                                                    withContext(Dispatchers.IO) { ds.equipBubble(0) }
                                                    snackbarHostState.showSnackbar("Unequipped")
                                                }
                                            },
                                            onBuy = { },
                                            onNoSelection = { }
                                        )
                                    } else {
                                        levelLockTargetId = 10
                                        showLevelLockDialog = true
                                    }
                                } else {
                                    handleBuyOrEquip(
                                        selectedBubble = selectedBubble,
                                        isPurchased = isPurchased(selectedBubble),
                                        isEquipped = equippedBubbleLocal == selectedBubble,
                                        onEquip = { id ->
                                            equippedBubbleLocal = id
                                            coroutineScope.launch {
                                                withContext(Dispatchers. IO) { ds.equipBubble(id) }
                                                snackbarHostState.showSnackbar("✅ Equipped!")
                                            }
                                        },
                                        onUnequip = {
                                            equippedBubbleLocal = 0
                                            coroutineScope.launch {
                                                withContext(Dispatchers.IO) { ds.equipBubble(0) }
                                                snackbarHostState.showSnackbar("Unequipped")
                                            }
                                        },
                                        onBuy = { id ->
                                            pendingBuyId = id
                                            showConfirm = true
                                        },
                                        onNoSelection = {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Select a bubble first")
                                            }
                                        }
                                    )
                                }
                            },
                            onReset = {
                                coroutineScope.launch {
                                    selectedBubble = 0
                                    equippedBubbleLocal = 0
                                    withContext(Dispatchers.IO) { ds.resetBubbleToDefault() }
                                    snackbarHostState.showSnackbar("🔄 Reset to default")
                                }
                            }
                        )
                    }

                    Card(
                        modifier = Modifier
                            .weight(0.45f)
                            .height(580.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        BubbleSelectorPanel(
                            equippedBubble = equippedBubbleLocal,
                            selectedBubble = selectedBubble,
                            onSelect = { selectedBubble = it },
                            onQuickBuy = { id ->
                                if (id == 10) {
                                    levelLockTargetId = 10
                                    showLevelLockDialog = true
                                } else {
                                    pendingBuyId = id
                                    showConfirm = true
                                }
                            },
                            isPurchasedProvider = { isPurchased(it) },
                            prices = prices,
                            isLevel7BubbleUnlocked = isLevel7BubbleUnlocked,
                            currentLevel = currentLevel
                        )
                    }
                }
            } else {
                // Phone layout
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    BubblePreviewPanel(
                        selectedBubble = selectedBubble,
                        selectedName = getBubbleName(selectedBubble),
                        isPurchased = isPurchased(selectedBubble),
                        isEquipped = equippedBubbleLocal == selectedBubble,
                        price = prices[selectedBubble] ?: 0,
                        isLuxPriced = isLuxPricedBubble(selectedBubble),
                        isLevelLocked = isLevelLockedItem(selectedBubble),
                        requiredLevel = if (selectedBubble == 10) 7 else 0,
                        currentLevel = currentLevel,
                        onBuyOrEquip = {
                            if (selectedBubble == 10) {
                                if (isLevel7BubbleUnlocked) {
                                    handleBuyOrEquip(
                                        selectedBubble = selectedBubble,
                                        isPurchased = isPurchased(selectedBubble),
                                        isEquipped = equippedBubbleLocal == selectedBubble,
                                        onEquip = { id ->
                                            equippedBubbleLocal = id
                                            coroutineScope.launch {
                                                withContext(Dispatchers.IO) { ds.equipBubble(id) }
                                                snackbarHostState.showSnackbar("✅ Equipped!")
                                            }
                                        },
                                        onUnequip = {
                                            equippedBubbleLocal = 0
                                            coroutineScope. launch {
                                                withContext(Dispatchers.IO) { ds.equipBubble(0) }
                                                snackbarHostState.showSnackbar("Unequipped")
                                            }
                                        },
                                        onBuy = { },
                                        onNoSelection = { }
                                    )
                                } else {
                                    levelLockTargetId = 10
                                    showLevelLockDialog = true
                                }
                            } else {
                                handleBuyOrEquip(
                                    selectedBubble = selectedBubble,
                                    isPurchased = isPurchased(selectedBubble),
                                    isEquipped = equippedBubbleLocal == selectedBubble,
                                    onEquip = { id ->
                                        equippedBubbleLocal = id
                                        coroutineScope.launch {
                                            withContext(Dispatchers.IO) { ds.equipBubble(id) }
                                            snackbarHostState.showSnackbar("✅ Equipped!")
                                        }
                                    },
                                    onUnequip = {
                                        equippedBubbleLocal = 0
                                        coroutineScope.launch {
                                            withContext(Dispatchers.IO) { ds.equipBubble(0) }
                                            snackbarHostState.showSnackbar("Unequipped")
                                        }
                                    },
                                    onBuy = { id ->
                                        pendingBuyId = id
                                        showConfirm = true
                                    },
                                    onNoSelection = {
                                        coroutineScope. launch {
                                            snackbarHostState.showSnackbar("Select a bubble first")
                                        }
                                    }
                                )
                            }
                        },
                        onReset = {
                            coroutineScope.launch {
                                selectedBubble = 0
                                equippedBubbleLocal = 0
                                withContext(Dispatchers.IO) { ds.resetBubbleToDefault() }
                                snackbarHostState.showSnackbar("🔄 Reset to default")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier. height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    BubbleSelectorPanel(
                        equippedBubble = equippedBubbleLocal,
                        selectedBubble = selectedBubble,
                        onSelect = { selectedBubble = it },
                        onQuickBuy = { id ->
                            if (id == 10) {
                                levelLockTargetId = 10
                                showLevelLockDialog = true
                            } else {
                                pendingBuyId = id
                                showConfirm = true
                            }
                        },
                        isPurchasedProvider = { isPurchased(it) },
                        prices = prices,
                        isLevel7BubbleUnlocked = isLevel7BubbleUnlocked,
                        currentLevel = currentLevel
                    )
                }
            }

            Spacer(modifier = Modifier. height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }

    // Purchase Dialog (for buyable items)
    if (showConfirm && pendingBuyId != 0 && pendingBuyId != 10) {
        val isLuxPriced = isLuxPricedBubble(pendingBuyId)

        BubblePurchaseDialog(
            bubbleId = pendingBuyId,
            bubbleName = getBubbleName(pendingBuyId),
            price = prices[pendingBuyId] ?: 0,
            isLuxPriced = isLuxPriced,
            onConfirm = {
                val id = pendingBuyId
                val price = prices[id] ?: 0
                val useLux = isLuxPricedBubble(id)

                setPurchased(id, true)
                equippedBubbleLocal = id
                selectedBubble = id
                showConfirm = false

                coroutineScope.launch {
                    withContext(Dispatchers. IO) {
                        try {
                            val ok = if (useLux) {
                                ds.buyBubbleWithLux(id, price)
                            } else {
                                ds.buyBubble(id, price)
                            }
                            if (ok) {
                                snackbarHostState.showSnackbar("🎉 Purchased!")
                            } else {
                                val actualPurchased = ds.isBubblePurchasedFlow(id).first()
                                val actualEquipped = ds.equippedBubbleFlow().first()
                                setPurchased(id, actualPurchased)
                                equippedBubbleLocal = actualEquipped
                                snackbarHostState.showSnackbar("❌ Not enough resources")
                            }
                        } catch (e: Exception) {
                            val actualPurchased = ds.isBubblePurchasedFlow(id).first()
                            val actualEquipped = ds.equippedBubbleFlow().first()
                            setPurchased(id, actualPurchased)
                            equippedBubbleLocal = actualEquipped
                            snackbarHostState.showSnackbar("Error: ${e.localizedMessage ?: "unknown"}")
                        }
                    }
                }
            },
            onDismiss = { showConfirm = false }
        )
    }

    // Level Lock Dialog
    if (showLevelLockDialog) {
        BubbleLevelLockDialog(
            requiredLevel = 7,
            currentLevel = currentLevel,
            itemName = getBubbleName(levelLockTargetId),
            onDismiss = { showLevelLockDialog = false }
        )
    }
}

// ==================== HELPER FUNCTIONS ====================

private fun getBubbleName(id: Int): String = when (id) {
    1 -> "Golden Bubble"
    2 -> "Rainbow Bubble"
    3 -> "Green Bubble"
    4 -> "Pink Bubble"
    5 -> "Cyberpunk Bubble"
    6 -> "Ocean Bubble"
    7 -> "Anime Bubble"
    8 -> "Space Bubble"
    10 -> "🌟 Level 7 Exclusive"
    11 -> "Fire Bubble"
    12 -> "Ice Bubble"
    13 -> "Electric Bubble"
    14 -> "Nature Bubble"
    15 -> "Galaxy Bubble"
    16 -> "Lava Bubble"
    17 -> "Crystal Bubble"
    18 -> "Sunset Bubble"
    19 -> "Midnight Bubble"
    20 -> "Cherry Blossom Bubble"
    21 -> "Toxic Bubble"
    22 -> "Water Bubble"
    23 -> "Diamond Bubble"
    24 -> "Neon Bubble"
    25 -> "Aurora Bubble"
    26 -> "Rainbow Swirl Bubble"
    27 -> "Smoke Bubble"
    28 -> "Candy Bubble"
    29 -> "Metal Bubble"
    30 -> "Plasma Bubble"
    else -> "Default Bubble"
}

private fun getBubbleDrawable(id: Int): Int = when (id) {
    1 -> R.drawable.goldenbubble
    2 -> R.drawable.rainbowbubble
    3 -> R.drawable.greenbubble
    4 -> R.drawable. pinkbubble
    5 -> R.drawable.cyberpunkbubble
    6 -> R.drawable.oceanbubble
    7 -> R.drawable.animebubble1
    8 -> R.drawable.spacebubble
    10 -> R.drawable.levelbubble // Level 7 exclusive bubble
    else -> R.drawable.bubble
}

private fun getBubbleRarity(id: Int): BubbleRarity = when (id) {
    // Estáticos
    1 -> BubbleRarity.RARE        // Golden
    2 -> BubbleRarity.LEGENDARY   // Rainbow
    3 -> BubbleRarity.COMMON      // Green
    4 -> BubbleRarity.COMMON      // Pink
    5 -> BubbleRarity.LEGENDARY   // Cyberpunk
    6 -> BubbleRarity.RARE        // Ocean
    7 -> BubbleRarity.EPIC        // Anime
    8 -> BubbleRarity. EPIC        // Space
    10 -> BubbleRarity.EXCLUSIVE  // Level 7
    // Generados
    11 -> BubbleRarity.EPIC       // Fire
    12 -> BubbleRarity.EPIC       // Ice
    13 -> BubbleRarity.EPIC       // Electric
    14 -> BubbleRarity.RARE       // Nature
    15 -> BubbleRarity.LEGENDARY  // Galaxy
    16 -> BubbleRarity.EPIC       // Lava
    17 -> BubbleRarity. LEGENDARY  // Crystal
    18 -> BubbleRarity.RARE       // Sunset
    19 -> BubbleRarity. EPIC       // Midnight
    20 -> BubbleRarity. RARE       // Cherry Blossom
    21 -> BubbleRarity.EPIC       // Toxic
    22 -> BubbleRarity. RARE       // Water
    23 -> BubbleRarity.LEGENDARY  // Diamond
    24 -> BubbleRarity.EPIC       // Neon
    25 -> BubbleRarity. LEGENDARY  // Aurora
    26 -> BubbleRarity. LEGENDARY  // Rainbow Swirl
    27 -> BubbleRarity.RARE       // Smoke
    28 -> BubbleRarity.RARE       // Candy
    29 -> BubbleRarity. EPIC       // Metal
    30 -> BubbleRarity.LEGENDARY  // Plasma
    else -> BubbleRarity.COMMON
}

// Helper to check if bubble is code-generated (vs drawable)
private fun isGeneratedBubble(id: Int): Boolean = id in 11..30

// ==================== FUNCIÓN CORREGIDA ====================
// LUX se usa para:
// - IDs 2, 5 (Legendary estáticos)
// - IDs 6-8 (Rare/Epic estáticos con Lux)
// - IDs 11-30 (TODOS los generados)
// COINS se usa para:
// - IDs 1, 3, 4 (Common/Rare estáticos con coins)
private fun isLuxPricedBubble(id: Int): Boolean = when (id) {
    2, 5 -> true       // Legendary estáticos
    in 6..8 -> true    // Rare/Epic estáticos con Lux
    in 11..30 -> true  // TODOS los generados usan Lux
    else -> false      // Common/Rare estáticos (1, 3, 4) usan coins
}

private fun isBuyableBubble(id: Int): Boolean = id != 10 && id != 0

// Get composable for generated bubbles
@Composable
private fun GetGeneratedBubble(id: Int, modifier: Modifier = Modifier) {
    when (id) {
        11 -> FireBubble(modifier)
        12 -> IceBubble(modifier)
        13 -> ElectricBubble(modifier)
        14 -> NatureBubble(modifier)
        15 -> GalaxyBubble(modifier)
        16 -> LavaBubble(modifier)
        17 -> CrystalBubble(modifier)
        18 -> SunsetBubble(modifier)
        19 -> MidnightBubble(modifier)
        20 -> CherryBlossomBubble(modifier)
        21 -> ToxicBubble(modifier)
        22 -> WaterBubble(modifier)
        23 -> DiamondBubble(modifier)
        24 -> NeonBubble(modifier)
        25 -> AuroraBubble(modifier)
        26 -> RainbowSwirlBubble(modifier)
        27 -> SmokeBubble(modifier)
        28 -> CandyBubble(modifier)
        29 -> MetalBubble(modifier)
        30 -> PlasmaBubble(modifier)
    }
}

private enum class BubbleRarity(val color: Color, val label: String) {
    COMMON(Color(0xFF9E9E9E), "Common"),
    RARE(Color(0xFF2196F3), "Rare"),
    EPIC(Color(0xFF9C27B0), "Epic"),
    LEGENDARY(Color(0xFFFFD700), "Legendary"),
    EXCLUSIVE(Color(0xFF00E676), "Exclusive")
}

private fun handleBuyOrEquip(
    selectedBubble: Int,
    isPurchased: Boolean,
    isEquipped: Boolean,
    onEquip: (Int) -> Unit,
    onUnequip: () -> Unit,
    onBuy: (Int) -> Unit,
    onNoSelection: () -> Unit
) {
    when {
        selectedBubble == 0 -> onNoSelection()
        !isPurchased -> onBuy(selectedBubble)
        isEquipped -> onUnequip()
        else -> onEquip(selectedBubble)
    }
}

// ==================== STYLED TEXT ====================

@Composable
private fun BubbleStyledText(
    text: String,
    fontSize: Int = 16,
    fontWeight: FontWeight = FontWeight. Normal,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        color = color,
        modifier = modifier,
        style = TextStyle(
            shadow = Shadow(
                color = Color.Black. copy(alpha = 0.6f),
                offset = Offset(1f, 1f),
                blurRadius = 2f
            )
        )
    )
}

// ==================== PREVIEW PANEL ====================

@Composable
private fun BubblePreviewPanel(
    selectedBubble: Int,
    selectedName: String,
    isPurchased: Boolean,
    isEquipped: Boolean,
    price: Int,
    isLuxPriced: Boolean,
    isLevelLocked: Boolean = false,
    requiredLevel: Int = 0,
    currentLevel: Int = 1,
    onBuyOrEquip: () -> Unit,
    onReset: () -> Unit
) {
    val rarity = getBubbleRarity(selectedBubble)
    val infiniteTransition = rememberInfiniteTransition(label = "bubbleFloat")

    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A1A4A).copy(alpha = 0.5f),
                        Color(0xFF1A1A2E)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                BubbleStyledText(
                    text = "Preview",
                    fontSize = 12,
                    color = Color.White. copy(alpha = 0.7f)
                )
                BubbleStyledText(
                    text = selectedName,
                    fontSize = 18,
                    fontWeight = FontWeight.Bold
                )
            }

            if (selectedBubble > 0) {
                RarityBadge(rarity = rarity)
            }
        }

        Spacer(modifier = Modifier. height(8.dp))

        // Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            rarity.color.copy(alpha = 0.15f),
                            Color(0xFF0A0A15).copy(alpha = 0.8f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            rarity.color.copy(alpha = 0.5f),
                            rarity.color.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment. Center
        ) {
            BubbleParticlesBackground(rarityColor = rarity.color)

            // Display generated bubble or drawable
            if (isGeneratedBubble(selectedBubble)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .graphicsLayer {
                            translationY = floatOffset
                        }
                        .alpha(if (isLevelLocked) 0.5f else 1f)
                ) {
                    GetGeneratedBubble(id = selectedBubble)
                }
            } else {
                val drawable = getBubbleDrawable(selectedBubble)
                Image(
                    painter = painterResource(id = drawable),
                    contentDescription = "bubble_preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .graphicsLayer {
                            translationY = floatOffset
                        }
                        .alpha(if (isLevelLocked) 0.5f else 1f),
                    contentScale = ContentScale. Fit
                )
            }

            // Level lock overlay
            if (isLevelLocked) {
                Box(
                    modifier = Modifier
                        . fillMaxSize()
                        . background(Color. Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔒", fontSize = 48.sp)
                        Spacer(modifier = Modifier. height(8.dp))
                        BubbleStyledText(
                            text = "Reach Level $requiredLevel",
                            fontSize = 16,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BubbleStyledText(
                            text = "Current:  Level $currentLevel",
                            fontSize = 12,
                            color = Color. White. copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Equipped badge
            if (isEquipped && !isLevelLocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF4CAF50))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✓ EQUIPPED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Generated badge
            if (isGeneratedBubble(selectedBubble) && !isLevelLocked) {
                Box(
                    modifier = Modifier
                        . align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        . background(Color(0xFFFF6D00))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "✨ ANIMATED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Info
        if (selectedBubble > 0) {
            if (isLevelLocked) {
                BubbleLevelLockInfo(requiredLevel = requiredLevel, currentLevel = currentLevel)
            } else {
                BubbleStatusInfo(
                    isPurchased = isPurchased,
                    isEquipped = isEquipped,
                    price = price,
                    isLuxPriced = isLuxPriced,
                    isLevelUnlock = selectedBubble == 10
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBuyOrEquip,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        selectedBubble == 0 -> Color(0xFF666666)
                        isLevelLocked -> Color(0xFF9C27B0)
                        ! isPurchased -> Color(0xFFFF6D00)
                        isEquipped -> Color(0xFFFF5252)
                        else -> Color(0xFF4CAF50)
                    }
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                when {
                    selectedBubble == 0 -> {
                        BubbleStyledText(text = "Select", fontSize = 14, fontWeight = FontWeight.Bold)
                    }
                    isLevelLocked -> {
                        Row(verticalAlignment = Alignment. CenterVertically) {
                            Text(text = "🔒", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            BubbleStyledText(text = "Level $requiredLevel Required", fontSize = 12, fontWeight = FontWeight.Bold)
                        }
                    }
                    !isPurchased && selectedBubble != 10 -> {
                        Row(verticalAlignment = Alignment. CenterVertically) {
                            BubbleStyledText(text = "BUY $price", fontSize = 14, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = painterResource(
                                    id = if (isLuxPriced) R.drawable.gemgame else R.drawable.coin
                                ),
                                contentDescription = null,
                                modifier = Modifier. size(16.dp)
                            )
                        }
                    }
                    isEquipped -> {
                        BubbleStyledText(text = "UNEQUIP", fontSize = 14, fontWeight = FontWeight.Bold)
                    }
                    else -> {
                        BubbleStyledText(text = "✓ EQUIP", fontSize = 14, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Button(
                onClick = onReset,
                modifier = Modifier
                    .width(70.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                elevation = ButtonDefaults. buttonElevation(defaultElevation = 4.dp)
            ) {
                BubbleStyledText(text = "↺", fontSize = 20, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== LEVEL LOCK INFO ====================

@Composable
private fun BubbleLevelLockInfo(requiredLevel: Int, currentLevel:  Int) {
    val progress = (currentLevel. toFloat() / requiredLevel.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF9C27B0).copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                . fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "⭐", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Level $requiredLevel Exclusive",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color. White. copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        . fillMaxWidth(progress)
                        .height(8.dp)
                        . clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF6D00), Color(0xFFFFD700))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Your Level:  $currentLevel / $requiredLevel",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "🎮 Keep playing to level up!",
                fontSize = 10.sp,
                color = Color(0xFF00E676)
            )
        }
    }
}

// ==================== RARITY BADGE ====================

@Composable
private fun RarityBadge(rarity: BubbleRarity) {
    val infiniteTransition = rememberInfiniteTransition(label = "rarityGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(rarity.color. copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = rarity.color.copy(alpha = glowAlpha),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = rarity.label. uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = rarity.color
        )
    }
}

// ==================== STATUS INFO ====================

@Composable
private fun BubbleStatusInfo(
    isPurchased: Boolean,
    isEquipped: Boolean,
    price: Int,
    isLuxPriced: Boolean,
    isLevelUnlock: Boolean = false
) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Status",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier. height(2.dp))
                Text(
                    text = when {
                        isEquipped -> "Equipped"
                        isPurchased -> "Owned"
                        else -> "Locked"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isEquipped -> Color(0xFF4CAF50)
                        isPurchased -> Color(0xFF2196F3)
                        else -> Color(0xFFFF5252)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            // Price/Unlock
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isLevelUnlock) "Unlock" else "Price",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(2.dp))

                if (isLevelUnlock) {
                    Row(verticalAlignment = Alignment. CenterVertically) {
                        Text(text = "⭐", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isPurchased) "UNLOCKED" else "Level 7",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPurchased) Color(0xFF4CAF50) else Color(0xFFFFD700)
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment. CenterVertically) {
                        Text(
                            text = if (isPurchased) "OWNED" else "$price",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPurchased) Color(0xFF4CAF50) else Color(0xFFFFD700)
                        )
                        if (! isPurchased) {
                            Spacer(modifier = Modifier. width(4.dp))
                            Image(
                                painter = painterResource(
                                    id = if (isLuxPriced) R.drawable.gemgame else R.drawable.coin
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==================== PARTICLES BACKGROUND ====================

@Composable
private fun BubbleParticlesBackground(rarityColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particle1Y by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Restart
        ),
        label = "p1"
    )

    val particle2Y by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, delayMillis = 500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Restart
        ),
        label = "p2"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.4f)) {
        val particleColor = rarityColor.copy(alpha = 0.3f)

        drawCircle(
            color = particleColor,
            radius = 6f,
            center = Offset(size.width * 0.2f, size.height * particle1Y)
        )

        drawCircle(
            color = particleColor,
            radius = 4f,
            center = Offset(size.width * 0.7f, size.height * particle2Y)
        )

        drawCircle(
            color = particleColor,
            radius = 8f,
            center = Offset(size.width * 0.5f, size.height * ((particle1Y + particle2Y) / 2))
        )
    }
}

// ==================== SELECTOR PANEL ====================

@Composable
private fun BubbleSelectorPanel(
    equippedBubble: Int,
    selectedBubble: Int,
    onSelect: (Int) -> Unit,
    onQuickBuy: (Int) -> Unit,
    isPurchasedProvider: (Int) -> Boolean,
    prices: Map<Int, Int>,
    isLevel7BubbleUnlocked: Boolean = false,
    currentLevel: Int = 1
) {
    // Include ID 10 (Level 7 exclusive) and new generated bubbles (11-30)
    val bubbleList = listOf(1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30)
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF2A1A4A).copy(alpha = 0.5f)
                    )
                )
            )
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BubbleStyledText(
                text = "🎯 Collection",
                fontSize = 16,
                fontWeight = FontWeight. Bold
            )

            Text(
                text = "${bubbleList.count { isPurchasedProvider(it) }}/${bubbleList.size}",
                fontSize = 12. sp,
                color = Color. White.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier. height(10.dp))

        // List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll),
            verticalArrangement = Arrangement. spacedBy(8.dp)
        ) {
            bubbleList.forEach { id ->
                val isLevelLockItem = id == 10
                val isPurchased = if (isLevelLockItem) isLevel7BubbleUnlocked else isPurchasedProvider(id)

                BubbleSelectorItem(
                    id = id,
                    name = getBubbleName(id),
                    drawableId = getBubbleDrawable(id),
                    rarity = getBubbleRarity(id),
                    isPurchased = isPurchased,
                    isEquipped = equippedBubble == id,
                    isSelected = selectedBubble == id,
                    price = prices[id] ?: 0,
                    isLuxPriced = isLuxPricedBubble(id),
                    isLevelLocked = isLevelLockItem && !isLevel7BubbleUnlocked,
                    requiredLevel = if (isLevelLockItem) 7 else 0,
                    currentLevel = currentLevel,
                    onSelect = { onSelect(id) },
                    onQuickBuy = { onQuickBuy(id) }
                )
            }

            Spacer(modifier = Modifier. height(4.dp))
        }
    }
}

// ==================== SELECTOR ITEM ====================

@Composable
private fun BubbleSelectorItem(
    id: Int,
    name: String,
    drawableId: Int,
    rarity: BubbleRarity,
    isPurchased:  Boolean,
    isEquipped:  Boolean,
    isSelected: Boolean,
    price: Int,
    isLuxPriced:  Boolean,
    isLevelLocked: Boolean = false,
    requiredLevel:  Int = 0,
    currentLevel: Int = 1,
    onSelect: () -> Unit,
    onQuickBuy:  () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "itemScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .scale(scale)
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                rarity.color.copy(alpha = 0.15f)
            else
                Color(0xFF0F0F1A).copy(alpha = 0.6f)
        ),
        elevation = CardDefaults. cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isSelected) {
                        Modifier. border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(rarity.color, rarity.color.copy(alpha = 0.5f))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else Modifier
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                rarity.color.copy(alpha = 0.2f),
                                Color. Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment. Center
            ) {
                // Display generated bubble or drawable
                if (isGeneratedBubble(id)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .alpha(if (isLevelLocked) 0.4f else 1f)
                    ) {
                        GetGeneratedBubble(id = id)
                    }
                } else {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "bubble_$id",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .alpha(if (isLevelLocked) 0.4f else 1f),
                        contentScale = ContentScale.Fit
                    )
                }

                // Lock overlay
                if (isLevelLocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⭐", fontSize = 14.sp)
                        }
                    }
                } else if (! isPurchased) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color. Black.copy(alpha = 0.4f))
                            .clip(RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔒", fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier. width(10.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight. Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Rarity
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(rarity.color)
                    )
                    Spacer(modifier = Modifier. width(4.dp))
                    Text(
                        text = rarity. label,
                        fontSize = 10.sp,
                        color = rarity.color
                    )

                    // Animated badge for generated bubbles
                    if (isGeneratedBubble(id)) {
                        Spacer(modifier = Modifier. width(6.dp))
                        Text(
                            text = "✨",
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Price/Status
                if (isLevelLocked) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onQuickBuy() }
                    ) {
                        Text(text = "⭐", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Level $requiredLevel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                } else if (isPurchased) {
                    Text(
                        text = "✓ Owned",
                        fontSize = 10.sp,
                        color = Color(0xFF4CAF50)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier. clickable { onQuickBuy() }
                    ) {
                        Text(
                            text = "$price",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier. width(3.dp))
                        Image(
                            painter = painterResource(
                                id = if (isLuxPriced) R.drawable.gemgame else R.drawable.coin
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Equipped badge
            if (isEquipped && !isLevelLocked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF4CAF50))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "✓",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==================== LEVEL LOCK DIALOG ====================

@Composable
private fun BubbleLevelLockDialog(
    requiredLevel: Int,
    currentLevel: Int,
    itemName: String,
    onDismiss: () -> Unit
) {
    val progress = (currentLevel. toFloat() / requiredLevel.toFloat()).coerceIn(0f, 1f)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier. fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "⭐", fontSize = 48.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Level $requiredLevel Required",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = itemName,
                    fontSize = 14.sp,
                    color = Color.White. copy(alpha = 0.8f)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment. CenterHorizontally
            ) {
                Card(
                    modifier = Modifier. fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Progress",
                            fontSize = 12.sp,
                            color = Color.White. copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Level display
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement. Center
                        ) {
                            Text(
                                text = "$currentLevel",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFF6D00)
                            )
                            Text(
                                text = " / $requiredLevel",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color. White.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color. White. copy(alpha = 0.1f))
                        ) {
                            Box(
                                modifier = Modifier
                                    . fillMaxWidth(progress)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        brush = Brush. horizontalGradient(
                                            colors = listOf(Color(0xFFFF6D00), Color(0xFFFFD700))
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${(progress * 100).toInt()}% Complete",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🎮 Keep playing games to earn XP and level up!",
                    fontSize = 12.sp,
                    color = Color(0xFF00E676),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This exclusive bubble can only be unlocked by reaching Level $requiredLevel.  It cannot be purchased with coins or gems.",
                    fontSize = 11.sp,
                    color = Color. White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6D00)
                )
            ) {
                Text(
                    text = "Got it!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}

// ==================== PURCHASE DIALOG ====================

@Composable
private fun BubblePurchaseDialog(
    bubbleId: Int,
    bubbleName: String,
    price: Int,
    isLuxPriced: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val rarity = getBubbleRarity(bubbleId)
    val isGenerated = isGeneratedBubble(bubbleId)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🛒 Confirm Purchase",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Preview
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(rarity.color. copy(alpha = 0.15f))
                        .border(
                            width = 2.dp,
                            color = rarity.color.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerated) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            GetGeneratedBubble(id = bubbleId)
                        }
                    } else {
                        Image(
                            painter = painterResource(id = getBubbleDrawable(bubbleId)),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = bubbleName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                RarityBadge(rarity = rarity)
            }
        },
        text = {
            Card(
                modifier = Modifier. fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price: ",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "$price",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Image(
                        painter = painterResource(
                            id = if (isLuxPriced) R.drawable.gemgame else R.drawable.coin
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "✓ Purchase",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 14.sp,
                    color = Color.White. copy(alpha = 0.8f)
                )
            }
        }
    )
}