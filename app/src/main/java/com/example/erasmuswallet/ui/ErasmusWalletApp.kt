@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.example.erasmuswallet.ui

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import com.example.erasmuswallet.domain.BudgetEngine
import com.example.erasmuswallet.domain.model.BudgetSummary
import com.example.erasmuswallet.domain.model.ReportSummary
import com.example.erasmuswallet.domain.model.SimulationRequest
import com.example.erasmuswallet.domain.model.SimulationResult
import com.example.erasmuswallet.ui.theme.Danger
import com.example.erasmuswallet.ui.theme.Aqua
import com.example.erasmuswallet.ui.theme.CyanGlow
import com.example.erasmuswallet.ui.theme.ElectricBlue
import com.example.erasmuswallet.ui.theme.LiquidBackground
import com.example.erasmuswallet.ui.theme.LiquidBackgroundDeep
import com.example.erasmuswallet.ui.theme.LiquidBorder
import com.example.erasmuswallet.ui.theme.LiquidBorderStrong
import com.example.erasmuswallet.ui.theme.LiquidSurface
import com.example.erasmuswallet.ui.theme.LiquidSurfaceElevated
import com.example.erasmuswallet.ui.theme.LiquidSurfaceSoft
import com.example.erasmuswallet.ui.theme.LiquidText
import com.example.erasmuswallet.ui.theme.LiquidTextSecondary
import com.example.erasmuswallet.ui.theme.Success
import com.example.erasmuswallet.ui.theme.Warning
import com.example.erasmuswallet.ui.util.parseItalianDate
import com.example.erasmuswallet.ui.util.toEuro
import com.example.erasmuswallet.ui.util.toItalianDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

