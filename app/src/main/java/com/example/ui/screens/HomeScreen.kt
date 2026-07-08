package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DistrictWaterStatus
import com.example.data.WaterZone
import com.example.data.WaterLog
import com.example.ui.theme.*
import com.example.ui.viewmodel.AquaUiState
import com.example.ui.viewmodel.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: AquaUiState.Success,
    waterLogs: List<WaterLog>,
    weatherState: WeatherUiState,
    onRefreshWeather: () -> Unit,
    onLogWater: (Int, String) -> Unit,
    onDeleteLog: (Int) -> Unit,
    onDistrictSelected: (DistrictWaterStatus) -> Unit,
    onRotateTip: () -> Unit,
    simulateError: Boolean,
    onToggleErrorSimulation: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val selectedDistrict = state.selectedDistrict
    val currentTip = state.currentTip

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Hero Image Header (styled as a premium editorial postcard)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_water_hero),
                    contentDescription = "AquaHaryana Water Conservation Hero Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Soft gradient overlay to blend perfectly with slate/blue aesthetic
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "जल ही जीवन है",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Save Haryana's Water Table",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Section: Haryana Groundwater Status & District Selector
        Text(
            text = "Groundwater Status by District",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Text(
            text = "Select your district to view the local water vulnerability status.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal Scrollable District Selector
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("district_selector_row")
        ) {
            items(state.districts) { district ->
                val isSelected = district.districtName == selectedDistrict.districtName
                FilterChip(
                    selected = isSelected,
                    onClick = { onDistrictSelected(district) },
                    label = { 
                        Text(
                            text = district.districtName,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ) 
                    },
                    leadingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Water",
                                modifier = Modifier.size(14.dp),
                                tint = if (district.groundWaterZone == WaterZone.SAFE) SafeGreen else OceanBlue
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        selectedLabelColor = MaterialTheme.colorScheme.primary,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        iconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.testTag("chip_${district.districtName.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Regional Alert / Status Card (Editorial Style)
        val zoneColorGradient = when (selectedDistrict.groundWaterZone) {
            WaterZone.OVER_EXPLOITED -> listOf(Color(0xFFE11D48), Color(0xFF9F1239))
            WaterZone.CRITICAL -> listOf(Color(0xFFF59E0B), Color(0xFFB45309))
            WaterZone.SEMI_CRITICAL -> listOf(Color(0xFF0284C7), Color(0xFF0369A1))
            WaterZone.SAFE -> listOf(Color(0xFF10B981), Color(0xFF047857))
        }

        val pulseDotColor = when (selectedDistrict.groundWaterZone) {
            WaterZone.OVER_EXPLOITED -> Color(0xFFFCA5A5)
            WaterZone.CRITICAL -> Color(0xFFFDE68A)
            WaterZone.SEMI_CRITICAL -> Color(0xFFBAE6FD)
            WaterZone.SAFE -> Color(0xFFA7F3D0)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("district_status_card"),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(zoneColorGradient))
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "REGIONAL STATUS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color.White
                                )
                            )
                        }

                        // Pulsing / glowing dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(pulseDotColor)
                        )
                    }

                    Text(
                        text = "Water Table Status",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = selectedDistrict.groundWaterZone.displayName,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Current Zone: ${selectedDistrict.districtName} District",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid-like cards below status to display stats beautifully
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Depth Card
            Card(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AVERAGE DEPTH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${selectedDistrict.averageWaterTableDepthMeters}m",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Below ground level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Blocks Card
            Card(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VULNERABLE BLOCKS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${selectedDistrict.criticalBlocksCount} / ${selectedDistrict.totalBlocksCount}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Highly vulnerable blocks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Vulnerability Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "VULNERABILITY OVERVIEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedDistrict.vulnerabilityDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Water Logger Widget
        DailyWaterLoggerWidget(
            waterLogs = waterLogs,
            onLogWater = onLogWater,
            onDeleteLog = onDeleteLog
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Live Haryana Weather context Widget
        HaryanaWeatherWidget(
            weatherState = weatherState,
            onRefreshWeather = onRefreshWeather
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Section: Daily Water-saving Tip (Editorial Style - Daily Awareness)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("daily_tip_card")
        ) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Water Conservation Tip Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "DAILY AWARENESS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Animating tip changes with slide+fade effect
            AnimatedContent(
                targetState = currentTip,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "TipChangeAnimation"
            ) { tip ->
                Column {
                    Text(
                        text = "“${tip.content}”",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Light,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 28.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    if (!tip.source.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "— Source: ${tip.source}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRotateTip,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("next_tip_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Next Tip",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Next Tip",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Developer Simulation Sandbox (Loading & Error Handler)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Simulation Sandbox",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "DEVELOPER SIMULATION SANDBOX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Toggle simulation error to experience the app's error handling screen, or trigger a dashboard status refresh.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Switch(
                            checked = simulateError,
                            onCheckedChange = { onToggleErrorSimulation() },
                            modifier = Modifier
                                .scale(0.8f)
                                .testTag("error_simulation_switch")
                        )
                        Text(
                            text = "Simulate Error",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                    }

                    Button(
                        onClick = onRefresh,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .testTag("sync_status_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sync Status",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWaterLoggerWidget(
    waterLogs: List<WaterLog>,
    onLogWater: (Int, String) -> Unit,
    onDeleteLog: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val todayDateStr = remember {
        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }
    val todayLogs = remember(waterLogs) {
        waterLogs.filter { it.date == todayDateStr }
    }
    val todayTotalLiters = remember(todayLogs) {
        todayLogs.sumOf { it.amountLiters }
    }

    // Daily Water Budget limit
    val dailyBudget = 135

    val budgetProgress = (todayTotalLiters.toFloat() / dailyBudget.toFloat()).coerceIn(0f, 1f)

    val statusColor = when {
        todayTotalLiters <= 80 -> SafeGreen
        todayTotalLiters <= dailyBudget -> WarnOrange
        else -> OverExploitedRed
    }

    val statusLabel = when {
        todayTotalLiters <= 80 -> "Water Champion 🌟"
        todayTotalLiters <= dailyBudget -> "Moderate User 💧"
        else -> "Excessive Usage ⚠️"
    }

    val statusDescription = when {
        todayTotalLiters <= 80 -> "Fantastic! You are preserving Haryana's water table beautifully."
        todayTotalLiters <= dailyBudget -> "Good job! Try to stay below 80L to help recharge aquifers."
        else -> "Budget exceeded! Please reduce shower durations and recycle household water."
    }

    var customAmountStr by remember { mutableStateOf("") }
    var selectedActivity by remember { mutableStateOf("Drinking") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val activities = listOf("Drinking", "Bathing", "Household", "Irrigation", "Cooking", "Other")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .testTag("water_logger_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Water Logger Icon",
                    tint = OceanBlue,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Daily Consumption Tracker",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Measure and manage your daily water footprint",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Visual Progress Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular/Arc Progress Indicator or Linear
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(80.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { budgetProgress },
                        modifier = Modifier.size(80.dp),
                        color = statusColor,
                        strokeWidth = 8.dp,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$todayTotalLiters",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Liters",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(statusColor, CircleShape)
                        )
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Standard Goal: $dailyBudget Liters/day",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Tap Addition Buttons
            Text(
                text = "QUICK ADD WATER",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Glass (0.25 L)
                Button(
                    onClick = { onLogWater(1, "Drinking") }, // 1 Liter for simplicity or we can treat numbers as liters
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_glass")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.LocalDrink, "Glass", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Glass (+1L)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                // Bucket (15 L)
                Button(
                    onClick = { onLogWater(15, "Household") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_bucket")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Home, "Household", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Bucket (+15L)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                // Shower (50 L)
                Button(
                    onClick = { onLogWater(50, "Bathing") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_shower")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Shower, "Bathing", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Shower (+50L)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                // Irrigation (100 L)
                Button(
                    onClick = { onLogWater(100, "Irrigation") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_add_irrigation")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Agriculture, "Irrigation", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Irrigate (+100L)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom Add Form
            Text(
                text = "LOG CUSTOM CONSUMPTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Liters Input
                OutlinedTextField(
                    value = customAmountStr,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            customAmountStr = input
                        }
                    },
                    label = { Text("Liters") },
                    placeholder = { Text("e.g. 20") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("custom_liters_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OceanBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )

                // Safe Click Dropdown for Activity
                Box(modifier = Modifier.weight(1.2f)) {
                    OutlinedTextField(
                        value = selectedActivity,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Activity") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Select Activity") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    // Click overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { dropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    ) {
                        activities.forEach { activity ->
                            DropdownMenuItem(
                                text = { Text(activity) },
                                onClick = {
                                    selectedActivity = activity
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Add Button
                IconButton(
                    onClick = {
                        val amount = customAmountStr.toIntOrNull()
                        if (amount != null && amount > 0) {
                            onLogWater(amount, selectedActivity)
                            customAmountStr = ""
                        }
                    },
                    enabled = customAmountStr.isNotEmpty(),
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (customAmountStr.isNotEmpty()) OceanBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .testTag("custom_log_add_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom water log",
                        tint = if (customAmountStr.isNotEmpty()) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logs Header
            Text(
                text = "TODAY'S LOGGED ACTIVITIES (${todayLogs.size})",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (todayLogs.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No activities logged today. Tap a quick add action above or submit a custom log to start measuring!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    todayLogs.forEach { log ->
                        val logIcon = when (log.activity) {
                            "Drinking" -> Icons.Default.LocalDrink
                            "Bathing" -> Icons.Default.Shower
                            "Household" -> Icons.Default.Home
                            "Irrigation" -> Icons.Default.Agriculture
                            "Cooking" -> Icons.Default.Restaurant
                            else -> Icons.Default.WaterDrop
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(OceanBlue.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = logIcon,
                                        contentDescription = log.activity,
                                        tint = OceanBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = log.activity,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val timeStr = remember(log.timestamp) {
                                        java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))
                                    }
                                    Text(
                                        text = timeStr,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${log.amountLiters} Liters",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                IconButton(
                                    onClick = { onDeleteLog(log.id) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("delete_log_${log.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete log entry",
                                        tint = OverExploitedRed.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HaryanaWeatherWidget(
    weatherState: WeatherUiState,
    onRefreshWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .testTag("haryana_weather_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Weather Context Icon",
                        tint = WarnOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Haryana Climate Context",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Real-time indicators affecting groundwater",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(
                    onClick = onRefreshWeather,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f), CircleShape)
                        .testTag("refresh_weather_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh weather details",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (weatherState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = OceanBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                is WeatherUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Error Fetching Weather",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Could not load weather context.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Check internet connection or tap refresh to reload.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is WeatherUiState.Success -> {
                    val temp = weatherState.temperature
                    val humidity = weatherState.humidity

                    val weatherAdvice = when {
                        temp > 35 -> "☀️ High temperatures detected in Haryana today! Rapid moisture evaporation is active. We recommend scheduling plant watering before 8:00 AM or after 6:00 PM to save up to 30% of irrigation water."
                        humidity < 40 -> "🍃 Dry humidity levels detected! Air is thirsty, accelerating open water evaporation. Please cover storage tanks tightly and avoid misting systems."
                        else -> "💧 Mild climate conditions. Standard water conservation guidelines apply. Keep checking our recommendations on the Education screen to help lower Haryana's extraction pressure."
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Temperature Metric Item
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(WarnOrange.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Thermostat,
                                            contentDescription = "Temperature Indicator",
                                            tint = WarnOrange,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Temperature",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        Text(
                                            text = "$temp°C",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.testTag("weather_temp_value")
                                        )
                                    }
                                }
                            }

                            // Humidity Metric Item
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(OceanBlue.copy(alpha = 0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Air,
                                            contentDescription = "Humidity Indicator",
                                            tint = OceanBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Rel. Humidity",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                        Text(
                                            text = "$humidity%",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.testTag("weather_humidity_value")
                                        )
                                    }
                                }
                            }
                        }

                        // Contextual Conservation Note
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = weatherAdvice,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


