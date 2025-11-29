package com.example.kiram.data.repository

import android.net.Uri
import com.example.kiram.data.model.User
import com.example.kiram.util.Constants
import com.example.kiram.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for user data operations
 */
class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    
    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val document = firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .await()
            
            val user = document.toObject(User::class.java)
                ?: throw Exception("Kullanıcı bulunamadı")
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Kullanıcı bilgisi alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.userId)
                .set(user)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Profil güncellenemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Upload profile photo
     */
    suspend fun uploadProfilePhoto(userId: String, photoUri: Uri): Result<String> {
        return try {
            val filename = "${userId}_${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child(Constants.STORAGE_PROFILE_PHOTOS)
                .child(filename)
            
            storageRef.putFile(photoUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error("Fotoğraf yüklenemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get users by building ID
     */
    suspend fun getUsersByBuilding(buildingId: String): Result<List<User>> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("buildingId", buildingId)
                .get()
                .await()
            
            val users = querySnapshot.documents.mapNotNull { 
                it.toObject(User::class.java) 
            }
            
            Result.Success(users)
        } catch (e: Exception) {
            Result.Error("Kullanıcılar alınamadı: ${e.localizedMessage}", e)
        }
    }
}
