package com.ariqhisyamsyahputra0025.mobpro1.network

sealed class ApiState<out T> {
    object Idle : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Error(val message: String) : ApiState<Nothing>()
}