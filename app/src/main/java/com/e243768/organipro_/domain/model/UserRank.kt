package com.e243768.organipro_.domain.model

data class UserRank(
    val userId: String = "",
    val userName: String = "",
    val userAlias: String = "",
    val avatarUrl: String? = null,
    val rank: Int = 0,
    val points: Int = 0,
    val weeklyPoints: Int = 0,
    val monthlyPoints: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val isCurrentUser: Boolean = false
) {
    /**
     * Obtiene los puntos formateados
     */
    fun getFormattedPoints(): String {
        return String.format("%,d pts.", points)
    }

    /**
     * Obtiene el nombre a mostrar
     */
    fun getDisplayName(): String = userAlias.ifEmpty { userName }

    /**
     * Verifica si está en el top 3
     */
    fun isTopThree(): Boolean = rank in 1..3

    /**
     * Obtiene el tipo de medalla según el rank
     */
    fun getMedalType(): MedalType? {
        return when (rank) {
            1 -> MedalType.GOLD
            2 -> MedalType.SILVER
            3 -> MedalType.BRONZE
            else -> null
        }
    }
}

enum class MedalType(val color: String) {
    GOLD("#FFC700"),
    SILVER("#E0E0E0"),
    BRONZE("#CD7F32")
}