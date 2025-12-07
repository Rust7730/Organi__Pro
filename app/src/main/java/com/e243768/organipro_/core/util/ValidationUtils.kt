package com.e243768.organipro_.core.util

object ValidationUtils {

    /**
     * Valida que el email tenga formato correcto
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }

    /**
     * Valida que la contraseña cumpla con requisitos mínimos
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida que el nombre tenga longitud mínima
     */
    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    /**
     * Valida que el alias tenga longitud mínima
     */
    fun isValidAlias(alias: String): Boolean {
        return alias.trim().length >= 3
    }

    /**
     * Valida que el título de tarea no esté vacío
     */
    fun isValidTaskTitle(title: String): Boolean {
        return title.trim().isNotEmpty()
    }

    /**
     * Valida que la descripción no exceda el máximo
     */
    fun isValidDescription(description: String, maxLength: Int = 500): Boolean {
        return description.length <= maxLength
    }

    /**
     * Valida que los puntos sean un valor positivo
     */
    fun isValidPoints(points: Int): Boolean {
        return points > 0
    }

    /**
     * Obtiene mensaje de error para email inválido
     */
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "El email no puede estar vacío"
            !isValidEmail(email) -> "Email inválido"
            else -> null
        }
    }

    /**
     * Obtiene mensaje de error para contraseña inválida
     */
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    /**
     * Obtiene mensaje de error para nombre inválido
     */
    fun getNameErrorMessage(name: String): String? {
        return when {
            name.isBlank() -> "El nombre no puede estar vacío"
            name.trim().length < 2 -> "El nombre debe tener al menos 2 caracteres"
            else -> null
        }
    }

    /**
     * Obtiene mensaje de error para alias inválido
     */
    fun getAliasErrorMessage(alias: String): String? {
        return when {
            alias.isBlank() -> "El alias no puede estar vacío"
            alias.trim().length < 3 -> "El alias debe tener al menos 3 caracteres"
            else -> null
        }
    }
}