private data class Destination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val destinations = listOf(
    Destination("dashboard", "Dashboard", Icons.Default.Home),
    Destination("movimenti", "Movimenti", Icons.Default.SwapHoriz),
    Destination("wallet", "Wallet", Icons.Default.AccountBalanceWallet),
    Destination("ricorrenti", "Ricorrenti", Icons.AutoMirrored.Filled.PlaylistAddCheck),
    Destination("simulatore", "Simulatore", Icons.AutoMirrored.Filled.ShowChart),
    Destination("report", "Report", Icons.Default.Analytics),
    Destination("impostazioni", "Impostazioni", Icons.Default.Settings)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErasmusWalletApp(viewModel: AppViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.messages.collect { message -> snackbarHostState.showSnackbar(message) }
    }

    if (uiState.onboardingNeeded) {
        OnboardingScreen(uiState = uiState, onComplete = viewModel::completeOnboarding)
        return
    }

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "dashboard"

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Erasmus Budget Guardian", color = LiquidText) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LiquidSurface.copy(alpha = 0.92f),
                    scrolledContainerColor = LiquidSurfaceElevated.copy(alpha = 0.96f),
                    titleContentColor = LiquidText,
                    navigationIconContentColor = LiquidText,
                    actionIconContentColor = LiquidText
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = LiquidSurface.copy(alpha = 0.94f),
                tonalElevation = 0.dp
            ) {
                destinations.forEach { destination -> 
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackdropBrush())
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard"
            ) {
                composable("dashboard") {
                    DashboardScreen(uiState, onScenarioChange = viewModel::setScenario)
                }
                composable("movimenti") {
                    MovementsScreen(
                        uiState = uiState,
                        onSave = viewModel::saveMovement,
                        onDelete = viewModel::deleteMovement,
                        onTransfer = viewModel::addTransfer
                    )
                }
                composable("wallet") {
                    WalletScreen(
                        uiState = uiState,
                        onSave = viewModel::saveWallet,
                        onDelete = viewModel::deleteWallet,
                        onToggleArchive = viewModel::toggleWalletArchive,
                        onOpenCategories = { navController.navigate("categorie") }
                    )
                }
                composable("ricorrenti") {
                    RecurringScreen(
                        uiState = uiState,
                        onSave = viewModel::saveRecurringRule,
                        onToggle = viewModel::toggleRecurringRule
                    )
                }
                composable("simulatore") {
                    SimulatorScreen(
                        uiState = uiState,
                        onScenarioChange = viewModel::setScenario,
                        onSimulate = viewModel::runSimulation,
                        onCommit = viewModel::commitSimulation,
                        onSaveScenario = viewModel::saveScenarioNote,
                        onClear = viewModel::clearSimulation
                    )
                }
                composable("report") {
                    ReportScreen(uiState)
                }
                composable("impostazioni") {
                    SettingsScreen(
                        uiState = uiState,
                        onSave = viewModel::saveSettings,
                        onExport = viewModel::exportBackup,
                        onImport = viewModel::importBackup,
                        onBackupChange = viewModel::updateBackupText,
                        onReset = viewModel::resetAllData,
                        onOpenCategories = { navController.navigate("categorie") },
                        onShareBackup = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, uiState.backupText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Condividi backup"))
                        }
                    )
                }
                composable("categorie") {
                    CategoryScreen(
                        uiState = uiState,
                        onSave = viewModel::saveCategory,
                        onToggle = viewModel::toggleCategory
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingScreen(
    uiState: AppUiState,
    onComplete: (
        LocalDate,
        LocalDate,
        Double,
        Double,
        Map<Long, Double>,
        List<OnboardingIncomeInput>,
        List<OnboardingRecurringInput>
    ) -> Unit
) {
    val wallets = uiState.wallets
    val categories = uiState.categories
    var startDate by rememberSaveable { mutableStateOf(LocalDate.now().toItalianDate()) }
    var endDate by rememberSaveable { mutableStateOf(LocalDate.now().plusMonths(6).toItalianDate()) }
    var finalGoal by rememberSaveable { mutableStateOf("1000") }
    var emergencyFund by rememberSaveable { mutableStateOf("300") }
    var walletInputs by rememberSaveable { mutableStateOf(wallets.associate { it.id to "0" }) }
    val incomeRows = remember {
        mutableStateListOf(
            OnboardingIncomeDraft(title = "Borsa Erasmus", amount = "0", walletId = wallets.firstOrNull()?.id),
            OnboardingIncomeDraft(title = "Genitori", amount = "0", walletId = wallets.firstOrNull()?.id)
        )
    }
    val expenseRows = remember {
        mutableStateListOf(
            OnboardingExpenseDraft(
                title = "Affitto",
                amount = "450",
                categoryId = categories.firstOrNull { it.name == "Affitto" }?.id
            ),
            OnboardingExpenseDraft(
                title = "Bollette",
                amount = "80",
                categoryId = categories.firstOrNull { it.name == "Bollette" }?.id
            ),
            OnboardingExpenseDraft(
                title = "Abbonamento pullman",
                amount = "35",
                categoryId = categories.firstOrNull { it.name == "Trasporti" }?.id
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HeroBanner(
                title = "Configura il tuo Erasmus in pochi minuti",
                subtitle = "Tutto rimane modificabile e si aggiorna in tempo reale."
            )
            MoneyField("Data inizio Erasmus", startDate) { startDate = it }
            MoneyField("Data fine Erasmus (Orientativo)", endDate) { endDate = it }
            MoneyField("Obiettivo finale da conservare", finalGoal) { finalGoal = it }
            MoneyField("Fondo imprevisti", emergencyFund) { emergencyFund = it }
            GlassSectionHeader("Saldi iniziali wallet")
            wallets.forEach { wallet ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(wallet.name, color = LiquidText, fontWeight = FontWeight.SemiBold)
                        MoneyField("Saldo iniziale", walletInputs[wallet.id].orEmpty()) {
                            walletInputs = walletInputs.toMutableMap().apply { put(wallet.id, it) }
                        }
                    }
                }
            }
            EditableIncomeSection(
                rows = incomeRows,
                wallets = wallets,
                onAdd = { incomeRows += OnboardingIncomeDraft(title = "Nuova entrata", amount = "0") },
                onRemove = { index -> if (incomeRows.size > 1) incomeRows.removeAt(index) },
                onChange = { index, row -> incomeRows[index] = row }
            )
            EditableExpenseSection(
                rows = expenseRows,
                categories = categories,
                wallets = wallets,
                onAdd = { expenseRows += OnboardingExpenseDraft(title = "Nuova spesa fissa", amount = "0") },
                onRemove = { index -> if (expenseRows.size > 1) expenseRows.removeAt(index) },
                onChange = { index, row -> expenseRows[index] = row }
            )
            Button(
                onClick = {
                    val parsedStart = parseItalianDate(startDate) ?: LocalDate.now()
                    val parsedEnd = parseItalianDate(endDate) ?: parsedStart.plusMonths(6)
                    val firstWalletId = wallets.firstOrNull()?.id ?: 0L
                    val expenses = expenseRows.mapNotNull { row ->
                        val amount = row.amount.toDoubleOrNull() ?: 0.0
                        val categoryId = row.categoryId ?: categories.firstOrNull()?.id
                        if (amount <= 0.0 || categoryId == null) return@mapNotNull null
                        OnboardingRecurringInput(row.title.ifBlank { "Spesa fissa" }, amount, categoryId, firstWalletId, parsedStart)
                    }
                    val incomes = incomeRows.mapNotNull { row ->
                        val amount = row.amount.toDoubleOrNull() ?: 0.0
                        if (amount <= 0.0) return@mapNotNull null
                        OnboardingIncomeInput(row.title.ifBlank { "Entrata" }, amount, row.reliability, parsedStart.plusMonths(1), row.walletId ?: firstWalletId)
                    }
                    onComplete(
                        parsedStart,
                        parsedEnd,
                        finalGoal.toDoubleOrNull() ?: 1000.0,
                        emergencyFund.toDoubleOrNull() ?: 0.0,
                        walletInputs.mapValues { it.value.toDoubleOrNull() ?: 0.0 },
                        incomes,
                        expenses
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Inizia a usare l'app")
            }
        }
    }
}

@Composable
private fun DashboardScreen(uiState: AppUiState, onScenarioChange: (ScenarioType) -> Unit) {
    val summary = uiState.summary ?: return
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ScenarioChooser(selected = uiState.selectedScenario, onScenarioChange = onScenarioChange)
        }
        item {
            StatusHero(summary)
        }
        item {
            SummaryGrid(summary)
        }
        item {
            SectionTitle("Prossime spese")
        }
        items(summary.upcomingExpenses) { event ->
            FutureEventCard(event.title, event.date.toItalianDate(), (-event.amount).toEuro(), summary.status)
        }
        item {
            SectionTitle("Prossime entrate")
        }
        items(summary.upcomingIncomes) { event ->
            FutureEventCard(event.title, event.date.toItalianDate(), event.amount.toEuro(), BudgetStatus.SICURO)
        }
        item {
            SectionTitle("Wallet")
        }
        items(summary.walletSummaries) { wallet ->
            MetricCard(
                title = wallet.wallet.name,
                value = wallet.currentBalance.toEuro(),
                subtitle = "${(wallet.percentageOnTotal * 100).toInt()}% del totale"
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun MovementsScreen(
    uiState: AppUiState,
    onSave: (Long, Long, Double, TransactionType, Long?, LocalDate, String, String?, Boolean, Boolean, IncomeReliability?) -> Unit,
    onDelete: (MovementEntity) -> Unit,
    onTransfer: (Long, Long, Double, LocalDate, String) -> Unit
) {
    var showMovementDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }
    var editingMovement by remember { mutableStateOf<MovementEntity?>(null) }
    var selectedWalletId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedCategoryId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedType by rememberSaveable { mutableStateOf<TransactionType?>(null) }
    var fromDate by rememberSaveable { mutableStateOf("") }
    var toDate by rememberSaveable { mutableStateOf("") }
    val filtered = uiState.movements.filter {
        (selectedWalletId == null || it.walletId == selectedWalletId) &&
            (selectedCategoryId == null || it.categoryId == selectedCategoryId) &&
            (selectedType == null || it.type == selectedType) &&
            (parseItalianDate(fromDate)?.let { start -> it.date >= start } ?: true) &&
            (parseItalianDate(toDate)?.let { end -> it.date <= end } ?: true)
    }
    Scaffold(
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FloatingActionButton(onClick = { showTransferDialog = true }) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Trasferimento")
                }
                FloatingActionButton(onClick = { showMovementDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi")
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterRow(
                wallets = uiState.wallets,
                categories = uiState.categories,
                selectedWalletId = selectedWalletId,
                selectedCategoryId = selectedCategoryId,
                selectedType = selectedType,
                onWalletChange = { selectedWalletId = it },
                onCategoryChange = { selectedCategoryId = it },
                onTypeChange = { selectedType = it }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fromDate,
                    onValueChange = { fromDate = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Da data") }
                )
                OutlinedTextField(
                    value = toDate,
                    onValueChange = { toDate = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("A data") }
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { movement ->
                    MetricCard(
                        title = movement.title,
                        value = when (movement.type) {
                            TransactionType.INCOME -> movement.amount.toEuro()
                            TransactionType.EXPENSE -> "-${movement.amount.toEuro()}"
                            TransactionType.TRANSFER -> "${movement.amount.toEuro()} trasferimento"
                        },
                        subtitle = movement.date.toItalianDate(),
                        trailing = {
                            Row {
                                TextButton(onClick = {
                                    editingMovement = movement
                                    showMovementDialog = true
                                }) { Text("Modifica") }
                                TextButton(onClick = { onDelete(movement) }) { Text("Elimina") }
                            }
                        }
                    )
                }
            }
        }
    }
    if (showMovementDialog) {
        MovementDialog(
            wallets = uiState.wallets,
            categories = uiState.categories,
            existing = editingMovement,
            onDismiss = { showMovementDialog = false },
            onSave = { walletId, amount, type, categoryId, date, title, notes, planned, confirmed, reliability ->
                onSave(editingMovement?.id ?: 0L, walletId, amount, type, categoryId, date, title, notes, planned, confirmed, reliability)
                editingMovement = null
                showMovementDialog = false
            }
        )
    }
    if (showTransferDialog) {
        TransferDialog(uiState.wallets, onDismiss = { showTransferDialog = false }) { from, to, amount, date, desc ->
            onTransfer(from, to, amount, date, desc)
            showTransferDialog = false
        }
    }
}

@Composable
private fun WalletScreen(
    uiState: AppUiState,
    onSave: (Long, String, WalletType, Double, String?) -> Unit,
    onDelete: (WalletEntity) -> Unit,
    onToggleArchive: (WalletEntity) -> Unit,
    onOpenCategories: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<WalletEntity?>(null) }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassSectionHeader("Wallet")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onOpenCategories) { Text("Gestisci categorie") }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(uiState.summary?.walletSummaries.orEmpty()) { summary ->
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(summary.wallet.name, color = LiquidText, fontWeight = FontWeight.SemiBold)
                                    Text(summary.currentBalance.toEuro(), style = MaterialTheme.typography.titleLarge, color = Aqua)
                                    Text(
                                        buildString {
                                            append(summary.wallet.type.name)
                                            if (summary.lastMovements.isNotEmpty()) {
                                                append(" • Ultimi: ")
                                                append(summary.lastMovements.joinToString { it.title })
                                            }
                                        },
                                        color = LiquidTextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                WalletBadge(summary.wallet.type)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = {
                                    editingWallet = summary.wallet
                                    showDialog = true
                                }) { Text("Modifica") }
                                TextButton(onClick = { onToggleArchive(summary.wallet) }) {
                                    Text(if (summary.wallet.isArchived) "Riattiva" else "Archivia")
                                }
                                TextButton(onClick = { onDelete(summary.wallet) }) {
                                    Text("Elimina", color = Danger)
                                }
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = CyanGlow,
            contentColor = LiquidBackground,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nuovo wallet")
        }
    }
    if (showDialog) {
        WalletDialog(existing = editingWallet, onDismiss = { showDialog = false }) { name, type, balance, color ->
            onSave(editingWallet?.id ?: 0L, name, type, balance, color)
            editingWallet = null
            showDialog = false
        }
    }
}

@Composable
private fun CategoryScreen(
    uiState: AppUiState,
    onSave: (Long, String, CategoryGroup, Boolean, String?) -> Unit,
    onToggle: (CategoryEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(onClick = { showDialog = true }) { Text("Nuova categoria") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CategoryGroup.values().forEach { group ->
                item { SectionTitle(group.name.replace("_", " ")) }
                items(uiState.categories.filter { it.group == group }) { category ->
                    MetricCard(
                        title = BudgetEngine().displayCategoryName(category, uiState.settings?.privacyMode == true),
                        value = if (category.isSensitive) "Sensibile" else "Standard",
                        subtitle = if (category.isActive) "Attiva" else "Archiviata",
                        trailing = {
                            Row {
                                TextButton(onClick = {
                                    editingCategory = category
                                    showDialog = true
                                }) { Text("Modifica") }
                                TextButton(onClick = { onToggle(category) }) {
                                    Text(if (category.isActive) "Archivia" else "Riattiva")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    if (showDialog) {
        CategoryDialog(existing = editingCategory, onDismiss = { showDialog = false }) { name, group, sensitive, alias ->
            onSave(editingCategory?.id ?: 0L, name, group, sensitive, alias)
            editingCategory = null
            showDialog = false
        }
    }
}

@Composable
private fun RecurringScreen(
    uiState: AppUiState,
    onSave: (
        Long,
        String,
        Double,
        TransactionType,
        Long?,
        Long,
        RecurrenceFrequency,
        LocalDate,
        LocalDate?,
        Boolean,
        IncomeReliability?,
        Boolean,
        Double?,
        Int?,
        Int?
    ) -> Unit,
    onToggle: (RecurringRuleEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<RecurringRuleEntity?>(null) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { showDialog = true }) { Text("Nuova ricorrenza") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(uiState.recurringRules) { rule ->
                MetricCard(
                    title = rule.name,
                    value = rule.amount.toEuro(),
                    subtitle = "${rule.frequency.name} • ${if (rule.isMandatory) "Obbligatoria" else "Pianificata"}",
                    trailing = {
                        Row {
                            TextButton(onClick = {
                                editingRule = rule
                                showDialog = true
                            }) { Text("Modifica") }
                            TextButton(onClick = { onToggle(rule) }) {
                                Text(if (rule.isActive) "Disattiva" else "Riattiva")
                            }
                        }
                    }
                )
            }
        }
    }
    if (showDialog) {
        RecurringDialog(uiState.wallets, uiState.categories, editingRule, onDismiss = { showDialog = false }) { name, amount, type, categoryId, walletId, frequency, start, end, mandatory, reliability, cancelable, upfront, minMonths, customDays ->
            onSave(editingRule?.id ?: 0L, name, amount, type, categoryId, walletId, frequency, start, end, mandatory, reliability, cancelable, upfront, minMonths, customDays)
            editingRule = null
            showDialog = false
        }
    }
}

@Composable
private fun SimulatorScreen(
    uiState: AppUiState,
    onScenarioChange: (ScenarioType) -> Unit,
    onSimulate: (SimulationRequest) -> Unit,
    onCommit: () -> Unit,
    onSaveScenario: () -> Unit,
    onClear: () -> Unit
) {
    var kind by rememberSaveable { mutableStateOf(SimulationKind.SPESA_SINGOLA) }
    var amount by rememberSaveable { mutableStateOf("80") }
    var description by rememberSaveable { mutableStateOf("Serata") }
    var date by rememberSaveable { mutableStateOf(LocalDate.now().toItalianDate()) }
    var frequencyDays by rememberSaveable { mutableStateOf("30") }
    var installments by rememberSaveable { mutableStateOf("3") }
    var upfrontCost by rememberSaveable { mutableStateOf("0") }
    var minimumMonths by rememberSaveable { mutableStateOf("0") }
    var walletId by rememberSaveable { mutableStateOf(uiState.wallets.firstOrNull()?.id ?: 0L) }
    var categoryId by rememberSaveable { mutableStateOf(uiState.categories.firstOrNull()?.id) }
    val result = uiState.simulationResult

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ScenarioChooser(selected = uiState.selectedScenario, onScenarioChange = onScenarioChange)
        EnumChipRow(
            items = SimulationKind.values().toList(),
            selected = kind,
            label = { it.name.replace("_", " ") },
            onSelected = { kind = it }
        )
        MoneyField("Importo", amount) { amount = it }
        MoneyField("Descrizione", description) { description = it }
        MoneyField("Data", date) { date = it }
        WalletSelector(uiState.wallets, walletId) { walletId = it }
        CategorySelector(uiState.categories, categoryId) { categoryId = it }
        if (kind == SimulationKind.ABBONAMENTO || kind == SimulationKind.RATE) {
            MoneyField("Frequenza giorni (7, 30, 365)", frequencyDays) { frequencyDays = it }
        }
        if (kind == SimulationKind.RATE) {
            MoneyField("Numero rate", installments) { installments = it }
        }
        if (kind == SimulationKind.ABBONAMENTO) {
            MoneyField("Costo iniziale", upfrontCost) { upfrontCost = it }
            MoneyField("Vincolo minimo mesi", minimumMonths) { minimumMonths = it }
        }
        Button(onClick = {
            val request = SimulationRequest(
                kind = kind,
                amount = amount.toDoubleOrNull() ?: 0.0,
                date = parseItalianDate(date) ?: LocalDate.now(),
                categoryId = categoryId,
                walletId = walletId,
                description = description,
                frequencyDays = frequencyDays.toIntOrNull(),
                installments = installments.toIntOrNull(),
                upfrontCost = upfrontCost.toDoubleOrNull(),
                minimumCommitmentMonths = minimumMonths.toIntOrNull()?.takeIf { it > 0 },
                recurringEndDate = uiState.settings?.endDate
            )
            onSimulate(request)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Calcola")
        }
        result?.let {
            SimulationResultCard(result = it)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onCommit) { Text("Aggiungi davvero questa spesa") }
                OutlinedButton(onClick = onSaveScenario) { Text("Salva come scenario") }
                OutlinedButton(onClick = onClear) { Text("Annulla") }
            }
        }
        if (uiState.savedScenarios.isNotEmpty()) {
            SectionTitle("Scenari salvati nella sessione")
            uiState.savedScenarios.forEach { Text("• $it") }
        }
    }
}

@Composable
private fun ReportScreen(uiState: AppUiState) {
    val report = uiState.report ?: return
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard("Spesa settimana corrente", report.weeklyExpense.toEuro(), "Totale confermato")
        MetricCard("Spesa mese corrente", report.monthlyExpense.toEuro(), "Totale confermato")
        MetricCard("Proiezione finale", report.projectedFinal.toEuro(), "Scenario ${uiState.selectedScenario.name.lowercase()}")
        SectionTitle("Spesa per categoria")
        report.byCategory.forEach { item ->
            MetricCard(item.categoryName, item.amount.toEuro(), "Mese corrente")
        }
        SectionTitle("Andamento saldo")
        report.balanceTrend.forEach { point ->
            MetricCard(point.date.toItalianDate(), point.balance.toEuro(), "Saldo previsto")
        }
    }
}

@Composable
private fun SettingsScreen(
    uiState: AppUiState,
    onSave: (ErasmusSettingsEntity) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onBackupChange: (String) -> Unit,
    onReset: () -> Unit,
    onOpenCategories: () -> Unit,
    onShareBackup: () -> Unit
) {
    val settings = uiState.settings ?: return
    var showResetConfirm by remember { mutableStateOf(false) }
    var startDate by rememberSaveable(settings.startDate) { mutableStateOf(settings.startDate.toItalianDate()) }
    var endDate by rememberSaveable(settings.endDate) { mutableStateOf(settings.endDate.toItalianDate()) }
    var finalGoal by rememberSaveable(settings.finalGoal) { mutableStateOf(settings.finalGoal.toString()) }
    var emergencyFund by rememberSaveable(settings.emergencyFund) { mutableStateOf(settings.emergencyFund.toString()) }
    var safeThreshold by rememberSaveable(settings.safeThreshold) { mutableStateOf(settings.safeThreshold.toString()) }
    var minLiquidity by rememberSaveable(settings.minimumLiquidityThreshold) { mutableStateOf(settings.minimumLiquidityThreshold.toString()) }
    var privacyMode by rememberSaveable(settings.privacyMode) { mutableStateOf(settings.privacyMode) }
    var defaultScenario by rememberSaveable(settings.defaultScenario) { mutableStateOf(settings.defaultScenario) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        MoneyField("Data inizio Erasmus", startDate) { startDate = it }
        MoneyField("Data fine Erasmus (Orientativo)", endDate) { endDate = it }
        MoneyField("Obiettivo finale", finalGoal) { finalGoal = it }
        MoneyField("Fondo imprevisti", emergencyFund) { emergencyFund = it }
        MoneyField("Soglia sicuro", safeThreshold) { safeThreshold = it }
        MoneyField("Soglia liquidità minima", minLiquidity) { minLiquidity = it }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Modalità privacy", modifier = Modifier.weight(1f))
            Switch(checked = privacyMode, onCheckedChange = { privacyMode = it })
        }
        EnumChipRow(items = ScenarioType.values().toList(), selected = defaultScenario, label = { it.name }, onSelected = { defaultScenario = it })
        Button(onClick = {
            onSave(
                settings.copy(
                    startDate = parseItalianDate(startDate) ?: settings.startDate,
                    endDate = parseItalianDate(endDate) ?: settings.endDate,
                    finalGoal = finalGoal.toDoubleOrNull() ?: settings.finalGoal,
                    emergencyFund = emergencyFund.toDoubleOrNull() ?: settings.emergencyFund,
                    safeThreshold = safeThreshold.toDoubleOrNull() ?: settings.safeThreshold,
                    minimumLiquidityThreshold = minLiquidity.toDoubleOrNull() ?: settings.minimumLiquidityThreshold,
                    privacyMode = privacyMode,
                    defaultScenario = defaultScenario
                )
            )
        }, modifier = Modifier.fillMaxWidth()) { Text("Salva impostazioni") }
        OutlinedButton(onClick = onOpenCategories, modifier = Modifier.fillMaxWidth()) { Text("Gestisci categorie") }
        SectionTitle("Backup JSON")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onExport) { Text("Esporta") }
            OutlinedButton(onClick = onImport) { Text("Importa") }
            OutlinedButton(onClick = onShareBackup) { Text("Condividi") }
        }
        OutlinedTextField(
            value = uiState.backupText,
            onValueChange = onBackupChange,
            modifier = Modifier.fillMaxWidth().height(180.dp),
            label = { Text("Backup") }
        )
        OutlinedButton(onClick = { showResetConfirm = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Reset dati con conferma manuale")
        }
    }
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            confirmButton = {
                Button(onClick = {
                    onReset()
                    showResetConfirm = false
                }) { Text("Conferma reset") }
            },
            dismissButton = { TextButton(onClick = { showResetConfirm = false }) { Text("Annulla") } },
            title = { Text("Conferma reset") },
            text = { Text("Tutti i dati locali verranno cancellati e ricreati i default.") }
        )
    }
}

@Composable
private fun StatusHero(summary: BudgetSummary) {
    val color = statusColor(summary.status)
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Stato ${summary.status.name.replace("_", " ")}", fontWeight = FontWeight.Bold, color = color)
            Text("Saldo totale attuale: ${summary.totalBalance.toEuro()}")
            Text("Obiettivo finale protetto: ${summary.protectedReserve.toEuro()}")
            Text("Soldi realmente spendibili: ${summary.spendableMoney.toEuro()}")
            Text("Proiezione fine Erasmus: ${summary.finalProjection.toEuro()}")
            Text("Saldo minimo previsto: ${summary.minimumLiquidity.balance.toEuro()} il ${summary.minimumLiquidity.date.toItalianDate()}")
        }
    }
}

