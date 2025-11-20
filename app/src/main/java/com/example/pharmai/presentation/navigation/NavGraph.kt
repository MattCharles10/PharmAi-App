package com.example.pharmai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pharmai.presentation.screen.DashboardScreen
import com.example.pharmai.presentation.screen.MedicationReminderScreen
import com.example.pharmai.presentation.screen.MedicationSearchScreen
import com.example.pharmai.presentation.screen.PillIdentificationScreen
import com.example.pharmai.presentation.screen.PlaceholderScreen
import com.example.pharmai.presentation.screen.auth.LoginScreen
import com.example.pharmai.presentation.screen.auth.RegisterScreen
import com.example.pharmai.presentation.screen.auth.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route
    ) {
        composable(Screens.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screens.Dashboard.route) {
                        popUpTo(Screens.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screens.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screens.Register.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screens.Dashboard.route) {
                        popUpTo(Screens.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Register.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.Dashboard.route) {
            DashboardScreen(
                onLogout = {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToMedicationSearch = {
                    navController.navigate(Screens.MedicationSearch.route)
                },
                onNavigateToInteractionChecker = {
                    navController.navigate(Screens.InteractionChecker.route)
                },
                onNavigateToReminders = {
                    navController.navigate(Screens.MedicationReminder.route)
                },
                onNavigateToPillIdentification = {
                    navController.navigate(Screens.PillIdentification.route)
                }
            )
        }

        composable(Screens.MedicationSearch.route) {
            MedicationSearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.InteractionChecker.route) {
            PlaceholderScreen(
                title = "Interaction Checker",
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.MedicationReminder.route) {
            MedicationReminderScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screens.PillIdentification.route) {
            PillIdentificationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}