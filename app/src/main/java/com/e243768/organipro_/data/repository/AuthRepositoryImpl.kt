package com.e243768.organipro_.data.repository

import com.e243768.organipro_.core.result.Result
import com.e243768.organipro_.domain.model.User
import com.e243768.organipro_.domain.repository.AuthRepository
import com.e243768.organipro_.domain.repository.UserRepository
import java.util.Date
import java.util.UUID

class AuthRepositoryImpl(
    private val userRepository: UserRepository
    // private val firebaseAuth: FirebaseAuth // TODO: Agregar cuando tengamos Firebase
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> {
        // TODO: Implementar cuando tengamos Firebase Auth
        return try {
            // 1. Autenticar con Firebase
            // val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            // val userId = authResult.user?.uid ?: return Result.Error("Usuario no encontrado")

            // 2. Obtener datos del usuario
            // userRepository.fetchUserFromRemote(userId)

            Result.Error("Firebase Auth no configurado aún")
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
        // TODO: Implementar cuando tengamos Firebase Auth
        return try {
            // 1. Crear usuario en Firebase Auth
            // val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // val userId = authResult.user?.uid ?: return Result.Error("Error al crear usuario")

            // 2. Crear perfil de usuario
            val userId = UUID.randomUUID().toString()
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

            // 3. Guardar en Firestore y local
            userRepository.saveUser(newUser)

            Result.Success(newUser)
        } catch (e: Exception) {
            Result.Error("Error al registrarse: ${e.message}", e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            // firebaseAuth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al cerrar sesión: ${e.message}", e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            // firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al enviar email: ${e.message}", e)
        }
    }

    override suspend fun getCurrentUserId(): String? {
        // return firebaseAuth.currentUser?.uid
        return null
    }

    override suspend fun isUserLoggedIn(): Boolean {
        // return firebaseAuth.currentUser != null
        return false
    }

    override suspend fun refreshSession(): Result<Unit> {
        return try {
            // firebaseAuth.currentUser?.reload()?.await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al refrescar sesión: ${e.message}", e)
        }
    }

    override suspend fun updateProfile(name: String, alias: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.Error("Usuario no autenticado")
            val userResult = userRepository.getUserById(userId)

            if (userResult is Result.Success) {
                val updatedUser = userResult.data.copy(
                    name = name,
                    alias = alias,
                    updatedAt = Date()
                )
                userRepository.updateUser(updatedUser)
            } else {
                Result.Error("Usuario no encontrado")
            }
        } catch (e: Exception) {
            Result.Error("Error al actualizar perfil: ${e.message}", e)
        }
    }

    override suspend fun updateEmail(newEmail: String, password: String): Result<Unit> {
        // TODO: Implementar cuando tengamos Firebase Auth
        return Result.Error("Firebase Auth no configurado aún")
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        // TODO: Implementar cuando tengamos Firebase Auth
        return Result.Error("Firebase Auth no configurado aún")
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        // TODO: Implementar cuando tengamos Firebase Auth
        return try {
            val userId = getCurrentUserId() ?: return Result.Error("Usuario no autenticado")

            // 1. Eliminar cuenta de Firebase Auth
            // firebaseAuth.currentUser?.delete()?.await()

            // 2. Eliminar datos del usuario
            userRepository.deleteUser(userId)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error al eliminar cuenta: ${e.message}", e)
        }
    }
}