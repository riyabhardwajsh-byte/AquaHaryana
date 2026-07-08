package com.example.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

data class DistrictWaterStatus(
    val districtName: String,
    val groundWaterZone: WaterZone,
    val averageWaterTableDepthMeters: Double,
    val criticalBlocksCount: Int,
    val totalBlocksCount: Int,
    val vulnerabilityDescription: String
)

enum class WaterZone(val displayName: String) {
    OVER_EXPLOITED("Over-exploited"),
    CRITICAL("Critical"),
    SEMI_CRITICAL("Semi-critical"),
    SAFE("Safe")
}

@Entity(tableName = "water_saving_techniques")
data class WaterSavingTechnique(
    @PrimaryKey val id: String,
    val title: String,
    val shortDescription: String,
    val detailedDescription: String,
    val steps: List<String>,
    val waterSavedPercent: String,
    val haryanaGovtScheme: String? = null,
    val iconName: String // To map to Material icons dynamically
)

data class WaterTip(
    val id: Int,
    val content: String,
    val source: String? = null
)
