package com.example.pharmai.presentation.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pharmai.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val _loginState = mutableStateOf(LoginState())
    val loginState: State<LoginState> = _loginState

    private val _currentUser = mutableStateOf<User?>(null)
    val currentUser: State<User?> = _currentUser

    fun login(email: String, password: String) {
        _loginState.value = LoginState(isLoading = true)

        viewModelScope.launch {
            try {
                // Simulate API call
                delay(1000)

                // Mock validation
                if (email.isBlank() || password.isBlank()) {
                    _loginState.value = LoginState(
                        isError = true,
                        errorMessage = "Email and password cannot be empty"
                    )
                    return@launch
                }

                if (!isValidEmail(email)) {
                    _loginState.value = LoginState(
                        isError = true,
                        errorMessage = "Please enter a valid email"
                    )
                    return@launch
                }

                if (password.length < 6) {
                    _loginState.value = LoginState(
                        isError = true,
                        errorMessage = "Password must be at least 6 characters"
                    )
                    return@launch
                }

                // Mock successful login
                val user = User(
                    id = "user_${System.currentTimeMillis()}",
                    email = email,
                    name = email.substringBefore("@").replace(".", " ").capitalizeWords()
                )

                _currentUser.value = user
                _loginState.value = LoginState(isSuccess = true)

            } catch (e: Exception) {
                _loginState.value = LoginState(
                    isError = true,
                    errorMessage = "Login failed: ${e.message}"
                )
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginState()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}