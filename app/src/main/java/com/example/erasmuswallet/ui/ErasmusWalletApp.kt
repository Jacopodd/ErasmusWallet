@file:OptIn(
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)

package com.example.erasmuswallet.ui

import android.content.Intent
import android.graphics.Color.parseColor
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.painterResource
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
import java.time.temporal.ChronoUnit
import java.util.UUID
import com.example.erasmuswallet.R

private data class Destination(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val destinations = listOf(
    Destination("dashboard", "Dashboard", Icons.Default.Home),
    Destination("movimenti", "Movimenti", Icons.Default.SwapHoriz),
    Destination("wallet", "Wallets", Icons.Default.AccountBalanceWallet),
    Destination("simulatore", "Simulatore", Icons.AutoMirrored.Filled.ShowChart)
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
        OnboardingScreen(
            uiState = uiState,
            onSaveWallet = viewModel::saveWallet,
            onDeleteWallet = viewModel::deleteWallet,
            onComplete = viewModel::completeOnboarding
        )
        return
    }

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "dashboard"

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Text("Erasmus Budget Guardian", color = LiquidText)
                    }
                },
                actions = {
                    if (currentRoute == "dashboard") {
                        IconButton(onClick = { navController.navigate("impostazioni") }) {
                            Icon(Icons.Default.Settings, contentDescription = "Impostazioni")
                        }
                    }
                },
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
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            NavHost(
                navController = navController,
                startDestination = "dashboard"
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        uiState = uiState,
                        onScenarioChange = viewModel::setScenario,
                        onOpenReport = { navController.navigate("report") },
                        onOpenRecurring = { navController.navigate("ricorrenti") },
                        onOpenCategories = { navController.navigate("categorie") }
                    )
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
    onSaveWallet: (Long, String, WalletType, Double, String?) -> Unit,
    onDeleteWallet: (WalletEntity) -> Unit,
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
    var walletInputs by rememberSaveable { mutableStateOf(wallets.associate { it.id to it.initialBalance.toString() }) }
    var showWalletDialog by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<WalletEntity?>(null) }
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
        LaunchedEffect(wallets) {
            walletInputs = wallets.associate { wallet ->
                wallet.id to (walletInputs[wallet.id] ?: wallet.initialBalance.toString())
            }.toMutableMap()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassSectionHeader("Wallets")
                TextButton(onClick = {
                    editingWallet = null
                    showWalletDialog = true
                }) {
                    Text("+")
                }
            }
            wallets.forEach { wallet ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(wallet.name, color = LiquidText, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "Saldo iniziale: ${walletInputs[wallet.id].orEmpty()}",
                                    color = LiquidTextSecondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (wallet.iconName != "base-cash") {
                                TextButton(onClick = {
                                    editingWallet = wallet
                                    showWalletDialog = true
                                }) { Text("Modifica") }
                                TextButton(onClick = { onDeleteWallet(wallet) }) { Text("Rimuovi", color = Danger) }
                            } else {
                                TextButton(onClick = {
                                    editingWallet = wallet
                                    showWalletDialog = true
                                }) { Text("Modifica") }
                            }
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
    if (showWalletDialog) {
        WalletDialog(
            existing = editingWallet,
            onDismiss = { showWalletDialog = false },
            onSave = { name, type, balance, color ->
                onSaveWallet(editingWallet?.id ?: 0L, name, type, balance, color)
                showWalletDialog = false
                editingWallet = null
            }
        )
    }
}

@Composable
private fun DashboardScreen(
    uiState: AppUiState,
    onScenarioChange: (ScenarioType) -> Unit,
    onOpenReport: () -> Unit,
    onOpenRecurring: () -> Unit,
    onOpenCategories: () -> Unit
) {
    val summary = uiState.summary ?: return
    var showUpcomingExpenses by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            DashboardHero(summary = summary)
        }
        item {
            ScenarioChooser(selected = uiState.selectedScenario, onScenarioChange = onScenarioChange)
        }
        item {
            StatusHero(summary, uiState.selectedScenario)
        }
        item {
            SummaryGrid(summary, uiState.settings)
        }
        item {
            QuickActionsCard(
                onOpenReport = onOpenReport,
                onOpenRecurring = onOpenRecurring,
                onOpenCategories = onOpenCategories,
                onOpenUpcomingExpenses = { showUpcomingExpenses = true }
            )
        }
        item {
            SectionTitle("Wallet")
        }
        items(summary.walletSummaries) { wallet ->
            MetricCard(
                modifier = Modifier.fillMaxWidth(),
                title = wallet.wallet.name,
                value = wallet.currentBalance.toEuro(),
                subtitle = "${(wallet.percentageOnTotal * 100).toInt()}% del totale"
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
    if (showUpcomingExpenses) {
        UpcomingEventsDialog(
            title = "Prossime spese",
            events = summary.upcomingExpenses,
            onDismiss = { showUpcomingExpenses = false }
        )
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
            GlassSectionHeader("Wallets")
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
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .background(colorFromHex(summary.wallet.colorHex ?: walletColorOptions.first().hex), RoundedCornerShape(50.dp))
                                        )
                                        Text(summary.wallet.name, color = LiquidText, fontWeight = FontWeight.SemiBold)
                                    }
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
                                if (summary.wallet.iconName != "base-cash") {
                                    TextButton(onClick = { onDelete(summary.wallet) }) {
                                        Text("Elimina", color = Danger)
                                    }
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
private fun DashboardHero(summary: BudgetSummary) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Liquid glass budget", style = MaterialTheme.typography.headlineSmall, color = LiquidText)
                Text(
                    "Dashboard compatta e professionale per il tuo Erasmus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LiquidTextSecondary
                )
                Text(
                    "Saldo totale: ${summary.totalBalance.toEuro()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Aqua
                )
            }
        }
    }
}