@Composable
private fun SummaryGrid(summary: BudgetSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MetricCard("Giorni rimanenti", summary.daysRemaining.toString(), "Fino a fine Erasmus")
        MetricCard("Budget giornaliero consigliato", summary.dailyBudget.toEuro(), "Scenario attivo")
        MetricCard("Budget settimanale consigliato", summary.weeklyBudget.toEuro(), "Scenario attivo")
        MetricCard("Speso questa settimana", summary.spentThisWeek.toEuro(), "Movimenti confermati")
        MetricCard("Rimanente questa settimana", summary.remainingThisWeek.toEuro(), "Budget residuo")
    }
}

@Composable
private fun SimulationResultCard(result: SimulationResult) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Esito: ${result.status.name.replace("_", " ")}", fontWeight = FontWeight.Bold, color = statusColor(result.status))
            Text("Costo totale reale: ${result.totalCost.toEuro()}", color = LiquidText)
            Text("Proiezione finale attuale: ${result.currentProjection.toEuro()}", color = LiquidText)
            Text("Dopo la spesa: ${result.newProjection.toEuro()}", color = LiquidText)
            Text("Differenza rispetto all'obiettivo: ${result.differenceFromGoal.toEuro()}", color = LiquidText)
            Text("Budget giornaliero attuale: ${result.currentDailyBudget.toEuro()}", color = LiquidText)
            Text("Budget giornaliero dopo: ${result.newDailyBudget.toEuro()}", color = LiquidText)
            Text("Budget settimanale attuale: ${result.currentWeeklyBudget.toEuro()}", color = LiquidText)
            Text("Budget settimanale dopo: ${result.newWeeklyBudget.toEuro()}", color = LiquidText)
            Text("Saldo minimo previsto: ${result.minimumLiquidity.balance.toEuro()} il ${result.minimumLiquidity.date.toItalianDate()}", color = LiquidText)
            Text("Massimo sostenibile: ${result.maximumSustainableAmount.toEuro()}", color = LiquidText)
            Text("Riduzione necessaria: ${result.reductionNeeded.toEuro()}", color = LiquidText)
            Text("Extra necessario: ${result.extraIncomeNeeded.toEuro()}", color = LiquidText)
        }
    }
}

