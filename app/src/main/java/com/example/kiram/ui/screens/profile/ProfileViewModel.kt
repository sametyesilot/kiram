package com.example.kiram.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiram.data.model.User
import com.example.kiram.data.repository.UserRepository
import com.example.kiram.util.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Profile Screen
 */
class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    
    private val _userState = MutableStateFlow<Result<User>>(Result.Idle)
    val userState: StateFlow<Result<User>> = _userState.asStateFlow()
    
    /**
     * Load user data from Firestore
     */
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _userState.value = Result.Loading
            _userState.value = userRepository.getUserById(userId)
        }
    }
    
    /**
     * Logout user
     */
    fun logout() {
        auth.signOut()
    }
}
