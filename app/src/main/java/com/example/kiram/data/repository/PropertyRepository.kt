package com.example.kiram.data.repository

import android.net.Uri
import com.example.kiram.data.model.Property
import com.example.kiram.util.Constants
import com.example.kiram.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for property/house operations
 */
class PropertyRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    
    /**
     * Add new property
     */
    suspend fun addProperty(property: Property): Result<Property> {
        return try {
            val propertyId = UUID.randomUUID().toString()
            val newProperty = property.copy(
                propertyId = propertyId,
                createdAt = System.currentTimeMillis()
            )
            
            firestore.collection(Constants.COLLECTION_PROPERTIES)
                .document(propertyId)
                .set(newProperty)
                .await()
            
            Result.Success(newProperty)
        } catch (e: Exception) {
            Result.Error("Ev eklenemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Update property
     */
    suspend fun updateProperty(property: Property): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_PROPERTIES)
                .document(property.propertyId)
                .set(property)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Ev güncellenemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Delete property
     */
    suspend fun deleteProperty(propertyId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_PROPERTIES)
                .document(propertyId)
                .delete()
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Ev silinemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get property by ID
     */
    suspend fun getPropertyById(propertyId: String): Result<Property> {
        return try {
            val document = firestore.collection(Constants.COLLECTION_PROPERTIES)
                .document(propertyId)
                .get()
                .await()
            
            val property = document.toObject(Property::class.java)
                ?: throw Exception("Ev bulunamadı")
            
            Result.Success(property)
        } catch (e: Exception) {
            Result.Error("Ev bilgisi alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get properties by landlord
     */
    suspend fun getPropertiesByLandlord(landlordId: String): Result<List<Property>> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_PROPERTIES)
                .whereEqualTo("landlordId", landlordId)
                .get()
                .await()
            
            val properties = querySnapshot.documents.mapNotNull { 
                it.toObject(Property::class.java) 
            }
            
            Result.Success(properties)
        } catch (e: Exception) {
            Result.Error("Evler alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get property by tenant
     */
    suspend fun getPropertyByTenant(tenantId: String): Result<Property?> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_PROPERTIES)
                .whereEqualTo("currentTenantId", tenantId)
                .get()
                .await()
            
            val property = querySnapshot.documents.firstOrNull()?.toObject(Property::class.java)
            
            Result.Success(property)
        } catch (e: Exception) {
            Result.Error("Ev bilgisi alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get properties by manager
     */
    suspend fun getPropertiesByManager(managerId: String): Result<List<Property>> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_PROPERTIES)
                .whereEqualTo("managerId", managerId)
                .get()
                .await()
            
            val properties = querySnapshot.documents.mapNotNull { 
                it.toObject(Property::class.java) 
            }
            
            Result.Success(properties)
        } catch (e: Exception) {
            Result.Error("Evler alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Upload property photos
     */
    suspend fun uploadPropertyPhotos(propertyId: String, photoUris: List<Uri>): Result<List<String>> {
        return try {
            val downloadUrls = mutableListOf<String>()
            
            for (photoUri in photoUris) {
                val filename = "${propertyId}_${UUID.randomUUID()}.jpg"
                val storageRef = storage.reference
                    .child(Constants.STORAGE_PROPERTY_PHOTOS)
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
