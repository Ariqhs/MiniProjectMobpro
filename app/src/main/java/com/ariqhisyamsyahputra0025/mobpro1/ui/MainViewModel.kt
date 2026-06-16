package com.ariqhisyamsyahputra0025.mobpro1.ui

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ariqhisyamsyahputra0025.mobpro1.network.ApiState
import com.ariqhisyamsyahputra0025.mobpro1.network.DiaryApi
import com.ariqhisyamsyahputra0025.mobpro1.network.DiaryEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    private val _diaries = MutableStateFlow<ApiState<List<DiaryEntry>>>(ApiState.Idle)
    val diaries: StateFlow<ApiState<List<DiaryEntry>>> = _diaries

    fun getDiaries(email: String) {
        viewModelScope.launch {
            _diaries.value = ApiState.Loading
            try {
                val result = DiaryApi.retrofitService.getDiaries(email)
                _diaries.value = ApiState.Success(result)
            } catch (e: Exception) {
                _diaries.value = ApiState.Error(e.message ?: "No Internet Connection")
            }
        }
    }

    fun addEntry(email: String, title: String, foreignAmount: Double, currencyCode: String, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val exchangeRate = when (currencyCode) {
                    "USD" -> 16000.0
                    "JPY" -> 104.0
                    "EUR" -> 17500.0
                    else -> 1.0
                }
                val convertedIdr = foreignAmount * exchangeRate

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                val requestBody = byteArray.toRequestBody("image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
                val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

                DiaryApi.retrofitService.addDiaryEntry(
                    email = email,
                    title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                    foreignAmount = foreignAmount.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    currencyCode = currencyCode.toRequestBody("text/plain".toMediaTypeOrNull()),
                    convertedIdr = convertedIdr.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    image = imagePart
                )

                getDiaries(email)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to add entry: ${e.message}")
            }
        }
    }

    fun deleteEntry(email: String, id: String) {
        viewModelScope.launch {
            try {
                DiaryApi.retrofitService.deleteDiaryEntry(email, id)
                getDiaries(email)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to delete entry: ${e.message}")
            }
        }
    }
}