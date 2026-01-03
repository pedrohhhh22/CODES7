package com.appsdevs.popit

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx. compose.foundation.Image
import androidx.compose.foundation. background
import androidx.compose.foundation.border
import androidx.compose. foundation.clickable
import androidx.compose.foundation.layout. Arrangement
import androidx.compose.foundation.layout.Box
import androidx. compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose. foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation. layout.fillMaxSize
import androidx.compose.foundation. layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation. layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout. width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation. lazy.grid.GridCells
import androidx. compose.foundation.lazy.grid. LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose. foundation.shape.RoundedCornerShape
import androidx.compose. foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose. material3.TextButton
import androidx.compose.runtime.Composable
import androidx. compose.runtime.LaunchedEffect
import androidx.compose.runtime. collectAsState
import androidx.compose. runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose. runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui. Modifier
import androidx.compose. ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose. ui.graphics. Brush
import androidx.compose.ui.graphics.Color
import androidx. compose.ui.platform.LocalContext
import androidx.compose.ui. res.painterResource
import androidx. compose.ui.text.font. FontWeight
import androidx.compose. ui.unit.dp
import androidx.compose. ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui. window.DialogProperties
import kotlinx.coroutines.launch

// ==================== BANNER COLOR SELECTOR ====================

