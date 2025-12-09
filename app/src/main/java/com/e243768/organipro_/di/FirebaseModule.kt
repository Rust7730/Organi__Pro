package com.e243768.organipro_.di

import com.e243768.organipro_.data.remote.firebase.FirebaseAuthService
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.firebase.FirebaseStorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.FirebaseApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        // Intentar obtener el storage configurado por google-services.json
        val defaultStorage = FirebaseStorage.getInstance()
        try {
            val bucket = FirebaseApp.getInstance().options.storageBucket
            // Si el bucket parece malformado (p.ej. contiene "firebasestorage.app"), usar fallback a gs://{projectId}.appspot.com
            if (bucket == null || bucket.contains("firebasestorage.app")) {
                val projectId = FirebaseApp.getInstance().options.projectId
                if (!projectId.isNullOrBlank()) {
                    val fallbackBucket = "gs://$projectId.appspot.com"
                    return FirebaseStorage.getInstance(fallbackBucket)
                }
            }
        } catch (_: Exception) {
            // Si algo falla, devolvemos la instancia por defecto
        }

        return defaultStorage
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthService(auth: FirebaseAuth): FirebaseAuthService {
        return FirebaseAuthService(auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestoreService(firestore: FirebaseFirestore): FirebaseFirestoreService {
        return FirebaseFirestoreService(firestore)
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageService(storage: FirebaseStorage, auth: FirebaseAuth): FirebaseStorageService {
        // Comprobar el bucket configurado y forzar fallback si parece malformado
        try {
            val configuredBucket = try { FirebaseApp.getInstance().options.storageBucket } catch (_: Exception) { null }
            if (configuredBucket == null || configuredBucket.contains("firebasestorage.app")) {
                val projectId = try { FirebaseApp.getInstance().options.projectId } catch (_: Exception) { null }
                if (!projectId.isNullOrBlank()) {
                    val fallbackBucket = "gs://$projectId.appspot.com"
                    val fallbackStorage = FirebaseStorage.getInstance(fallbackBucket)
                    println("[FirebaseModule] Using fallback storage bucket=$fallbackBucket instead of configured=$configuredBucket")
                    return FirebaseStorageService(fallbackStorage, auth)
                }
            }
        } catch (e: Exception) {
            // Ignorar y usar la instancia provista
            println("[FirebaseModule] Error determining fallback bucket: ${e.message}")
        }

        return FirebaseStorageService(storage, auth)
    }
}
