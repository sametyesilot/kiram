package com.example.kiram.data.model

/**
 * Chat message data model
 */
data class Message(
    val messageId: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val attachmentUrl: String? = null,
    val attachmentType: AttachmentType = AttachmentType.NONE,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false
)

/**
 * Conversation between two users
 */
data class Conversation(
    val conversationId: String = "",
    val participant1Id: String = "",
    val participant2Id: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount1: Int = 0, // Unread count for participant1
    val unreadCount2: Int = 0  // Unread count for participant2
)

/**
 * Attachment types for messages
 */
enum class AttachmentType {
    NONE,
    PHOTO,
    DOCUMENT,
    VIDEO
}
