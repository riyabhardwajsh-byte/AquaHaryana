package com.example.data

import kotlinx.coroutines.delay

class WaterRepository {

    // Ground water data of Haryana districts
    private val districtsData = listOf(
        DistrictWaterStatus(
            districtName = "Kaithal",
            groundWaterZone = WaterZone.OVER_EXPLOITED,
            averageWaterTableDepthMeters = 35.4,
            criticalBlocksCount = 6,
            totalBlocksCount = 7,
            vulnerabilityDescription = "Rice-wheat cropping system dominates, leading to severe depletion of groundwater resources. Promotion of DSR and crop diversification is critically needed."
        ),
        DistrictWaterStatus(
            districtName = "Kurukshetra",
            groundWaterZone = WaterZone.OVER_EXPLOITED,
            averageWaterTableDepthMeters = 42.5,
            criticalBlocksCount = 7,
            totalBlocksCount = 7,
            vulnerabilityDescription = "Highly intensive paddy cultivation has led to extreme water depletion. Immediate water conservation and crop diversification are mandatory."
        ),
        DistrictWaterStatus(
            districtName = "Mahendragarh",
            groundWaterZone = WaterZone.OVER_EXPLOITED,
            averageWaterTableDepthMeters = 61.2,
            criticalBlocksCount = 5,
            totalBlocksCount = 5,
            vulnerabilityDescription = "Arid climate paired with excessive extraction for agriculture. Groundwater levels are dropping rapidly with heavy fluoride concentration."
        ),
        DistrictWaterStatus(
            districtName = "Gurugram (Gurgaon)",
            groundWaterZone = WaterZone.OVER_EXPLOITED,
            averageWaterTableDepthMeters = 38.0,
            criticalBlocksCount = 4,
            totalBlocksCount = 4,
            vulnerabilityDescription = "Rapid urbanization and unregulated deep tube-wells have caused severe groundwater extraction. Rainwater harvesting is legally mandated."
        ),
        DistrictWaterStatus(
            districtName = "Karnal",
            groundWaterZone = WaterZone.CRITICAL,
            averageWaterTableDepthMeters = 24.8,
            criticalBlocksCount = 4,
            totalBlocksCount = 6,
            vulnerabilityDescription = "Known as the Rice Bowl, Karnal faces a critical drop in groundwater tables due to high water-demand rice varieties."
        ),
        DistrictWaterStatus(
            districtName = "Rohtak",
            groundWaterZone = WaterZone.SEMI_CRITICAL,
            averageWaterTableDepthMeters = 12.3,
            criticalBlocksCount = 2,
            totalBlocksCount = 5,
            vulnerabilityDescription = "Water table is relatively higher but high salinity limits its use. Soil waterlogging is also a secondary challenge."
        ),
        DistrictWaterStatus(
            districtName = "Ambala",
            groundWaterZone = WaterZone.SEMI_CRITICAL,
            averageWaterTableDepthMeters = 18.5,
            criticalBlocksCount = 1,
            totalBlocksCount = 6,
            vulnerabilityDescription = "Moderately stable, but agricultural runoff and industrial effluents pose pollution risks alongside steady level decline."
        ),
        DistrictWaterStatus(
            districtName = "Panchkula",
            groundWaterZone = WaterZone.SAFE,
            averageWaterTableDepthMeters = 8.1,
            criticalBlocksCount = 0,
            totalBlocksCount = 4,
            vulnerabilityDescription = "Sub-mountainous Shivalik region gets ample precipitation. Water tables are healthy, but hilly terrains suffer from soil erosion and surface run-offs."
        )
    )