@Composable
private fun ScenarioChooser(selected: ScenarioType, onScenarioChange: (ScenarioType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Scenario di calcolo", fontWeight = FontWeight.SemiBold)
        EnumChipRow(items = ScenarioType.values().toList(), selected = selected, label = { it.name.lowercase() }, onSelected = onScenarioChange)
    }
}

@Composable
private fun <T> EnumChipRow(
    items: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            AssistChip(
                onClick = { onSelected(item) },
                label = { Text(label(item)) },
                leadingIcon = if (item == selected) ({ Icon(Icons.AutoMirrored.Filled.PlaylistAddCheck, null, modifier = Modifier.size(16.dp)) }) else null
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = LiquidText)
                Text(value, style = MaterialTheme.typography.titleMedium, color = Aqua)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = LiquidTextSecondary)
            }
            trailing?.invoke()
        }
    }
}

@Composable
private fun FutureEventCard(title: String, date: String, amount: String, status: BudgetStatus) {
    MetricCard(title, amount, date)
}

@Composable
private fun SectionTitle(text: String) {
    GlassSectionHeader(text)
}

@Composable
private fun MoneyField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = LiquidText,
            unfocusedTextColor = LiquidText,
            focusedLabelColor = CyanGlow,
            unfocusedLabelColor = LiquidTextSecondary,
            cursorColor = CyanGlow,
            focusedBorderColor = CyanGlow,
            unfocusedBorderColor = LiquidBorder,
            focusedContainerColor = LiquidSurface.copy(alpha = 0.78f),
            unfocusedContainerColor = LiquidSurface.copy(alpha = 0.62f)
        )
    )
}

