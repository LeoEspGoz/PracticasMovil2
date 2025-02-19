package com.example.proyectodivisa.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Actualizacion_table")
data class Actualizacion(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "actualizacion_id") val id: Int = 0,
    // Marca de tiempo UNIX de la última actualización
    @ColumnInfo(name = "time_last_update_unix") val timeLastUpdateUnix: Long,
    // Fecha y hora de la última actualización en formato UTC
    @ColumnInfo(name = "time_last_update_utc") val timeLastUpdateUtc: String,
    // Marca de tiempo UNIX de la próxima actualización
    @ColumnInfo(name = "time_next_update_unix") val timeNextUpdateUnix: Long,
    // Fecha y hora de la próxima actualización en formato UTC
    @ColumnInfo(name = "time_next_update_utc") val timeNextUpdateUtc: String,
    // Moneda base
    @ColumnInfo(name = "base_code") val baseCode: String,
    @ColumnInfo(name = "result") val result: String,
    @ColumnInfo(name = "documentation") val documentation: String,
    @ColumnInfo(name = "terms_of_use") val termsOfUse: String
)
