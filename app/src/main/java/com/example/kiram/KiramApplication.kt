package com.example.kiram

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.messaging.FirebaseMessaging
import com.example.kiram.util.Constants

/**
 * Application class to handle Firebase initialization and DataStore singleton
 */
class KiramApplication : Application() {
    
    // Singleton DataStore instance
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)
    
    override fun onCreate() {
        super.onCreate()
        
        // Try to initialize Firebase Messaging, but don't let errors crash the app
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("KiramApp", "FCM Token: $token")
                } else {
                    Log.w("KiramApp", "FCM Token retrieval failed: ${task.exception?.message}")
                    // Don't crash - this is not critical for login
                }
            }
        } catch (e: Exception) {
            Log.w("KiramApp", "Firebase Messaging initialization failed: ${e.message}")
            // Don't crash - this is not critical for login
        }
    }
}

/**
 * Extension property to access DataStore from any Context
 * This is a top-level extension property that can be used throughout the app
 */
val Context.dataStore: DataStore<Preferences>
    get() = (applicationContext as KiramApplication).dataStore

