package com.appsdevs.popit

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.EaseInOutSine
import androidx. compose.animation.core.RepeatMode
import androidx.compose.animation. core.animateFloat
import androidx.compose.animation.core. infiniteRepeatable
import androidx.compose.animation. core.rememberInfiniteTransition
import androidx.compose. animation.core.tween
import androidx. compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose. foundation.clickable
import androidx.compose. foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout. Box
import androidx. compose.foundation.layout.Column
import androidx.compose.foundation. layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx. compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout. asPaddingValues
import androidx.compose.foundation. layout.fillMaxSize
import androidx.compose.foundation.layout. fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose. foundation.layout.padding
import androidx.compose.foundation.layout. size
import androidx.compose.foundation.layout. systemBars
import androidx.compose.foundation. layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation. shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx. compose.material3.Button
import androidx. compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime. Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime. setValue
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.draw.alpha
import androidx.compose.ui.draw. clip
import androidx. compose.ui.geometry.Offset
import androidx.compose.ui.graphics. Brush
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose. ui.text.TextStyle
import androidx. compose.ui.text.font.FontWeight
import androidx.compose.ui.text. style.TextAlign
import androidx.compose. ui.unit. Dp
import androidx. compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window. DialogProperties

// ==================== FULLSCREEN STORE DIALOG ====================

@Composable
fun FullscreenStoreDialog(
    coins: Int,
    lux: Int,
    onClose: () -> Unit,
    onBuyLuxPack: (luxAmount: Int, priceLabel: String) -> Unit,
    onBuyGoldWithLux: (luxCost: Int, goldAmount: Int) -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                . fillMaxSize()
                .background(Color(0xE6000000))
        ) {
            StoreScreen(
                modifier = Modifier.fillMaxSize(),
                coins = coins,
                lux = lux,
                onClose = onClose,
                onRequestBuyLuxPack = { luxAmt, priceLabel -> onBuyLuxPack(luxAmt, priceLabel) },
                onRequestBuyGoldWithLux = { luxCost, goldAmt -> onBuyGoldWithLux(luxCost, goldAmt) },
                statusBarPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
            )
        }
    }
}

// ==================== STORE SCREEN ====================

