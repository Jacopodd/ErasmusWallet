@file:UseSerializers(LocalDateTimeSerializer::class)

package com.example.erasmuswallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.erasmuswallet.data.model.WalletType
import com.example.erasmuswallet.data.serialization.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDateTime

@Serializable
@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: WalletType,
    val initialBalance: Double,
    val currency: String = "EUR",
    val colorHex: String? = null,
    val iconName: String? = null,
    val isArchived: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
