package com.ariqhisyamsyahputra0025.mobpro1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RiwayatKonversi::class], version = 1, exportSchema = false)
abstract class RiwayatDatabase : RoomDatabase() {

    abstract fun riwayatDao(): RiwayatDao

    companion object {
        @Volatile
        private var INSTANCE: RiwayatDatabase? = null

        fun getDatabase(context: Context): RiwayatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RiwayatDatabase::class.java,
                    "riwayat_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}