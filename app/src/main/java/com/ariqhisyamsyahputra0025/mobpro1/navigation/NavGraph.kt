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
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.EditScreen
import com.ariqhisyamsyahputra0025.mobpro1.ui.screen.MainScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController = navController)
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

        composable(
            route = "edit/{id}/{nama}/{nominal}/{kurs}/{simbolAsal}/{simbolTujuan}/{tipe}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
                navArgument("nama") { type = NavType.StringType },
                navArgument("nominal") { type = NavType.FloatType },
                navArgument("kurs") { type = NavType.FloatType },
                navArgument("simbolAsal") { type = NavType.StringType },
                navArgument("simbolTujuan") { type = NavType.StringType },
                navArgument("tipe") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getInt("id") ?: 0
            val nama = backStackEntry.arguments?.getString("nama") ?: ""
            val nominal = backStackEntry.arguments?.getFloat("nominal") ?: 0f
            backStackEntry.arguments?.getFloat("kurs") ?: 0f
            val simbolAsal = backStackEntry.arguments?.getString("simbolAsal") ?: ""
            val simbolTujuan = backStackEntry.arguments?.getString("simbolTujuan") ?: ""
            val tipe = backStackEntry.arguments?.getString("tipe") ?: ""

            EditScreen(
                navController = navController,
                nama = nama,
                nominalLama = nominal,
                simbolAsal = simbolAsal,
                simbolTujuan = simbolTujuan,
                tipeLama = tipe
            )
        }
    }
}