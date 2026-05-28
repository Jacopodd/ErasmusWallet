package com.example.erasmuswallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.erasmuswallet.data.local.entity.ErasmusSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM erasmus_settings WHERE id = 1")
    fun observe(): Flow<ErasmusSettingsEntity?>

    @Query("SELECT * FROM erasmus_settings WHERE id = 1")
    suspend fun get(): ErasmusSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: ErasmusSettingsEntity)

    @Query("DELETE FROM erasmus_settings")
    suspend fun deleteAll()
}
