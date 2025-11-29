package com.example.kiram.data.repository

import android.net.Uri
import com.example.kiram.data.model.User
import com.example.kiram.data.model.UserRole
import com.example.kiram.util.Constants
import com.example.kiram.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for authentication operations
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    
    /**
     * Register new user
     */
    suspend fun registerUser(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        role: UserRole,
        buildingId: String? = null
    ): Result<User> {
        return try {
            // Create Firebase Auth user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUid = authResult.user?.uid ?: throw Exception("Failed to create user")
            
            // Generate unique user ID based on role
            val userId = generateUserId(role)
            
            // Create user document
            val user = User(
                userId = userId,
                fullName = fullName,
                email = email,
                phone = phone,
                role = role,
                buildingId = buildingId,
                createdAt = System.currentTimeMillis()
            )
            
            // Save to Firestore
            firestore.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .set(user)
                .await()
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Kayıt başarısız: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Login user
     */
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            // Sign in with Firebase Auth
            auth.signInWithEmailAndPassword(email, password).await()
            
            // Get user data from Firestore
            val querySnapshot = firestore.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("email", email)
                .get()
                .await()
            
            if (querySnapshot.documents.isEmpty()) {
                throw Exception("Kullanıcı bulunamadı")
            }
            
            val user = querySnapshot.documents[0].toObject(User::class.java)
                ?: throw Exception("Kullanıcı verileri okunamadı")
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Giriş başarısız: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Logout user
     */
    fun logoutUser() {
        auth.signOut()
    }
    
    /**
     * Reset password
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Şifre sıfırlama başarısız: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get current user
     */
    suspend fun getCurrentUser(): Result<User?> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                return Result.Success(null)
            }
            
            val querySnapshot = firestore.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("email", firebaseUser.email)
                .get()
                .await()
            
            if (querySnapshot.documents.isEmpty()) {
                return Result.Success(null)
            }
            
            val user = querySnapshot.documents[0].toObject(User::class.java)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Kullanıcı bilgisi alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Generate unique user ID based on role
     */
    private fun generateUserId(role: UserRole): String {
        val prefix = when (role) {
            UserRole.TENANT -> Constants.USER_ID_PREFIX_TENANT
            UserRole.LANDLORD -> Constants.USER_ID_PREFIX_LANDLORD
            UserRole.MANAGER -> Constants.USER_ID_PREFIX_MANAGER
        }
        val timestamp = System.currentTimeMillis() % 100000 // Last 5 digits
        val random = (1000..9999).random()
        return "$prefix-$timestamp-$random"
    }
}
