package com.example.erasmuswallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.erasmuswallet.data.model.CategoryGroup
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val group: CategoryGroup,
    val iconName: String? = null,
    val colorHex: String? = null,
    val isSensitive: Boolean = false,
    val privacyAlias: String? = null,
    val isActive: Boolean = true
)
