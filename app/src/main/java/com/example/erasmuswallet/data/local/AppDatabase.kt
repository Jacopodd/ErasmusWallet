package com.example.erasmuswallet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.erasmuswallet.data.local.dao.CategoryDao
import com.example.erasmuswallet.data.local.dao.MovementDao
import com.example.erasmuswallet.data.local.dao.RecurringRuleDao
import com.example.erasmuswallet.data.local.dao.SettingsDao
import com.example.erasmuswallet.data.local.dao.WalletDao
import com.example.erasmuswallet.data.local.entity.CategoryEntity
import com.example.erasmuswallet.data.local.entity.ErasmusSettingsEntity
import com.example.erasmuswallet.data.local.entity.MovementEntity
import com.example.erasmuswallet.data.local.entity.RecurringRuleEntity
import com.example.erasmuswallet.data.local.entity.WalletEntity

@Database(
    entities = [
        WalletEntity::class,
        CategoryEntity::class,
        MovementEntity::class,
        RecurringRuleEntity::class,
        ErasmusSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun categoryDao(): CategoryDao
    abstract fun movementDao(): MovementDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "erasmus_wallet.db"
                ).build().also { instance = it }
            }
    }
}
