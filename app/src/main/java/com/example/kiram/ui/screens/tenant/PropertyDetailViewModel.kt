package com.example.kiram.ui.screens.tenant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiram.data.model.Property
import com.example.kiram.data.model.User
import com.example.kiram.data.repository.PropertyRepository
import com.example.kiram.data.repository.UserRepository
import com.example.kiram.util.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropertyDetailViewModel(
    private val propertyRepository: PropertyRepository = PropertyRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _propertyState = MutableStateFlow<Result<Property>>(Result.Idle)
    val propertyState: StateFlow<Result<Property>> = _propertyState.asStateFlow()

    private val _landlordState = MutableStateFlow<Result<User>>(Result.Idle)
    val landlordState: StateFlow<Result<User>> = _landlordState.asStateFlow()

    fun loadPropertyDetails(propertyId: String) {
        viewModelScope.launch {
            _propertyState.value = Result.Loading
            
            // Fetch Property
            when (val result = propertyRepository.getPropertyById(propertyId)) {
                is Result.Success -> {
                    _propertyState.value = result
                    // Fetch Landlord
                    loadLandlord(result.data.landlordId)
                }
                is Result.Error -> {
                    _propertyState.value = Result.Error(result.message, result.exception)
                }
                else -> {}
            }
        }
    }

    private fun loadLandlord(landlordId: String) {
        viewModelScope.launch {
            _landlordState.value = userRepository.getUserById(landlordId)
        }
    }
}
