package com.example.kiram.ui.screens.landlord

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kiram.data.model.Property
import com.example.kiram.data.repository.PropertyRepository
import com.example.kiram.util.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * ViewModel for Add Property Screen
 */
class AddPropertyViewModel(
    private val propertyRepository: PropertyRepository = PropertyRepository(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : ViewModel() {
    
    // Form state
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()
    
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()
    
    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()
    
    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city.asStateFlow()
    
    private val _district = MutableStateFlow("")
    val district: StateFlow<String> = _district.asStateFlow()
    
    private val _propertyType = MutableStateFlow("Daire")
    val propertyType: StateFlow<String> = _propertyType.asStateFlow()
    
    private val _roomCount = MutableStateFlow("2+1")
    val roomCount: StateFlow<String> = _roomCount.asStateFlow()
    
    private val _squareMeters = MutableStateFlow("")
    val squareMeters: StateFlow<String> = _squareMeters.asStateFlow()
    
    private val _floor = MutableStateFlow("")
    val floor: StateFlow<String> = _floor.asStateFlow()
    
    private val _buildingAge = MutableStateFlow("")
    val buildingAge: StateFlow<String> = _buildingAge.asStateFlow()
    
    private val _rentAmount = MutableStateFlow("")
    val rentAmount: StateFlow<String> = _rentAmount.asStateFlow()
    
    private val _depositAmount = MutableStateFlow("")
    val depositAmount: StateFlow<String> = _depositAmount.asStateFlow()
    
    private val _isFurnished = MutableStateFlow(false)
    val isFurnished: StateFlow<Boolean> = _isFurnished.asStateFlow()
    
    private val _selectedFeatures = MutableStateFlow<Set<String>>(emptySet())
    val selectedFeatures: StateFlow<Set<String>> = _selectedFeatures.asStateFlow()
    
    private val _selectedPhotos = MutableStateFlow<List<Uri>>(emptyList())
    val selectedPhotos: StateFlow<List<Uri>> = _selectedPhotos.asStateFlow()
    
    // Save state
    private val _saveState = MutableStateFlow<Result<String>>(Result.Idle)
    val saveState: StateFlow<Result<String>> = _saveState.asStateFlow()
    
    // Update functions
    fun updateTitle(value: String) { _title.value = value }
    fun updateDescription(value: String) { _description.value = value }
    fun updateAddress(value: String) { _address.value = value }
    fun updateCity(value: String) { _city.value = value }
    fun updateDistrict(value: String) { _district.value = value }
    fun updatePropertyType(value: String) { _propertyType.value = value }
    fun updateRoomCount(value: String) { _roomCount.value = value }
    fun updateSquareMeters(value: String) { _squareMeters.value = value }
    fun updateFloor(value: String) { _floor.value = value }
    fun updateBuildingAge(value: String) { _buildingAge.value = value }
    fun updateRentAmount(value: String) { _rentAmount.value = value }
    fun updateDepositAmount(value: String) { _depositAmount.value = value }
    fun updateIsFurnished(value: Boolean) { _isFurnished.value = value }
    
    fun toggleFeature(feature: String) {
        _selectedFeatures.value = if (_selectedFeatures.value.contains(feature)) {
            _selectedFeatures.value - feature
        } else {
            _selectedFeatures.value + feature
        }
    }
    
    fun addPhoto(uri: Uri) {
        _selectedPhotos.value = _selectedPhotos.value + uri
    }
    
    fun removePhoto(uri: Uri) {
        _selectedPhotos.value = _selectedPhotos.value - uri
    }
    
    /**
     * Validate and save property
     */
    fun saveProperty(landlordId: String) {
        viewModelScope.launch {
            try {
                _saveState.value = Result.Loading
                
                // Validation
                if (_title.value.isBlank()) {
                    _saveState.value = Result.Error("Lütfen başlık girin")
                    return@launch
                }
                if (_address.value.isBlank()) {
                    _saveState.value = Result.Error("Lütfen adres girin")
                    return@launch
                }
                if (_city.value.isBlank()) {
                    _saveState.value = Result.Error("Lütfen şehir girin")
                    return@launch
                }
                if (_rentAmount.value.isBlank() || _rentAmount.value.toDoubleOrNull() == null) {
                    _saveState.value = Result.Error("Lütfen geçerli bir kira tutarı girin")
                    return@launch
                }
                
                // Upload photos
                val photoUrls = uploadPhotos(_selectedPhotos.value)
                
                // Create property
                val property = Property(
                    propertyId = UUID.randomUUID().toString(),
                    landlordId = landlordId,
                    title = _title.value,
                    description = _description.value,
                    address = _address.value,
                    city = _city.value,
                    district = _district.value,
                    propertyType = _propertyType.value,
                    roomCount = _roomCount.value,
                    squareMeters = _squareMeters.value.toIntOrNull() ?: 0,
                    floor = _floor.value.toIntOrNull() ?: 0,
                    buildingAge = _buildingAge.value.toIntOrNull() ?: 0,
                    rentAmount = _rentAmount.value.toDoubleOrNull() ?: 0.0,
                    depositAmount = _depositAmount.value.toDoubleOrNull() ?: 0.0,
                    isFurnished = _isFurnished.value,
                    features = _selectedFeatures.value.toList(),
                    photos = photoUrls,
                    isAvailable = true
                )
                
                // Save to Firestore
                val result = propertyRepository.createProperty(property)
                _saveState.value = result
                
            } catch (e: Exception) {
                _saveState.value = Result.Error("Hata: ${e.message}")
            }
        }
    }
    
    /**
     * Upload photos to Firebase Storage
     */
    private suspend fun uploadPhotos(uris: List<Uri>): List<String> {
        val urls = mutableListOf<String>()
        
        uris.forEach { uri ->
            try {
                val filename = "property_${UUID.randomUUID()}.jpg"
                val ref = storage.reference.child("property_photos/$filename")
                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await().toString()
                urls.add(downloadUrl)
            } catch (e: Exception) {
                // Skip failed uploads
            }
        }
        
        return urls
    }
    
    fun resetSaveState() {
        _saveState.value = Result.Idle
    }
}
