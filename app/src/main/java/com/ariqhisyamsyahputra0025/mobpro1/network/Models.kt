package com.ariqhisyamsyahputra0025.mobpro1.network

import com.google.gson.annotations.SerializedName

data class DiaryEntry(
    val id: String,
    @SerializedName("user_email") val userEmail: String,
    val title: String,
    @SerializedName("foreign_amount") val foreignAmount: Double,
    @SerializedName("currency_code") val currencyCode: String,
    @SerializedName("converted_idr") val convertedIdr: Double,
    @SerializedName("image_path") val imagePath: String,
    // We add this to hold the full URL from Laravel
    @SerializedName("image_url") val imageUrl: String? = null
)
