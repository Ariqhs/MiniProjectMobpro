package com.ariqhisyamsyahputra0025.mobpro1.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tabel_diary")
data class DiaryEntity(
    @PrimaryKey
    val id: String,
    val title: String,

    @SerializedName("foreign_amount")
    val foreignAmount: Double,

    @SerializedName("currency_code")
    val currencyCode: String,

    @SerializedName("converted_idr")
    val convertedIdr: Double,

    @SerializedName("image_url")
    val imageUrl: String,

    val localImagePath: String? = null
)