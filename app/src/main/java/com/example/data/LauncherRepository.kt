package com.example.data

import kotlinx.coroutines.flow.Flow

class LauncherRepository(private val launcherItemDao: LauncherItemDao) {
    val allItems: Flow<List<LauncherItem>> = launcherItemDao.getAllItems()

    suspend fun insertItem(item: LauncherItem) {
        launcherItemDao.insertItem(item)
    }

    suspend fun insertAll(items: List<LauncherItem>) {
        launcherItemDao.insertAll(items)
    }

    suspend fun updateTransform(id: String, x: Float, y: Float, scale: Float, rotation: Float) {
        launcherItemDao.updateTransform(id, x, y, scale, rotation)
    }

    suspend fun updateFilterStyle(id: String, filterStyle: String) {
        launcherItemDao.updateFilterStyle(id, filterStyle)
    }

    suspend fun incrementUsageCount(id: String) {
        launcherItemDao.incrementUsageCount(id)
    }

    suspend fun updateNotification(id: String, hasNotification: Boolean) {
        launcherItemDao.updateNotification(id, hasNotification)
    }

    suspend fun deleteItemById(id: String) {
        launcherItemDao.deleteItemById(id)
    }

    suspend fun deleteAll() {
        launcherItemDao.deleteAll()
    }
}
