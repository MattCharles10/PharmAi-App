package com.example.pharmai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pharmai.presentation.screen.HomeScreen
import com.example.pharmai.presentation.screen.InteractionCheckerScreen
import com.example.pharmai.presentation.screen.MainScreen
import com.example.pharmai.presentation.screen.MedicationReminderScreen
import com.example.pharmai.presentation.screen.MedicationSearchScreen
import com.example.pharmai.presentation.screen.PillIdentificationScreen
import com.example.pharmai.presentation.screen.ProfileScreen
import com.example.pharmai.presentation.screen.SettingsScreen
import com.example.pharmai.presentation.screen.auth.LoginScreen
import com.example.pharmai.presentation.screen.auth.RegisterScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Home.route
    ) {
        composable(Screens.Home.route) {
            HomeScreen(
                onLoginClick = { navController.navigate(Screens.Login.route) }
            )
        }

        composable(Screens.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screens.Main.route) {
                        popUpTo(Screens.Home.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screens.Register.route)
                }
            )
        }

        composable(Screens.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screens.Main.route) {
                        popUpTo(Screens.Home.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screens.Main.route) {
            MainScreen(
                onMedicationSearch = { navController.navigate(Screens.MedicationSearch.route) },
                onInteractionCheck = { navController.navigate(Screens.InteractionChecker.route) },
                onPillIdentification = { navController.navigate(Screens.PillIdentification.route) },
                onProfile = { navController.navigate(Screens.Profile.route) }
            )
        }

        composable(Screens.MedicationSearch.route) {
            MedicationSearchScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screens.InteractionChecker.route) {
            InteractionCheckerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screens.PillIdentification.route) {
            PillIdentificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screens.Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfile = {
                    // TODO: Implement edit profile screen
                    println("Edit profile clicked")
                },
                onMedicationHistory = {
                    // TODO: Implement medication history screen
                    println("Medication history clicked")
                },
                onSettings = {
                    println("Settings clicked - navigating to settings")
                    navController.navigate(Screens.Settings.route)
                },
                onLogout = {
                    println("Logout clicked")
                    navController.navigate(Screens.Home.route) {
                        popUpTo(Screens.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screens.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screens.MedicationReminders.route) {
            MedicationReminderScreen(
                onBackClick = { navController.popBackStack() },
                onAddReminder = {
                    // TODO: Implement add reminder functionality
                    println("Add reminder clicked")
                }
            )
        }

        composable(Screens.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}



