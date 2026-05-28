package com.example.erasmuswallet.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.erasmuswallet.data.local.entity.CategoryEntity
import com.example.erasmuswallet.data.local.entity.ErasmusSettingsEntity
import com.example.erasmuswallet.data.local.entity.MovementEntity
import com.example.erasmuswallet.data.local.entity.RecurringRuleEntity
import com.example.erasmuswallet.data.local.entity.WalletEntity
import com.example.erasmuswallet.data.model.BudgetStatus
import com.example.erasmuswallet.data.model.CategoryGroup
import com.example.erasmuswallet.data.model.IncomeReliability
import com.example.erasmuswallet.data.model.RecurrenceFrequency
import com.example.erasmuswallet.data.model.ScenarioType
import com.example.erasmuswallet.data.model.SimulationKind
import com.example.erasmuswallet.data.model.TransactionType
import com.example.erasmuswallet.data.model.WalletType
import com.example.erasmuswallet.data.repository.AppRepository
import com.example.erasmuswallet.domain.BudgetEngine
import com.example.erasmuswallet.domain.model.BudgetSnapshot
import com.example.erasmuswallet.domain.model.BudgetSummary
import com.example.erasmuswallet.domain.model.ReportSummary
import com.example.erasmuswallet.domain.model.SimulationRequest
import com.example.erasmuswallet.domain.model.SimulationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

data class AppUiState(
    val loading: Boolean = true,
    val onboardingNeeded: Boolean = true,
    val wallets: List<WalletEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val movements: List<MovementEntity> = emptyList(),
    val recurringRules: List<RecurringRuleEntity> = emptyList(),
    val settings: ErasmusSettingsEntity? = null,
    val selectedScenario: ScenarioType = ScenarioType.REALISTICO,
    val summary: BudgetSummary? = null,
    val report: ReportSummary? = null,
    val simulationResult: SimulationResult? = null,
    val lastSimulationRequest: SimulationRequest? = null,
    val savedScenarios: List<String> = emptyList(),
    val backupText: String = ""
)

