package com.example.proyectodivisa.dao

import androidx.room.*
import com.example.proyectodivisa.entities.Diviza

@Dao
interface DivizaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDiviza(diviza: Diviza)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDivizas(divizas: List<Diviza>)

    @Query("SELECT * FROM Diviza_table")
    fun getAllDivizas(): List<Diviza>
}
