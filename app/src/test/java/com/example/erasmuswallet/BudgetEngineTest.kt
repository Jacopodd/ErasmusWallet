package com.example.erasmuswallet

import com.example.erasmuswallet.data.local.entity.CategoryEntity
import com.example.erasmuswallet.data.local.entity.ErasmusSettingsEntity
import com.example.erasmuswallet.data.local.entity.MovementEntity
import com.example.erasmuswallet.data.local.entity.RecurringRuleEntity
import com.example.erasmuswallet.data.local.entity.WalletEntity
import com.example.erasmuswallet.data.model.CategoryGroup
import com.example.erasmuswallet.data.model.IncomeReliability
import com.example.erasmuswallet.data.model.RecurrenceFrequency
import com.example.erasmuswallet.data.model.ScenarioType
import com.example.erasmuswallet.data.model.SimulationKind
import com.example.erasmuswallet.data.model.TransactionType
import com.example.erasmuswallet.data.model.WalletType
import com.example.erasmuswallet.domain.BudgetEngine
import com.example.erasmuswallet.domain.model.BudgetSnapshot
import com.example.erasmuswallet.domain.model.SimulationRequest
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class BudgetEngineTest {
    private val today = LocalDate.of(2026, 1, 15)
    private val now = LocalDateTime.of(2026, 1, 15, 10, 0)
    private val engine = BudgetEngine { today }

    private val wallets = listOf(
        WalletEntity(1, "Intesa", WalletType.CARD, 3000.0, createdAt = now, updatedAt = now),
        WalletEntity(2, "Revolut", WalletType.CARD, 2000.0, createdAt = now, updatedAt = now)
    )

    private val categories = listOf(
        CategoryEntity(1, "Affitto", CategoryGroup.OBBLIGATORIE),
        CategoryEntity(2, "Serate", CategoryGroup.SVAGO),
        CategoryEntity(3, "Svago sessuale", CategoryGroup.SVAGO, isSensitive = true, privacyAlias = "Extra personali")
    )

    @Test
    fun transfer_between_wallets_does_not_change_total_balance() {
        val movements = listOf(
            transferMovement(1, 200.0, "Trasferimento in uscita: giroconto"),
            transferMovement(2, 200.0, "Trasferimento in entrata: giroconto")
        )
        val summary = engine.calculateSummary(snapshot(movements = movements))

        assertThat(summary.totalBalance).isEqualTo(5000.0)
    }

    @Test
    fun daily_budget_is_calculated_correctly() {
        val settings = settings(endDate = LocalDate.of(2026, 1, 25), finalGoal = 1000.0, emergencyFund = 0.0)
        val summary = engine.calculateSummary(snapshot(settings = settings))

        assertThat(summary.dailyBudget).isEqualTo(400.0)
    }

    @Test
    fun prudente_scenario_excludes_estimated_and_uncertain_incomes() {
        val summary = engine.calculateSummary(snapshot(movements = futureIncomeSet()), ScenarioType.PRUDENTE)

        assertThat(summary.finalProjection).isEqualTo(5400.0)
    }

    @Test
    fun realistico_scenario_includes_estimated_but_not_uncertain_incomes() {
        val summary = engine.calculateSummary(snapshot(movements = futureIncomeSet()), ScenarioType.REALISTICO)

        assertThat(summary.finalProjection).isEqualTo(5600.0)
    }

    @Test
    fun ottimistico_scenario_includes_uncertain_incomes() {
        val summary = engine.calculateSummary(snapshot(movements = futureIncomeSet()), ScenarioType.OTTIMISTICO)

        assertThat(summary.finalProjection).isEqualTo(5900.0)
    }

    @Test
    fun monthly_subscription_counts_occurrences_correctly_even_for_short_months() {
        val rule = recurringRule(
            amount = 50.0,
            startDate = LocalDate.of(2026, 1, 31),
            endDate = LocalDate.of(2026, 3, 31)
        )

        val occurrences = engine.generateOccurrences(rule, today, LocalDate.of(2026, 3, 31))

        assertThat(occurrences).containsExactly(
            LocalDate.of(2026, 1, 31),
            LocalDate.of(2026, 2, 28),
            LocalDate.of(2026, 3, 31)
        ).inOrder()
    }

    @Test
    fun single_expense_reduces_final_projection() {
        val result = engine.simulate(
            snapshot(),
            ScenarioType.REALISTICO,
            SimulationRequest(
                kind = SimulationKind.SPESA_SINGOLA,
                amount = 80.0,
                date = today,
                walletId = 1,
                categoryId = 2,
                description = "Serata"
            )
        )

        assertThat(result.newProjection).isEqualTo(result.currentProjection - 80.0)
    }

    @Test
    fun gym_subscription_50_monthly_calculates_total_cost_until_end_of_erasmus() {
        val result = engine.simulate(
            snapshot(settings = settings(endDate = LocalDate.of(2026, 6, 30))),
            ScenarioType.REALISTICO,
            SimulationRequest(
                kind = SimulationKind.ABBONAMENTO,
                amount = 50.0,
                date = today,
                walletId = 1,
                categoryId = 2,
                description = "Palestra",
                frequencyDays = 30,
                recurringEndDate = LocalDate.of(2026, 6, 30)
            )
        )

        assertThat(result.totalCost).isEqualTo(300.0)
    }

    @Test
    fun minimum_liquidity_is_detected() {
        val movements = listOf(
            MovementEntity(
                walletId = 1,
                amount = 4900.0,
                type = TransactionType.EXPENSE,
                categoryId = 1,
                date = LocalDate.of(2026, 1, 20),
                title = "Affitto maxi",
                isPlanned = true,
                isConfirmed = false,
                createdAt = now,
                updatedAt = now
            ),
            MovementEntity(
                walletId = 1,
                amount = 1000.0,
                type = TransactionType.INCOME,
                date = LocalDate.of(2026, 1, 25),
                title = "Borsa",
                isPlanned = true,
                isConfirmed = false,
                incomeReliability = IncomeReliability.CONFERMATA,
                createdAt = now,
                updatedAt = now
            )
        )
        val summary = engine.calculateSummary(snapshot(movements = movements))

        assertThat(summary.minimumLiquidity.balance).isEqualTo(100.0)
        assertThat(summary.minimumLiquidity.date).isEqualTo(LocalDate.of(2026, 1, 20))
    }

    @Test
    fun sensitive_category_uses_alias_in_privacy_mode() {
        val name = engine.displayCategoryName(categories[2], privacyMode = true)

        assertThat(name).isEqualTo("Extra personali")
    }

    private fun futureIncomeSet(): List<MovementEntity> = listOf(
        MovementEntity(
            walletId = 1,
            amount = 500.0,
            type = TransactionType.INCOME,
            date = LocalDate.of(2026, 2, 1),
            title = "Confermata",
            isPlanned = true,
            isConfirmed = false,
            incomeReliability = IncomeReliability.CONFERMATA,
            createdAt = now,
            updatedAt = now
        ),
        MovementEntity(
            walletId = 1,
            amount = 200.0,
            type = TransactionType.INCOME,
            date = LocalDate.of(2026, 2, 10),
            title = "Stimata",
            isPlanned = true,
            isConfirmed = false,
            incomeReliability = IncomeReliability.STIMATA,
            createdAt = now,
            updatedAt = now
        ),
        MovementEntity(
            walletId = 1,
            amount = 300.0,
            type = TransactionType.INCOME,
            date = LocalDate.of(2026, 2, 15),
            title = "Incerta",
            isPlanned = true,
            isConfirmed = false,
            incomeReliability = IncomeReliability.INCERTA,
            createdAt = now,
            updatedAt = now
        ),
        MovementEntity(
            walletId = 1,
            amount = 100.0,
            type = TransactionType.EXPENSE,
            categoryId = 1,
            date = LocalDate.of(2026, 2, 5),
            title = "Affitto",
            isPlanned = true,
            isConfirmed = false,
            createdAt = now,
            updatedAt = now
        )
    )

    private fun transferMovement(walletId: Long, amount: Double, title: String) = MovementEntity(
        walletId = walletId,
        amount = amount,
        type = TransactionType.TRANSFER,
        date = today,
        title = title,
        isConfirmed = true,
        createdAt = now,
        updatedAt = now
    )

    private fun recurringRule(amount: Double, startDate: LocalDate, endDate: LocalDate) = RecurringRuleEntity(
        id = 1,
        name = "Palestra",
        amount = amount,
        type = TransactionType.EXPENSE,
        categoryId = 2,
        walletId = 1,
        frequency = RecurrenceFrequency.MONTHLY,
        startDate = startDate,
        endDate = endDate,
        isMandatory = false,
        createdAt = now,
        updatedAt = now
    )

    private fun settings(
        endDate: LocalDate = LocalDate.of(2026, 6, 30),
        finalGoal: Double = 1000.0,
        emergencyFund: Double = 0.0
    ) = ErasmusSettingsEntity(
        startDate = LocalDate.of(2026, 1, 1),
        endDate = endDate,
        finalGoal = finalGoal,
        emergencyFund = emergencyFund,
        defaultScenario = ScenarioType.REALISTICO
    )

    private fun snapshot(
        movements: List<MovementEntity> = emptyList(),
        settings: ErasmusSettingsEntity = settings(),
        recurringRules: List<RecurringRuleEntity> = emptyList()
    ) = BudgetSnapshot(
        wallets = wallets,
        movements = movements,
        categories = categories,
        recurringRules = recurringRules,
        settings = settings
    )
}
