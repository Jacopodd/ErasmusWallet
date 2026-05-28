package com.example.erasmuswallet.domain

import com.example.erasmuswallet.data.local.entity.CategoryEntity
import com.example.erasmuswallet.data.local.entity.MovementEntity
import com.example.erasmuswallet.data.local.entity.RecurringRuleEntity
import com.example.erasmuswallet.data.model.BudgetStatus
import com.example.erasmuswallet.data.model.IncomeReliability
import com.example.erasmuswallet.data.model.RecurrenceFrequency
import com.example.erasmuswallet.data.model.ScenarioType
import com.example.erasmuswallet.data.model.SimulationKind
import com.example.erasmuswallet.data.model.TransactionType
import com.example.erasmuswallet.domain.model.BudgetSnapshot
import com.example.erasmuswallet.domain.model.BudgetSummary
import com.example.erasmuswallet.domain.model.CategorySpend
import com.example.erasmuswallet.domain.model.FutureEvent
import com.example.erasmuswallet.domain.model.LiquidityPoint
import com.example.erasmuswallet.domain.model.ReportSummary
import com.example.erasmuswallet.domain.model.SimulationRequest
import com.example.erasmuswallet.domain.model.SimulationResult
import com.example.erasmuswallet.domain.model.WalletSummary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.max

