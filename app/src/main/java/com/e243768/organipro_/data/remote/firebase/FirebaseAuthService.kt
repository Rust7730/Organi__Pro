package com.e243768.organipro_.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Obtener el usuario actual autenticado
     */
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    /**
     * Obtener el ID del usuario actual
     */
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    /**
     * Verificar si hay un usuario autenticado
     */
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Registrar un nuevo usuario con email y contraseña
     */
    suspend fun signUp(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Iniciar sesión con email y contraseña
     */
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Cerrar sesión
     */
    fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Enviar email de recuperación de contraseña
     */
    suspend fun sendPasswordResetEmail(email: String) {
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Actualizar el perfil del usuario (nombre y foto)
     */
    suspend fun updateUserProfile(displayName: String?, photoUrl: String?) {
        val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .apply {
                photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
            }
            .build()

        user.updateProfile(profileUpdates).await()
    }

    /**
     * Actualizar el email del usuario
     */
    suspend fun updateEmail(newEmail: String) {
        val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
        user.updateEmail(newEmail).await()
    }

    /**
     * Actualizar la contraseña del usuario
     */
    suspend fun updatePassword(newPassword: String) {
        val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
        user.updatePassword(newPassword).await()
    }

    /**
     * Reautenticar usuario (necesario antes de operaciones sensibles)
     */
    suspend fun reauthenticate(email: String, password: String) {
        val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
        val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential).await()
    }

    /**
     * Eliminar cuenta del usuario
     */
    suspend fun deleteAccount() {
        val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
        user.delete().await()
    }

    /**
     * Refrescar el token del usuario
     */
    suspend fun refreshToken() {
        val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
        user.reload().await()
    }

    /**
     * Enviar email de verificación
     */
    suspend fun sendEmailVerification() {
        val user = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
        user.sendEmailVerification().await()
    }

    /**
     * Verificar si el email está verificado
     */
    fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }
}