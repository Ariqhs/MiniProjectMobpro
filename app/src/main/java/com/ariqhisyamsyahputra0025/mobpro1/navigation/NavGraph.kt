package com.ariqhisyamsyahputra0025.mobpro1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.AboutScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.DollarScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.EuroScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.JapaneseYenScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.MainScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.MbgScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable(route = Screen.About.route) {
            AboutScreen(navController)
        }
        composable(route = Screen.Dollar.route) {
            DollarScreen(navController)
        }
        composable (route = Screen.Euro.route) {
            EuroScreen(navController)
        }
        composable(route = Screen.JapaneseYen.route) {
            JapaneseYenScreen(navController)
        }
        composable(route = Screen.Mbg.route) {
            MbgScreen(navController)
        }
    }
}