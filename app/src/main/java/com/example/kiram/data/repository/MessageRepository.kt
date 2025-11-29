package com.example.kiram.data.repository

import android.net.Uri
import com.example.kiram.data.model.Message
import com.example.kiram.data.model.Conversation
import com.example.kiram.data.model.AttachmentType
import com.example.kiram.util.Constants
import com.example.kiram.util.Result
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for messaging operations using Firebase Realtime Database
 */
class MessageRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    
    /**
     * Send a message
     */
    suspend fun sendMessage(message: Message): Result<Message> {
        return try {
            val messageId = UUID.randomUUID().toString()
            val newMessage = message.copy(
                messageId = messageId,
                timestamp = System.currentTimeMillis()
            )
            
            // Save message to Realtime Database
            database.getReference(Constants.COLLECTION_MESSAGES)
                .child(message.conversationId)
                .child(messageId)
                .setValue(newMessage)
                .await()
            
            // Update conversation last message
            updateConversationLastMessage(
                message.conversationId,
                message.content,
                newMessage.timestamp,
                message.receiverId
            )
            
            Result.Success(newMessage)
        } catch (e: Exception) {
            Result.Error("Mesaj gönderilemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get messages for a conversation as Flow (realtime updates)
     */
    fun getMessagesFlow(conversationId: String): Flow<Result<List<Message>>> = callbackFlow {
        val messagesRef = database.getReference(Constants.COLLECTION_MESSAGES)
            .child(conversationId)
            .orderByChild("timestamp")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { 
                    it.getValue(Message::class.java) 
                }.sortedBy { it.timestamp }
                
                trySend(Result.Success(messages))
            }
            
            override fun onCancelled(error: DatabaseError) {
                trySend(Result.Error("Mesajlar alınamadı: ${error.message}"))
            }
        }
        
        messagesRef.addValueEventListener(listener)
        
        awaitClose { messagesRef.removeEventListener(listener) }
    }
    
    /**
     * Mark message as read
     */
    suspend fun markMessageAsRead(conversationId: String, messageId: String): Result<Unit> {
        return try {
            database.getReference(Constants.COLLECTION_MESSAGES)
                .child(conversationId)
                .child(messageId)
                .child("isRead")
                .setValue(true)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Mesaj güncellenemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get or create conversation
     */
    suspend fun getOrCreateConversation(user1Id: String, user2Id: String): Result<Conversation> {
        return try {
            // Sort IDs to ensure consistent conversation ID
            val ids = listOf(user1Id, user2Id).sorted()
            val conversationId = "${ids[0]}_${ids[1]}"
            
            val conversationRef = database.getReference(Constants.COLLECTION_CONVERSATIONS)
                .child(conversationId)
            
            val snapshot = conversationRef.get().await()
            
            val conversation = if (snapshot.exists()) {
                snapshot.getValue(Conversation::class.java) ?: throw Exception("Konuşma okunamadı")
            } else {
                // Create new conversation
                val newConversation = Conversation(
                    conversationId = conversationId,
                    participant1Id = ids[0],
                    participant2Id = ids[1],
                    lastMessage = "",
                    lastMessageTimestamp = System.currentTimeMillis()
                )
                conversationRef.setValue(newConversation).await()
                newConversation
            }
            
            Result.Success(conversation)
        } catch (e: Exception) {
            Result.Error("Konuşma oluşturulamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get all conversations for a user
     */
    fun getConversationsFlow(userId: String): Flow<Result<List<Conversation>>> = callbackFlow {
        val conversationsRef = database.getReference(Constants.COLLECTION_CONVERSATIONS)
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val conversations = snapshot.children.mapNotNull { 
                    it.getValue(Conversation::class.java) 
                }.filter { 
                    it.participant1Id == userId || it.participant2Id == userId 
                }.sortedByDescending { it.lastMessageTimestamp }
                
                trySend(Result.Success(conversations))
            }
            
            override fun onCancelled(error: DatabaseError) {
                trySend(Result.Error("Konuşmalar alınamadı: ${error.message}"))
            }
        }
        
        conversationsRef.addValueEventListener(listener)
        
        awaitClose { conversationsRef.removeEventListener(listener) }
    }
    
    /**
     * Upload message attachment
     */
    suspend fun uploadAttachment(conversationId: String, fileUri: Uri, type: AttachmentType): Result<String> {
        return try {
            val extension = when (type) {
                AttachmentType.PHOTO -> "jpg"
                AttachmentType.DOCUMENT -> "pdf"
                AttachmentType.VIDEO -> "mp4"
                AttachmentType.NONE -> throw Exception("Geçersiz dosya tipi")
            }
            
            val filename = "${conversationId}_${UUID.randomUUID()}.$extension"
            val storageRef = storage.reference
                .child(Constants.STORAGE_MESSAGE_ATTACHMENTS)
                .child(filename)
            
            storageRef.putFile(fileUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error("Dosya yüklenemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Update conversation's last message
     */
    private suspend fun updateConversationLastMessage(
        conversationId: String,
        lastMessage: String,
        timestamp: Long,
        receiverId: String
    ) {
        try {
            val conversationRef = database.getReference(Constants.COLLECTION_CONVERSATIONS)
                .child(conversationId)
            
            val updates = mapOf(
                "lastMessage" to lastMessage,
                "lastMessageTimestamp" to timestamp
            )
            
            conversationRef.updateChildren(updates).await()
        } catch (e: Exception) {
            // Log error but don't throw - message was already sent
        }
    }
}
