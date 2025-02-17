package com.example.proyectodivisas.Data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.processor.Context

@Database(entities = [ExchangeRate::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exchangeRateDao(): ExchangeRateDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exchange_rates_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}