@Composable
fun BannerColorSelector(
    currentColorId: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (BannerColors.isAnimated(currentColorId)) 1 else 0) }
    var selectedBannerId by remember { mutableIntStateOf(currentColorId) }

    val context = LocalContext. current
    val dataStore = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    // Observar lux y banners comprados
    val currentLux by dataStore.luxFlow().collectAsState(initial = 0)
    val purchasedBanners by dataStore.purchasedAnimatedBannersFlow().collectAsState(initial = emptySet())

    // Estado para mostrar diálogo de compra
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var bannerToPurchase by remember { mutableIntStateOf(-1) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2A1A4A), Color(0xFF1A1A2E))
                        )
                    )
                    . padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header con Lux
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement. SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎨", fontSize = 24.sp)
                        Spacer(modifier = Modifier. width(8.dp))
                        Text(
                            text = "Choose Banner",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color. White
                        )
                    }

                    // Mostrar Lux disponible
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A1A4A))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable. gemgame),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier. width(4.dp))
                        Text(
                            text = "$currentLux",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00BFFF)
                        )
                    }
                }

                Spacer(modifier = Modifier. height(4.dp))

                // Cerrar botón
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement. End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("✕", fontSize = 18.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier. height(8.dp))

                // Preview
                HorizontalProfileBanner(
                    bannerColorId = selectedBannerId,
                    width = 260.dp,
                    height = 100.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = BannerColors.getById(selectedBannerId).name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (BannerColors.isAnimated(selectedBannerId))
                        Color(0xFFFFD700) else BannerColors.getById(selectedBannerId).secondaryColor
                )

                if (BannerColors.isAnimated(selectedBannerId)) {
                    val isPurchased = selectedBannerId in purchasedBanners
                    Text(
                        text = if (isPurchased) "✨ Animated • Owned" else "✨ Animated • 75 Lux",
                        fontSize = 11.sp,
                        color = if (isPurchased) Color(0xFF4CAF50) else Color(0xFFFFD700).copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = "🆓 Free",
                        fontSize = 11.sp,
                        color = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color. Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier. tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFF6D00)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "🎨 Static (${BannerColors.staticColors.size})",
                                color = if (selectedTab == 0) Color. White else Color.White. copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "✨ Animated (${BannerColors.animatedBanners.size})",
                                color = if (selectedTab == 1) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement. spacedBy(12.dp)
                ) {
                    when (selectedTab) {
                        0 -> {
                            // Static banners - FREE
                            val rows = BannerColors.staticColors.chunked(3)
                            rows.forEach { row ->
                                Row(
                                    modifier = Modifier. fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    row.forEach { bannerColor ->
                                        BannerThumbnailWithStatus(
                                            bannerColorId = bannerColor.id,
                                            isSelected = bannerColor.id == selectedBannerId,
                                            isPurchased = true, // Static siempre gratis
                                            onClick = {
                                                selectedBannerId = bannerColor. id
                                            }
                                        )
                                    }
                                    repeat(3 - row.size) {
                                        Spacer(modifier = Modifier.width(100.dp))
                                    }
                                }
                            }
                        }
                        1 -> {
                            // Animated banners - 75 LUX
                            Text(
                                text = "✨ Premium Animated Banners • 75 Lux each",
                                fontSize = 12.sp,
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier. align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier. height(8.dp))

                            BannerColors.animatedBanners.forEach { banner ->
                                val isPurchased = banner.id in purchasedBanners
                                AnimatedBannerOptionWithPrice(
                                    banner = banner,
                                    isSelected = banner.id == selectedBannerId,
                                    isPurchased = isPurchased,
                                    currentLux = currentLux,
                                    onClick = {
                                        selectedBannerId = banner.id
                                    },
                                    onBuyClick = {
                                        bannerToPurchase = banner.id
                                        showPurchaseDialog = true
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de selección
                val isAnimated = BannerColors.isAnimated(selectedBannerId)
                val isPurchased = ! isAnimated || selectedBannerId in purchasedBanners
                val canSelect = isPurchased

                Button(
                    onClick = {
                        if (canSelect) {
                            scope.launch {
                                val success = dataStore.selectBanner(selectedBannerId)
                                if (success) {
                                    onColorSelected(selectedBannerId)
                                    onDismiss()
                                }
                            }
                        } else {
                            // Mostrar diálogo de compra
                            bannerToPurchase = selectedBannerId
                            showPurchaseDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canSelect) Color(0xFF4CAF50) else Color(0xFFFF6D00)
                    )
                ) {
                    if (canSelect) {
                        Text(
                            text = "✓ Select Banner",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable. gemgame),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buy for 75 Lux",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de compra
    if (showPurchaseDialog && bannerToPurchase >= 0) {
        PurchaseBannerDialog(
            bannerId = bannerToPurchase,
            currentLux = currentLux,
            onConfirm = {
                scope.launch {
                    val success = dataStore.purchaseAnimatedBanner(bannerToPurchase)
                    if (success) {
                        selectedBannerId = bannerToPurchase
                    }
                    showPurchaseDialog = false
                    bannerToPurchase = -1
                }
            },
            onDismiss = {
                showPurchaseDialog = false
                bannerToPurchase = -1
            }
        )
    }
}

@Composable
private fun BannerThumbnailWithStatus(
    bannerColorId: Int,
    isSelected: Boolean,
    isPurchased: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(55.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color.White. copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {
        HorizontalProfileBanner(
            bannerColorId = bannerColorId,
            width = 100.dp,
            height = 55.dp
        )

        // Badge de seleccionado
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50)),
                contentAlignment = Alignment. Center
            ) {
                Text("✓", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AnimatedBannerOptionWithPrice(
    banner: BannerColor,
    isSelected: Boolean,
    isPurchased: Boolean,
    currentLux: Int,
    onClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "optionScale"
    )

    val canAfford = currentLux >= DataStoreManager. ANIMATED_BANNER_PRICE_LUX

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected && isPurchased -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                isSelected -> Color(0xFFFFD700).copy(alpha = 0.15f)
                else -> Color(0xFF0F0F1A).copy(alpha = 0.5f)
            }
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = when {
                isSelected && isPurchased -> Color(0xFF4CAF50)
                isSelected -> Color(0xFFFFD700)
                else -> Color. White.copy(alpha = 0.2f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(55.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                HorizontalProfileBanner(
                    bannerColorId = banner. id,
                    width = 120.dp,
                    height = 55.dp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = banner.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = if (isPurchased) "✨ Owned" else "✨ Animated Effect",
                    fontSize = 11.sp,
                    color = if (isPurchased) Color(0xFF4CAF50) else Color(0xFFFFD700).copy(alpha = 0.8f)
                )
            }

            // Mostrar estado de compra o precio
            if (isPurchased) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Owned",
                            fontSize = 11.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // Botón de compra
                Button(
                    onClick = onBuyClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) Color(0xFFFF6D00) else Color(0xFF555555)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier. height(36.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable. gemgame),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "75",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseBannerDialog(
    bannerId: Int,
    currentLux: Int,
    onConfirm: () -> Unit,
    onDismiss:  () -> Unit
) {
    val banner = BannerColors.getById(bannerId)
    val canAfford = currentLux >= DataStoreManager.ANIMATED_BANNER_PRICE_LUX

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                . fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎨 Purchase Banner",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Preview del banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    HorizontalProfileBanner(
                        bannerColorId = bannerId,
                        width = 200.dp,
                        height = 80.dp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = banner.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )

                Text(
                    text = "✨ Animated Banner",
                    fontSize = 12.sp,
                    color = Color. White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Precio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement. Center
                ) {
                    Text(
                        text = "Price: ",
                        fontSize = 16.sp,
                        color = Color. White
                    )
                    Image(
                        painter = painterResource(id = R.drawable. gemgame),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${DataStoreManager.ANIMATED_BANNER_PRICE_LUX}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00BFFF)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lux actual
                Text(
                    text = "Your Lux: $currentLux",
                    fontSize = 14.sp,
                    color = if (canAfford) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )

                if (! canAfford) {
                    Spacer(modifier = Modifier. height(4.dp))
                    Text(
                        text = "Not enough Lux! ",
                        fontSize = 12.sp,
                        color = Color(0xFFFF5252)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement. spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF555555)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) Color(0xFF4CAF50) else Color(0xFF555555)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Buy", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==================== AVATAR SELECTOR DIALOG ====================

@Composable
fun AvatarSelectorDialog(
    currentAvatarId: Int,
    onAvatarSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (GeneratedAvatars.isAnimated(currentAvatarId)) 1 else 0) }
    var selectedAvatarId by remember { mutableIntStateOf(currentAvatarId) }

    val context = LocalContext.current
    val dataStore = remember { DataStoreManager(context) }
    val scope = rememberCoroutineScope()

    // Observar lux y avatares comprados
    val currentLux by dataStore.luxFlow().collectAsState(initial = 0)
    val purchasedAvatars by dataStore.purchasedAnimatedAvatarsFlow().collectAsState(initial = emptySet())

    // Estado para mostrar diálogo de compra
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var avatarToPurchase by remember { mutableIntStateOf(-1) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2A1A4A), Color(0xFF1A1A2E))
                        )
                    )
                    . padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header con Lux
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement. SpaceBetween,
                    verticalAlignment = Alignment. CenterVertically
                ) {
                    Row(verticalAlignment = Alignment. CenterVertically) {
                        Text(text = "👤", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Choose Avatar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color. White
                        )
                    }

                    // Mostrar Lux disponible
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2A1A4A))
                            . padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable. gemgame),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$currentLux",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00BFFF)
                        )
                    }
                }

                // Cerrar botón
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement. End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("✕", fontSize = 18.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier. height(8.dp))

                // Preview
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFF4CAF50), CircleShape)
                ) {
                    GeneratedAvatar(
                        avatarId = selectedAvatarId,
                        modifier = Modifier. fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = GeneratedAvatars.getById(selectedAvatarId).name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (GeneratedAvatars.isAnimated(selectedAvatarId))
                        Color(0xFFFFD700) else Color. White
                )

                if (GeneratedAvatars.isAnimated(selectedAvatarId)) {
                    val isPurchased = selectedAvatarId in purchasedAvatars
                    Text(
                        text = if (isPurchased) "✨ Animated • Owned" else "✨ Animated • 75 Lux",
                        fontSize = 11.sp,
                        color = if (isPurchased) Color(0xFF4CAF50) else Color(0xFFFFD700).copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        text = "🆓 Free",
                        fontSize = 11.sp,
                        color = Color(0xFF4CAF50)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color. Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF4CAF50)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "🎭 Static (${GeneratedAvatars.staticAvatars.size})",
                                color = if (selectedTab == 0) Color. White else Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "✨ Animated (${GeneratedAvatars.animatedAvatars.size})",
                                color = if (selectedTab == 1) Color(0xFFFFD700) else Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Info de precio para animated
                if (selectedTab == 1) {
                    Text(
                        text = "✨ Premium Avatars • 75 Lux each",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Avatar grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement. spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val avatarList = if (selectedTab == 0)
                        GeneratedAvatars.staticAvatars
                    else
                        GeneratedAvatars.animatedAvatars

                    itemsIndexed(avatarList) { _, avatar ->
                        val isPurchased = ! avatar.isAnimated || avatar. id in purchasedAvatars
                        GeneratedAvatarThumbnailWithStatus(
                            avatarId = avatar.id,
                            isSelected = avatar.id == selectedAvatarId,
                            isPurchased = isPurchased,
                            onClick = { selectedAvatarId = avatar.id },
                            onBuyClick = {
                                avatarToPurchase = avatar.id
                                showPurchaseDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de selección
                val isAnimated = GeneratedAvatars.isAnimated(selectedAvatarId)
                val isPurchased = ! isAnimated || selectedAvatarId in purchasedAvatars
                val canSelect = isPurchased

                Button(
                    onClick = {
                        if (canSelect) {
                            scope.launch {
                                val success = dataStore.selectAvatar(selectedAvatarId)
                                if (success) {
                                    onAvatarSelected(selectedAvatarId)
                                    onDismiss()
                                }
                            }
                        } else {
                            avatarToPurchase = selectedAvatarId
                            showPurchaseDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canSelect) Color(0xFF4CAF50) else Color(0xFFFF6D00)
                    )
                ) {
                    if (canSelect) {
                        Text(
                            text = "✓ Select Avatar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable. gemgame),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buy for 75 Lux",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de compra
    if (showPurchaseDialog && avatarToPurchase >= 0) {
        PurchaseAvatarDialog(
            avatarId = avatarToPurchase,
            currentLux = currentLux,
            onConfirm = {
                scope.launch {
                    val success = dataStore.purchaseAnimatedAvatar(avatarToPurchase)
                    if (success) {
                        selectedAvatarId = avatarToPurchase
                    }
                    showPurchaseDialog = false
                    avatarToPurchase = -1
                }
            },
            onDismiss = {
                showPurchaseDialog = false
                avatarToPurchase = -1
            }
        )
    }
}

@Composable
private fun GeneratedAvatarThumbnailWithStatus(
    avatarId: Int,
    isSelected: Boolean,
    isPurchased: Boolean,
    onClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "scale"
    )

    val isAnimated = GeneratedAvatars.isAnimated(avatarId)

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = when {
                    isSelected && isPurchased -> Color(0xFF4CAF50)
                    isSelected -> Color(0xFFFFD700)
                    else -> Color.White. copy(alpha = 0.3f)
                },
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment. Center
    ) {
        GeneratedAvatar(
            avatarId = avatarId,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay de candado si es animado y no comprado
        if (isAnimated && !isPurchased) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment. Center
            ) {
                Text("🔒", fontSize = 16.sp)
            }
        }

        // Badge de seleccionado
        if (isSelected && isPurchased) {
            Box(
                modifier = Modifier
                    .align(Alignment. BottomEnd)
                    . size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50))
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment. Center
            ) {
                Text(
                    "✓",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun PurchaseAvatarDialog(
    avatarId: Int,
    currentLux: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val avatar = GeneratedAvatars.getById(avatarId)
    val canAfford = currentLux >= DataStoreManager.ANIMATED_AVATAR_PRICE_LUX

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                . fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "👤 Purchase Avatar",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Preview del avatar
                Box(
                    modifier = Modifier
                        . size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFFFD700), CircleShape)
                ) {
                    GeneratedAvatar(
                        avatarId = avatarId,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = avatar. name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )

                Text(
                    text = "✨ Animated Avatar",
                    fontSize = 12.sp,
                    color = Color. White. copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier. height(20.dp))

                // Precio
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement. Center
                ) {
                    Text(
                        text = "Price: ",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Image(
                        painter = painterResource(id = R.drawable. gemgame),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier. width(4.dp))
                    Text(
                        text = "${DataStoreManager.ANIMATED_AVATAR_PRICE_LUX}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00BFFF)
                    )
                }

                Spacer(modifier = Modifier. height(8.dp))

                // Lux actual
                Text(
                    text = "Your Lux: $currentLux",
                    fontSize = 14.sp,
                    color = if (canAfford) Color(0xFF4CAF50) else Color(0xFFFF5252)
                )

                if (! canAfford) {
                    Spacer(modifier = Modifier. height(4.dp))
                    Text(
                        text = "Not enough Lux! ",
                        fontSize = 12.sp,
                        color = Color(0xFFFF5252)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement. spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF555555)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) Color(0xFF4CAF50) else Color(0xFF555555)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Buy", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}