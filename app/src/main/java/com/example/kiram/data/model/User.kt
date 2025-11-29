package com.example.kiram.data.model

/**
 * User data model representing all user types in the system
 */
data class User(
    val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.TENANT,
    val profilePhotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val buildingId: String? = null // For managers and residents
)

/**
 * User roles in the KİRÂM system
 */
enum class UserRole {
    TENANT,      // Kiracı
    LANDLORD,    // Ev Sahibi
    MANAGER      // Apartman Yöneticisi
}