    // Educational water-saving techniques
    private val techniques = listOf(
        WaterSavingTechnique(
            id = "drip_irrigation",
            title = "Drip & Sprinkler Irrigation",
            shortDescription = "Applies water directly to the plant roots, minimizing evaporation and run-off.",
            detailedDescription = "Micro-irrigation systems deliver water slowly and precisely. Sprinklers cover larger fields evenly. They are highly suitable for Haryana's water-scarce southern districts like Mahendragarh, Rewari, and Bhiwani.",
            steps = listOf(
                "Install a filtration unit near the water source.",
                "Lay main, sub-main, and lateral pipelines across the field.",
                "Attach emitters or drippers at specific intervals matching crop spacing.",
                "Utilize automated valves or timers to irrigate during cool early mornings to avoid evaporation."
            ),
            waterSavedPercent = "45% to 70%",
            haryanaGovtScheme = "Micro Irrigation Scheme: Haryana government provides up to 85% subsidy on installation of drip and sprinkler units to registered farmers.",
            iconName = "water"
        ),
        WaterSavingTechnique(
            id = "rainwater_harvesting",
            title = "Rooftop Rainwater Harvesting",
            shortDescription = "Capturing and storing rainwater from rooftops to recharge borewells or for direct household use.",
            detailedDescription = "Haryana receives limited annual rainfall. Capturing monsoon precipitation from rooftops helps recharge depleting aquifers and relieves municipal water dependency in cities like Gurugram, Faridabad, and Panchkula.",
            steps = listOf(
                "Clean rooftops and fix catchments (PVC gutters) along the slopes.",
                "Connect conduits to channel water to a filtration tank filled with layers of sand, gravel, and charcoal.",
                "Direct the filtered water to a dedicated storage tank or a dry recharge well.",
                "Conduct regular pre-monsoon maintenance and pipe flushing."
            ),
            waterSavedPercent = "30% to 50% household supply",
            haryanaGovtScheme = "Haryana Building Code Mandate: Rainwater harvesting is legally mandatory for all buildings with a plot area of 100 square yards and above.",
            iconName = "umbrella"
        ),
        WaterSavingTechnique(
            id = "crop_diversification",
            title = "Crop Diversification (Mera Pani - Meri Virasat)",
            shortDescription = "Shifting farming from heavy water-guzzling crops (Paddy) to low-water alternatives.",
            detailedDescription = "Cultivating 1 kg of paddy rice requires over 3,000 liters of water. Shifting to alternative crops like maize, millet (bajra), pulses, and oilseeds dramatically lowers water consumption and restores soil health.",
            steps = listOf(
                "Evaluate soil and climate conditions for alternatives like Maize or Bajra.",
                "Reduce area under paddy transplanting gradually.",
                "Adopt high-yield hybrid seeds for alternative crops to ensure financial returns.",
                "Register on the state portal to claim agricultural incentives."
            ),
            waterSavedPercent = "60% agricultural water reduction",
            haryanaGovtScheme = "Mera Pani - Meri Virasat: Haryana government offers an incentive of Rs. 7,000 per acre to farmers who replace paddy with water-saving alternative crops.",
            iconName = "eco"
        ),
        WaterSavingTechnique(
            id = "dsr_farming",
            title = "Direct Seeded Rice (DSR)",
            shortDescription = "Sowing seeds directly in fields, bypassing nursery transplantation and flooded puddling.",
            detailedDescription = "Conventional rice farming requires flooding the field for weeks. Direct Seeded Rice (DSR) sows pre-germinated seeds directly into unflooded soil using specialized seed drills, saving huge volumes of groundwater and diesel fuel.",
            steps = listOf(
                "Laser-level the agricultural land precisely.",
                "Perform pre-sowing irrigation to prepare optimal soil moisture.",
                "Use a DSR tractor machine to sow rice seeds alongside precise weedicide application.",
                "Apply water only at critical growth stages, avoiding continuous flooding."
            ),
            waterSavedPercent = "20% to 30%",
            haryanaGovtScheme = "DSR Promotion Incentive: Govt of Haryana provides a financial incentive of Rs. 4,000 per acre to farmers adopting DSR technology.",
            iconName = "agriculture"
        ),
        WaterSavingTechnique(
            id = "laser_land_leveling",
            title = "Laser Land Leveling",
            shortDescription = "Achieves zero-gradient leveling of agricultural fields using laser-guided scrapers.",
            detailedDescription = "Uneven fields lead to water pooling in low spots and under-watering in high spots. Laser leveling ensures a perfectly flat field, ensuring uniform moisture, saving water, and boosting crop yields.",
            steps = listOf(
                "Position the laser transmitter on a high tripod in the center of the field.",
                "Calibrate the receiver on the tractor-mounted scraper.",
                "Drive the scraper across the field to automatically cut high soil and fill low areas.",
                "Repeat periodically every 3 years."
            ),
            waterSavedPercent = "15% to 25%",
            haryanaGovtScheme = "Subsidized Laser Levelers under SMAM: Standard subsidies are available for purchasing laser leveling machines via agricultural co-ops.",
            iconName = "gradient"
        ),
        WaterSavingTechnique(
            id = "organic_mulching",
            title = "Organic Soil Mulching",
            shortDescription = "Covering the soil surface with organic materials to retain moisture and suppress weeds.",
            detailedDescription = "By placing a layer of straw, dry leaves, or crop residue over the soil, evaporation is significantly reduced, keeping soil temperatures stable and retaining groundwater.",
            steps = listOf(
                "Collect organic residue such as straw or dry leaves.",
                "Spread a 2-3 inch layer evenly around the root zone of plants.",
                "Leave space around the plant stems to prevent rot.",
                "Replenish the mulch layer as it decomposes into organic matter."
            ),
            waterSavedPercent = "20% to 35%",
            haryanaGovtScheme = "Incentives under Horti-mulching Schemes: Haryana Horticulture department offers subsidies for plastic and organic mulching practices.",
            iconName = "eco"
        ),
        WaterSavingTechnique(
            id = "zero_tillage",
            title = "Zero Tillage Cultivation",
            shortDescription = "Sowing crops directly into untilled soil to preserve soil structure and retain residual moisture.",
            detailedDescription = "Tilling dries out the soil. Zero tillage bypasses land preparation, planting seeds directly into undisturbed soil. This method conserves soil moisture and significantly saves pre-sowing irrigation water.",
            steps = listOf(
                "Leave the residue of the previous crop on the field.",
                "Configure a specialized Zero-Till seed drill machine.",
                "Sow wheat or mustard seeds directly into the residue-covered soil.",
                "Irrigate sparingly since the residue layer locks in moisture."
            ),
            waterSavedPercent = "15% to 25%",
            haryanaGovtScheme = "Promoted under Rashtriya Krishi Vikas Yojana (RKVY) with custom hiring centers offering zero-till drill machines at subsidized rental rates.",
            iconName = "agriculture"
        ),
        WaterSavingTechnique(
            id = "greywater_recycling",
            title = "Greywater Treatment & Reuse",
            shortDescription = "Recycling household greywater from baths and sinks for garden or toilet use.",
            detailedDescription = "Greywater can be safely reused for domestic gardens or toilet flushing after basic filtration, massively reducing the demand on freshwater aquifers.",
            steps = listOf(
                "Separate greywater pipes (baths, sinks) from blackwater lines (toilets).",
                "Route greywater to a settling basin to filter large debris.",
                "Pass water through a sand and gravel filter bed for purification.",
                "Store treated greywater in a tank and use it within 24 hours for irrigation."
            ),
            waterSavedPercent = "30% to 40% domestic supply",
            haryanaGovtScheme = "Swachh Bharat Mission (Gramin) Phase-II supports the installation of community greywater management systems in rural villages.",
            iconName = "water"
        ),
        WaterSavingTechnique(
            id = "smart_irrigation",
            title = "Smart IoT Irrigation Systems",
            shortDescription = "Using soil moisture sensors and weather data to automate irrigation schedules.",
            detailedDescription = "Smart irrigation applies water only when sensors indicate that soil moisture levels have dropped below a critical threshold, eliminating wasteful over-watering.",
            steps = listOf(
                "Place wireless soil moisture sensors at key root depths.",
                "Connect the sensors to an automated IoT irrigation controller.",
                "Sync the controller with local weather forecasting to skip cycles when rain is predicted.",
                "Monitor and adjust watering zones using a smartphone app."
            ),
            waterSavedPercent = "30% to 50%",
            haryanaGovtScheme = "Horticulture Department Haryana offers dynamic incentives and technology grants for smart farming and IoT-based polyhouse installations.",
            iconName = "gradient"
        ),
        WaterSavingTechnique(
            id = "farm_ponds",
            title = "On-Farm Ponds",
            shortDescription = "Excavating small ponds in agricultural fields to capture monsoon runoff.",
            detailedDescription = "Farm ponds capture excess rain and surface runoff. This water can be stored for supplementary irrigation during dry periods, relieving deep aquifer extraction.",
            steps = listOf(
                "Identify low-lying areas of the farm where surface runoff naturally flows.",
                "Excavate a pond with sloping walls to prevent soil collapse.",
                "Line the pond with LDPE geomembrane sheets to prevent seepage loss.",
                "Install a silt trap at the inlet to collect sediment before water enters the pond."
            ),
            waterSavedPercent = "25% to 40% groundwater relief",
            haryanaGovtScheme = "Pond Rejuvenation Authority of Haryana provides incentives up to 70% for excavating farm ponds and community rain-pools.",
            iconName = "water"
        ),
        WaterSavingTechnique(
            id = "rice_intensification",
            title = "System of Rice Intensification (SRI)",
            shortDescription = "An eco-friendly methodology for increasing the yield of rice produced while saving water.",
            detailedDescription = "Unlike conventional flooding, SRI keeps rice soils moist but unflooded during vegetative growth, reducing water demand and improving root development.",
            steps = listOf(
                "Transplant young seedlings (8-12 days old) individually with wide spacing.",
                "Apply water to keep soil moist but not continuously flooded.",
                "Perform mechanical weeding to aerate the soil.",
                "Apply organic compost to enrich soil microbiology."
            ),
            waterSavedPercent = "30% to 50%",
            haryanaGovtScheme = "Agricultural Extension Services provide training, seeds, and cash inputs to farmers transitioning to SRI methods in northern districts.",
            iconName = "agriculture"
        ),
        WaterSavingTechnique(
            id = "hydroponics",
            title = "Hydroponics & Soilless Farming",
            shortDescription = "Growing plants in nutrient-rich water solutions without soil, recirculating water.",
            detailedDescription = "Soilless cultivation recirculates the nutrient water, avoiding run-off and deep soil seepage. It is incredibly water-efficient, especially for high-value leafy vegetables and berries.",
            steps = listOf(
                "Set up a food-safe PVC channel system (NFT) or grow beds.",
                "Fill the reservoir with water and balanced plant nutrient solution.",
                "Place seedlings in net pots supported by clay pebbles.",
                "Use a pump to continuously circulate and aerate the water mixture."
            ),
            waterSavedPercent = "80% to 90%",
            haryanaGovtScheme = "Integrated Horticulture Development Scheme provides up to 50% subsidy on hydroponic setups and protected cultivation structures.",
            iconName = "eco"
        ),
        WaterSavingTechnique(
            id = "happy_seeder",
            title = "Happy Seeder Technology",
            shortDescription = "Sows wheat seeds directly into paddy stubble without burning, conserving soil moisture.",
            detailedDescription = "Instead of burning stubble or intensive tilling, the Happy Seeder cuts rice straw, sows wheat seeds, and deposits the cut straw back as organic mulch, retaining vital soil moisture.",
            steps = listOf(
                "Ensure paddy fields are harvested with a SMS-enabled combine harvester.",
                "Mount the Happy Seeder onto a heavy-duty tractor.",
                "Sow wheat seeds directly into the standing straw and residue.",
                "Let the shredded straw act as a natural blanket to lock in moisture."
            ),
            waterSavedPercent = "15% to 20%",
            haryanaGovtScheme = "Crop Residue Management (CRM) Scheme provides up to 50% subsidy to individual farmers and 80% to custom hiring centers for Happy Seeders.",
            iconName = "agriculture"
        ),
        WaterSavingTechnique(
            id = "borewell_recharge",
            title = "Artificial Borewell Recharge",
            shortDescription = "Channelling clean surface runoff directly into deep aquifers via customized shafts.",
            detailedDescription = "Recharge shafts direct clean rainwater bypass layers straight to depleted aquifers, effectively restoring groundwater tables during heavy monsoon seasons.",
            steps = listOf(
                "Dig a 10x10 foot filter pit around the casing pipe of an inactive borewell.",
                "Perforate the casing pipe at selected depths to allow water entry.",
                "Fill the filter pit with successive layers of boulders, gravel, sand, and charcoal.",
                "Divert clean surface runoff or rooftop water into the filter pit."
            ),
            waterSavedPercent = "50% aquifer level boost",
            haryanaGovtScheme = "Atal Bhujal Yojana offers structural grants for creating check dams and deep recharge shafts in critical blocks.",
            iconName = "gradient"
        ),
        WaterSavingTechnique(
            id = "awd_irrigation",
            title = "Alternate Wetting & Drying (AWD)",
            shortDescription = "Intermittently drying and flooding paddy fields using perforated pipes to monitor water depth.",
            detailedDescription = "AWD is a water-management practice where fields are flooded, then allowed to dry until water levels drop below soil level (monitored via field water tubes) before re-flooding.",
            steps = listOf(
                "Install a 30cm perforated plastic pipe (field water tube) 15cm into the soil.",
                "Flood the paddy field to normal depth (about 5cm).",
                "Monitor the water table level inside the field water tube daily.",
                "Irrigate again only when water falls to 15cm below the soil surface."
            ),
            waterSavedPercent = "25% to 35%",
            haryanaGovtScheme = "Promoted under the Climate-Smart Agriculture programs of CCS Haryana Agricultural University (HAU).",
            iconName = "water"
        ),
        WaterSavingTechnique(
            id = "subsurface_drip",
            title = "Sub-surface Drip Irrigation",
            shortDescription = "Burying drip lateral lines below the plow depth to deliver water directly to root zones.",
            detailedDescription = "By burying laterals under the soil, surface evaporation is completely eliminated. Weeds cannot grow due to dry topsoil, and water efficiency is maximized for row crops like sugarcane and cotton.",
            steps = listOf(
                "Conduct soil mapping to plan deep line layouts.",
                "Trench and bury micro-irrigation lateral pipelines 20-40cm below the surface.",
                "Install air release valves to prevent vacuum suction from pulling soil into emitters.",
                "Run water and fertilizer cycles directly into the root zone."
            ),
            waterSavedPercent = "50% to 80%",
            haryanaGovtScheme = "Sub-surface micro-irrigation receives premium incentives under the PMKSY (Pradhan Mantri Krishi Sinchayee Yojana) state channel.",
            iconName = "gradient"
        ),
        WaterSavingTechnique(
            id = "water_budgeting",
            title = "Community Water Budgeting",
            shortDescription = "Village-level planning to align crop choices with annual groundwater availability.",
            detailedDescription = "Empowering Panchayats to map domestic water supply and agricultural requirements against annual recharge estimates, encouraging cooperative resource sharing.",
            steps = listOf(
                "Train village community volunteers in measuring rainfall and water tables.",
                "Map total village water availability against domestic and cropping demands.",
                "Formulate a Crop Water Budget Plan at the Gram Sabha level.",
                "Promote collective agreements to limit high water crops if recharge is low."
            ),
            waterSavedPercent = "15% to 30% aggregate savings",
            haryanaGovtScheme = "Atal Bhujal Yojana mandates Village Water Security Plans (VWSP) across 1,600+ gram panchayats in Haryana.",
            iconName = "umbrella"
        )
    )

