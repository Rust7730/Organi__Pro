package com.e243768.organipro_.domain.model

data class Task(
    val id: String,
    val title: String,
    val points: String,
    val priority: String,
    val time: String,
    val isToday: Boolean
)