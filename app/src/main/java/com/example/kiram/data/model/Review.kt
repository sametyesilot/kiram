package com.example.kiram.data.model

/**
 * Review/Rating data model for tenant-landlord evaluations
 */
data class Review(
    val reviewId: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val propertyId: String = "",
    val ratings: Map<String, Float> = emptyMap(), // Category -> Rating (1-5)
    val comment: String = "",
    val isAnonymous: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Rating categories when Tenant rates Landlord
 */
object TenantToLandlordRating {
    const val CLEANLINESS = "cleanliness"           // Temizlik
    const val COMMUNICATION = "communication"       // İletişim
    const val PRICE_STABILITY = "price_stability"   // Fiyat İstikrarı
    const val CONFLICT = "conflict_resolution"      // Uyuşmazlık Durumu
}

/**
 * Rating categories when Landlord rates Tenant
 */
object LandlordToTenantRating {
    const val BEHAVIOR = "behavior"                    // Kiracı Davranışı
    const val PAYMENT = "payment_regularity"          // Ödeme Düzeni
    const val RULE_COMPLIANCE = "rule_compliance"     // Ev Kurallarına Uyum
    const val CLEANLINESS = "cleanliness"             // Temizlik Düzeyi
}
