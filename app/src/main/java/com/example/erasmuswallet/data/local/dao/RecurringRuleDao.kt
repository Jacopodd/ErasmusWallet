package com.example.erasmuswallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.erasmuswallet.data.local.entity.RecurringRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringRuleDao {
    @Query("SELECT * FROM recurring_rules ORDER BY isActive DESC, startDate ASC")
    fun observeAll(): Flow<List<RecurringRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RecurringRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<RecurringRuleEntity>)

    @Update
    suspend fun update(rule: RecurringRuleEntity)

    @Query("DELETE FROM recurring_rules WHERE walletId = :walletId")
    suspend fun deleteByWalletId(walletId: Long)

    @Query("DELETE FROM recurring_rules")
    suspend fun deleteAll()
}
