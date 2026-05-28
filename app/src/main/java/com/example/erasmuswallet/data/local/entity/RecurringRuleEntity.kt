@file:UseSerializers(LocalDateSerializer::class, LocalDateTimeSerializer::class)

package com.example.erasmuswallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.erasmuswallet.data.model.IncomeReliability
import com.example.erasmuswallet.data.model.RecurrenceFrequency
import com.example.erasmuswallet.data.model.TransactionType
import com.example.erasmuswallet.data.serialization.LocalDateSerializer
import com.example.erasmuswallet.data.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
@Entity(tableName = "recurring_rules")
data class RecurringRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val type: TransactionType,
    val categoryId: Long? = null,
    val walletId: Long,
    val frequency: RecurrenceFrequency,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val dayOfMonth: Int? = null,
    val dayOfWeek: Int? = null,
    val customIntervalDays: Int? = null,
    val isMandatory: Boolean = false,
    val isActive: Boolean = true,
    val incomeReliability: IncomeReliability? = null,
    val isCancelable: Boolean = true,
    val upfrontCost: Double? = null,
    val minimumCommitmentMonths: Int? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
