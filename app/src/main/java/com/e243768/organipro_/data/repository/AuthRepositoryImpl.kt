package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.constants.FirebaseConstants
import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.data.remote.firebase.FirebaseAuthService
import com.e243768.organipro_.data.remote.firebase.FirebaseFirestoreService
import com.e243768.organipro_.data.remote.mappers.UserMapper
import com.e243768.organipro_.domain.model.User
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.UserRepository
import java.util.Date

class AuthRepositoryImpl(
    private val firebaseAuthService: FirebaseAuthService,
    private val firestoreService: FirebaseFirestoreService,
    private val userRepository: UserRepository
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // 1. Autenticar con Firebase
            val firebaseUser = firebaseAuthService.signIn(email, password)
                ?: return Result.Error("Error al iniciar sesión")

            val userId = firebaseUser.uid

            // 2. Obtener datos del usuario desde Firestore
            val userMap = firestoreService.getDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                clazz = Map::class.java
            ) as? Map<String, Any?> ?: return Result.Error("Usuario no encontrado")

            val user = UserMapper.fromFirebaseMap(userMap)

            // 3. Actualizar lastLoginAt
            val updatedUser = user.copy(lastLoginAt = Date())
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                updates = mapOf("lastLoginAt" to com.google.firebase.Timestamp.now())
            )

            // 4. Guardar en base de datos local
            userRepository.saveUser(updatedUser)

            Result.Success(updatedUser)
        } catch (e: Exception) {
            Result.Error("Error al iniciar sesión: ${e.message}", e)
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
                ?: return Result.Error("Error al crear usuario")

            val userId = firebaseUser.uid

            // 2. Crear perfil de usuario
            val newUser = User(
                id = userId,
                name = name,
                alias = alias,
                email = email,
                avatarUrl = null,
                level = 1,
                currentXP = 0,
                totalPoints = 0,
                currentStreak = 0,
                longestStreak = 0,
                tasksCompleted = 0,
                createdAt = Date(),
                updatedAt = Date(),
                lastLoginAt = Date(),
                isActive = true
            )

            // 3. Guardar en Firestore
            val userMap = UserMapper.toFirebaseMap(newUser)
            firestoreService.setDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                data = userMap
            )

            // 4. Guardar en base de datos local
            userRepository.saveUser(newUser)

            // 5. Actualizar nombre en Firebase Auth
            firebaseAuthService.updateUserProfile(displayName = name, photoUrl = null)

            Result.Success(newUser)
        } catch (e: Exception) {
            Result.Error("Error al registrarse: ${e.message}", e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseAuthService.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al cerrar sesión: ${e.message}", e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuthService.sendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al enviar email: ${e.message}", e)
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseAuthService.getCurrentUserId()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuthService.isUserLoggedIn()
    }

    override suspend fun refreshSession(): Result<Unit> {
        return try {
            firebaseAuthService.refreshToken()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al refrescar sesión: ${e.message}", e)
        }
    }

    override suspend fun updateProfile(name: String, alias: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.Error("Usuario no autenticado")

            // 1. Actualizar en Firestore
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                updates = mapOf(
                    "name" to name,
                    "alias" to alias,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )

            // 2. Actualizar en Firebase Auth
            firebaseAuthService.updateUserProfile(displayName = name, photoUrl = null)

            // 3. Actualizar en base de datos local
            val userResult = userRepository.getUserById(userId)
            if (userResult is Result.Success) {
                val updatedUser = userResult.data.copy(
                    name = name,
                    alias = alias,
                    updatedAt = Date()
                )
                userRepository.updateUser(updatedUser)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar perfil: ${e.message}", e)
        }
    }

    override suspend fun updateEmail(newEmail: String, password: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.Error("Usuario no autenticado")
            val currentUser = firebaseAuthService.getCurrentUser()
                ?: return Result.Error("Usuario no autenticado")

            // 1. Reautenticar
            firebaseAuthService.reauthenticate(currentUser.email ?: "", password)

            // 2. Actualizar email en Firebase Auth
            firebaseAuthService.updateEmail(newEmail)

            // 3. Actualizar en Firestore
            firestoreService.updateDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId,
                updates = mapOf(
                    "email" to newEmail,
                    "updatedAt" to com.google.firebase.Timestamp.now()
                )
            )

            // 4. Actualizar en base de datos local
            val userResult = userRepository.getUserById(userId)
            if (userResult is Result.Success) {
                val updatedUser = userResult.data.copy(
                    email = newEmail,
                    updatedAt = Date()
                )
                userRepository.updateUser(updatedUser)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar email: ${e.message}", e)
        }
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val currentUser = firebaseAuthService.getCurrentUser()
                ?: return Result.Error("Usuario no autenticado")

            // 1. Reautenticar
            firebaseAuthService.reauthenticate(currentUser.email ?: "", currentPassword)

            // 2. Actualizar contraseña
            firebaseAuthService.updatePassword(newPassword)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al actualizar contraseña: ${e.message}", e)
        }
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.Error("Usuario no autenticado")
            val currentUser = firebaseAuthService.getCurrentUser()
                ?: return Result.Error("Usuario no autenticado")

            // 1. Reautenticar
            firebaseAuthService.reauthenticate(currentUser.email ?: "", password)

            // 2. Eliminar datos del usuario en Firestore
            firestoreService.deleteDocument(
                collection = FirebaseConstants.COLLECTION_USERS,
                documentId = userId
            )

            // 3. Eliminar tareas del usuario
            firestoreService.deleteDocuments(
                collection = FirebaseConstants.COLLECTION_TASKS
            ) { query ->
                query.whereEqualTo("userId", userId)
            }

            // 4. Eliminar de base de datos local
            userRepository.deleteUser(userId)

            // 5. Eliminar cuenta de Firebase Auth
            firebaseAuthService.deleteAccount()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar cuenta: ${e.message}", e)
        }
    }
}