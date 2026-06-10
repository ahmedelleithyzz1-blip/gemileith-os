package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "launcher_items")
data class LauncherItem(
    @PrimaryKey val id: String,
    val label: String,
    val packageName: String,
    val xPos: Float,
    val yPos: Float,
    val scale: Float = 1.0f,
    val rotation: Float = 0.0f,
    val filterStyle: String = "NEON_ECLIPSE",
    val isWidget: Boolean = false,
    val widgetType: String = "NONE",
    val hasNotification: Boolean = false,
    val usageCount: Int = 0
)
