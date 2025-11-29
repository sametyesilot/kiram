package com.example.kiram.data.repository

import android.net.Uri
import com.example.kiram.data.model.Damage
import com.example.kiram.data.model.DamageStatus
import com.example.kiram.util.Constants
import com.example.kiram.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for damage report operations
 */
class DamageRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    
    /**
     * Report a damage
     */
    suspend fun reportDamage(damage: Damage): Result<Damage> {
        return try {
            val damageId = UUID.randomUUID().toString()
            val newDamage = damage.copy(
                damageId = damageId,
                createdAt = System.currentTimeMillis()
            )
            
            firestore.collection(Constants.COLLECTION_DAMAGES)
                .document(damageId)
                .set(newDamage)
                .await()
            
            Result.Success(newDamage)
        } catch (e: Exception) {
            Result.Error("Hasar bildirimi gönderilemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Update damage status
     */
    suspend fun updateDamageStatus(
        damageId: String, 
        status: DamageStatus, 
        resolverNotes: String = ""
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>(
                "status" to status.name,
                "resolverNotes" to resolverNotes
            )
            
            if (status == DamageStatus.RESOLVED) {
                updates["resolvedAt"] = System.currentTimeMillis()
            }
            
            firestore.collection(Constants.COLLECTION_DAMAGES)
                .document(damageId)
                .update(updates)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Durum güncellenemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get damages for a property
     */
    suspend fun getDamagesForProperty(propertyId: String): Result<List<Damage>> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_DAMAGES)
                .whereEqualTo("propertyId", propertyId)
                .get()
                .await()
            
            val damages = querySnapshot.documents.mapNotNull { 
                it.toObject(Damage::class.java) 
            }.sortedByDescending { it.createdAt }
            
            Result.Success(damages)
        } catch (e: Exception) {
            Result.Error("Hasar kayıtları alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get damage by ID
     */
    suspend fun getDamageById(damageId: String): Result<Damage> {
        return try {
            val document = firestore.collection(Constants.COLLECTION_DAMAGES)
                .document(damageId)
                .get()
                .await()
            
            val damage = document.toObject(Damage::class.java)
                ?: throw Exception("Hasar kaydı bulunamadı")
            
            Result.Success(damage)
        } catch (e: Exception) {
            Result.Error("Hasar bilgisi alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Upload damage photos
     */
    suspend fun uploadDamagePhotos(damageId: String, photoUris: List<Uri>): Result<List<String>> {
        return try {
            val downloadUrls = mutableListOf<String>()
            
            for (photoUri in photoUris) {
                val filename = "${damageId}_${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference
                    .child(Constants.STORAGE_DAMAGE_PHOTOS)
                    .child(filename)
                
                storageRef.putFile(photoUri).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                downloadUrls.add(downloadUrl)
            }
            
            Result.Success(downloadUrls)
        } catch (e: Exception) {
            Result.Error("Fotoğraflar yüklenemedi: ${e.localizedMessage}", e)
        }
    }
}
