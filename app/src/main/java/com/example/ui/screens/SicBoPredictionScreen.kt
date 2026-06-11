package com.example.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SicBoEntity
import com.example.ui.viewmodel.SicBoViewModel
import com.example.ui.viewmodel.SicBoStats
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Professional Polish Theme Suite (Charcoal / Onyx with violet/purple highlights)
val CasinoBg = Color(0xFF121212)         // Matte onyx black background
val CasinoCardBg = Color(0xFF1E1E1E)     // Premium dark graphite card
val CasinoPurple = Color(0xFF9333EA)     // Vibrant violet/purple brand accent
val CasinoPurpleLight = Color(0xFFA855F7)// Bright lavender highlights
val CasinoRed = Color(0xFFEF4444)        // BESAR / GENAP
val CasinoGreen = Color(0xFF22C55E)      // KECIL / GANJIL
val CasinoYellow = Color(0xFFEAB308)     // LEOPARD
val DarkGrey = Color(0xFF2E2E35)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SicBoPredictionScreen(
    viewModel: SicBoViewModel,
    modifier: Modifier = Modifier
) {
    val inputSeed by viewModel.inputSeed.collectAsStateWithLifecycle()
    val isRolling by viewModel.isRolling.collectAsStateWithLifecycle()
    val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
    val historyList by viewModel.history.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Virtual Dice animation cycling while rolling
    var animDie1 by remember { mutableStateOf(1) }
    var animDie2 by remember { mutableStateOf(3) }
    var animDie3 by remember { mutableStateOf(5) }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            try {
                val toneG = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
            } catch (e: Exception) {
                // Tone generation fallback
            }

            var tick = 0
            while (tick < 15) {
                animDie1 = (1..6).random()
                animDie2 = (1..6).random()
                animDie3 = (1..6).random()
                delay(100)
                tick++
            }
        }
    }

    val stats = viewModel.getStats(historyList)

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(CasinoBg),
        containerColor = CasinoBg,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CasinoPurple)
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎲", fontSize = 24.sp)
                        }
                        Column {
                            Text(
                                text = "SICBO PREDICTION",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "Crypto-RNG Engine v4.2",
                                fontSize = 10.sp,
                                color = CasinoPurpleLight,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    // Reset button styled elegant
                    IconButton(
                        onClick = { viewModel.clearAllHistory() },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset History",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // SECTION 1: SPINNER LOGS / SEED INPUT CONTAINER (Visually white as requested for premium distinction)
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INPUT SEED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "SECURE PORT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CasinoPurple,
                                letterSpacing = 1.sp
                            )
                        }

                        // Input control
                        OutlinedTextField(
                            value = inputSeed,
                            onValueChange = { viewModel.onSeedChange(it) },
                            placeholder = {
                                Text(
                                    "ENTER 3 DIGITS (e.g. 842)",
                                    color = Color.Gray,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = CasinoPurple,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                cursorColor = Color.Black
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seed_input_field"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Selector chips to assist speed simulation
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("168", "888", "842", "369").forEach { quickSeed ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF3F4F6))
                                        .clickable { viewModel.onSeedChange(quickSeed) }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = quickSeed,
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Large luxury trigger button
                        Button(
                            onClick = { viewModel.triggerPrediction() },
                            enabled = inputSeed.length == 3 && !isRolling,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CasinoPurple,
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFFE5E7EB),
                                disabledContentColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("predict_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (isRolling) "ANALYZING ENCRYPTION..." else "SPIN & GENERATE PREDICTION",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // SECTION 2: STATUS INDICATOR (Syncing/glowing indicator)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val statusText = if (isRolling) "ANALYZING CRYPTO MATRIX..." else "Analysis Complete - Syncing Results"
                    val dotColor = if (isRolling) CasinoYellow else CasinoGreen

                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 0.6f,
                        targetValue = 1.4f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .drawBehind {
                                drawCircle(
                                    color = dotColor.copy(alpha = 0.3f),
                                    radius = size.minDimension / 2 * pulseScale
                                )
                                drawCircle(
                                    color = dotColor,
                                    radius = size.minDimension / 4
                                )
                            }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusText,
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // SECTION 3: DICE DISPLAY (Professional white blocks in white/5 card with border)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val activeD1 = if (isRolling) animDie1 else (currentResult?.die1 ?: 5)
                            val activeD2 = if (isRolling) animDie2 else (currentResult?.die2 ?: 2)
                            val activeD3 = if (isRolling) animDie3 else (currentResult?.die3 ?: 4)

                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AnimatedDiceComponent(value = activeD1, isRolling = isRolling)
                                Text("DADU 1", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AnimatedDiceComponent(value = activeD2, isRolling = isRolling)
                                Text("DADU 2", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AnimatedDiceComponent(value = activeD3, isRolling = isRolling)
                                Text("DADU 3", color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        val displayTotal = if (isRolling) "?" else (currentResult?.total?.toString() ?: "11")
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "TOTAL NILAI: $displayTotal",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // SECTION 4: PREDICTION OUTPUT (Luxury black card with border-l-4 style)
            item {
                AnimatedVisibility(
                    visible = currentResult != null && !isRolling,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    currentResult?.let { res ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔮 PREDICTION RESULT",
                                color = CasinoPurpleLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )

                            if (res.isLeopard) {
                                PredictionCardItem(
                                    title = "Triple Multiplier",
                                    outcome = "🟡 [PREDIKSI: LEOPARD]",
                                    color = CasinoYellow
                                )
                            } else {
                                if (res.isKecil) {
                                    PredictionCardItem(
                                        title = "Size Category",
                                        outcome = "🟢 [PREDIKSI: KECIL]",
                                        color = CasinoGreen
                                    )
                                } else if (res.isBesar) {
                                    PredictionCardItem(
                                        title = "Size Category",
                                        outcome = "🔴 [PREDIKSI: BESAR]",
                                        color = CasinoRed
                                    )
                                }

                                if (res.isGanjil) {
                                    PredictionCardItem(
                                        title = "Parity Result",
                                        outcome = "🟢 [PREDIKSI: GANJIL]",
                                        color = CasinoGreen
                                    )
                                } else if (res.isGenap) {
                                    PredictionCardItem(
                                        title = "Parity Result",
                                        outcome = "🔴 [PREDIKSI: GENAP]",
                                        color = CasinoRed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 5: STATS AUDIBILITY DISPLAY
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📈 SIMULATOR AUDIT & STATS",
                        color = Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CasinoCardBg),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatDisplayBox(title = "Total Spins", value = stats.totalSpins.toString(), modifier = Modifier.weight(1f))
                                StatDisplayBox(
                                    title = "Leopard (Triple)",
                                    value = String.format(Locale.US, "%.1f%%", stats.leopardPercentage),
                                    color = CasinoYellow,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatDisplayBox(
                                    title = "Kecil (4-10)",
                                    value = String.format(Locale.US, "%.1f%%", stats.kecilPercentage),
                                    color = CasinoGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                StatDisplayBox(
                                    title = "Besar (11-17)",
                                    value = String.format(Locale.US, "%.1f%%", stats.besarPercentage),
                                    color = CasinoRed,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatDisplayBox(
                                    title = "Ganjil (Odd)",
                                    value = String.format(Locale.US, "%.1f%%", stats.ganjilPercentage),
                                    color = CasinoGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                StatDisplayBox(
                                    title = "Genap (Even)",
                                    value = String.format(Locale.US, "%.1f%%", stats.genapPercentage),
                                    color = CasinoRed,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 6: VIP HISTORY LOGS
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📋 PREDICTION HISTORY LOGS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    if (historyList.isNotEmpty()) {
                        Text(
                            text = "${historyList.size} Records",
                            color = CasinoPurpleLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (historyList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔔", fontSize = 24.sp)
                            Text(
                                "No history logs found. Spin above to generate predictions.",
                                color = Color.Gray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(historyList) { item ->
                    PredictionHistoryItem(item)
                }
            }
        }
    }
}

@Composable
fun AnimatedDiceComponent(value: Int, isRolling: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isRolling) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(2.dp, CasinoPurple.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        DiceFaceDrawer(value = value)
    }
}

@Composable
fun DiceFaceDrawer(value: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val radius = 4.5.dp.toPx()
                val center = Offset(size.width / 2, size.height / 2)
                val spacingFactor = 0.25f
                val leftX = size.width * spacingFactor
                val rightX = size.width * (1f - spacingFactor)
                val topY = size.height * spacingFactor
                val bottomY = size.height * (1f - spacingFactor)

                val dotColor = if (value == 1 || value == 4) CasinoRed else Color.Black

                when (value) {
                    1 -> {
                        drawCircle(color = CasinoRed, radius = radius * 1.5f, center = center)
                    }
                    2 -> {
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, topY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, bottomY))
                    }
                    3 -> {
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, topY))
                        drawCircle(color = dotColor, radius = radius, center = center)
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, bottomY))
                    }
                    4 -> {
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, topY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, topY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, bottomY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, bottomY))
                    }
                    5 -> {
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, topY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, topY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, bottomY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, bottomY))
                        drawCircle(color = dotColor, radius = radius, center = center)
                    }
                    6 -> {
                        val midY = size.height / 2
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, topY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, midY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(leftX, bottomY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, topY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, midY))
                        drawCircle(color = dotColor, radius = radius, center = Offset(rightX, bottomY))
                    }
                }
            }
    )
}

@Composable
fun PredictionCardItem(
    title: String,
    outcome: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Thick left-border 4.dp
                    drawRect(
                        color = color,
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(4.dp.toPx(), size.height)
                    )
                }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(start = 6.dp)) {
                Text(
                    text = title,
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = outcome,
                    color = color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val iconStr = if (color == CasinoGreen) "✓" else if (color == CasinoRed) "⚠" else "★"
                Text(
                    text = iconStr,
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatDisplayBox(
    title: String,
    value: String,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = color, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PredictionHistoryItem(item: SicBoEntity) {
    val formatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val formattedTime = remember(item.timestamp) { formatter.format(Date(item.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CasinoCardBg),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("📥 Seed:", color = Color.Gray, fontSize = 11.sp)
                    Text(item.seed, color = CasinoPurpleLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "⏱️ $formattedTime",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiniDice(item.die1)
                    MiniDice(item.die2)
                    MiniDice(item.die3)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Total: ${item.total}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (item.isLeopard) {
                        MiniOutcomeTag(text = "LEOPARD", color = CasinoYellow)
                    } else {
                        if (item.isKecil) MiniOutcomeTag(text = "KECIL", color = CasinoGreen)
                        if (item.isBesar) MiniOutcomeTag(text = "BESAR", color = CasinoRed)
                        if (item.isGanjil) MiniOutcomeTag(text = "GANJIL", color = CasinoGreen)
                        if (item.isGenap) MiniOutcomeTag(text = "GENAP", color = CasinoRed)
                    }
                }
            }
        }
    }
}

@Composable
fun MiniDice(value: Int) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(Color.White, RoundedCornerShape(4.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        val diceChar = when(value) {
            1 -> "⚀"
            2 -> "⚁"
            3 -> "⚂"
            4 -> "⚃"
            5 -> "⚄"
            else -> "⚅"
        }
        val diceColor = if (value == 1 || value == 4) CasinoRed else Color.Black
        Text(
            text = diceChar,
            color = diceColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = (-1).dp)
        )
    }
}

@Composable
fun MiniOutcomeTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.5.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
