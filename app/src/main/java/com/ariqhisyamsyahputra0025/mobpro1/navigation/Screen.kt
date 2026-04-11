package com.ariqhisyamsyahputra0025.mobpro1.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("mainScreen")
    data object About : Screen("aboutScreen")
    data object Dollar : Screen("dollar")
    data object Euro : Screen("euro")
    data object JapaneseYen : Screen("japaneseYen")
    data object Mbg : Screen("mbg")
}
