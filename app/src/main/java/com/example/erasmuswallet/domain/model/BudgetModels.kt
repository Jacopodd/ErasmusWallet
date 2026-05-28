package com.example.erasmuswallet.domain.model

import com.example.erasmuswallet.data.local.entity.CategoryEntity
import com.example.erasmuswallet.data.local.entity.ErasmusSettingsEntity
import com.example.erasmuswallet.data.local.entity.MovementEntity
import com.example.erasmuswallet.data.local.entity.RecurringRuleEntity
import com.example.erasmuswallet.data.local.entity.WalletEntity
import com.example.erasmuswallet.data.model.BudgetStatus
import com.example.erasmuswallet.data.model.ScenarioType
import com.example.erasmuswallet.data.model.SimulationKind
import java.time.LocalDate

data class BudgetSnapshot(
    val wallets: List<WalletEntity>,
    val movements: List<MovementEntity>,
    val categories: List<CategoryEntity>,
    val recurringRules: List<RecurringRuleEntity>,
    val settings: ErasmusSettingsEntity
)

data class WalletSummary(
    val wallet: WalletEntity,
    val currentBalance: Double,
    val percentageOnTotal: Double,
    val lastMovements: List<MovementEntity>
)

data class BudgetSummary(
    val totalBalance: Double,
    val protectedReserve: Double,
    val spendableMoney: Double,
    val daysRemaining: Long,
    val dailyBudget: Double,
    val weeklyBudget: Double,
    val spentThisWeek: Double,
    val remainingThisWeek: Double,
    val finalProjection: Double,
    val status: BudgetStatus,
    val statusMargin: Double,
    val upcomingExpenses: List<FutureEvent>,
    val upcomingIncomes: List<FutureEvent>,
    val walletSummaries: List<WalletSummary>,
    val minimumLiquidity: LiquidityPoint,
    val scenario: ScenarioType
)

data class FutureEvent(
    val date: LocalDate,
    val amount: Double,
    val title: String,
    val mandatory: Boolean,
    val walletId: Long? = null
)

data class LiquidityPoint(
    val date: LocalDate,
    val balance: Double
)

data class CategorySpend(
    val categoryName: String,
    val amount: Double
)

data class ReportSummary(
    val weeklyExpense: Double,
    val monthlyExpense: Double,
    val byCategory: List<CategorySpend>,
    val balanceTrend: List<LiquidityPoint>,
    val projectedFinal: Double
)

data class SimulationRequest(
    val kind: SimulationKind,
    val amount: Double,
    val date: LocalDate,
    val categoryId: Long? = null,
    val walletId: Long? = null,
    val description: String = "",
    val mandatory: Boolean = false,
    val frequencyDays: Int? = null,
    val recurringEndDate: LocalDate? = null,
    val installments: Int? = null,
    val upfrontCost: Double? = null,
    val minimumCommitmentMonths: Int? = null
)

data class SimulationResult(
    val status: BudgetStatus,
    val totalCost: Double,
    val currentProjection: Double,
    val newProjection: Double,
    val finalGoal: Double,
    val differenceFromGoal: Double,
    val currentDailyBudget: Double,
    val newDailyBudget: Double,
    val currentWeeklyBudget: Double,
    val newWeeklyBudget: Double,
    val minimumLiquidity: LiquidityPoint,
    val maximumSustainableAmount: Double,
    val reductionNeeded: Double,
    val extraIncomeNeeded: Double,
    val generatedEvents: List<FutureEvent>
)
