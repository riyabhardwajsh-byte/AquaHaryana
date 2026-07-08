package com.example.data

import kotlinx.coroutines.flow.Flow

class WaterLogRepository(
    private val waterLogDao: WaterLogDao,
    private val favoriteTechniqueDao: FavoriteTechniqueDao,
    private val waterSavingTechniqueDao: WaterSavingTechniqueDao
) {
    val allLogs: Flow<List<WaterLog>> = waterLogDao.getAllLogs()

    fun getLogsForDate(date: String): Flow<List<WaterLog>> = waterLogDao.getLogsForDate(date)

    suspend fun insertLog(log: WaterLog) = waterLogDao.insertLog(log)

    suspend fun deleteLogById(id: Int) = waterLogDao.deleteLogById(id)

    // Favorites
    val favoriteTechniqueIds: Flow<List<String>> = favoriteTechniqueDao.getFavoriteTechniqueIds()

    suspend fun insertFavorite(techniqueId: String) {
        favoriteTechniqueDao.insertFavorite(FavoriteTechnique(techniqueId))
    }

    suspend fun deleteFavorite(techniqueId: String) {
        favoriteTechniqueDao.deleteFavorite(techniqueId)
    }

    // Cached Techniques
    val cachedTechniques: Flow<List<WaterSavingTechnique>> = waterSavingTechniqueDao.getAllTechniques()

    suspend fun insertTechniques(techniques: List<WaterSavingTechnique>) {
        waterSavingTechniqueDao.insertTechniques(techniques)
    }

    suspend fun clearAllTechniques() {
        waterSavingTechniqueDao.deleteAllTechniques()
    }
}
