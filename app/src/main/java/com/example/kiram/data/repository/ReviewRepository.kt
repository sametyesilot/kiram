package com.example.kiram.data.repository

import com.example.kiram.data.model.Review
import com.example.kiram.util.Constants
import com.example.kiram.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for review/rating operations
 */
class ReviewRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    
    /**
     * Submit a review
     */
    suspend fun submitReview(review: Review): Result<Review> {
        return try {
            val reviewId = UUID.randomUUID().toString()
            val newReview = review.copy(
                reviewId = reviewId,
                createdAt = System.currentTimeMillis()
            )
            
            firestore.collection(Constants.COLLECTION_REVIEWS)
                .document(reviewId)
                .set(newReview)
                .await()
            
            Result.Success(newReview)
        } catch (e: Exception) {
            Result.Error("Değerlendirme gönderilemedi: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get reviews for a user
     */
    suspend fun getReviewsForUser(userId: String): Result<List<Review>> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_REVIEWS)
                .whereEqualTo("toUserId", userId)
                .get()
                .await()
            
            val reviews = querySnapshot.documents.mapNotNull { 
                it.toObject(Review::class.java) 
            }
            
            Result.Success(reviews)
        } catch (e: Exception) {
            Result.Error("Değerlendirmeler alınamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Get average ratings for a user
     */
    suspend fun getAverageRatings(userId: String): Result<Map<String, Float>> {
        return try {
            val reviews = when (val result = getReviewsForUser(userId)) {
                is Result.Success -> result.data
                is Result.Error -> throw Exception(result.message)
                Result.Loading -> emptyList()
            }
            
            if (reviews.isEmpty()) {
                return Result.Success(emptyMap())
            }
            
            // Calculate average for each rating category
            val allCategories = reviews.flatMap { it.ratings.keys }.distinct()
            val averages = allCategories.associateWith { category ->
                val categoryRatings = reviews.mapNotNull { it.ratings[category] }
                if (categoryRatings.isNotEmpty()) {
                    categoryRatings.average().toFloat()
                } else {
                    0f
                }
            }
            
            Result.Success(averages)
        } catch (e: Exception) {
            Result.Error("Ortalama puanlar hesaplanamadı: ${e.localizedMessage}", e)
        }
    }
    
    /**
     * Check if user can review another user
     */
    suspend fun canUserReview(fromUserId: String, toUserId: String, propertyId: String): Result<Boolean> {
        return try {
            val querySnapshot = firestore.collection(Constants.COLLECTION_REVIEWS)
                .whereEqualTo("fromUserId", fromUserId)
                .whereEqualTo("toUserId", toUserId)
                .whereEqualTo("propertyId", propertyId)
                .get()
                .await()
            
            // User can review if they haven't already reviewed for this property
            Result.Success(querySnapshot.documents.isEmpty())
        } catch (e: Exception) {
            Result.Error("Kontrol başarısız: ${e.localizedMessage}", e)
        }
    }
}