@Composable
private fun WalletSelector(wallets: List<WalletEntity>, selected: Long, onSelected: (Long) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        wallets.forEach { wallet ->
            AssistChip(
                onClick = { onSelected(wallet.id) },
                label = { Text(wallet.name) }
            )
        }
    }
}

@Composable
private fun CategorySelector(categories: List<CategoryEntity>, selected: Long?, onSelected: (Long?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AssistChip(onClick = { onSelected(null) }, label = { Text("Nessuna") })
        categories.take(10).forEach { category ->
            AssistChip(onClick = { onSelected(category.id) }, label = { Text(category.name) })
        }
    }
}

@Composable
private fun FilterRow(
    wallets: List<WalletEntity>,
    categories: List<CategoryEntity>,
    selectedWalletId: Long?,
    selectedCategoryId: Long?,
    selectedType: TransactionType?,
    onWalletChange: (Long?) -> Unit,
    onCategoryChange: (Long?) -> Unit,
    onTypeChange: (TransactionType?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { onWalletChange(null) }, label = { Text("Tutti i wallet") })
            wallets.take(4).forEach { wallet ->
                AssistChip(onClick = { onWalletChange(wallet.id) }, label = { Text(wallet.name) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { onCategoryChange(null) }, label = { Text("Tutte le categorie") })
            categories.take(4).forEach { category ->
                AssistChip(onClick = { onCategoryChange(category.id) }, label = { Text(category.name) })
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { onTypeChange(null) }, label = { Text("Tutti i tipi") })
            TransactionType.values().forEach { type ->
                AssistChip(onClick = { onTypeChange(type) }, label = { Text(type.name) })
            }
        }
    }
}

