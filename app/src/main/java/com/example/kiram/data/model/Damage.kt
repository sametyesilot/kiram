package com.example.kiram.data.model

/**
 * Damage report data model
 */
data class Damage(
    val damageId: String = "",
    val propertyId: String = "",
    val reportedByUserId: String = "",
    val description: String = "",
    val photos: List<String> = emptyList(),
    val urgency: DamageUrgency = DamageUrgency.LOW,
    val status: DamageStatus = DamageStatus.REPORTED,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val resolverNotes: String = ""
)

/**
 * Damage urgency levels
 */
enum class DamageUrgency {
    LOW,        // Düşük
    MEDIUM,     // Orta
    HIGH,       // Yüksek
    CRITICAL    // Kritik
}

/**
 * Damage status in lifecycle
 */
enum class DamageStatus {
    REPORTED,      // Bildirildi
    IN_PROGRESS,   // Çözülüyor
    RESOLVED,      // Çözüldü
    REJECTED       // Reddedildi
}
