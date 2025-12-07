package com.example.kiram.data.model

/**
 * Property/House data model
 */
data class Property(
    val propertyId: String = "",
    val landlordId: String = "",
    val managerId: String = "",
    val buildingId: String = "",
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val city: String = "",
    val district: String = "",
    val floor: Int = 0,
    val roomCount: String = "", // e.g., "2+1", "3+1"
    val squareMeters: Int = 0,
    val buildingAge: Int = 0,
    val rentAmount: Double = 0.0,
    val depositAmount: Double = 0.0,
    val propertyType: String = "", // Apartment, House, Studio, etc.
    @get:com.google.firebase.firestore.PropertyName("isFurnished")
    val isFurnished: Boolean = false,
    val features: List<String> = emptyList(), // Balcony, Parking, Elevator, etc.
    val photos: List<String> = emptyList(),
    val currentTenantId: String? = null,
    @get:com.google.firebase.firestore.PropertyName("isAvailable")
    val isAvailable: Boolean = true,
    val contractStartDate: Long? = null,
    val contractEndDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
