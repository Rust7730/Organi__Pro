package com.e243768.organipro_.data.remote.cloudinary

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class CloudinaryService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val cloudName = "dmbsbqpxl"
    private val uploadPreset = "organipro_preset" // El que creaste como 'Unsigned'

    init {
        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = cloudName
            MediaManager.init(context, config)
        } catch (e: Exception) {
            // MediaManager ya inicializado
        }
    }

    suspend fun uploadImage(uri: Uri): String? = suspendCancellableCoroutine { continuation ->
        MediaManager.get().upload(uri)
            .unsigned(uploadPreset)
            .option("resource_type", "image")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    // Obtenemos la URL segura (https)
                    val secureUrl = resultData?.get("secure_url") as? String
                    if (continuation.isActive) {
                        continuation.resume(secureUrl)
                    }
                }

                override fun onError(requestId: String?, errorInfo: ErrorInfo?) {
                    if (continuation.isActive) {
                        continuation.resume(null) // O lanzar excepción si prefieres
                    }
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }
}