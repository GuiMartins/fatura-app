package com.faturaapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.faturaapp.ui.dashboard.DashboardScreen
import com.faturaapp.ui.setup.SetupScreen

private object Rotas {
    const val SETUP = "setup"
    const val DASHBOARD = "dashboard"
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Rotas.SETUP) {
        composable(Rotas.SETUP) {
            SetupScreen(
                onSetupConcluido = {
                    navController.navigate(Rotas.DASHBOARD) {
                        popUpTo(Rotas.SETUP) { inclusive = true }
                    }
                }
            )
        }
        composable(Rotas.DASHBOARD) {
            DashboardScreen()
        }
    }
}
