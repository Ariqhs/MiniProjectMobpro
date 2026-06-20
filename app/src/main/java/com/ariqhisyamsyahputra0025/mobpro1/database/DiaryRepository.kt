package com.ariqhisyamsyahputra0025.mobpro1.database

import android.content.Context
import android.util.Log
import com.ariqhisyamsyahputra0025.mobpro1.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class DiaryRepository(
    private val apiService: ApiService,
    private val diaryDao: DiaryDao,
    private val appContext: Context
) {
    val localDiariesFlow: Flow<List<DiaryEntity>> = diaryDao.getAllDiaries()

    suspend fun refreshDiariesFromNetwork(email: String) {
        try {
            val networkData = apiService.getDiaries(email)

            val entities = networkData.map { entry ->
                val remoteUrl = entry.imageUrl ?: ""
                val localPath = if (remoteUrl.isNotEmpty()) {
                    downloadAndCacheImage(remoteUrl, entry.id)
                } else {
                    null
                }

                DiaryEntity(
                    id = entry.id,
                    title = entry.title,
                    foreignAmount = entry.foreignAmount,
                    currencyCode = entry.currencyCode,
                    convertedIdr = entry.convertedIdr,
                    imageUrl = remoteUrl,
                    localImagePath = localPath
                )
            }

            diaryDao.insertAll(entities)

        } catch (e: Exception) {
            Log.e("Repository", "Mode offline aktif: Gagal memuat dari API -> ${e.message}")
        }
    }

    private suspend fun downloadAndCacheImage(url: String, id: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val dir = File(appContext.filesDir, "diary_images")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, "$id.jpg")

                if (file.exists() && file.length() > 0) {
                    return@withContext file.absolutePath
                }

                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    file.absolutePath
                } else {
                    Log.e("Repository", "Gagal download image: HTTP ${connection.responseCode}")
                    null
                }
            } catch (e: Exception) {
                Log.e("Repository", "Gagal download/simpan image '$id': ${e.message}")
                null
            }
        }

    fun deleteLocalImage(id: String) {
        val file = File(File(appContext.filesDir, "diary_images"), "$id.jpg")
        if (file.exists()) file.delete()
    }
}