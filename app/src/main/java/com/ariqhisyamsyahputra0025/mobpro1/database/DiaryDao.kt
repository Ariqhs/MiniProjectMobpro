package com.ariqhisyamsyahputra0025.mobpro1.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM tabel_diary")
    fun getAllDiaries(): Flow<List<DiaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(diaries: List<DiaryEntity>)

    @Query("DELETE FROM tabel_diary")
    suspend fun clearAll()
}