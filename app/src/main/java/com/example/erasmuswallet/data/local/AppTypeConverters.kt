package com.example.erasmuswallet.data.local

import androidx.room.TypeConverter
import com.example.erasmuswallet.data.model.BudgetStatus
import com.example.erasmuswallet.data.model.CategoryGroup
import com.example.erasmuswallet.data.model.IncomeReliability
import com.example.erasmuswallet.data.model.RecurrenceFrequency
import com.example.erasmuswallet.data.model.ScenarioType
import com.example.erasmuswallet.data.model.SimulationKind
import com.example.erasmuswallet.data.model.TransactionType
import com.example.erasmuswallet.data.model.WalletType
import java.time.LocalDate
import java.time.LocalDateTime

class AppTypeConverters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let(LocalDateTime::parse)

    @TypeConverter
    fun fromWalletType(value: WalletType?): String? = value?.name

    @TypeConverter
    fun toWalletType(value: String?): WalletType? = value?.let(WalletType::valueOf)

    @TypeConverter
    fun fromTransactionType(value: TransactionType?): String? = value?.name

    @TypeConverter
    fun toTransactionType(value: String?): TransactionType? = value?.let(TransactionType::valueOf)

    @TypeConverter
    fun fromCategoryGroup(value: CategoryGroup?): String? = value?.name

    @TypeConverter
    fun toCategoryGroup(value: String?): CategoryGroup? = value?.let(CategoryGroup::valueOf)

    @TypeConverter
    fun fromIncomeReliability(value: IncomeReliability?): String? = value?.name

    @TypeConverter
    fun toIncomeReliability(value: String?): IncomeReliability? = value?.let(IncomeReliability::valueOf)

    @TypeConverter
    fun fromRecurrenceFrequency(value: RecurrenceFrequency?): String? = value?.name

    @TypeConverter
    fun toRecurrenceFrequency(value: String?): RecurrenceFrequency? = value?.let(RecurrenceFrequency::valueOf)

    @TypeConverter
    fun fromScenarioType(value: ScenarioType?): String? = value?.name

    @TypeConverter
    fun toScenarioType(value: String?): ScenarioType? = value?.let(ScenarioType::valueOf)

    @TypeConverter
    fun fromBudgetStatus(value: BudgetStatus?): String? = value?.name

    @TypeConverter
    fun toBudgetStatus(value: String?): BudgetStatus? = value?.let(BudgetStatus::valueOf)

    @TypeConverter
    fun fromSimulationKind(value: SimulationKind?): String? = value?.name

    @TypeConverter
    fun toSimulationKind(value: String?): SimulationKind? = value?.let(SimulationKind::valueOf)
}
