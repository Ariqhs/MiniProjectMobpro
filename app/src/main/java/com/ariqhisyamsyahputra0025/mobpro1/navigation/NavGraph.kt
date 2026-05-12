package com.ariqhisyamsyahputra0025.mobpro1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.AboutScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.CalculatorScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.MainScreen

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

        composable(
            route = "calculator/{nama}/{kurs}/{simbolAsal}/{simbolTujuan}",
            arguments = listOf(
                navArgument("nama") { type = NavType.StringType },
                navArgument("kurs") { type = NavType.FloatType },
                navArgument("simbolAsal") { type = NavType.StringType },
                navArgument("simbolTujuan") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val nama = backStackEntry.arguments?.getString("nama") ?: ""
            val kurs = backStackEntry.arguments?.getFloat("kurs") ?: 0f
            val simbolAsal = backStackEntry.arguments?.getString("simbolAsal") ?: ""
            val simbolTujuan = backStackEntry.arguments?.getString("simbolTujuan") ?: ""

            CalculatorScreen(
                navController = navController,
                namaMataUang = nama,
                kurs = kurs,
                simbolAsal = simbolAsal,
                simbolTujuan = simbolTujuan
            )
        }
    }
}