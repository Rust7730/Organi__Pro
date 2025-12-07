package com.e243768.organipro_.core.result

/**
 * Clase sellada para representar el resultado de operaciones que pueden fallar
 * Útil para manejo de estados en ViewModels y Repositories
 */
sealed class Result<out T> {
    /**
     * Estado de éxito con datos
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Estado de error con mensaje
     */
    data class Error(
        val message: String,
        val exception: Exception? = null
    ) : Result<Nothing>()

    /**
     * Estado de carga
     */
    object Loading : Result<Nothing>()

    /**
     * Verifica si el resultado es exitoso
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Verifica si el resultado es un error
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Verifica si está cargando
     */
    val isLoading: Boolean
        get() = this is Loading

    /**
     * Obtiene los datos si es exitoso, null en caso contrario
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Obtiene el mensaje de error si existe
     */
    fun getErrorMessage(): String? = when (this) {
        is Error -> message
        else -> null
    }

    /**
     * Ejecuta una acción si el resultado es exitoso
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }

    /**
     * Ejecuta una acción si el resultado es un error
     */
    inline fun onError(action: (String) -> Unit): Result<T> {
        if (this is Error) {
            action(message)
        }
        return this
    }

    /**
     * Ejecuta una acción si está cargando
     */
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) {
            action()
        }
        return this
    }
}

/**
 * Convierte cualquier valor en un Result.Success
 */
fun <T> T.toSuccess(): Result<T> = Result.Success(this)

/**
 * Crea un Result.Error con mensaje
 */
fun String.toError(): Result<Nothing> = Result.Error(this)

/**
 * Crea un Result.Error con excepción
 */
fun Exception.toError(message: String? = null): Result<Nothing> =
    Result.Error(message ?: this.message ?: "Error desconocido", this)