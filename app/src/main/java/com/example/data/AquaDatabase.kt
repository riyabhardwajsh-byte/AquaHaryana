package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString("||")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split("||")
    }
}

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // format "yyyy-MM-dd"
    val amountLiters: Int,
    val activity: String, // e.g. "Drinking", "Bathing", "Irrigation", "Cooking", "Other"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_techniques")
data class FavoriteTechnique(
    @PrimaryKey val techniqueId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getLogsForDate(date: String): Flow<List<WaterLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WaterLog)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteLogById(id: Int)
}

@Dao
interface FavoriteTechniqueDao {
    @Query("SELECT techniqueId FROM favorite_techniques")
    fun getFavoriteTechniqueIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteTechnique)

    @Query("DELETE FROM favorite_techniques WHERE techniqueId = :techniqueId")
    suspend fun deleteFavorite(techniqueId: String)
}

@Dao
interface WaterSavingTechniqueDao {
    @Query("SELECT * FROM water_saving_techniques")
    fun getAllTechniques(): Flow<List<WaterSavingTechnique>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTechniques(techniques: List<WaterSavingTechnique>)

    @Query("DELETE FROM water_saving_techniques")
    suspend fun deleteAllTechniques()
}

@Database(entities = [WaterLog::class, FavoriteTechnique::class, WaterSavingTechnique::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AquaDatabase : RoomDatabase() {
    abstract fun waterLogDao(): WaterLogDao
    abstract fun favoriteTechniqueDao(): FavoriteTechniqueDao
    abstract fun waterSavingTechniqueDao(): WaterSavingTechniqueDao

    companion object {
        @Volatile
        private var INSTANCE: AquaDatabase? = null

        fun getDatabase(context: Context): AquaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AquaDatabase::class.java,
                    "aqua_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
