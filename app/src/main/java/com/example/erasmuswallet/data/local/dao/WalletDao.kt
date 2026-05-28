package com.example.erasmuswallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.erasmuswallet.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets ORDER BY isArchived ASC, name ASC")
    fun observeAll(): Flow<List<WalletEntity>>

    @Query("SELECT COUNT(*) FROM wallets")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: WalletEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(wallets: List<WalletEntity>)

    @Update
    suspend fun update(wallet: WalletEntity)

    @Query("DELETE FROM wallets WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM wallets")
    suspend fun deleteAll()
}
