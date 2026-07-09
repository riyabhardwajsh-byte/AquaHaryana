package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface AquaUiState {
    object Loading : AquaUiState
    data class Success(
        val districts: List<DistrictWaterStatus>,
        val selectedDistrict: DistrictWaterStatus,
        val techniques: List<WaterSavingTechnique>,
        val waterTips: List<WaterTip>,
        val currentTip: WaterTip
    ) : AquaUiState
    data class Error(val message: String) : AquaUiState
}

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val temperature: Double, val humidity: Int) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

data class FootprintCalculatorState(
    val showerMinutes: Float = 5f,
    val useBucketInsteadOfShower: Boolean = false,
    val brushingTapRunning: Boolean = false,
    val leaksPresent: Boolean = false,
    val carWashingTimesPerWeek: Float = 1f,
    val calculatedDailyLiters: Double? = null,
    val hasCalculated: Boolean = false
)

class AquaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WaterRepository()
    private val database = AquaDatabase.getDatabase(application)
    private val waterLogRepository = WaterLogRepository(
        database.waterLogDao(),
        database.favoriteTechniqueDao(),
        database.waterSavingTechniqueDao()
    )

    // Water Logs State
    val waterLogs: StateFlow<List<WaterLog>> = waterLogRepository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Favorite Techniques State
    val favoriteTechniqueIds: StateFlow<List<String>> = waterLogRepository.favoriteTechniqueIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Weather State
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    fun fetchWeather() {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                val response = WeatherRetrofitClient.service.getCurrentWeather()
                _weatherState.value = WeatherUiState.Success(
                    temperature = response.current.temperature,
                    humidity = response.current.humidity
                )
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error(e.message ?: "Could not fetch weather data")
            }
        }
    }

    fun toggleFavoriteTechnique(techniqueId: String) {
        viewModelScope.launch {
            val currentFavorites = favoriteTechniqueIds.value
            if (currentFavorites.contains(techniqueId)) {
                waterLogRepository.deleteFavorite(techniqueId)
            } else {
                waterLogRepository.insertFavorite(techniqueId)
            }
        }
    }

    // Screen State
    private val _uiState = MutableStateFlow<AquaUiState>(AquaUiState.Loading)
    val uiState: StateFlow<AquaUiState> = _uiState.asStateFlow()

    // Interactive Calculator State
    private val _calculatorState = MutableStateFlow(FootprintCalculatorState())
    val calculatorState: StateFlow<FootprintCalculatorState> = _calculatorState.asStateFlow()

    // Simulated error flag for error boundary presentation
    private val _simulateErrorOnRefresh = MutableStateFlow(false)
    val simulateErrorOnRefresh: StateFlow<Boolean> = _simulateErrorOnRefresh.asStateFlow()

    // Navigation screen state: home, education, resources
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    init {
        prepopulateTechniquesIfEmpty()
        loadDashboardData()
        prepopulateLogsIfEmpty()
    }

    private fun prepopulateTechniquesIfEmpty() {
        viewModelScope.launch {
            try {
                val cached = waterLogRepository.cachedTechniques.first()
                if (cached.isEmpty()) {
                    val defaultTechniques = repository.getWaterSavingTechniques()
                    waterLogRepository.insertTechniques(defaultTechniques)
                }
            } catch (e: Exception) {
                // Ignore initial database errors
            }
        }
    }

    private fun prepopulateLogsIfEmpty() {
        viewModelScope.launch {
            try {
                val currentLogs = waterLogRepository.allLogs.first()
                if (currentLogs.isEmpty()) {
                    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val calendar = java.util.Calendar.getInstance()
                    val today = sdf.format(calendar.time)
                    
                    calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
                    val yesterday = sdf.format(calendar.time)
                    
                    waterLogRepository.insertLog(WaterLog(date = yesterday, amountLiters = 4, activity = "Drinking"))
                    waterLogRepository.insertLog(WaterLog(date = yesterday, amountLiters = 50, activity = "Bathing"))
                    waterLogRepository.insertLog(WaterLog(date = today, amountLiters = 3, activity = "Drinking"))
                    waterLogRepository.insertLog(WaterLog(date = today, amountLiters = 15, activity = "Household"))
                }
            } catch (e: Exception) {
                // Ignore initial errors
            }
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun loadDashboardData() {
        fetchWeather()
        viewModelScope.launch {
            _uiState.value = AquaUiState.Loading
            try {
                if (_simulateErrorOnRefresh.value) {
                    throw Exception("Network Timeout: Unable to reach Haryana Water Portal API. Please check your internet connection and try again.")
                }

                val districts = repository.getDistrictsWaterStatus()
                val techniques = repository.getWaterSavingTechniques()
                val tips = repository.getWaterTips()
                
                // Cache the fetched techniques locally in Room
                try {
                    waterLogRepository.insertTechniques(techniques)
                } catch (dbEx: Exception) {
                    // Ignore DB write errors if any
                }
                
                val defaultDistrict = districts.find { it.districtName == "Kurukshetra" } ?: districts.first()
                val initialTip = tips.random()

                _uiState.value = AquaUiState.Success(
                    districts = districts,
                    selectedDistrict = defaultDistrict,
                    techniques = techniques,
                    waterTips = tips,
                    currentTip = initialTip
                )
            } catch (e: Exception) {
                try {
                    val cachedTechniques = waterLogRepository.cachedTechniques.first()
                    if (cachedTechniques.isNotEmpty()) {
                        // Fallback gracefully with cached techniques
                        val districts = repository.getDistrictsWaterStatus()
                        val tips = repository.getWaterTips()
                        val defaultDistrict = districts.find { it.districtName == "Kurukshetra" } ?: districts.first()
                        val initialTip = tips.random()

                        _uiState.value = AquaUiState.Success(
                            districts = districts,
                            selectedDistrict = defaultDistrict,
                            techniques = cachedTechniques,
                            waterTips = tips,
                            currentTip = initialTip
                        )
                    } else {
                        _uiState.value = AquaUiState.Error(e.message ?: "An unexpected error occurred.")
                    }
                } catch (fallbackEx: Exception) {
                    _uiState.value = AquaUiState.Error(e.message ?: "An unexpected error occurred.")
                }
            }
        }
    }

    fun retryLoading() {
        loadDashboardData()
    }

    fun toggleErrorSimulation() {
        _simulateErrorOnRefresh.update { !it }
    }

    fun selectDistrict(district: DistrictWaterStatus) {
        val currentState = _uiState.value
        if (currentState is AquaUiState.Success) {
            _uiState.value = currentState.copy(selectedDistrict = district)
        }
    }

    fun rotateTip() {
        val currentState = _uiState.value
        if (currentState is AquaUiState.Success) {
            var newTip = currentState.waterTips.random()
            // Make sure we pick a different one if possible
            while (newTip.id == currentState.currentTip.id && currentState.waterTips.size > 1) {
                newTip = currentState.waterTips.random()
            }
            _uiState.value = currentState.copy(currentTip = newTip)
        }
    }

    // Footprint Calculator Logics
    fun updateShowerMinutes(mins: Float) {
        _calculatorState.update { it.copy(showerMinutes = mins, hasCalculated = false) }
    }

    fun updateUseBucket(use: Boolean) {
        _calculatorState.update { it.copy(useBucketInsteadOfShower = use, hasCalculated = false) }
    }

    fun updateBrushingTap(running: Boolean) {
        _calculatorState.update { it.copy(brushingTapRunning = running, hasCalculated = false) }
    }

    fun updateLeaks(hasLeaks: Boolean) {
        _calculatorState.update { it.copy(leaksPresent = hasLeaks, hasCalculated = false) }
    }

    fun updateCarWashing(times: Float) {
        _calculatorState.update { it.copy(carWashingTimesPerWeek = times, hasCalculated = false) }
    }

    fun calculateFootprint() {
        _calculatorState.update { state ->
            // Base standards:
            // Shower consumes ~15 liters per minute
            // Bucket bath consumes solid ~25 liters total
            val bathingLiters = if (state.useBucketInsteadOfShower) {
                25.0
            } else {
                state.showerMinutes * 15.0
            }

            // Brushing: tap running consumes ~12 liters per min. Standard brush time is 2 min = 24 liters.
            // Keeping tap off consumes ~1 liter.
            val brushingLiters = if (state.brushingTapRunning) 24.0 else 1.0

            // Leaking taps: adds ~30 liters per day
            val leakLiters = if (state.leaksPresent) 30.0 else 0.0

            // Car washing: hose pipe consumes ~150 liters. Bucket wash consumes ~25 liters.
            // Daily average based on weekly frequency
            val carWashLitersPerDay = (state.carWashingTimesPerWeek * 150.0) / 7.0

            // Other default daily allocations (drinking, toilet, cooking): ~60 liters (basic Indian standard)
            val baselineHouseholdAllocation = 60.0

            val totalDailyLiters = bathingLiters + brushingLiters + leakLiters + carWashLitersPerDay + baselineHouseholdAllocation

            state.copy(
                calculatedDailyLiters = totalDailyLiters,
                hasCalculated = true
            )
        }
    }

    fun resetCalculator() {
        _calculatorState.value = FootprintCalculatorState()
    }

    // Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I am AquaBot, your AI Water Conservation assistant for Haryana. Ask me any questions about local groundwater levels, water-saving farming practices like Direct Seeded Rice (DSR), micro-irrigation systems, rainwater harvesting, or local government incentives like 'Mera Pani Meri Virasat'.",
                sender = MessageSender.BOT
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        // 1. Add User Message
        val userMsg = ChatMessage(text = text, sender = MessageSender.USER)
        _chatMessages.update { it + userMsg }

        _isChatLoading.value = true

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                val isApiKeyConfigured = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

                val replyText = if (isApiKeyConfigured) {
                    // Call Direct REST API
                    val contentsList = mutableListOf<GeminiContent>()
                    _chatMessages.value
                        .filter { it.sender == MessageSender.USER || it.sender == MessageSender.BOT }
                        .takeLast(10)
                        .forEach { msg ->
                            val role = if (msg.sender == MessageSender.USER) "user" else "model"
                            contentsList.add(
                                GeminiContent(
                                    parts = listOf(GeminiPart(text = msg.text)),
                                    role = role
                                )
                            )
                        }

                    val systemInstruction = GeminiContent(
                        parts = listOf(
                            GeminiPart(
                                text = "You are AquaBot, an expert AI assistant specializing in water conservation, groundwater hydrology, and sustainable agricultural practices tailored to Haryana's specific climate, soil, and geography. Provide highly relevant, accurate, and actionable water-saving tips. Refer back to the following 17 key water-saving conservation practices: " +
                                       "1. Drip & Sprinkler Irrigation (saves 40-50% water, up to 85% MICADA subsidy), " +
                                       "2. Rooftop Rainwater Harvesting (saves 30% domestic water), " +
                                       "3. Crop Diversification (paddy to maize/pulses, saves 60% water, ₹7,000/acre under Mera Pani Meri Virasat), " +
                                       "4. Direct Seeded Rice (DSR) (saves 15-20% water, ₹4,000/acre state incentive), " +
                                       "5. Laser Land Leveling (saves 15-20% water, ensures uniform moisture), " +
                                       "6. Organic Soil Mulching (saves 20-30% soil moisture, prevents evaporation), " +
                                       "7. Zero Tillage Cultivation (saves 15-20% water/fuel, ₹1,000/acre state support), " +
                                       "8. Greywater Treatment & Reuse (saves 40% household water, great for gardens), " +
                                       "9. Smart IoT Irrigation Systems (saves 30-45% water using real-time soil sensors), " +
                                       "10. On-Farm Ponds (saves 30% groundwater extraction by holding monsoon runoff), " +
                                       "11. System of Rice Intensification (SRI) (saves 25-35% water via alternate wetting), " +
                                       "12. Hydroponics & Soilless Farming (saves 90% water, perfect for vegetables), " +
                                       "13. Happy Seeder Technology (saves 15-20% water, manages wheat residue, reduces stubble burning), " +
                                       "14. Artificial Borewell Recharge (recharges deep aquifers, prevents well drying), " +
                                       "15. Alternate Wetting & Drying (AWD) (saves 20-25% water in rice fields), " +
                                       "16. Sub-surface Drip Irrigation (saves 50-60% water, targets roots directly), " +
                                       "17. Community Water Budgeting (saves 15% village water through shared auditing). " +
                                       "Highlight these practices and relevant Haryana Government Schemes (e.g. Mera Pani Meri Virasat, MICADA subsidies of up to 85%, Atal Bhujal Yojana). Keep responses structured, professional, friendly, and concise."
                            )
                        )
                    )

                    val request = GeminiRequest(
                        contents = contentsList,
                        systemInstruction = systemInstruction
                    )

                    val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
                    response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                        ?: "I'm sorry, I couldn't formulate a response. Please try again."
                } else {
                    // Generate Haryana specific mock response
                    generateMockBotResponse(text)
                }

                _chatMessages.update { it + ChatMessage(text = replyText, sender = MessageSender.BOT) }
            } catch (e: Exception) {
                _chatMessages.update {
                    it + ChatMessage(
                        text = "Connection Error: Unable to reach AquaBot server. ${e.message ?: "Please try again later."}",
                        sender = MessageSender.ERROR
                    )
                }
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                text = "Hello! I am AquaBot, your AI Water Conservation assistant for Haryana. Ask me any questions about local groundwater levels, water-saving farming practices like Direct Seeded Rice (DSR), micro-irrigation systems, rainwater harvesting, or local government incentives like 'Mera Pani Meri Virasat'.",
                sender = MessageSender.BOT
            )
        )
    }

    private fun generateMockBotResponse(prompt: String): String {
        val lowercasePrompt = prompt.lowercase()
        return when {
            lowercasePrompt.contains("dsr") || lowercasePrompt.contains("direct seed") -> {
                "**Direct Seeded Rice (DSR)** is highly recommended for Haryana's water-scarce districts like Kurukshetra and Kaithal.\n\n" +
                        "• **Water Saving:** Reduces water consumption by 15% to 20% compared to traditional transplanting.\n" +
                        "• **Benefits:** Saves labor, reduces methane emissions, and maintains soil structure.\n" +
                        "• **Haryana Govt Incentive:** The state government offers a direct incentive of **₹4,000 per acre** under the 'Mera Pani Meri Virasat' scheme for adopting DSR."
            }
            lowercasePrompt.contains("crop diversification") || lowercasePrompt.contains("diversif") || lowercasePrompt.contains("paddy") || lowercasePrompt.contains("rice") -> {
                "To combat critical groundwater depletion in districts like Kaithal and Kurukshetra, Haryana promotes shifting away from water-guzzling paddy crops:\n\n" +
                        "• **Alternative Crops:** Maize, pulses (moong, arhar), cotton, and oilseeds.\n" +
                        "• **Mera Pani Meri Virasat Scheme:** Provides **₹7,000 per acre** incentive to farmers who diversify from paddy to other low-water crops.\n" +
                        "• **Drip Irrigation Integration:** Highly subsidized (up to 85%) for diversified crops."
            }
            lowercasePrompt.contains("drip") || lowercasePrompt.contains("micro") || lowercasePrompt.contains("sprinkler") || lowercasePrompt.contains("irrigation") -> {
                "**Micro-Irrigation (Drip and Sprinkler)** is vital for dry zones in Southern and Western Haryana (Mahendragarh, Bhiwani, Sirsa):\n\n" +
                        "• **Efficiency:** Delivers water directly to the plant root zone, saving up to 50% water.\n" +
                        "• **Haryana Subsidies:** The Haryana Micro Irrigation and Command Area Development Authority (MICADA) offers **up to 85% subsidy** on drip irrigation setup costs."
            }
            lowercasePrompt.contains("borewell") || lowercasePrompt.contains("recharge") || lowercasePrompt.contains("groundwater") -> {
                "Haryana's groundwater table is depleting at an alarming rate, with many blocks classified as 'Over-exploited'.\n\n" +
                        "• **Artificial Recharge:** Constructing filter pits with boulders, gravel, and sand around borewells helps channel monsoon runoff into deep aquifers.\n" +
                        "• **Atal Bhujal Yojana:** Active in critical districts of Haryana, this scheme funds community borewell recharge structures and check dams."
            }
            lowercasePrompt.contains("rainwater") || lowercasePrompt.contains("harvesting") -> {
                "**Rainwater Harvesting** is excellent for both rural farms and urban areas like Gurugram and Faridabad:\n\n" +
                        "• **Rooftop Harvesting:** Collects clean rainwater in storage tanks for domestic use.\n" +
                        "• **Farm Ponds:** Excavating ponds with plastic linings captures monsoon runoff, relieving borewell extraction by 30%.\n" +
                        "• **Government Mandate:** Haryana building bylaws mandate rainwater harvesting systems for all new buildings over a certain size."
            }
            lowercasePrompt.contains("laser") || lowercasePrompt.contains("leveling") || lowercasePrompt.contains("leveler") -> {
                "**Laser Land Leveling** is a precision farming practice widely supported in Haryana:\n\n" +
                        "• **Water Saving:** Saves **15% to 20%** of irrigation water by ensuring flat, uniform fields.\n" +
                        "• **Yield Boost:** Improves crop yields by **10% to 15%** due to uniform moisture distribution.\n" +
                        "• **Haryana Govt Support:** Levelers are heavily subsidized for cooperative farming groups and custom hiring centers across Haryana."
            }
            lowercasePrompt.contains("mulch") || lowercasePrompt.contains("mulching") -> {
                "**Organic Soil Mulching** involves covering the soil surface with crop residue or plastic sheets:\n\n" +
                        "• **Water Saving:** Saves **20% to 30%** soil moisture by drastically reducing evaporation.\n" +
                        "• **Other Benefits:** Controls weed growth, maintains optimal soil temperature, and prevents soil erosion.\n" +
                        "• **Best Applied on:** Vegetable crops, orchards, and high-value horticulture in districts like Sonipat and Karnal."
            }
            lowercasePrompt.contains("zero tillage") || lowercasePrompt.contains("no-till") || lowercasePrompt.contains("tillage") || lowercasePrompt.contains("zero till") -> {
                "**Zero Tillage Cultivation** allows direct sowing of wheat or pulses immediately after paddy harvest without plowing:\n\n" +
                        "• **Water Saving:** Saves **15% to 20%** of water on the first irrigation cycle.\n" +
                        "• **Benefits:** Reduces fuel consumption, saves time, and preserves soil organic carbon.\n" +
                        "• **Haryana Govt Incentive:** Farmers receive state assistance of **₹1,000 per acre** for adopting zero-tillage practices."
            }
            lowercasePrompt.contains("greywater") || lowercasePrompt.contains("graywater") || lowercasePrompt.contains("waste-water") || lowercasePrompt.contains("recycling") -> {
                "**Greywater Treatment & Reuse** channels wastewater from bathrooms, washing machines, and kitchens:\n\n" +
                        "• **Water Saving:** Can offset up to **40%** of domestic fresh-water demand.\n" +
                        "• **Haryana 3-Pond System:** In rural Haryana, the government is building decentralized 3-pond system waste treatment plants in villages to reuse wastewater for agriculture."
            }
            lowercasePrompt.contains("smart") || lowercasePrompt.contains("iot") || lowercasePrompt.contains("sensor") -> {
                "**Smart IoT Irrigation Systems** utilize soil moisture sensors and weather data to automate watering:\n\n" +
                        "• **Water Saving:** Prevents over-watering, saving **30% to 45%** of irrigation water.\n" +
                        "• **Haryana Tech Push:** The government promotes smart farming systems through agricultural universities (like HAU Hisar) and offers grants for Agri-Tech startups."
            }
            lowercasePrompt.contains("pond") || lowercasePrompt.contains("ponds") -> {
                "**On-Farm Ponds** capture excess monsoon runoff to store water for supplementary irrigation:\n\n" +
                        "• **Water Saving:** Prevents reliance on depleting tubewells and recharges local groundwater.\n" +
                        "• **Haryana Govt Subsidy:** Offers up to **70% to 85% financial assistance** for constructing lined farm ponds and solar-powered pump sets."
            }
            lowercasePrompt.contains("sri") || lowercasePrompt.contains("system of rice") || lowercasePrompt.contains("intensification") -> {
                "**System of Rice Intensification (SRI)** is a low-water alternative method of transplanting young seedlings:\n\n" +
                        "• **Water Saving:** Reduces water consumption by **25% to 35%** compared to traditional flood irrigation.\n" +
                        "• **Benefits:** Uses 90% fewer seeds, produces robust roots, and increases paddy yield by 20%."
            }
            lowercasePrompt.contains("hydroponic") || lowercasePrompt.contains("hydroponics") || lowercasePrompt.contains("soilless") -> {
                "**Hydroponics & Soilless Farming** grows crops in nutrient-rich water solutions without soil:\n\n" +
                        "• **Water Saving:** Drastically saves up to **90% water** compared to traditional open-field farming.\n" +
                        "• **Haryana Govt Scheme:** The Horticulture Department provides high subsidies (up to 50%-65%) for setting up state-of-the-art polyhouses and hydroponic structures."
            }
            lowercasePrompt.contains("happy seeder") || lowercasePrompt.contains("stubble") || lowercasePrompt.contains("burning") -> {
                "**Happy Seeder Technology** is a tractor-mounted machine that sows wheat seeds directly through standing paddy straw residue:\n\n" +
                        "• **Water Saving:** Conserves residual soil moisture, reducing the first irrigation requirement by **15% to 20%**.\n" +
                        "• **Air Quality:** Combats Haryana's winter air pollution by providing a viable alternative to stubble burning."
            }
            lowercasePrompt.contains("awd") || lowercasePrompt.contains("alternate wetting") -> {
                "**Alternate Wetting & Drying (AWD)** is a water management technique for rice cultivation:\n\n" +
                        "• **Water Saving:** Reduces water use by **20% to 25%** without sacrificing paddy yield.\n" +
                        "• **Method:** Instead of keeping the field permanently flooded, it is allowed to dry for a few days until water table drops to 15cm below soil surface before re-flooding."
            }
            lowercasePrompt.contains("subsurface") || lowercasePrompt.contains("sub-surface") -> {
                "**Sub-surface Drip Irrigation** installs drip tubes 10-30 cm below the soil surface directly into plant root zones:\n\n" +
                        "• **Water Saving:** Conserves **50% to 60%** water compared to flood irrigation by eliminating surface evaporation.\n" +
                        "• **Haryana Govt Subsidy:** Highly promoted by MICADA with subsidies up to **85%** to help save water in sandy soils of districts like Mahendragarh."
            }
            lowercasePrompt.contains("budgeting") || lowercasePrompt.contains("audit") || lowercasePrompt.contains("village water") -> {
                "**Community Water Budgeting** involves villages monitoring, auditing, and sharing local water resources collectively:\n\n" +
                        "• **Water Saving:** Empowers rural communities to save up to **15% to 20%** water through cooperative planning.\n" +
                        "• **Atal Bhujal Yojana:** Active in Haryana, this scheme funds village Water Security Plans and educates Panchayat members on sustainable budgeting."
            }
            lowercasePrompt.contains("kaithal") || lowercasePrompt.contains("kurukshetra") || lowercasePrompt.contains("district") -> {
                "Both Kurukshetra and Kaithal are classified in the **Over-Exploited** groundwater zone.\n\n" +
                        "• **Average Depth:** Water table depths exceed 35 meters and 42 meters respectively.\n" +
                        "• **Critical Challenge:** Rice-wheat monoculture has depleted local aquifers drastically.\n" +
                        "• **Local Solutions:** State campaigns encourage switching to maize/oilseeds and deploying Direct Seeded Rice (DSR) alongside Laser Land Leveling."
            }
            else -> {
                "That's a great question about water conservation in Haryana!\n\n" +
                        "Haryana faces severe groundwater challenges due to intensive rice-wheat cultivation. To address this, consider:\n" +
                        "1. **Micro-Irrigation:** Drip and sprinkler systems (up to 85% subsidy in Haryana).\n" +
                        "2. **Crop Diversification:** Shifting from paddy to maize/pulses (₹7,000/acre incentive under 'Mera Pani Meri Virasat').\n" +
                        "3. **Rainwater Harvesting:** Recharge shafts and farm ponds to collect monsoon runoff.\n\n" +
                        "What specific area (agriculture, household, or government schemes) would you like to explore further?"
            }
        }
    }

    fun logWaterUsage(amountLiters: Int, activity: String, customDate: String? = null) {
        val dateStr = customDate ?: java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        viewModelScope.launch {
            waterLogRepository.insertLog(
                WaterLog(
                    date = dateStr,
                    amountLiters = amountLiters,
                    activity = activity
                )
            )
        }
    }

    fun deleteLog(id: Int) {
        viewModelScope.launch {
            waterLogRepository.deleteLogById(id)
        }
    }
}
