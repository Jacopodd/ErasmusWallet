@file:UseSerializers(LocalDateSerializer::class)

package com.example.erasmuswallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.erasmuswallet.data.model.ScenarioType
import com.example.erasmuswallet.data.serialization.LocalDateSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.time.LocalDate

@Serializable
@Entity(tableName = "erasmus_settings")
data class ErasmusSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val finalGoal: Double,
    val emergencyFund: Double = 0.0,
    val safeThreshold: Double = 100.0,
    val minimumLiquidityThreshold: Double = 150.0,
    val defaultScenario: ScenarioType = ScenarioType.REALISTICO,
    val privacyMode: Boolean = false,
    val pinEnabled: Boolean = false
)
