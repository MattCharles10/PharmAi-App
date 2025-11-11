package com.example.pharmai.domain.usecase

import com.example.pharmai.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, fullName: String) =
        authRepository.register(email, password, fullName)
}