@Composable
fun StoreScreen(
    modifier: Modifier = Modifier,
    coins: Int,
    lux: Int,
    onClose:  () -> Unit,
    onRequestBuyLuxPack: (luxAmount: Int, simulatedPriceLabel: String) -> Unit,
    onRequestBuyGoldWithLux: (luxCost: Int, goldAmount: Int) -> Unit,
    statusBarPadding:  Dp = 0.dp
) {
    val scrollState = rememberScrollState()

    // LUX Packs data
    val luxPacks = listOf(
        LuxPackData(
            id = 1,
            name = "Starter",
            luxAmount = 499,
            price = "$0.99",
            bonus = null,
            isPopular = false,
            gradientColors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0))
        ),
        LuxPackData(
            id = 2,
            name = "Popular",
            luxAmount = 1999,
            price = "$3.99",
            bonus = "+10%",
            isPopular = true,
            gradientColors = listOf(Color(0xFF7B1FA2), Color(0xFF4A148C))
        ),
        LuxPackData(
            id = 3,
            name = "Best Value",
            luxAmount = 9999,
            price = "$19.99",
            bonus = "+25%",
            isPopular = false,
            gradientColors = listOf(Color(0xFFFF6D00), Color(0xFFE65100))
        )
    )

    // Gold Exchange data - Precios calculados correctamente
    // Base rate: 1 LUX = 10 GOLD
    // 100 LUX = 1,000 GOLD (base, sin bonus)
    // 300 LUX = 3,000 base + 50% bonus = 4,500 GOLD
    // 700 LUX = 7,000 base + 50% bonus = 10,500 GOLD
    val goldExchanges = listOf(
        GoldExchangeData(
            id = 1,
            luxCost = 100,
            goldAmount = 1000,
            bonus = null,
            gradientColors = listOf(Color(0xFF43A047), Color(0xFF2E7D32))
        ),
        GoldExchangeData(
            id = 2,
            luxCost = 300,
            goldAmount = 4500,
            bonus = "+50%",
            gradientColors = listOf(Color(0xFF43A047), Color(0xFF1B5E20))
        ),
        GoldExchangeData(
            id = 3,
            luxCost = 700,
            goldAmount = 10500,
            bonus = "+50%",
            gradientColors = listOf(Color(0xFFFFB300), Color(0xFFFF8F00))
        )
    )

    Surface(
        modifier = modifier,
        color = Color. Transparent
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background gradient
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0F0F1A),
                                Color(0xFF1A1A2E),
                                Color(0xFF16213E)
                            )
                        )
                    )
            )

            // Decorative particles
            StoreParticlesBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = statusBarPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Header
                StoreHeader(
                    coins = coins,
                    lux = lux,
                    onClose = onClose
                )

                Spacer(modifier = Modifier.height(20.dp))

                // LUX Section
                StoreSectionHeader(
                    emoji = "",
                    title = "Buy LUX",
                    subtitle = "Premium currency for exclusive items"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // LUX Packs - Horizontal scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        . horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    luxPacks.forEach { pack ->
                        LuxPackCard(
                            data = pack,
                            onBuy = { onRequestBuyLuxPack(pack. luxAmount, pack. price) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider
                Row(
                    modifier = Modifier. fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color. White.copy(alpha = 0.1f)
                    )
                    Text(
                        text = "  ⚡  ",
                        fontSize = 16.sp,
                        color = Color.White. copy(alpha = 0.5f)
                    )
                    HorizontalDivider(
                        modifier = Modifier. weight(1f),
                        color = Color.White.copy(alpha = 0.1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // GOLD Section
                StoreSectionHeader(
                    emoji = "",
                    title = "Convert LUX → GOLD",
                    subtitle = "Exchange your LUX for in-game gold"
                )

                Spacer(modifier = Modifier. height(12.dp))

                // Gold Exchange Cards - Horizontal scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    goldExchanges.forEach { exchange ->
                        GoldExchangeCard(
                            data = exchange,
                            currentLux = lux,
                            onBuy = { onRequestBuyGoldWithLux(exchange.luxCost, exchange. goldAmount) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info card
                Box(modifier = Modifier.height(0.dp))

                Spacer(modifier = Modifier. height(40.dp))
            }
        }
    }
}

// ==================== PARTICLES BACKGROUND ====================

@Composable
private fun StoreParticlesBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "storeParticles")

    val particle1Y by infiniteTransition. animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode. Reverse
        ),
        label = "p1"
    )

    val particle2Y by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p2"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.3f)) {
        drawCircle(
            color = Color(0xFF7B1FA2),
            radius = 60f,
            center = Offset(size.width * 0.1f, size.height * particle1Y)
        )
        drawCircle(
            color = Color(0xFF1E88E5),
            radius = 40f,
            center = Offset(size.width * 0.85f, size.height * particle2Y)
        )
        drawCircle(
            color = Color(0xFFFFB300),
            radius = 30f,
            center = Offset(size.width * 0.5f, size.height * ((particle1Y + particle2Y) / 2))
        )
    }
}

// ==================== HEADER ====================