@Composable
private fun WalletDialog(existing: WalletEntity? = null, onDismiss: () -> Unit, onSave: (String, WalletType, Double, String?) -> Unit) {
    var name by rememberSaveable(existing?.id) { mutableStateOf(existing?.name.orEmpty()) }
    var balance by rememberSaveable(existing?.id) { mutableStateOf((existing?.initialBalance ?: 0.0).toString()) }
    var type by rememberSaveable(existing?.id) { mutableStateOf(existing?.type ?: WalletType.CARD) }
    var color by rememberSaveable(existing?.id) { mutableStateOf(existing?.colorHex ?: "#17624A") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onSave(name, type, balance.toDoubleOrNull() ?: 0.0, color) }) { Text("Salva") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } },
        title = { Text("Wallet") },
        containerColor = LiquidSurfaceElevated,
        textContentColor = LiquidText,
        titleContentColor = LiquidText,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MoneyField("Nome", name) { name = it }
                MoneyField("Saldo iniziale", balance) { balance = it }
                MoneyField("Colore HEX", color) { color = it }
                EnumChipRow(items = WalletType.values().toList(), selected = type, label = { it.name }, onSelected = { type = it })
            }
        }
    )
}

@Composable
private fun CategoryDialog(existing: CategoryEntity? = null, onDismiss: () -> Unit, onSave: (String, CategoryGroup, Boolean, String?) -> Unit) {
    var name by rememberSaveable(existing?.id) { mutableStateOf(existing?.name.orEmpty()) }
    var group by rememberSaveable(existing?.id) { mutableStateOf(existing?.group ?: CategoryGroup.ALTRO) }
    var sensitive by rememberSaveable(existing?.id) { mutableStateOf(existing?.isSensitive == true) }
    var alias by rememberSaveable(existing?.id) { mutableStateOf(existing?.privacyAlias ?: "Extra personali") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = { onSave(name, group, sensitive, alias.takeIf { sensitive }) }) { Text("Salva") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } },
        title = { Text("Categoria") },
        containerColor = LiquidSurfaceElevated,
        textContentColor = LiquidText,
        titleContentColor = LiquidText,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MoneyField("Nome", name) { name = it }
                EnumChipRow(items = CategoryGroup.values().toList(), selected = group, label = { it.name }, onSelected = { group = it })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = sensitive, onCheckedChange = { sensitive = it })
                    Text("Categoria sensibile")
                }
                if (sensitive) MoneyField("Alias privacy", alias) { alias = it }
            }
        }
    )
}

