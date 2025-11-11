package com.example.pharmai.presentation.navigation

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Login : Screens("login")
    object Register : Screens("register")
    object Main : Screens("main")
    object MedicationSearch : Screens("medication_search")
    object InteractionChecker : Screens("interaction_checker")
    object PillIdentification : Screens("pill_identification")
    object Profile : Screens("profile")
    object MedicationReminders : Screens("medication_reminders")
    object Settings : Screens("settings")
}