class BudgetEngine(
    private val todayProvider: () -> LocalDate = { LocalDate.now() }
) {
    fun calculateSummary(
        snapshot: BudgetSnapshot,
        scenario: ScenarioType = snapshot.settings.defaultScenario
    ): BudgetSummary {
        val today = todayProvider()
        val endDate = snapshot.settings.endDate
        val totalBalance = calculateCurrentTotalBalance(snapshot.movements, snapshot.wallets, today)
        val protectedReserve = snapshot.settings.finalGoal + snapshot.settings.emergencyFund
        val recurringEvents = generateRecurringEvents(snapshot.recurringRules, today, endDate, scenario)
        val futureMovements = futureMovementEvents(snapshot.movements, today, scenario)
        val upcomingExpenses = (futureMovements.filter { it.amount < 0 } + recurringEvents.filter { it.amount < 0 })
            .sortedBy { it.date }
        val upcomingIncomes = (futureMovements.filter { it.amount > 0 } + recurringEvents.filter { it.amount > 0 })
            .sortedBy { it.date }
        val futureFixedIncome = recurringEvents.filter { it.amount > 0 }.sumOf { it.amount }
        val futureFixedExpense = recurringEvents.filter { it.amount < 0 }.sumOf { -it.amount }
        val futureIncome = upcomingIncomes.sumOf { it.amount }
        val futureMandatoryExpense = upcomingExpenses.filter { it.mandatory }.sumOf { -it.amount }
        val futurePlannedExpense = upcomingExpenses.filterNot { it.mandatory }.sumOf { -it.amount }
        val spendableMoney = totalBalance + futureFixedIncome - futureFixedExpense - protectedReserve
        val daysRemaining = max(1, ChronoUnit.DAYS.between(today, endDate).coerceAtLeast(1))
        val dailyBudget = spendableMoney / daysRemaining
        val weeklyBudget = dailyBudget * 7
        val spentThisWeek = currentWeekExpense(snapshot.movements, today)
        val remainingThisWeek = weeklyBudget - spentThisWeek
        val finalProjection = totalBalance + futureIncome - futureMandatoryExpense - futurePlannedExpense
        val minimumLiquidity = minimumLiquidity(
            startBalance = totalBalance,
            events = (upcomingExpenses + upcomingIncomes).sortedBy { it.date },
            startDate = today
        )
        val statusMargin = finalProjection - protectedReserve
        val status = classifyStatus(
            margin = statusMargin,
            minimumBalance = minimumLiquidity.balance,
            safeThreshold = snapshot.settings.safeThreshold,
            minimumLiquidityThreshold = snapshot.settings.minimumLiquidityThreshold
        )
        val walletBalances = snapshot.wallets.map { wallet ->
            val balance = currentWalletBalance(wallet.id, snapshot.wallets, snapshot.movements, today)
            WalletSummary(
                wallet = wallet,
                currentBalance = balance,
                percentageOnTotal = if (totalBalance == 0.0) 0.0 else balance / totalBalance,
                lastMovements = snapshot.movements.filter { it.walletId == wallet.id }.take(3)
            )
        }
        return BudgetSummary(
            totalBalance = totalBalance,
            protectedReserve = protectedReserve,
            spendableMoney = spendableMoney,
            futureFixedIncome = futureFixedIncome,
            futureFixedExpense = futureFixedExpense,
            daysRemaining = daysRemaining,
            dailyBudget = dailyBudget,
            weeklyBudget = weeklyBudget,
            spentThisWeek = spentThisWeek,
            remainingThisWeek = remainingThisWeek,
            finalProjection = finalProjection,
            status = status,
            statusMargin = statusMargin,
            upcomingExpenses = upcomingExpenses.take(8),
            upcomingIncomes = upcomingIncomes.take(8),
            walletSummaries = walletBalances,
            minimumLiquidity = minimumLiquidity,
            scenario = scenario
        )
    }

    fun calculateReport(snapshot: BudgetSnapshot, scenario: ScenarioType): ReportSummary {
        val today = todayProvider()
        val summary = calculateSummary(snapshot, scenario)
        val currentMonth = YearMonth.from(today)
        val weeklyExpense = currentWeekExpense(snapshot.movements, today)
        val monthlyExpense = snapshot.movements
            .filter { it.isConfirmed && it.type == TransactionType.EXPENSE && YearMonth.from(it.date) == currentMonth }
            .sumOf { it.amount }
        val byCategory = snapshot.movements
            .filter { it.isConfirmed && it.type == TransactionType.EXPENSE && YearMonth.from(it.date) == currentMonth }
            .groupBy { it.categoryId }
            .map { (categoryId, items) ->
                val category = snapshot.categories.firstOrNull { it.id == categoryId }
                CategorySpend(
                    categoryName = displayCategoryName(category, snapshot.settings.privacyMode),
                    amount = items.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.amount }
        val trend = buildBalanceTrend(snapshot, today)
        return ReportSummary(
            weeklyExpense = weeklyExpense,
            monthlyExpense = monthlyExpense,
            byCategory = byCategory,
            balanceTrend = trend,
            projectedFinal = summary.finalProjection
        )
    }

    fun simulate(
        snapshot: BudgetSnapshot,
        scenario: ScenarioType,
        request: SimulationRequest
    ): SimulationResult {
        val summary = calculateSummary(snapshot, scenario)
        val simulatedEvents = buildSimulationEvents(snapshot, request)
        val totalCost = simulatedEvents.filter { it.amount < 0 }.sumOf { -it.amount }
        val newProjection = summary.finalProjection - totalCost
        val differenceFromGoal = newProjection - snapshot.settings.finalGoal
        val newSpendable = summary.spendableMoney - totalCost
        val newDailyBudget = newSpendable / summary.daysRemaining
        val newWeeklyBudget = newDailyBudget * 7
        val liquidity = minimumLiquidity(
            startBalance = summary.totalBalance,
            events = (summary.upcomingExpenses + summary.upcomingIncomes + simulatedEvents).sortedBy { it.date },
            startDate = todayProvider()
        )
        val status = classifyStatus(
            margin = newProjection - (snapshot.settings.finalGoal + snapshot.settings.emergencyFund),
            minimumBalance = liquidity.balance,
            safeThreshold = snapshot.settings.safeThreshold,
            minimumLiquidityThreshold = snapshot.settings.minimumLiquidityThreshold
        )
        val maxSustainable = max(0.0, summary.spendableMoney)
        val reductionNeeded = max(0.0, totalCost - summary.spendableMoney)
        return SimulationResult(
            status = status,
            totalCost = totalCost,
            currentProjection = summary.finalProjection,
            newProjection = newProjection,
            finalGoal = snapshot.settings.finalGoal,
            differenceFromGoal = differenceFromGoal,
            currentDailyBudget = summary.dailyBudget,
            newDailyBudget = newDailyBudget,
            currentWeeklyBudget = summary.weeklyBudget,
            newWeeklyBudget = newWeeklyBudget,
            minimumLiquidity = liquidity,
            maximumSustainableAmount = when (request.kind) {
                SimulationKind.ABBONAMENTO -> sustainableSubscription(summary, snapshot, request)
                else -> maxSustainable
            },
            reductionNeeded = reductionNeeded,
            extraIncomeNeeded = reductionNeeded,
            generatedEvents = simulatedEvents
        )
    }

    fun generateRecurringEvents(
        rules: List<RecurringRuleEntity>,
        from: LocalDate,
        to: LocalDate,
        scenario: ScenarioType
    ): List<FutureEvent> = rules
        .filter { it.isActive }
        .flatMap { rule ->
            if (rule.type == TransactionType.INCOME && !includeIncome(rule.incomeReliability, scenario)) {
                emptyList()
            } else {
                generateOccurrences(rule, from, to).map {
                    FutureEvent(
                        date = it,
                        amount = signedAmount(rule.type, rule.amount),
                        title = rule.name,
                        mandatory = rule.isMandatory,
                        walletId = rule.walletId
                    )
                } + listOfNotNull(
                    rule.upfrontCost?.takeIf { it > 0 }?.let {
                        FutureEvent(
                            date = rule.startDate,
                            amount = -it,
                            title = "${rule.name} - costo iniziale",
                            mandatory = rule.isMandatory,
                            walletId = rule.walletId
                        )
                    }
                )
            }
        }

    fun generateOccurrences(
        rule: RecurringRuleEntity,
        from: LocalDate,
        to: LocalDate
    ): List<LocalDate> {
        val effectiveEnd = minOf(to, rule.endDate ?: to)
        if (effectiveEnd < from) return emptyList()
        val start = maxOf(from, rule.startDate)
        val occurrences = mutableListOf<LocalDate>()
        var cursor = rule.startDate
        while (cursor <= effectiveEnd) {
            if (cursor >= start) occurrences += cursor
            cursor = when (rule.frequency) {
                RecurrenceFrequency.DAILY -> cursor.plusDays(1)
                RecurrenceFrequency.WEEKLY -> cursor.plusWeeks(1)
                RecurrenceFrequency.MONTHLY -> nextMonthlyDate(cursor, rule.dayOfMonth ?: rule.startDate.dayOfMonth)
                RecurrenceFrequency.YEARLY -> cursor.plusYears(1)
                RecurrenceFrequency.CUSTOM -> cursor.plusDays((rule.customIntervalDays ?: 30).toLong())
            }
        }
        val minimumCommitment = rule.minimumCommitmentMonths
        if (minimumCommitment != null && rule.frequency == RecurrenceFrequency.MONTHLY) {
            val commitmentEnd = rule.startDate.plusMonths((minimumCommitment - 1).toLong())
            var commitmentCursor = rule.startDate
            while (commitmentCursor <= commitmentEnd && commitmentCursor <= effectiveEnd) {
                if (commitmentCursor >= start && commitmentCursor !in occurrences) occurrences += commitmentCursor
                commitmentCursor = nextMonthlyDate(commitmentCursor, rule.dayOfMonth ?: rule.startDate.dayOfMonth)
            }
        }
        return occurrences.sorted()
    }

    fun displayCategoryName(category: CategoryEntity?, privacyMode: Boolean): String {
        if (category == null) return "Senza categoria"
        return if (privacyMode && category.isSensitive) category.privacyAlias ?: category.name else category.name
    }

    private fun buildSimulationEvents(
        snapshot: BudgetSnapshot,
        request: SimulationRequest
    ): List<FutureEvent> {
        return when (request.kind) {
            SimulationKind.SPESA_SINGOLA -> listOf(
                FutureEvent(request.date, -request.amount, request.description.ifBlank { "Nuova spesa" }, request.mandatory, request.walletId)
            )
            SimulationKind.ABBONAMENTO -> {
                val endDate = request.recurringEndDate ?: snapshot.settings.endDate
                val pseudoRule = RecurringRuleEntity(
                    name = request.description.ifBlank { "Nuovo abbonamento" },
                    amount = request.amount,
                    type = TransactionType.EXPENSE,
                    categoryId = request.categoryId,
                    walletId = request.walletId ?: snapshot.wallets.firstOrNull()?.id ?: 0L,
                    frequency = frequencyFromDays(request.frequencyDays),
                    startDate = request.date,
                    endDate = endDate,
                    customIntervalDays = request.frequencyDays,
                    isMandatory = request.mandatory,
                    upfrontCost = request.upfrontCost,
                    minimumCommitmentMonths = request.minimumCommitmentMonths,
                    createdAt = java.time.LocalDateTime.now(),
                    updatedAt = java.time.LocalDateTime.now()
                )
                generateRecurringEvents(listOf(pseudoRule), todayProvider(), snapshot.settings.endDate, snapshot.settings.defaultScenario)
            }
            SimulationKind.RATE -> {
                val stepDays = request.frequencyDays?.toLong() ?: 30L
                (0 until (request.installments ?: 1)).map { index ->
                    FutureEvent(
                        date = request.date.plusDays(index * stepDays),
                        amount = -request.amount,
                        title = request.description.ifBlank { "Pagamento a rate" },
                        mandatory = request.mandatory,
                        walletId = request.walletId
                    )
                }
            }
            SimulationKind.MASSIMO_SOSTENIBILE -> emptyList()
        }
    }

    private fun sustainableSubscription(
        summary: BudgetSummary,
        snapshot: BudgetSnapshot,
        request: SimulationRequest
    ): Double {
        val endDate = request.recurringEndDate ?: snapshot.settings.endDate
        val occurrences = countOccurrences(
            request.date,
            endDate,
            frequencyFromDays(request.frequencyDays),
            request.frequencyDays
        )
        if (occurrences == 0) return 0.0
        val fixedPart = request.upfrontCost ?: 0.0
        return max(0.0, (summary.spendableMoney - fixedPart) / occurrences)
    }

    private fun countOccurrences(
        startDate: LocalDate,
        endDate: LocalDate,
        frequency: RecurrenceFrequency,
        customDays: Int?
    ): Int {
        var count = 0
        var cursor = startDate
        while (cursor <= endDate) {
            count++
            cursor = when (frequency) {
                RecurrenceFrequency.DAILY -> cursor.plusDays(1)
                RecurrenceFrequency.WEEKLY -> cursor.plusWeeks(1)
                RecurrenceFrequency.MONTHLY -> nextMonthlyDate(cursor, startDate.dayOfMonth)
                RecurrenceFrequency.YEARLY -> cursor.plusYears(1)
                RecurrenceFrequency.CUSTOM -> cursor.plusDays((customDays ?: 30).toLong())
            }
        }
        return count
    }

    private fun frequencyFromDays(days: Int?): RecurrenceFrequency = when (days) {
        null, 30 -> RecurrenceFrequency.MONTHLY
        7 -> RecurrenceFrequency.WEEKLY
        1 -> RecurrenceFrequency.DAILY
        365 -> RecurrenceFrequency.YEARLY
        else -> RecurrenceFrequency.CUSTOM
    }

    private fun futureMovementEvents(
        movements: List<MovementEntity>,
        today: LocalDate,
        scenario: ScenarioType
    ): List<FutureEvent> = movements
        .filter { it.date >= today && (it.isPlanned || !it.isConfirmed || it.date > today) }
        .filter {
            when (it.type) {
                TransactionType.INCOME -> includeIncome(it.incomeReliability, scenario)
                TransactionType.TRANSFER -> false
                TransactionType.EXPENSE -> true
            }
        }
        .map {
            FutureEvent(
                date = it.date,
                amount = signedAmount(it.type, it.amount),
                title = it.title,
                mandatory = isMandatoryMovement(it),
                walletId = it.walletId
            )
        }

    private fun isMandatoryMovement(movement: MovementEntity): Boolean {
        val title = movement.title.lowercase()
        return listOf("affitto", "bollette", "trasporti", "abbonamento", "assicurazione", "telefono").any { title.contains(it) }
    }

    fun currentWalletBalance(
        walletId: Long,
        wallets: List<com.example.erasmuswallet.data.local.entity.WalletEntity>,
        movements: List<MovementEntity>,
        today: LocalDate
    ): Double {
        val wallet = wallets.firstOrNull { it.id == walletId } ?: return 0.0
        return wallet.initialBalance + movements.filter { it.walletId == walletId && it.isConfirmed && it.date <= today }
            .sumOf { movementDelta(it) }
    }

    private fun calculateCurrentTotalBalance(
        movements: List<MovementEntity>,
        wallets: List<com.example.erasmuswallet.data.local.entity.WalletEntity>,
        today: LocalDate
    ): Double = wallets.filterNot { it.isArchived }.sumOf { currentWalletBalance(it.id, wallets, movements, today) }

    private fun currentWeekExpense(movements: List<MovementEntity>, today: LocalDate): Double {
        val weekStart = today.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(6)
        return movements.filter {
            it.isConfirmed && it.type == TransactionType.EXPENSE && it.date in weekStart..weekEnd
        }.sumOf { it.amount }
    }

    private fun buildBalanceTrend(snapshot: BudgetSnapshot, today: LocalDate): List<LiquidityPoint> {
        val currentBalance = calculateCurrentTotalBalance(snapshot.movements, snapshot.wallets, today)
        val futureEvents = (futureMovementEvents(snapshot.movements, today, snapshot.settings.defaultScenario) +
            generateRecurringEvents(snapshot.recurringRules, today, snapshot.settings.endDate, snapshot.settings.defaultScenario))
            .sortedBy { it.date }
        val points = mutableListOf(LiquidityPoint(today, currentBalance))
        var balance = currentBalance
        futureEvents.take(12).forEach {
            balance += it.amount
            points += LiquidityPoint(it.date, balance)
        }
        return points
    }

    fun minimumLiquidity(
        startBalance: Double,
        events: List<FutureEvent>,
        startDate: LocalDate
    ): LiquidityPoint {
        var minPoint = LiquidityPoint(startDate, startBalance)
        var running = startBalance
        events.forEach { event ->
            running += event.amount
            if (running < minPoint.balance) minPoint = LiquidityPoint(event.date, running)
        }
        return minPoint
    }

    fun classifyStatus(
        margin: Double,
        minimumBalance: Double,
        safeThreshold: Double,
        minimumLiquidityThreshold: Double
    ): BudgetStatus = when {
        margin < 0 -> BudgetStatus.NON_SOSTENIBILE
        margin >= safeThreshold && minimumBalance >= minimumLiquidityThreshold -> BudgetStatus.SICURO
        minimumBalance < minimumLiquidityThreshold -> BudgetStatus.RISCHIOSO
        margin < safeThreshold -> BudgetStatus.AL_LIMITE
        else -> BudgetStatus.SOSTENIBILE
    }

    private fun includeIncome(reliability: IncomeReliability?, scenario: ScenarioType): Boolean = when (scenario) {
        ScenarioType.PRUDENTE -> reliability == IncomeReliability.CONFERMATA || reliability == IncomeReliability.RICEVUTA
        ScenarioType.REALISTICO -> reliability == IncomeReliability.CONFERMATA || reliability == IncomeReliability.RICEVUTA || reliability == IncomeReliability.STIMATA
        ScenarioType.OTTIMISTICO -> reliability != null
    }

    private fun signedAmount(type: TransactionType, amount: Double): Double = when (type) {
        TransactionType.INCOME -> amount
        TransactionType.EXPENSE -> -amount
        TransactionType.TRANSFER -> 0.0
    }

    private fun movementDelta(movement: MovementEntity): Double = when (movement.type) {
        TransactionType.INCOME -> movement.amount
        TransactionType.EXPENSE -> -movement.amount
        TransactionType.TRANSFER -> {
            val title = movement.title.lowercase()
            if (title.contains("uscita")) -movement.amount else movement.amount
        }
    }

    private fun nextMonthlyDate(date: LocalDate, targetDay: Int): LocalDate {
        val nextMonth = date.plusMonths(1)
        val lastDay = YearMonth.from(nextMonth).lengthOfMonth()
        return nextMonth.withDayOfMonth(minOf(targetDay, lastDay))
    }
}
