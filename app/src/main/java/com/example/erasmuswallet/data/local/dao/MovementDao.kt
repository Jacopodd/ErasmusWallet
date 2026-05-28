package com.example.erasmuswallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.erasmuswallet.data.local.entity.MovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {
    @Query("SELECT * FROM movements ORDER BY date DESC, createdAt DESC")
    fun observeAll(): Flow<List<MovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movement: MovementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movements: List<MovementEntity>)

    @Update
    suspend fun update(movement: MovementEntity)

    @Query("DELETE FROM movements WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM movements WHERE transferGroupId = :groupId")
    suspend fun deleteByTransferGroup(groupId: String)

    @Query("DELETE FROM movements")
    suspend fun deleteAll()
}
