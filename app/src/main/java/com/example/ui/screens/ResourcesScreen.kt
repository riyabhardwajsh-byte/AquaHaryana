package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FootprintCalculatorState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourcesScreen(
    calculatorState: FootprintCalculatorState,
    onShowerMinsChange: (Float) -> Unit,
    onUseBucketChange: (Boolean) -> Unit,
    onBrushingTapChange: (Boolean) -> Unit,
    onLeaksChange: (Boolean) -> Unit,
    onCarWashingChange: (Float) -> Unit,
    onCalculate: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val officialResources = listOf(
        OfficialPortal(
            title = "Haryana Water Resources Authority (HWRA)",
            description = "Official body regulating groundwater conservation, water tariffs, extraction permissions, and recycling directives across Haryana.",
            url = "https://hwra.org.in"
        ),
        OfficialPortal(
            title = "Mera Pani - Meri Virasat Portal",
            description = "State government portal for farmers to register and claim financial incentives of Rs. 7,000/acre for agricultural crop diversification.",
            url = "https://ekharid.haryana.gov.in"
        ),
        OfficialPortal(
            title = "Central Ground Water Board (CGWB) India",
            description = "National government authority monitoring ground water levels, conducting surveys, and releasing water level statistics in India.",
            url = "https://cgwb.gov.in"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. HEADER (Styled to match the elegant editorial layout)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 12.dp)
            ) {
                Text(
                    text = "Official Resources & Tools",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "Calculate your personal water consumption or visit official state portals for registration, incentives, and reports.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. SECTION: WATER FOOTPRINT CALCULATOR
        Text(
            text = "Household Water Calculator",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Text(
            text = "Find out how many liters of water you consume daily and learn how to reduce your daily footprint.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("water_calculator_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input 1: Shower Time vs Bucket
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bathing Method",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Checkbox(
                                checked = calculatorState.useBucketInsteadOfShower,
                                onCheckedChange = onUseBucketChange,
                                modifier = Modifier.testTag("calculator_bucket_checkbox")
                            )
                            Text(
                                text = "Use Bucket (Saves water!)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }

                    if (!calculatorState.useBucketInsteadOfShower) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Daily Shower Duration: ${calculatorState.showerMinutes.roundToInt()} minutes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Slider(
                            value = calculatorState.showerMinutes,
                            onValueChange = onShowerMinsChange,
                            valueRange = 1f..20f,
                            steps = 19,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calculator_shower_slider")
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Standard bucket bath set to 25 liters of water.",
                            style = MaterialTheme.typography.bodySmall,
                            color = SafeGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Input 2: Brushing Tap Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Brushing Tap Status",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Leaving water running while brushing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = calculatorState.brushingTapRunning,
                        onCheckedChange = onBrushingTapChange,
                        modifier = Modifier.testTag("calculator_brushing_switch")
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Input 3: Leakages
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Leaking Taps / Pipes",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Any slow dripping faucets in the house",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = calculatorState.leaksPresent,
                        onCheckedChange = onLeaksChange,
                        modifier = Modifier.testTag("calculator_leaks_switch")
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Input 4: Car Washing Frequency
                Column {
                    Text(
                        text = "Car Washing with Hose Pipe: ${calculatorState.carWashingTimesPerWeek.roundToInt()} times/week",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Using open hose pipes for cars consumes 150L per wash.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Slider(
                        value = calculatorState.carWashingTimesPerWeek,
                        onValueChange = onCarWashingChange,
                        valueRange = 0f..7f,
                        steps = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calculator_carwash_slider")
                    )
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onReset,
                        modifier = Modifier.testTag("calculator_reset_button")
                    ) {
                        Text("Reset")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onCalculate,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("calculator_calculate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Calculate",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calculate")
                    }
                }

                // Results view
                AnimatedVisibility(
                    visible = calculatorState.hasCalculated,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    val result = calculatorState.calculatedDailyLiters ?: 0.0
                    val indianStandard = 135.0 // National benchmark
                    val percentOfStandard = ((result / indianStandard) * 100).roundToInt()

                    val resultBgColor = if (result <= indianStandard) SafeGreen.copy(alpha = 0.12f) else OverExploitedRed.copy(alpha = 0.12f)
                    val resultColor = if (result <= indianStandard) SafeGreen else OverExploitedRed

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(resultBgColor)
                            .padding(16.dp)
                            .testTag("calculator_results_panel")
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Your Estimated Daily Footprint:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${result.roundToInt()} Liters",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = resultColor
                                    )
                                )
                                Text(
                                    text = "/ day",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Text(
                                text = "This is $percentOfStandard% of the Indian National Standard target (${indianStandard.toInt()} Liters per person daily).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            // Custom advisory message
                            val (advTitle, advMsg, advIcon) = if (result <= indianStandard) {
                                Triple(
                                    "Excellent job!",
                                    "Your daily footprint is well within the sustainable national benchmark. Continuing these practices safeguards Haryana's precious water resources. Share these tips with friends!",
                                    Icons.Default.Celebration
                                )
                            } else {
                                Triple(
                                    "Room for conservation",
                                    "Your daily footprint is higher than recommended. Switch to taking a bucket bath (saves ~50L), resolve pipe leakages (saves ~30L), or turn off the faucet while brushing (saves ~23L) to drastically conserve water.",
                                    Icons.Default.WaterDamage
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(color = resultColor.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = advIcon,
                                    contentDescription = advTitle,
                                    tint = resultColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = advTitle,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = resultColor
                                    )
                                    Text(
                                        text = advMsg,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. SECTION: OFFICIAL PORTALS & LINKS
        Text(
            text = "Official Water Portals",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Text(
            text = "Click to visit official websites (requires an internet connection).",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .testTag("official_portals_column"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            officialResources.forEach { portal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("portal_card_${portal.title.split(" ").first().lowercase()}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = portal.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = portal.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(portal.url))
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .align(Alignment.End)
                                .testTag("btn_visit_${portal.title.split(" ").first().lowercase()}")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Launch,
                                contentDescription = "Visit Site",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Visit Portal",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

data class OfficialPortal(
    val title: String,
    val description: String,
    val url: String
)
