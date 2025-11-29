package com.example.kiram.data.model

/**
 * Building manager announcement data model
 */
data class Announcement(
    val announcementId: String = "",
    val managerId: String = "",
    val buildingId: String = "",
    val title: String = "",
    val content: String = "",
    val imageUrl: String? = null,
    val targetAudience: AnnouncementTarget = AnnouncementTarget.ALL,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Target audience for announcements
 */
enum class AnnouncementTarget {
    TENANTS_ONLY,   // Sadece Kiracılar
    LANDLORDS_ONLY, // Sadece Ev Sahipleri
    ALL             // Tüm Bina
}