class AppViewModel(
    private val repository: AppRepository,
    private val budgetEngine: BudgetEngine = BudgetEngine()
) : ViewModel() {
    private val selectedScenario = MutableStateFlow<ScenarioType?>(null)
    private val simulationResult = MutableStateFlow<SimulationResult?>(null)
    private val lastSimulationRequest = MutableStateFlow<SimulationRequest?>(null)
    private val backupText = MutableStateFlow("")
    private val savedScenarios = MutableStateFlow<List<String>>(emptyList())
    val messages = MutableSharedFlow<String>()

    init {
        viewModelScope.launch {
            repository.seedDefaultsIfNeeded()
        }
    }

    private val baseSnapshot = combine(
        repository.wallets,
        repository.categories,
        repository.movements,
        repository.recurringRules,
        repository.settings
    ) { wallets, categories, movements, rules, settings ->
        SnapshotBundle(wallets, categories, movements, rules, settings)
    }

    private val uiExtras = combine(
        lastSimulationRequest,
        savedScenarios,
        backupText
    ) { request, scenarios, backup ->
        UiExtras(request, scenarios, backup)
    }

    val uiState: StateFlow<AppUiState> = combine(
        baseSnapshot,
        selectedScenario,
        simulationResult,
        uiExtras
    ) { base, scenarioOverride, simulation, extras ->
        val wallets = base.wallets
        val categories = base.categories
        val movements = base.movements
        val rules = base.rules
        val settings = base.settings
        if (settings == null) {
            AppUiState(
                loading = false,
                onboardingNeeded = true,
                wallets = wallets,
                categories = categories,
                movements = movements,
                recurringRules = rules,
                backupText = extras.backup
            )
        } else {
            val scenario = scenarioOverride ?: settings.defaultScenario
            val snapshot = BudgetSnapshot(wallets, movements, categories, rules, settings)
            AppUiState(
                loading = false,
                onboardingNeeded = false,
                wallets = wallets,
                categories = categories,
                movements = movements,
                recurringRules = rules,
                settings = settings,
                selectedScenario = scenario,
                summary = budgetEngine.calculateSummary(snapshot, scenario),
                report = budgetEngine.calculateReport(snapshot, scenario),
                simulationResult = simulation,
                lastSimulationRequest = extras.request,
                savedScenarios = extras.scenarios,
                backupText = extras.backup
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

    fun setScenario(scenario: ScenarioType) {
        selectedScenario.value = scenario
    }

    fun completeOnboarding(
        startDate: LocalDate,
        endDate: LocalDate,
        finalGoal: Double,
        emergencyFund: Double,
        walletBalances: Map<Long, Double>,
        plannedIncomes: List<OnboardingIncomeInput>,
        fixedExpenses: List<OnboardingRecurringInput>
    ) {
        viewModelScope.launch {
            val settings = ErasmusSettingsEntity(
                startDate = startDate,
                endDate = endDate,
                finalGoal = finalGoal,
                emergencyFund = emergencyFund
            )
            val incomeMovements = plannedIncomes.map {
                AppRepository.plannedIncome(it.walletId, it.amount, it.title, it.reliability, it.date)
            }
            val now = LocalDateTime.now()
            val recurring = fixedExpenses.map {
                RecurringRuleEntity(
                    name = it.name,
                    amount = it.amount,
                    type = TransactionType.EXPENSE,
                    categoryId = it.categoryId,
                    walletId = it.walletId,
                    frequency = RecurrenceFrequency.MONTHLY,
                    startDate = it.startDate,
                    endDate = endDate,
                    isMandatory = true,
                    createdAt = now,
                    updatedAt = now
                )
            }
            repository.saveOnboarding(settings, walletBalances, incomeMovements, recurring)
            messages.emit("Onboarding completato")
        }
    }

    fun saveWallet(id: Long, name: String, type: WalletType, initialBalance: Double, colorHex: String?) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            repository.upsertWallet(
                WalletEntity(
                    id = id,
                    name = name,
                    type = type,
                    initialBalance = initialBalance,
                    colorHex = colorHex,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun toggleWalletArchive(wallet: WalletEntity) {
        viewModelScope.launch {
            repository.archiveWallet(wallet, !wallet.isArchived)
        }
    }

    fun saveCategory(
        id: Long,
        name: String,
        group: CategoryGroup,
        sensitive: Boolean,
        alias: String?
    ) {
        viewModelScope.launch {
            repository.upsertCategory(
                CategoryEntity(
                    id = id,
                    name = name,
                    group = group,
                    isSensitive = sensitive,
                    privacyAlias = alias,
                    isActive = true
                )
            )
        }
    }

    fun toggleCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.archiveCategory(category, !category.isActive)
        }
    }

    fun saveMovement(
        id: Long = 0,
        walletId: Long,
        amount: Double,
        type: TransactionType,
        categoryId: Long?,
        date: LocalDate,
        title: String,
        notes: String?,
        planned: Boolean,
        confirmed: Boolean,
        incomeReliability: IncomeReliability?
    ) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            val movement = MovementEntity(
                id = id,
                walletId = walletId,
                amount = amount,
                type = type,
                categoryId = categoryId,
                date = date,
                title = title,
                notes = notes,
                isPlanned = planned,
                isConfirmed = confirmed,
                incomeReliability = incomeReliability,
                createdAt = now,
                updatedAt = now
            )
            if (id == 0L) repository.addMovement(movement) else repository.updateMovement(movement)
        }
    }

    fun deleteMovement(movement: MovementEntity) {
        viewModelScope.launch {
            repository.deleteMovement(movement)
        }
    }

    fun addTransfer(fromWalletId: Long, toWalletId: Long, amount: Double, date: LocalDate, description: String) {
        viewModelScope.launch {
            repository.addTransfer(fromWalletId, toWalletId, amount, date, description)
        }
    }

    fun saveRecurringRule(
        id: Long = 0L,
        name: String,
        amount: Double,
        type: TransactionType,
        categoryId: Long?,
        walletId: Long,
        frequency: RecurrenceFrequency,
        startDate: LocalDate,
        endDate: LocalDate?,
        mandatory: Boolean,
        reliability: IncomeReliability?,
        cancelable: Boolean,
        upfrontCost: Double?,
        minimumCommitmentMonths: Int?,
        customIntervalDays: Int?
    ) {
        viewModelScope.launch {
            val now = LocalDateTime.now()
            repository.upsertRecurringRule(
                RecurringRuleEntity(
                    id = id,
                    name = name,
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    walletId = walletId,
                    frequency = frequency,
                    startDate = startDate,
                    endDate = endDate,
                    customIntervalDays = customIntervalDays,
                    isMandatory = mandatory,
                    incomeReliability = reliability,
                    isCancelable = cancelable,
                    upfrontCost = upfrontCost,
                    minimumCommitmentMonths = minimumCommitmentMonths,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun toggleRecurringRule(rule: RecurringRuleEntity) {
        viewModelScope.launch {
            repository.toggleRecurringRule(rule, !rule.isActive)
        }
    }

    fun saveSettings(settings: ErasmusSettingsEntity) {
        viewModelScope.launch {
            repository.upsertSettings(settings)
            messages.emit("Impostazioni salvate")
        }
    }

    fun runSimulation(request: SimulationRequest) {
        val currentState = uiState.value
        val settings = currentState.settings ?: return
        val snapshot = BudgetSnapshot(
            wallets = currentState.wallets,
            movements = currentState.movements,
            categories = currentState.categories,
            recurringRules = currentState.recurringRules,
            settings = settings
        )
        lastSimulationRequest.value = request
        simulationResult.value = budgetEngine.simulate(snapshot, currentState.selectedScenario, request)
    }

    fun clearSimulation() {
        simulationResult.value = null
        lastSimulationRequest.value = null
    }

    fun commitSimulation() {
        val request = lastSimulationRequest.value ?: return
        when (request.kind) {
            SimulationKind.SPESA_SINGOLA -> saveMovement(
                walletId = request.walletId ?: return,
                amount = request.amount,
                type = TransactionType.EXPENSE,
                categoryId = request.categoryId,
                date = request.date,
                title = request.description.ifBlank { "Spesa simulata" },
                notes = "Aggiunta dal simulatore",
                planned = false,
                confirmed = true,
                incomeReliability = null
            )
            SimulationKind.ABBONAMENTO -> saveRecurringRule(
                name = request.description.ifBlank { "Abbonamento simulato" },
                amount = request.amount,
                type = TransactionType.EXPENSE,
                categoryId = request.categoryId,
                walletId = request.walletId ?: return,
                frequency = when (request.frequencyDays) {
                    7 -> RecurrenceFrequency.WEEKLY
                    365 -> RecurrenceFrequency.YEARLY
                    null, 30 -> RecurrenceFrequency.MONTHLY
                    else -> RecurrenceFrequency.CUSTOM
                },
                startDate = request.date,
                endDate = request.recurringEndDate,
                mandatory = request.mandatory,
                reliability = null,
                cancelable = true,
                upfrontCost = request.upfrontCost,
                minimumCommitmentMonths = request.minimumCommitmentMonths,
                customIntervalDays = request.frequencyDays
            )
            SimulationKind.RATE -> {
                val installments = request.installments ?: 1
                val stepDays = (request.frequencyDays ?: 30).toLong()
                repeat(installments) { index ->
                    saveMovement(
                        walletId = request.walletId ?: return,
                        amount = request.amount,
                        type = TransactionType.EXPENSE,
                        categoryId = request.categoryId,
                        date = request.date.plusDays(index * stepDays),
                        title = "${request.description.ifBlank { "Rata" }} ${index + 1}/$installments",
                        notes = "Creata dal simulatore",
                        planned = index > 0,
                        confirmed = index == 0 && request.date <= LocalDate.now(),
                        incomeReliability = null
                    )
                }
            }
            SimulationKind.MASSIMO_SOSTENIBILE -> Unit
        }
        viewModelScope.launch { messages.emit("Simulazione applicata") }
    }

    fun saveScenarioNote() {
        val simulation = simulationResult.value ?: return
        savedScenarios.value = savedScenarios.value + buildString {
            append(simulation.status.name)
            append(" - ")
            append("Costo ")
            append(simulation.totalCost)
        }
        viewModelScope.launch { messages.emit("Scenario salvato in locale per questa sessione") }
    }

    fun exportBackup() {
        viewModelScope.launch {
            backupText.value = repository.exportBackup()
            messages.emit("Backup JSON generato")
        }
    }

    fun updateBackupText(value: String) {
        backupText.value = value
    }

    fun importBackup() {
        viewModelScope.launch {
            repository.importBackup(backupText.value)
            messages.emit("Backup importato")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            simulationResult.value = null
            lastSimulationRequest.value = null
            backupText.value = ""
            messages.emit("Dati resettati")
        }
    }
}

private data class SnapshotBundle(
    val wallets: List<WalletEntity>,
    val categories: List<CategoryEntity>,
    val movements: List<MovementEntity>,
    val rules: List<RecurringRuleEntity>,
    val settings: ErasmusSettingsEntity?
)

private data class UiExtras(
    val request: SimulationRequest?,
    val scenarios: List<String>,
    val backup: String
)

data class OnboardingIncomeInput(
    val title: String,
    val amount: Double,
    val reliability: IncomeReliability,
    val date: LocalDate?,
    val walletId: Long
)

data class OnboardingRecurringInput(
    val name: String,
    val amount: Double,
    val categoryId: Long?,
    val walletId: Long,
    val startDate: LocalDate
)

class AppViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(repository) as T
    }
}