@Composable
private fun StatusHero(summary: BudgetSummary, selectedScenario: ScenarioType) {
    val color = statusColor(summary.status)
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Stato ${summary.status.name.replace("_", " ")}", fontWeight = FontWeight.Bold, color = color)
            Text("Prospettiva attiva: ${selectedScenario.name.lowercase().replaceFirstChar { it.uppercase() }}", color = LiquidTextSecondary)
            Text("Saldo totale attuale: ${summary.totalBalance.toEuro()}", color = LiquidText)
            Text("Obiettivo finale protetto: ${summary.protectedReserve.toEuro()}", color = LiquidText)
            Text(
                "Soldi totali spendibili: ${summary.spendableMoney.toEuro()}",
                color = LiquidText
            )
            Text(
                "Formula: saldo + entrate fisse - spese fisse - obiettivo protetto",
                color = LiquidTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            Text("Proiezione saldo finale attesa: ${summary.finalProjection.toEuro()}", color = LiquidText)
            Text(
                "Include anche i movimenti futuri programmati",
                color = LiquidTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                "Punto di liquidità minima: ${summary.minimumLiquidity.balance.toEuro()} il ${summary.minimumLiquidity.date.toItalianDate()}",
                color = LiquidText
            )
            Text(
                "Momento più critico della cassa prevista",
                color = LiquidTextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SummaryGrid(summary: BudgetSummary, settings: ErasmusSettingsEntity?) {
    val totalDays = if (settings != null) {
        maxOf(1L, ChronoUnit.DAYS.between(settings.startDate, settings.endDate).coerceAtLeast(1))
    } else {
        summary.daysRemaining
    }
    val daysElapsed = (totalDays - summary.daysRemaining).coerceAtLeast(0)
    val metrics = listOf(
        DashboardMetric(
            title = "Giorni rimanenti",
            value = summary.daysRemaining.toString(),
            subtitle = "Fino a fine Erasmus",
            progress = (daysElapsed.toDouble() / totalDays.toDouble()).coerceIn(0.0, 1.0),
            progressLabel = "Avanzamento periodo"
        ),
        DashboardMetric(
            title = "Budget giornaliero",
            value = summary.dailyBudget.toEuro(),
            subtitle = "Quota media al giorno",
            progress = (summary.dailyBudget / maxOf(summary.weeklyBudget, 1.0)).coerceIn(0.0, 1.0),
            progressLabel = "Quota del settimanale"
        ),
        DashboardMetric(
            title = "Budget settimanale",
            value = summary.weeklyBudget.toEuro(),
            subtitle = "Quota media alla settimana",
            progress = (summary.weeklyBudget / maxOf(summary.totalBalance + summary.futureFixedIncome - summary.futureFixedExpense, 1.0)).coerceIn(0.0, 1.0),
            progressLabel = "Peso sul totale"
        ),
        DashboardMetric(
            title = "Speso questa settimana",
            value = summary.spentThisWeek.toEuro(),
            subtitle = "Movimenti confermati",
            progress = (summary.spentThisWeek / maxOf(summary.weeklyBudget, 1.0)).coerceIn(0.0, 1.0),
            progressLabel = "Budget usato"
        ),
        DashboardMetric(
            title = "Rimanente questa settimana",
            value = summary.remainingThisWeek.toEuro(),
            subtitle = "Budget residuo",
            progress = (summary.remainingThisWeek / maxOf(summary.weeklyBudget, 1.0)).coerceIn(0.0, 1.0),
            progressLabel = "Budget disponibile"
        )
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            metrics.take(3).forEach { metric ->
                CompactMetricCard(
                    modifier = Modifier.weight(1f),
                    metric = metric
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            metrics.drop(3).forEach { metric ->
                CompactMetricCard(
                    modifier = Modifier.weight(1f),
                    metric = metric
                )
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    onOpenReport: () -> Unit,
    onOpenRecurring: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenUpcomingExpenses: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassSectionHeader("Collegamenti rapidi")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onOpenReport, modifier = Modifier.weight(1f)) { Text("Report") }
                OutlinedButton(onClick = onOpenRecurring, modifier = Modifier.weight(1f)) { Text("Ricorrenti") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onOpenCategories, modifier = Modifier.weight(1f)) { Text("Categorie") }
                OutlinedButton(onClick = onOpenUpcomingExpenses, modifier = Modifier.weight(1f)) { Text("Prossime spese") }
            }
        }
    }
}

private data class DashboardMetric(
    val title: String,
    val value: String,
    val subtitle: String,
    val progress: Double,
    val progressLabel: String
)

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
            Text("Punto di liquidità minima: ${result.minimumLiquidity.balance.toEuro()} il ${result.minimumLiquidity.date.toItalianDate()}", color = LiquidText)
            Text("Massimo sostenibile: ${result.maximumSustainableAmount.toEuro()}", color = LiquidText)
            Text("Riduzione necessaria: ${result.reductionNeeded.toEuro()}", color = LiquidText)
            Text("Extra necessario: ${result.extraIncomeNeeded.toEuro()}", color = LiquidText)
        }
    }
}

