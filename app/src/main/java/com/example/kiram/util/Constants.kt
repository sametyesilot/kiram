package com.example.kiram.util

/**
 * App-wide constants
 */
object Constants {
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_PROPERTIES = "properties"
    const val COLLECTION_REVIEWS = "reviews"
    const val COLLECTION_DAMAGES = "damages"
    const val COLLECTION_CONVERSATIONS = "conversations"
    const val COLLECTION_MESSAGES = "messages"
    const val COLLECTION_ANNOUNCEMENTS = "announcements"
    const val COLLECTION_DISPUTES = "disputes"
    const val COLLECTION_MAINTENANCE_FEES = "maintenance_fees"
    const val COLLECTION_BUILDINGS = "buildings"
    
    // User ID Prefixes
    const val USER_ID_PREFIX_TENANT = "KRC"
    const val USER_ID_PREFIX_LANDLORD = "KRL"
    const val USER_ID_PREFIX_MANAGER = "KRM"
    
    // Storage Paths
    const val STORAGE_PROFILE_PHOTOS = "profile_photos"
    const val STORAGE_PROPERTY_PHOTOS = "property_photos"
    const val STORAGE_DAMAGE_PHOTOS = "damage_photos"
    const val STORAGE_MESSAGE_ATTACHMENTS = "message_attachments"
    const val STORAGE_ANNOUNCEMENT_IMAGES = "announcement_images"
    const val STORAGE_DISPUTE_EVIDENCE = "dispute_evidence"
    
    // DataStore Keys
    const val DATASTORE_NAME = "kiram_preferences"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    const val KEY_THEME_MODE = "theme_mode"
    
    // Date Formats
    const val DATE_FORMAT_LONG = "dd MMMM yyyy, HH:mm"
    const val DATE_FORMAT_SHORT = "dd.MM.yyyy"
    
    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val PHONE_NUMBER_LENGTH = 10
    
    // Pagination
    const val PAGE_SIZE = 20
}