    // Curated conservation tips
    private val tips = listOf(
        WaterTip(1, "Turn off the tap while brushing your teeth. This saves up to 12 liters of water every single minute!", "Central Ground Water Board"),
        WaterTip(2, "Water your lawns and plants in the early morning or late evening. Cooler temperatures reduce evaporation significantly.", "Haryana Agriculture Dept"),
        WaterTip(3, "Fix leaking household taps immediately! A single leaking tap dripping once per second wastes over 10,000 liters of water annually.", "HWRA"),
        WaterTip(4, "Install aerators on your kitchen and bathroom taps. They mix air with water, giving a strong flow while reducing water output by 50%.", "Sujal Initiative"),
        WaterTip(5, "Wash your cars using a bucket and sponge instead of an open hose pipe. This can save up to 300 liters of water per wash!", "Urban Local Bodies Haryana"),
        WaterTip(6, "Use wastewater from your RO water purifier to sweep floors, wash utensils, or water household plants.", "Environment Club Haryana"),
        WaterTip(7, "Prefer bucket baths over long shower baths. A typical 10-minute shower consumes 150 liters, while a bucket bath takes only 20 liters.", "Indian Water Resources Ministry"),
        WaterTip(8, "Report active roadside pipeline bursts or municipal leaks to the Haryana Water Resources Authority (HWRA) via official portals.", "HWRA")
    )

    suspend fun getDistrictsWaterStatus(): List<DistrictWaterStatus> {
        delay(800) // Simulate slight network loading to show clean Material 3 loading states
        return districtsData
    }

    suspend fun getWaterSavingTechniques(): List<WaterSavingTechnique> {
        delay(600) // Simulate network loading
        return techniques
    }

    suspend fun getWaterTips(): List<WaterTip> {
        delay(400) // Simulate network loading
        return tips
    }
}
