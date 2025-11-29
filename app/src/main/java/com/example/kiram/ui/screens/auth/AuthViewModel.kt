package com.example.kiram.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiram.data.model.User
import com.example.kiram.data.model.UserRole
import com.example.kiram.data.repository.AuthRepository
import com.example.kiram.util.Result
import com.example.kiram.util.isValidEmail
import com.example.kiram.util.isValidFullName
import com.example.kiram.util.isValidPassword
import com.example.kiram.util.isValidPhone
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication (Login, Register, Forgot Password)
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {
    
    // Login state
    private val _loginState = MutableStateFlow<Result<User>>(Result.Loading)
    val loginState: StateFlow<Result<User>> = _loginState.asStateFlow()
    
    // Registration state
    private val _registerState = MutableStateFlow<Result<User>>(Result.Loading)
    val registerState: StateFlow<Result<User>> = _registerState.asStateFlow()
    
    // Password reset state
    private val _passwordResetState = MutableStateFlow<Result<Unit>>(Result.Loading)
    val passwordResetState: StateFlow<Result<Unit>> = _passwordResetState.asStateFlow()
    
    /**
     * Login user
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            
            // Validate inputs
            if (!email.isValidEmail()) {
                _loginState.value = Result.Error("Geçersiz e-posta adresi")
                return@launch
            }
            
            if (!password.isValidPassword()) {
                _loginState.value = Result.Error("Şifre en az 6 karakter olmalıdır")
                return@launch
            }
            
            _loginState.value = authRepository.loginUser(email, password)
        }
    }
    
    /**
     * Register new user
     */
    fun register(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: UserRole,
        buildingId: String? = null
    ) {
        viewModelScope.launch {
            _registerState.value = Result.Loading
            
            // Validate inputs
            if (!fullName.isValidFullName()) {
                _registerState.value = Result.Error("Ad soyad en az 2 kelime olmalıdır")
                return@launch
            }
            
            if (!phone.isValidPhone()) {
                _registerState.value = Result.Error("Geçersiz telefon numarası")
                return@launch
            }
            
            if (!email.isValidEmail()) {
                _registerState.value = Result.Error("Geçersiz e-posta adresi")
                return@launch
            }
            
            if (!password.isValidPassword()) {
                _registerState.value = Result.Error("Şifre en az 6 karakter olmalıdır")
                return@launch
            }
            
            if (password != confirmPassword) {
                _registerState.value = Result.Error("Şifreler eşleşmiyor")
                return@launch
            }
            
            _registerState.value = authRepository.registerUser(
                email = email,
                password = password,
                fullName = fullName,
                phone = phone,
                role = role,
                buildingId = buildingId
            )
        }
    }
    
    /**
     * Send password reset email
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _passwordResetState.value = Result.Loading
            
            if (!email.isValidEmail()) {
                _passwordResetState.value = Result.Error("Geçersiz e-posta adresi")
                return@launch
            }
            
            _passwordResetState.value = authRepository.resetPassword(email)
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        authRepository.logoutUser()
    }
    
    /**
     * Reset login state
     */
    fun resetLoginState() {
        _loginState.value = Result.Loading
    }
    
    /**
     * Reset registration state
     */
    fun resetRegisterState() {
        _registerState.value = Result.Loading
    }
    
    /**
     * Reset password reset state
     */
    fun resetPasswordResetState() {
        _passwordResetState.value = Result.Loading
    }
}
