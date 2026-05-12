package com.ariqhisyamsyahputra0025.mobpro1.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_riwayat")
data class RiwayatKonversi(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mataUang: String,
    val nominal: Float,
    val hasil: Float,
    val tipeKonversi: String
)