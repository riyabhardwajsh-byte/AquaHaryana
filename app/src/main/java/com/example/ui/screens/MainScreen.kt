package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.ui.platform.LocalContext
import com.example.R
import com.example.ui.theme.OceanBlue
import com.example.ui.theme.OverExploitedRed
import com.example.ui.theme.WaterCyan
import com.example.ui.viewmodel.AquaUiState
import com.example.ui.viewmodel.AquaViewModel
import com.example.ui.viewmodel.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AquaViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val calculatorState by viewModel.calculatorState.collectAsState()
    val simulateErrorOnRefresh by viewModel.simulateErrorOnRefresh.collectAsState()
    val waterLogs by viewModel.waterLogs.collectAsState()
    val favoriteTechniqueIds by viewModel.favoriteTechniqueIds.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()

    val isOffline by rememberIsOffline()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 8.dp)
                    .testTag("main_top_app_bar")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "HARYANA, INDIA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Aqua",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "Haryana",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }

                    // Avatar/Logo circle and refresh button grouped
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isOffline) {
                            var showOfflineDialog by remember { mutableStateOf(false) }

                            IconButton(
                                onClick = { showOfflineDialog = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f), CircleShape)
                                    .testTag("offline_status_icon")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudOff,
                                    contentDescription = "Offline Mode Active",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            if (showOfflineDialog) {
                                AlertDialog(
                                    onDismissRequest = { showOfflineDialog = false },
                                    confirmButton = {
                                        TextButton(
                                            onClick = { showOfflineDialog = false },
                                            modifier = Modifier.testTag("offline_dialog_ok_button")
                                        ) {
                                            Text("Got It")
                                        }
                                    },
                                    title = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CloudOff,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Text("Offline Mode Active")
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = "Your device is currently disconnected from the internet. You can still log your daily water usage and browse saved conservation techniques offline! However, real-time chatbot responses (AquaBot) and live map updates may be unavailable until you reconnect.",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.testTag("offline_explanation_dialog")
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.loadDashboardData() },
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("appbar_refresh_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh data",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.aquasave_icon),
                            contentDescription = "AquaHaryana Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .width(80.dp)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("app_logo_image")
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Navigation item definitions
                val navItems = listOf(
                    NavigationItem("home", "Dashboard", Icons.Default.Dashboard),
                    NavigationItem("education", "Education", Icons.Default.School),
                    NavigationItem("resources", "Resources", Icons.Default.Language),
                    NavigationItem("chat", "AquaBot", Icons.Default.SmartToy)
                )

                navItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.testTag("nav_item_${item.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        // Handle loading, success, and error globally
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is AquaUiState.Loading -> {
                    LoadingScreen()
                }

                is AquaUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.retryLoading() },
                        onResetErrorSim = { viewModel.toggleErrorSimulation() }
                    )
                }

                is AquaUiState.Success -> {
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("home") {
                            HomeScreen(
                                state = state,
                                waterLogs = waterLogs,
                                weatherState = weatherState,
                                onRefreshWeather = { viewModel.fetchWeather() },
                                onLogWater = { liters, activity -> viewModel.logWaterUsage(liters, activity) },
                                onDeleteLog = { id -> viewModel.deleteLog(id) },
                                onDistrictSelected = { viewModel.selectDistrict(it) },
                                onRotateTip = { viewModel.rotateTip() },
                                simulateError = simulateErrorOnRefresh,
                                onToggleErrorSimulation = { viewModel.toggleErrorSimulation() },
                                onRefresh = { viewModel.loadDashboardData() }
                            )
                        }

                        composable("education") {
                            EducationScreen(
                                techniques = state.techniques,
                                favoriteTechniqueIds = favoriteTechniqueIds,
                                onToggleFavorite = { viewModel.toggleFavoriteTechnique(it) }
                            )
                        }

                        composable("resources") {
                            ResourcesScreen(
                                calculatorState = calculatorState,
                                onShowerMinsChange = { viewModel.updateShowerMinutes(it) },
                                onUseBucketChange = { viewModel.updateUseBucket(it) },
                                onBrushingTapChange = { viewModel.updateBrushingTap(it) },
                                onLeaksChange = { viewModel.updateLeaks(it) },
                                onCarWashingChange = { viewModel.updateCarWashing(it) },
                                onCalculate = { viewModel.calculateFootprint() },
                                onReset = { viewModel.resetCalculator() }
                            )
                        }

                        composable("chat") {
                            val chatMessages by viewModel.chatMessages.collectAsState()
                            val isChatLoading by viewModel.isChatLoading.collectAsState()
                            ChatScreen(
                                messages = chatMessages,
                                isLoading = isChatLoading,
                                onSendMessage = { viewModel.sendChatMessage(it) },
                                onClearChat = { viewModel.clearChat() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Spinning stylized water drop
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(OceanBlue.copy(alpha = 0.12f))
            ) {
                CircularProgressIndicator(
                    color = OceanBlue,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(56.dp)
                )
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = "Loading",
                    tint = OceanBlue,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Syncing with Haryana Water Portal...",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = OceanBlue
                )
            )
            Text(
                text = "Fetching district levels and ground statistics",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onResetErrorSim: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("error_screen"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Warning icon bubble
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(OverExploitedRed.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Offline Error",
                        tint = OverExploitedRed,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "Connection Offline",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = OverExploitedRed,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Recovery Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("error_retry_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Retry Sync",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // Helpful bypass button since they are simulating it
                    OutlinedButton(
                        onClick = {
                            onResetErrorSim()
                            onRetry()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("error_disable_sim_button")
                    ) {
                        Text(
                            text = "Disable Simulated Error & Load",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun rememberIsOffline(): State<Boolean> {
    val context = LocalContext.current
    return produceState(initialValue = false) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val checkIsOffline = {
            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            capabilities == null || !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        
        value = checkIsOffline()
        
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                value = false
            }
            override fun onLost(network: Network) {
                value = checkIsOffline()
            }
        }
        
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (e: Exception) {
            value = checkIsOffline()
        }
        
        awaitDispose {
            try {
                connectivityManager.unregisterNetworkCallback(callback)
            } catch (e: Exception) {}
        }
    }
}

