package com.example.proyectodivisa.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectodivisa.entities.Actualizacion
import kotlinx.coroutines.flow.Flow

@Dao
interface ActualizacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertActualizacion(actualizacion: Actualizacion)

    @Query("SELECT * FROM Actualizacion_table ")
    fun getActualizacion(): Actualizacion?

    @Query("SELECT * FROM Actualizacion_table ORDER BY time_last_update_unix DESC LIMIT 1")
    fun getLatestActualizacion(): Actualizacion?
}