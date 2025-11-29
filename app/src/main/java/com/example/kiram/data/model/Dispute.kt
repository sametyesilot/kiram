package com.example.kiram.data.model

/**
 * Dispute/Conflict record between landlord and tenant
 */
data class Dispute(
    val disputeId: String = "",
    val propertyId: String = "",
    val landlordId: String = "",
    val tenantId: String = "",
    val type: DisputeType = DisputeType.OTHER,
    val description: String = "",
    val evidence: List<String> = emptyList(), // Photos/documents
    val status: DisputeStatus = DisputeStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null,
    val resolution: String = ""
)

/**
 * Types of disputes
 */
enum class DisputeType {
    PRICE_INCREASE,        // Fiyat Artışı Şikayeti
    GENERAL_CONFLICT,      // Genel Anlaşmazlık
    DAMAGE_DISPUTE,        // Hasar Tespiti Anlaşmazlığı
    SOCIAL_INCOMPATIBILITY,// Sosyal Uyumsuzluk
    EVICTION_PROCESS,      // Tahliye Süreci
    PAYMENT_DELAY,         // Ödeme Gecikmesi
    OTHER                  // Diğer
}

/**
 * Dispute status
 */
enum class DisputeStatus {
    OPEN,          // Açık
    IN_MEDIATION,  // Arabuluculukta
    RESOLVED,      // Çözüldü
    ESCALATED      // Üst Makamlara İletildi
}

/**
 * Maintenance fee record for building
 */
data class MaintenanceFee(
    val feeId: String = "",
    val buildingId: String = "",
    val propertyId: String = "",
    val month: Int = 0,
    val year: Int = 0,
    val amount: Double = 0.0,
    val isPaid: Boolean = false,
    val paidBy: String? = null, // landlordId or tenantId
    val paidAt: Long? = null,
    val dueDate: Long = System.currentTimeMillis()
)
