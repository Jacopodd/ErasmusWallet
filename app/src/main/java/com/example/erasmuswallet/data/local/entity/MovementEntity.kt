@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package com.example.erasmuswallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.erasmuswallet.data.model.IncomeReliability
import com.example.erasmuswallet.data.model.TransactionType
import com.example.erasmuswallet.data.serialization.LocalDateSerializer
import com.example.erasmuswallet.data.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
@Entity(tableName = "movements")
data class MovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val walletId: Long,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long? = null,
    val date: LocalDate,
    val title: String,
    val notes: String? = null,
    val isPlanned: Boolean = false,
    val isConfirmed: Boolean = true,
    val recurringRuleId: Long? = null,
    val transferGroupId: String? = null,
    val incomeReliability: IncomeReliability? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
