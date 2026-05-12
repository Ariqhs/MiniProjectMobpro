package com.ariqhisyamsyahputra0025.mobpro1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RiwayatDao {
    @Insert
    suspend fun insertRiwayat(riwayat: RiwayatKonversi)

    @Query("SELECT * FROM tabel_riwayat ORDER BY id DESC")
    fun getAllRiwayat(): Flow<List<RiwayatKonversi>>

    @Query("DELETE FROM tabel_riwayat")
    suspend fun deleteAllRiwayat()

    @Delete
    suspend fun deleteRiwayat(riwayat: RiwayatKonversi)

    @Update
    suspend fun updateRiwayat(riwayat: RiwayatKonversi)
}