package com.example.proyectodivisa.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ExchangeRates.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_RATES = "rates"
        private const val COLUMN_CURRENCY = "currency"
        private const val COLUMN_RATE = "rate"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_RATES (
                $COLUMN_CURRENCY TEXT PRIMARY KEY,
                $COLUMN_RATE REAL
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RATES")
        onCreate(db)
    }

    fun insertOrUpdateRate(currency: String, rate: Double) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CURRENCY, currency)
            put(COLUMN_RATE, rate)
        }
        db.insertWithOnConflict(TABLE_RATES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }
}