@Composable
private fun MovementDialog(
    wallets: List<WalletEntity>,
    categories: List<CategoryEntity>,
    existing: MovementEntity? = null,
    onDismiss: () -> Unit,
    onSave: (Long, Double, TransactionType, Long?, LocalDate, String, String?, Boolean, Boolean, IncomeReliability?) -> Unit
) {
    var walletId by rememberSaveable(existing?.id) { mutableStateOf(existing?.walletId ?: wallets.firstOrNull()?.id ?: 0L) }
    var amount by rememberSaveable(existing?.id) { mutableStateOf((existing?.amount ?: 0.0).takeIf { it != 0.0 }?.toString().orEmpty()) }
    var type by rememberSaveable(existing?.id) { mutableStateOf(existing?.type ?: TransactionType.EXPENSE) }
    var categoryId by rememberSaveable(existing?.id) { mutableStateOf(existing?.categoryId) }
    var date by rememberSaveable(existing?.id) { mutableStateOf((existing?.date ?: LocalDate.now()).toItalianDate()) }
    var title by rememberSaveable(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var notes by rememberSaveable(existing?.id) { mutableStateOf(existing?.notes.orEmpty()) }
    var planned by rememberSaveable(existing?.id) { mutableStateOf(existing?.isPlanned == true) }
    var confirmed by rememberSaveable(existing?.id) { mutableStateOf(existing?.isConfirmed != false) }
    var reliability by rememberSaveable(existing?.id) { mutableStateOf(existing?.incomeReliability ?: IncomeReliability.CONFERMATA) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onSave(
                    walletId,
                    amount.toDoubleOrNull() ?: 0.0,
                    type,
                    categoryId,
                    parseItalianDate(date) ?: LocalDate.now(),
                    title,
                    notes,
                    planned,
                    confirmed,
                    if (type == TransactionType.INCOME) reliability else null
                )
            }) { Text("Salva") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } },
        title = { Text("Nuovo movimento") },
        containerColor = LiquidSurfaceElevated,
        textContentColor = LiquidText,
        titleContentColor = LiquidText,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                WalletSelector(wallets, walletId) { walletId = it }
                MoneyField("Importo", amount) { amount = it }
                EnumChipRow(items = TransactionType.values().toList(), selected = type, label = { it.name }, onSelected = { type = it })
                CategorySelector(categories, categoryId) { categoryId = it }
                MoneyField("Data", date) { date = it }
                MoneyField("Descrizione", title) { title = it }
                MoneyField("Note", notes) { notes = it }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = planned, onCheckedChange = { planned = it })
                    Text("Programmato")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = confirmed, onCheckedChange = { confirmed = it })
                    Text("Confermato")
                }
                if (type == TransactionType.INCOME) {
                    EnumChipRow(items = IncomeReliability.values().toList(), selected = reliability, label = { it.name }, onSelected = { reliability = it })
                }
            }
        }
    )
}

@Composable
private fun TransferDialog(
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onSave: (Long, Long, Double, LocalDate, String) -> Unit
) {
    var fromWalletId by rememberSaveable { mutableStateOf(wallets.firstOrNull()?.id ?: 0L) }
    var toWalletId by rememberSaveable { mutableStateOf(wallets.drop(1).firstOrNull()?.id ?: wallets.firstOrNull()?.id ?: 0L) }
    var amount by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(LocalDate.now().toItalianDate()) }
    var description by rememberSaveable { mutableStateOf("Trasferimento") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onSave(fromWalletId, toWalletId, amount.toDoubleOrNull() ?: 0.0, parseItalianDate(date) ?: LocalDate.now(), description)
            }) { Text("Salva") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } },
        title = { Text("Trasferimento") },
        containerColor = LiquidSurfaceElevated,
        textContentColor = LiquidText,
        titleContentColor = LiquidText,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Wallet origine")
                WalletSelector(wallets, fromWalletId) { fromWalletId = it }
                Text("Wallet destinazione")
                WalletSelector(wallets, toWalletId) { toWalletId = it }
                MoneyField("Importo", amount) { amount = it }
                MoneyField("Data", date) { date = it }
                MoneyField("Descrizione", description) { description = it }
            }
        }
    )
}

