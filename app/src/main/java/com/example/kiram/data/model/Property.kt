package com.example.kiram.data.model

/**
 * Property/House data model
 */
data class Property(
    val propertyId: String = "",
    val landlordId: String = "",
    val managerId: String = "",
    val buildingId: String = "",
    val address: String = "",
    val floor: Int = 0,
    val roomCount: Int = 0,
    val rentAmount: Double = 0.0,
    val photos: List<String> = emptyList(),
    val currentTenantId: String? = null,
    val contractStartDate: Long? = null,
    val contractEndDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
