package com.e243768.organipro_.data.repository

import android.util.Log
import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.remote.firebase.FirebaseAuthService
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.mappers.UserMapper
import com.e243768.organipro_.domain.model.User
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val firestoreService: FirebaseFirestoreService,
    private val userRepository: UserRepository
) : AuthRepository {

    override fun getAuthStateFlow(): Flow<Boolean> {
        return firebaseAuthService.getAuthStateFlow().map { it != null }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // 1. Autenticar con Firebase Auth
            val firebaseUser = firebaseAuthService.signIn(email, password)
                ?: return Result.Error("Error de autenticación: Credenciales inválidas.")

            val userId = firebaseUser.uid

            // 2. Obtener perfil de Firestore
            val userMap = try {
                firestoreService.getDocument(
                    collection = FirebaseConstants.COLLECTION_USERS,
                    documentId = userId,
                    clazz = Map::class.java
                ) as? Map<String, Any?>
            } catch (e: Exception) {
                return Result.Error("Error de conexión al obtener perfil: ${e.message}")
            }

            if (userMap == null) {
                // Caso raro: Usuario existe en Auth pero no en Firestore (Datos corruptos)
                firebaseAuthService.signOut()
                return Result.Error("Perfil de usuario no encontrado. Contacta soporte.")
            }

            val user = UserMapper.fromFirebaseMap(userMap)

            // 3. Actualizar última conexión en Firestore
            try {
                firestoreService.updateDocument(
                    collection = FirebaseConstants.COLLECTION_USERS,
                    documentId = userId,
                    updates = mapOf("lastLoginAt" to com.google.firebase.Timestamp.now())
                )
            } catch (e: Exception) {
                Log.e("AuthRepo", "No se pudo actualizar lastLoginAt", e)
            }

            // 4. Guardar/Actualizar en base de datos local (Room) para uso offline
            userRepository.saveUser(user)

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido al iniciar sesión")
        }
    }

    override suspend fun signUp(
        name: String,
        alias: String,
        email: String,
        password: String
    ): Result<User> {
        return try {
            // 1. Crear usuario en Firebase Auth
            val firebaseUser = firebaseAuthService.signUp(email, password)
                ?: return Result.Error("No se pudo crear el usuario en el servidor.")

            val userId = firebaseUser.uid

            // 2. Preparar el modelo de usuario
            val newUser = User(
                id = userId,
                name = name,
                alias = alias,
                email = email,
                level = 1,
                currentXP = 0,
                totalPoints = 0,
                createdAt = Date(),
                updatedAt = Date(),
                lastLoginAt = Date(),
                isActive = true
            )

            // 3. Guardar en Firestore (Con ROLLBACK si falla)
            try {
                val userMap = UserMapper.toFirebaseMap(newUser)
                firestoreService.setDocument(
                    collection = FirebaseConstants.COLLECTION_USERS,
                    documentId = userId,
                    data = userMap
                )
            } catch (e: Exception) {
                // ROLLBACK: Si falla la BD, borramos el usuario de Auth para no dejarlo "zombie"
                try {
                    firebaseUser.delete()
                } catch (deleteEx: Exception) {
                    Log.e("AuthRepo", "Error al hacer rollback de usuario", deleteEx)
                }
                return Result.Error("Error al guardar datos del perfil. Intenta de nuevo.")
            }

            // 4. Actualizar perfil de Auth (DisplayName)
            try {
                firebaseAuthService.updateUserProfile(displayName = name, photoUrl = null)
            } catch (e: Exception) {
                Log.w("AuthRepo", "No se pudo actualizar el DisplayName en Auth", e)
            }

            // 5. Guardar en local
            userRepository.saveUser(newUser)

            Result.Success(newUser)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error desconocido al registrarse")
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuthService.signOut()
            // Opcional: Limpiar datos locales sensibles aquí si fuera necesario
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al cerrar sesión", e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuthService.sendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al enviar email de recuperación", e)
        }
    }

    override suspend fun getCurrentUserId(): String? = firebaseAuthService.getCurrentUserId()

    override suspend fun refreshSession(): Result<Unit> {
        return try {
            firebaseAuthService.refreshToken()
            Result.Success(Unit)
        } catch (e: Exception) { Result.Error(e.message ?: "") }
    }
    override suspend fun updateProfile(name: String, alias: String): Result<Unit> = Result.Success(Unit) // Implementar luego
    override suspend fun updateEmail(newEmail: String, password: String): Result<Unit> = Result.Success(Unit)
    override suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> = Result.Success(Unit)
    override suspend fun deleteAccount(password: String): Result<Unit> = Result.Success(Unit)
}