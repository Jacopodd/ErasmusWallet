package com.example.erasmuswallet.data.repository

import androidx.room.withTransaction
import com.example.erasmuswallet.data.local.AppDatabase
import com.example.erasmuswallet.data.local.SeedData
import com.example.erasmuswallet.data.local.entity.CategoryEntity
import com.example.erasmuswallet.data.local.entity.ErasmusSettingsEntity
import com.example.erasmuswallet.data.local.entity.MovementEntity
import com.example.erasmuswallet.data.local.entity.RecurringRuleEntity
import com.example.erasmuswallet.data.local.entity.WalletEntity
import com.example.erasmuswallet.data.model.IncomeReliability
import com.example.erasmuswallet.data.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class AppRepository(
    private val database: AppDatabase,
    private val json: Json = Json { prettyPrint = true; ignoreUnknownKeys = true }
) {
    val wallets: Flow<List<WalletEntity>> = database.walletDao().observeAll()
    val categories: Flow<List<CategoryEntity>> = database.categoryDao().observeAll()
    val movements: Flow<List<MovementEntity>> = database.movementDao().observeAll()
    val recurringRules: Flow<List<RecurringRuleEntity>> = database.recurringRuleDao().observeAll()
    val settings: Flow<ErasmusSettingsEntity?> = database.settingsDao().observe()

    suspend fun seedDefaultsIfNeeded() {
        database.withTransaction {
            val now = LocalDateTime.now()
            if (database.walletDao().count() == 0) {
                database.walletDao().insertAll(SeedData.defaultWallets(now))
            }
            if (database.categoryDao().count() == 0) {
                database.categoryDao().insertAll(SeedData.defaultCategories())
            }
        }
    }

    suspend fun saveOnboarding(
        settings: ErasmusSettingsEntity,
        walletBalances: Map<Long, Double>,
        plannedIncomes: List<MovementEntity>,
        recurringExpenses: List<RecurringRuleEntity>
    ) {
        database.withTransaction {
            database.settingsDao().upsert(settings)
            walletsSnapshot().forEach { wallet ->
                val amount = walletBalances[wallet.id] ?: wallet.initialBalance
                database.walletDao().update(
                    wallet.copy(initialBalance = amount, updatedAt = LocalDateTime.now())
                )
            }
            if (plannedIncomes.isNotEmpty()) database.movementDao().insertAll(plannedIncomes)
            if (recurringExpenses.isNotEmpty()) database.recurringRuleDao().insertAll(recurringExpenses)
        }
    }

    suspend fun walletsSnapshot(): List<WalletEntity> = wallets.first()
    suspend fun categoriesSnapshot(): List<CategoryEntity> = categories.first()
    suspend fun movementsSnapshot(): List<MovementEntity> = movements.first()
    suspend fun recurringRulesSnapshot(): List<RecurringRuleEntity> = recurringRules.first()
    suspend fun settingsSnapshot(): ErasmusSettingsEntity? = database.settingsDao().get()

    suspend fun upsertWallet(wallet: WalletEntity) {
        if (wallet.id == 0L) database.walletDao().insert(wallet) else database.walletDao().update(wallet)
    }

    suspend fun archiveWallet(wallet: WalletEntity, archived: Boolean) {
        database.walletDao().update(wallet.copy(isArchived = archived, updatedAt = LocalDateTime.now()))
    }

    suspend fun deleteWallet(wallet: WalletEntity) {
        database.withTransaction {
            database.movementDao().deleteByWalletId(wallet.id)
            database.recurringRuleDao().deleteByWalletId(wallet.id)
            database.walletDao().deleteById(wallet.id)
            if (database.walletDao().count() == 0) {
                val now = LocalDateTime.now()
                database.walletDao().insert(
                    WalletEntity(
                        name = "Contanti",
                        type = com.example.erasmuswallet.data.model.WalletType.CASH,
                        initialBalance = 0.0,
                        colorHex = "#35F0D3",
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }
        }
    }

    suspend fun upsertCategory(category: CategoryEntity) {
        if (category.id == 0L) database.categoryDao().insert(category) else database.categoryDao().update(category)
    }

    suspend fun archiveCategory(category: CategoryEntity, active: Boolean) {
        database.categoryDao().update(category.copy(isActive = active))
    }

    suspend fun addMovement(movement: MovementEntity) {
        database.movementDao().insert(movement)
    }

    suspend fun updateMovement(movement: MovementEntity) {
        database.movementDao().update(movement.copy(updatedAt = LocalDateTime.now()))
    }

    suspend fun deleteMovement(movement: MovementEntity) {
        val groupId = movement.transferGroupId
        if (movement.type == TransactionType.TRANSFER && !groupId.isNullOrBlank()) {
            database.movementDao().deleteByTransferGroup(groupId)
        } else {
            database.movementDao().deleteById(movement.id)
        }
    }

    suspend fun addTransfer(
        fromWalletId: Long,
        toWalletId: Long,
        amount: Double,
        date: LocalDate,
        description: String
    ) {
        val now = LocalDateTime.now()
        val groupId = UUID.randomUUID().toString()
        val out = MovementEntity(
            walletId = fromWalletId,
            amount = amount,
            type = TransactionType.TRANSFER,
            date = date,
            title = if (description.isBlank()) "Trasferimento in uscita" else "Trasferimento in uscita: $description",
            isConfirmed = true,
            transferGroupId = groupId,
            createdAt = now,
            updatedAt = now
        )
        val incoming = MovementEntity(
            walletId = toWalletId,
            amount = amount,
            type = TransactionType.TRANSFER,
            date = date,
            title = if (description.isBlank()) "Trasferimento in entrata" else "Trasferimento in entrata: $description",
            isConfirmed = true,
            transferGroupId = groupId,
            createdAt = now,
            updatedAt = now
        )
        database.movementDao().insertAll(listOf(out, incoming))
    }

    suspend fun upsertRecurringRule(rule: RecurringRuleEntity) {
        if (rule.id == 0L) database.recurringRuleDao().insert(rule) else database.recurringRuleDao().update(rule)
    }

    suspend fun toggleRecurringRule(rule: RecurringRuleEntity, active: Boolean) {
        database.recurringRuleDao().update(rule.copy(isActive = active, updatedAt = LocalDateTime.now()))
    }

    suspend fun upsertSettings(settings: ErasmusSettingsEntity) {
        database.settingsDao().upsert(settings)
    }

    suspend fun resetAllData() {
        database.withTransaction {
            database.movementDao().deleteAll()
            database.recurringRuleDao().deleteAll()
            database.settingsDao().deleteAll()
            database.categoryDao().deleteAll()
            database.walletDao().deleteAll()
        }
        seedDefaultsIfNeeded()
    }

    suspend fun exportBackup(): String {
        val payload = BackupPayload(
            wallets = walletsSnapshot(),
            categories = categoriesSnapshot(),
            movements = movementsSnapshot(),
            recurringRules = recurringRulesSnapshot(),
            settings = settingsSnapshot()
        )
        return json.encodeToString(payload)
    }

    suspend fun importBackup(payloadJson: String) {
        val payload = json.decodeFromString<BackupPayload>(payloadJson)
        database.withTransaction {
            database.movementDao().deleteAll()
            database.recurringRuleDao().deleteAll()
            database.settingsDao().deleteAll()
            database.categoryDao().deleteAll()
            database.walletDao().deleteAll()
            database.walletDao().insertAll(payload.wallets)
            database.categoryDao().insertAll(payload.categories)
            database.movementDao().insertAll(payload.movements)
            database.recurringRuleDao().insertAll(payload.recurringRules)
            payload.settings?.let { database.settingsDao().upsert(it) }
        }
    }

    companion object {
        fun plannedIncome(
            walletId: Long,
            amount: Double,
            title: String,
            reliability: IncomeReliability,
            date: LocalDate?
        ): MovementEntity {
            val now = LocalDateTime.now()
            return MovementEntity(
                walletId = walletId,
                amount = amount,
                type = TransactionType.INCOME,
                date = date ?: LocalDate.now(),
                title = title,
                isPlanned = true,
                isConfirmed = reliability == IncomeReliability.RICEVUTA,
                incomeReliability = reliability,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}

@Serializable
data class BackupPayload(
    val wallets: List<WalletEntity>,
    val categories: List<CategoryEntity>,
    val movements: List<MovementEntity>,
    val recurringRules: List<RecurringRuleEntity>,
    val settings: ErasmusSettingsEntity?
)
