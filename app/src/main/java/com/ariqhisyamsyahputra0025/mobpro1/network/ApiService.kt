package com.ariqhisyamsyahputra0025.mobpro1.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

private const val BASE_URL = "http://192.168.31.38:8000/api/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiService {

    @GET("diaries")
    suspend fun getDiaries(@Header("Authorization") email: String): List<DiaryEntry>

    @Multipart
    @POST("diaries")
    suspend fun addDiaryEntry(
        @Header("Authorization") email: String,
        @Part("title") title: RequestBody,
        @Part("foreign_amount") foreignAmount: RequestBody,
        @Part("currency_code") currencyCode: RequestBody,
        @Part("converted_idr") convertedIdr: RequestBody,
        @Part image: MultipartBody.Part
    )

    @DELETE("diaries/{id}")
    suspend fun deleteDiaryEntry(
        @Header("Authorization") email: String,
        @Path("id") id: String
    )
}

object DiaryApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}