package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_logs")
data class DayLogEntity(
    @PrimaryKey val date: String, // "yyyy-MM-dd"
    val status: String,           // "CLEAN", "RELAPSE", "UNMARKED"
    val note: String = "",
    val urgeLevel: Int = 0,
    val triggerReason: String = "",
    val penaltyAssigned: String = "",
    val penaltyCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "punishments")
data class PunishmentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val intensity: String,
    val isCompleted: Boolean,
    val dateAssigned: String,
    val completedAt: Long? = null
)
