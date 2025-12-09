package com.e243768.organipro_.di

import com.e243768.organipro_.data.remote.firebase.FirebaseAuthService
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.firebase.FirebaseStorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
        return FirebaseStorage.getInstance()
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
    fun provideFirebaseStorageService(storage: FirebaseStorage): FirebaseStorageService {
        return FirebaseStorageService(storage)
    }
}
