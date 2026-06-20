package com.ariqhisyamsyahputra0025.mobpro1.network

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://miniprojectmobpro-production-e5b0.up.railway.app/api/"

private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(client)
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