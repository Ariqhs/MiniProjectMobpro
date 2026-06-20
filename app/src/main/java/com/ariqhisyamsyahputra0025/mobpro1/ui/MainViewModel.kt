package com.ariqhisyamsyahputra0025.mobpro1.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ariqhisyamsyahputra0025.mobpro1.database.DiaryDatabase
import com.ariqhisyamsyahputra0025.mobpro1.database.DiaryRepository
import com.ariqhisyamsyahputra0025.mobpro1.network.DiaryApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DiaryRepository(
        apiService = DiaryApi.retrofitService,
        diaryDao = DiaryDatabase.getDatabase(application).diaryDao(),
        appContext = application.applicationContext
    )

    val diaries = repository.localDiariesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getDiaries(email: String) {
        viewModelScope.launch {
            repository.refreshDiariesFromNetwork(email)
        }
    }

    fun addEntry(email: String, title: String, amount: Double, currency: String, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val amountBody = amount.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val currencyBody = currency.toRequestBody("text/plain".toMediaTypeOrNull())
                val idrBody = "0".toRequestBody("text/plain".toMediaTypeOrNull())

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                val imageBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", "diary.jpg", imageBody)

                DiaryApi.retrofitService.addDiaryEntry(email, titleBody, amountBody, currencyBody, idrBody, imagePart)

                repository.refreshDiariesFromNetwork(email)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Gagal menambah data: ${e.message}")
            }
        }
    }

    fun deleteEntry(email: String, id: String) {
        viewModelScope.launch {
            try {
                DiaryApi.retrofitService.deleteDiaryEntry(email, id)
                repository.deleteLocalImage(id)

                repository.refreshDiariesFromNetwork(email)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Gagal menghapus data: ${e.message}")
            }
        }
    }
}