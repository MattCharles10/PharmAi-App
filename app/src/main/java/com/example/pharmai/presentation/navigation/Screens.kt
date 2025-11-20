package com.example.pharmai.presentation.navigation



sealed class Screens(val route: String) {
    object Splash : Screens("splash")
    object Login : Screens("login")
    object Register : Screens("register")
    object Dashboard : Screens("dashboard")
    object MedicationSearch : Screens("medication_search")
    object InteractionChecker : Screens("interaction_checker")
    object MedicationReminder : Screens("medication_reminder")
    object PillIdentification : Screens("pill_identification")
}