@Composable
private fun RecurringDialog(
    wallets: List<WalletEntity>,
    categories: List<CategoryEntity>,
    existing: RecurringRuleEntity? = null,
    onDismiss: () -> Unit,
    onSave: (String, Double, TransactionType, Long?, Long, RecurrenceFrequency, LocalDate, LocalDate?, Boolean, IncomeReliability?, Boolean, Double?, Int?, Int?) -> Unit
) {
    var name by rememberSaveable(existing?.id) { mutableStateOf(existing?.name.orEmpty()) }
    var amount by rememberSaveable(existing?.id) { mutableStateOf((existing?.amount ?: 0.0).takeIf { it != 0.0 }?.toString().orEmpty()) }
    var type by rememberSaveable(existing?.id) { mutableStateOf(existing?.type ?: TransactionType.EXPENSE) }
    var categoryId by rememberSaveable(existing?.id) { mutableStateOf(existing?.categoryId) }
    var walletId by rememberSaveable(existing?.id) { mutableStateOf(existing?.walletId ?: wallets.firstOrNull()?.id ?: 0L) }
    var frequency by rememberSaveable(existing?.id) { mutableStateOf(existing?.frequency ?: RecurrenceFrequency.MONTHLY) }
    var startDate by rememberSaveable(existing?.id) { mutableStateOf((existing?.startDate ?: LocalDate.now()).toItalianDate()) }
    var endDate by rememberSaveable(existing?.id) { mutableStateOf((existing?.endDate ?: LocalDate.now().plusMonths(6)).toItalianDate()) }
    var mandatory by rememberSaveable(existing?.id) { mutableStateOf(existing?.isMandatory != false) }
    var reliability by rememberSaveable(existing?.id) { mutableStateOf(existing?.incomeReliability ?: IncomeReliability.CONFERMATA) }
    var cancelable by rememberSaveable(existing?.id) { mutableStateOf(existing?.isCancelable != false) }
    var upfrontCost by rememberSaveable(existing?.id) { mutableStateOf((existing?.upfrontCost ?: 0.0).toString()) }
    var minMonths by rememberSaveable(existing?.id) { mutableStateOf((existing?.minimumCommitmentMonths ?: 0).toString()) }
    var customDays by rememberSaveable(existing?.id) { mutableStateOf((existing?.customIntervalDays ?: 30).toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onSave(
                    name,
                    amount.toDoubleOrNull() ?: 0.0,
                    type,
                    categoryId,
                    walletId,
                    frequency,
                    parseItalianDate(startDate) ?: LocalDate.now(),
                    parseItalianDate(endDate),
                    mandatory,
                    if (type == TransactionType.INCOME) reliability else null,
                    cancelable,
                    upfrontCost.toDoubleOrNull(),
                    minMonths.toIntOrNull()?.takeIf { it > 0 },
                    customDays.toIntOrNull()
                )
            }) { Text("Salva") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } },
        title = { Text("Ricorrenza") },
        containerColor = LiquidSurfaceElevated,
        textContentColor = LiquidText,
        titleContentColor = LiquidText,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MoneyField("Nome", name) { name = it }
                MoneyField("Importo", amount) { amount = it }
                EnumChipRow(items = TransactionType.values().filter { it != TransactionType.TRANSFER }, selected = type, label = { it.name }, onSelected = { type = it })
                WalletSelector(wallets, walletId) { walletId = it }
                CategorySelector(categories, categoryId) { categoryId = it }
                EnumChipRow(items = RecurrenceFrequency.values().toList(), selected = frequency, label = { it.name }, onSelected = { frequency = it })
                MoneyField("Data inizio", startDate) { startDate = it }
                MoneyField("Data fine", endDate) { endDate = it }
                if (frequency == RecurrenceFrequency.CUSTOM) MoneyField("Intervallo giorni", customDays) { customDays = it }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = mandatory, onCheckedChange = { mandatory = it })
                    Text("Obbligatoria")
                }
                if (type == TransactionType.INCOME) {
                    EnumChipRow(items = IncomeReliability.values().toList(), selected = reliability, label = { it.name }, onSelected = { reliability = it })
                }
                MoneyField("Costo iniziale", upfrontCost) { upfrontCost = it }
                MoneyField("Vincolo minimo mesi", minMonths) { minMonths = it }
            }
        }
    )
}

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(1.dp, LiquidBorderStrong, RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(containerColor = LiquidSurface.copy(alpha = 0.78f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun HeroBanner(title: String, subtitle: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                color = LiquidText
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = LiquidTextSecondary
            )
        }
    }
}

@Composable
private fun GlassSectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = LiquidTextSecondary,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun WalletBadge(type: WalletType) {
    AssistChip(
        onClick = { },
        enabled = false,
        label = { Text(type.name) }
    )
}

@Composable
private fun AppBackdropBrush(): Brush = Brush.linearGradient(
    colors = listOf(
        LiquidBackground,
        LiquidBackgroundDeep,
        Color(0xFF08112B),
        LiquidBackground
    )
)

private data class OnboardingIncomeDraft(
    val title: String,
    val amount: String,
    val reliability: IncomeReliability = IncomeReliability.STIMATA,
    val walletId: Long? = null
)

private data class OnboardingExpenseDraft(
    val title: String,
    val amount: String,
    val categoryId: Long? = null
)

@Composable
private fun EditableIncomeSection(
    rows: List<OnboardingIncomeDraft>,
    wallets: List<WalletEntity>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    onChange: (Int, OnboardingIncomeDraft) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            GlassSectionHeader("Entrate")
            TextButton(onClick = onAdd) { Text("+") }
        }
        rows.forEachIndexed { index, row ->
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MoneyField("Nome entrata", row.title) { onChange(index, row.copy(title = it)) }
                    MoneyField("Importo", row.amount) { onChange(index, row.copy(amount = it)) }
                    WalletSelector(wallets, row.walletId ?: wallets.firstOrNull()?.id ?: 0L) { onChange(index, row.copy(walletId = it)) }
                    EnumChipRow(
                        items = IncomeReliability.values().toList(),
                        selected = row.reliability,
                        label = { it.name },
                        onSelected = { onChange(index, row.copy(reliability = it)) }
                    )
                    if (rows.size > 1) {
                        TextButton(onClick = { onRemove(index) }) { Text("Rimuovi", color = Danger) }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableExpenseSection(
    rows: List<OnboardingExpenseDraft>,
    categories: List<CategoryEntity>,
    wallets: List<WalletEntity>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit,
    onChange: (Int, OnboardingExpenseDraft) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            GlassSectionHeader("Spese fisse")
            TextButton(onClick = onAdd) { Text("+") }
        }
        rows.forEachIndexed { index, row ->
            GlassCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MoneyField("Nome spesa", row.title) { onChange(index, row.copy(title = it)) }
                    MoneyField("Importo", row.amount) { onChange(index, row.copy(amount = it)) }
                    CategorySelector(categories, row.categoryId) { onChange(index, row.copy(categoryId = it)) }
                    Text(
                        "Wallet di default: ${wallets.firstOrNull()?.name ?: "Contanti"}",
                        color = LiquidTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (rows.size > 1) {
                        TextButton(onClick = { onRemove(index) }) { Text("Rimuovi", color = Danger) }
                    }
                }
            }
        }
    }
}

private fun statusColor(status: BudgetStatus): Color = when (status) {
    BudgetStatus.SICURO, BudgetStatus.SOSTENIBILE -> Success
    BudgetStatus.AL_LIMITE, BudgetStatus.RISCHIOSO -> Warning
    BudgetStatus.NON_SOSTENIBILE -> Danger
}