@Composable
private fun ScenarioChooser(selected: ScenarioType, onScenarioChange: (ScenarioType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GlassSectionHeader("Prospettive")
        EnumChipRow(
            items = ScenarioType.values().toList(),
            selected = selected,
            label = { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
            onSelected = onScenarioChange
        )
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
            FilterChip(
                selected = item == selected,
                onClick = { onSelected(item) },
                label = { Text(label(item)) },
                leadingIcon = if (item == selected) ({ Icon(Icons.AutoMirrored.Filled.PlaylistAddCheck, null, modifier = Modifier.size(16.dp)) }) else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricBlue.copy(alpha = 0.28f),
                    selectedLabelColor = LiquidText,
                    selectedLeadingIconColor = CyanGlow,
                    containerColor = LiquidSurfaceSoft,
                    labelColor = LiquidTextSecondary
                )
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    trailing: @Composable (() -> Unit)? = null
) {
    GlassCard(modifier = modifier) {
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
private fun CompactMetricCard(
    modifier: Modifier = Modifier,
    metric: DashboardMetric
) {
    GlassCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(metric.title, color = LiquidTextSecondary, style = MaterialTheme.typography.labelMedium)
            Text(metric.value, color = LiquidText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(metric.subtitle, color = LiquidTextSecondary, style = MaterialTheme.typography.bodySmall)
            LinearProgressIndicator(
                progress = { metric.progress.toFloat() },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = CyanGlow,
                trackColor = LiquidSurfaceSoft
            )
            Text(
                "${metric.progressLabel} · ${(metric.progress * 100).toInt()}%",
                color = Aqua,
                style = MaterialTheme.typography.labelSmall
            )
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
    var color by rememberSaveable(existing?.id) { mutableStateOf(existing?.colorHex ?: walletColorOptions.first().hex) }
    var showPalette by remember { mutableStateOf(false) }
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
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Colore wallet", color = LiquidTextSecondary, style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .border(1.dp, LiquidBorderStrong, RoundedCornerShape(14.dp))
                                .background(colorFromHex(color), RoundedCornerShape(14.dp))
                        )
                        TextButton(onClick = { showPalette = true }) {
                            Text("Scegli colore")
                        }
                    }
                    Text(
                        "Anteprima visiva, salvataggio interno in HEX",
                        color = LiquidTextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                EnumChipRow(items = WalletType.values().toList(), selected = type, label = { it.name }, onSelected = { type = it })
            }
        }
    )
    if (showPalette) {
        WalletColorPaletteDialog(
            selectedHex = color,
            onDismiss = { showPalette = false },
            onSelect = {
                color = it
                showPalette = false
            }
        )
    }
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

private data class WalletColorOption(val name: String, val hex: String)

private val walletColorOptions = listOf(
    WalletColorOption("Cyan", "#58F0FF"),
    WalletColorOption("Aqua", "#35F0D3"),
    WalletColorOption("Blue", "#2A7BFF"),
    WalletColorOption("Purple", "#9B5CFF"),
    WalletColorOption("Deep Blue", "#1E3FFF"),
    WalletColorOption("Night", "#0E1733"),
    WalletColorOption("Mint", "#73F7CE"),
    WalletColorOption("Pink", "#E66BFF")
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
private fun WalletColorPaletteDialog(
    selectedHex: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Chiudi") } },
        title = { Text("Scegli colore wallet") },
        containerColor = LiquidSurfaceElevated,
        textContentColor = LiquidText,
        titleContentColor = LiquidText,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .border(1.dp, LiquidBorderStrong, RoundedCornerShape(20.dp))
                        .background(colorFromHex(selectedHex), RoundedCornerShape(20.dp))
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    walletColorOptions.forEach { option ->
                        FilterChip(
                            selected = selectedHex.equals(option.hex, ignoreCase = true),
                            onClick = { onSelect(option.hex) },
                            label = { Text(option.name) },
                            leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(colorFromHex(option.hex), RoundedCornerShape(50.dp))
                        )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBlue.copy(alpha = 0.28f),
                                selectedLabelColor = LiquidText,
                                selectedLeadingIconColor = LiquidText,
                                containerColor = LiquidSurfaceSoft,
                                labelColor = LiquidTextSecondary
                            )
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun UpcomingEventsDialog(
    title: String,
    events: List<com.example.erasmuswallet.domain.model.FutureEvent>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Chiudi") } },
        title = { Text(title) },
        containerColor = LiquidSurfaceElevated,
        textContentColor = LiquidText,
        titleContentColor = LiquidText,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (events.isEmpty()) {
                    Text("Nessuna spesa futura prevista.")
                } else {
                    events.take(10).forEach { event ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(event.title, color = LiquidText, fontWeight = FontWeight.SemiBold)
                                Text(event.date.toItalianDate(), color = LiquidTextSecondary, style = MaterialTheme.typography.bodySmall)
                                Text((-event.amount).toEuro(), color = Danger)
                            }
                        }
                    }
                }
            }
        }
    )
}

private fun colorFromHex(hex: String): Color = try {
    Color(parseColor(hex))
} catch (_: IllegalArgumentException) {
    Color(parseColor(walletColorOptions.first().hex))
}

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
