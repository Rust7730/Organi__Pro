package com.e243768.organipro_.domain.model

enum class Priority(val displayName: String, val points: Int) {
    BAJA("Baja", 50),
    MEDIA("Media", 100),
    ALTA("Alta", 200);

    companion object {
        fun fromString(value: String): Priority {
            return when (value.lowercase()) {
                "baja" -> BAJA
                "media" -> MEDIA
                "alta" -> ALTA
                else -> MEDIA
            }
        }
    }
}