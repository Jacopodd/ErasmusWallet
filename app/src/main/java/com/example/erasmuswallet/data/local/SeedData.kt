package com.example.erasmuswallet.data.local

import com.example.erasmuswallet.data.local.entity.CategoryEntity
import com.example.erasmuswallet.data.local.entity.WalletEntity
import com.example.erasmuswallet.data.model.CategoryGroup
import com.example.erasmuswallet.data.model.WalletType
import java.time.LocalDateTime

object SeedData {
    fun defaultWallets(now: LocalDateTime): List<WalletEntity> = listOf(
        WalletEntity(name = "Intesa", type = WalletType.CARD, initialBalance = 0.0, colorHex = "#17624A", createdAt = now, updatedAt = now),
        WalletEntity(name = "Revolut", type = WalletType.CARD, initialBalance = 0.0, colorHex = "#2057FF", createdAt = now, updatedAt = now),
        WalletEntity(name = "Trade Republic", type = WalletType.INVESTMENT, initialBalance = 0.0, colorHex = "#101820", createdAt = now, updatedAt = now),
        WalletEntity(name = "Contanti", type = WalletType.CASH, initialBalance = 0.0, colorHex = "#B7791F", createdAt = now, updatedAt = now)
    )

    fun defaultCategories(): List<CategoryEntity> = listOf(
        CategoryEntity(name = "Affitto", group = CategoryGroup.OBBLIGATORIE),
        CategoryEntity(name = "Bollette", group = CategoryGroup.OBBLIGATORIE),
        CategoryEntity(name = "Spese casa", group = CategoryGroup.OBBLIGATORIE),
        CategoryEntity(name = "Trasporti", group = CategoryGroup.OBBLIGATORIE),
        CategoryEntity(name = "Telefono", group = CategoryGroup.OBBLIGATORIE),
        CategoryEntity(name = "Documenti", group = CategoryGroup.OBBLIGATORIE),
        CategoryEntity(name = "Assicurazione", group = CategoryGroup.OBBLIGATORIE),
        CategoryEntity(name = "Spesa / Cibo", group = CategoryGroup.NECESSARIE_FLESSIBILI),
        CategoryEntity(name = "Farmacia", group = CategoryGroup.NECESSARIE_FLESSIBILI),
        CategoryEntity(name = "Prodotti casa", group = CategoryGroup.NECESSARIE_FLESSIBILI),
        CategoryEntity(name = "Vestiti necessari", group = CategoryGroup.NECESSARIE_FLESSIBILI),
        CategoryEntity(name = "Serate", group = CategoryGroup.SVAGO),
        CategoryEntity(name = "Ristoranti", group = CategoryGroup.SVAGO),
        CategoryEntity(name = "Viaggi", group = CategoryGroup.SVAGO),
        CategoryEntity(name = "Eventi", group = CategoryGroup.SVAGO),
        CategoryEntity(
            name = "Svago sessuale",
            group = CategoryGroup.SVAGO,
            isSensitive = true,
            privacyAlias = "Extra personali"
        ),
        CategoryEntity(name = "Imprevisti", group = CategoryGroup.ALTRO),
        CategoryEntity(name = "Regali", group = CategoryGroup.ALTRO),
        CategoryEntity(name = "Commissioni bancarie", group = CategoryGroup.ALTRO),
        CategoryEntity(name = "Prelievi", group = CategoryGroup.ALTRO),
        CategoryEntity(name = "Trasferimenti", group = CategoryGroup.ALTRO),
        CategoryEntity(name = "Altro", group = CategoryGroup.ALTRO)
    )
}
