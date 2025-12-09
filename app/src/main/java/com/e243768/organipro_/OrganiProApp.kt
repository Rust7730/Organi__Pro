package com.e243768.organipro_

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OrganiProApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Diagnóstico automático: intentar listar el contenido del bucket root para validar que el bucket existe y es accesible
        try {
            val bucket = try { FirebaseApp.getInstance().options.storageBucket } catch (_: Exception) { null }
            Log.i("OrganiProApp", "Configured storage bucket: $bucket")

            val storage = FirebaseStorage.getInstance()
            val rootRef = storage.reference
            rootRef.listAll()
                .addOnSuccessListener { listResult ->
                    Log.i("OrganiProApp", "Storage listAll success: items=${listResult.items.size}, prefixes=${listResult.prefixes.size}")
                }
                .addOnFailureListener { e ->
                    Log.e("OrganiProApp", "Storage listAll failed: ${e.message}", e)
                }
        } catch (e: Exception) {
            Log.e("OrganiProApp", "Error diagnosticando Firebase Storage: ${e.message}", e)
        }
    }
}