@Composable
private fun StoreHeader(
    coins: Int,
    lux: Int,
    onClose: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement. SpaceBetween,
            verticalAlignment = Alignment. CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🏪", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "STORE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color. White,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color. Black. copy(alpha = 0.5f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color. White. copy(alpha = 0.1f))
                    .border(1.dp, Color.White. copy(alpha = 0.2f), CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier. height(12.dp))

        Row(
            modifier = Modifier. fillMaxWidth(),
            horizontalArrangement = Arrangement. End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyPill(
                iconRes = R.drawable. gemgame,
                amount = lux,
                label = "LUX",
                backgroundColor = Color(0xFF7B1FA2).copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            CurrencyPill(
                iconRes = R.drawable. coin,
                amount = coins,
                label = "GOLD",
                backgroundColor = Color(0xFFFFB300).copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun CurrencyPill(
    @DrawableRes iconRes: Int,
    amount:  Int,
    label: String,
    backgroundColor: Color
) {
    Row(
        modifier = Modifier
            . clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, Color. White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale. Fit
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = formatStoreCurrency(amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

// ==================== SECTION HEADER ====================

@Composable
private fun StoreSectionHeader(
    emoji: String,
    title:  String,
    subtitle: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ==================== DATA CLASSES ====================

private data class LuxPackData(
    val id: Int,
    val name: String,
    val luxAmount: Int,
    val price: String,
    val bonus: String?,
    val isPopular: Boolean,
    val gradientColors: List<Color>
)

private data class GoldExchangeData(
    val id: Int,
    val luxCost: Int,
    val goldAmount:  Int,
    val bonus: String?  = null,
    val gradientColors:  List<Color>
)

// ==================== HELPER FUNCTION ====================

private fun formatStoreCurrency(amount: Int): String {
    return when {
        amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000f)
        amount >= 10_000 -> String.format("%.1fK", amount / 1_000f)
        else -> String.format("%,d", amount)
    }
}

// ==================== LUX PACK CARD ====================

@Composable
private fun LuxPackCard(
    data:  LuxPackData,
    onBuy: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "luxCardGlow")
    val glowAlpha by infiniteTransition. animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = Modifier
            .width(170.dp)
            .height(260.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults. cardColors(
            containerColor = Color(0xFF1A1A2E)
        ),
        elevation = CardDefaults. cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                . fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            data.gradientColors[0]. copy(alpha = 0.3f),
                            data.gradientColors[1].copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            data.gradientColors[0].copy(alpha = glowAlpha),
                            data.gradientColors[1]. copy(alpha = glowAlpha * 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(26.dp))

                // Icon
                Box(
                    modifier = Modifier
                        . size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    data.gradientColors[0].copy(alpha = 0.4f),
                                    Color. Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gemgame),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pack name
                Text(
                    text = data.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // LUX amount
                Text(
                    text = "+${formatStoreCurrency(data.luxAmount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = data.gradientColors[0]
                )

                // Bonus badge
                if (data.bonus != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF4CAF50))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = data. bonus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight. Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Buy button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(data.gradientColors[0])
                        .clickable { showConfirmDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = data.price,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

    if (showConfirmDialog) {
        LuxPurchaseConfirmDialog(
            packName = data.name,
            luxAmount = data.luxAmount,
            price = data.price,
            bonus = data.bonus,
            gradientColors = data. gradientColors,
            onConfirm = {
                onBuy()
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}

// ==================== GOLD EXCHANGE CARD ====================

@Composable
private fun GoldExchangeCard(
    data:  GoldExchangeData,
    currentLux: Int,
    onBuy: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    val canAfford = currentLux >= data.luxCost

    val infiniteTransition = rememberInfiniteTransition(label = "goldCardGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (canAfford) 0.6f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Card(
        modifier = Modifier
            .width(170.dp)
            .height(220.dp)
            .alpha(if (canAfford) 1f else 0.6f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush. verticalGradient(
                        colors = listOf(
                            data.gradientColors[0]. copy(alpha = 0.3f),
                            data. gradientColors[1].copy(alpha = 0.1f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush. linearGradient(
                        colors = listOf(
                            data.gradientColors[0].copy(alpha = glowAlpha),
                            data.gradientColors[1].copy(alpha = glowAlpha * 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        . size(52.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.4f),
                                    Color. Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gold amount
                Text(
                    text = "+${formatStoreCurrency(data.goldAmount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD700)
                )

                // Bonus badge (si tiene)
                if (data.bonus != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF4CAF50))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = data.bonus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color. White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Exchange arrow
                Text(
                    text = "⬇️",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Cost button
                Box(
                    modifier = Modifier
                        . fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        . background(
                            if (canAfford) data.gradientColors[0] else Color.Gray
                        )
                        .then(
                            if (canAfford) {
                                Modifier.clickable { showConfirmDialog = true }
                            } else {
                                Modifier
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${data.luxCost}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.gemgame),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Not enough indicator
                if (! canAfford) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Need ${data.luxCost - currentLux} more",
                        fontSize = 10.sp,
                        color = Color(0xFFFF5252)
                    )
                }
            }
        }
    }

    if (showConfirmDialog) {
        GoldExchangeConfirmDialog(
            luxCost = data.luxCost,
            goldAmount = data. goldAmount,
            currentLux = currentLux,
            onConfirm = {
                onBuy()
                showConfirmDialog = false
            },
            onDismiss = { showConfirmDialog = false }
        )
    }
}

// ==================== INFO CARD ====================

@Composable
private fun StoreInfoCard() {
    Card(
        modifier = Modifier. fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults. cardColors(
            containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "ℹ️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "How it works",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            StoreInfoRow(emoji = "💎", text = "LUX is the premium currency purchased with real money")
            Spacer(modifier = Modifier.height(6.dp))
            StoreInfoRow(emoji = "🪙", text = "GOLD is earned by playing or exchanging LUX")
            Spacer(modifier = Modifier.height(6.dp))
            StoreInfoRow(emoji = "🎁", text = "Use both currencies to unlock exclusive items")
            Spacer(modifier = Modifier.height(6.dp))
            StoreInfoRow(emoji = "💰", text = "Base rate: 1 LUX = 10 GOLD (bonuses apply! )")
        }
    }
}

@Composable
private fun StoreInfoRow(emoji: String, text:  String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = emoji, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color. White.copy(alpha = 0.7f),
            lineHeight = 16.sp
        )
    }
}

// ==================== LUX PURCHASE CONFIRM DIALOG ====================

@Composable
private fun LuxPurchaseConfirmDialog(
    packName: String,
    luxAmount: Int,
    price:  String,
    bonus: String?,
    gradientColors: List<Color>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "💎", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Confirm Purchase",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier. fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = gradientColors[0]. copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            . padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$packName Pack",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.gemgame),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+${formatStoreCurrency(luxAmount)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = gradientColors[0]
                            )
                        }
                        if (bonus != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF4CAF50))
                                    . padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Bonus: $bonus",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier. height(16.dp))

                Text(
                    text = "Total: $price",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier. height(8.dp))

                Box(modifier = Modifier.height(0.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "✓ Confirm Purchase",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    color = Color.White. copy(alpha = 0.8f)
                )
            }
        }
    )
}

// ==================== GOLD EXCHANGE CONFIRM DIALOG ====================

@Composable
private fun GoldExchangeConfirmDialog(
    luxCost: Int,
    goldAmount: Int,
    currentLux: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // Calcular el rate efectivo
    val baseGold = luxCost * 10
    val bonusGold = goldAmount - baseGold
    val bonusPercent = if (baseGold > 0) ((bonusGold. toFloat() / baseGold) * 100).toInt() else 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A2E),
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🔄", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Confirm Exchange",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color. White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F0F1A).copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // What you pay
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.gemgame),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "-$luxCost",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7B1FA2)
                            )
                            Text(
                                text = " LUX",
                                fontSize = 14.sp,
                                color = Color. White. copy(alpha = 0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "⬇️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // What you get
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable. coin),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier. width(8.dp))
                            Text(
                                text = "+${formatStoreCurrency(goldAmount)}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }

                        // Show bonus if applicable
                        if (bonusPercent > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    . clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF4CAF50))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Includes +$bonusPercent% bonus! ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Balance after:  ${currentLux - luxCost} LUX",
                    fontSize = 13.sp,
                    color = Color. White.copy(alpha = 0.6f)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    . fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "✓ Exchange Now",
                    fontSize = 16.sp,
                    fontWeight = FontWeight. Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    . height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    )
}
