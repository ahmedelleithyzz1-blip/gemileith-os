package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherItemDao {
    @Query("SELECT * FROM launcher_items")
    fun getAllItems(): Flow<List<LauncherItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LauncherItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LauncherItem>)

    @Query("UPDATE launcher_items SET xPos = :x, yPos = :y, scale = :scale, rotation = :rotation WHERE id = :id")
    suspend fun updateTransform(id: String, x: Float, y: Float, scale: Float, rotation: Float)

    @Query("UPDATE launcher_items SET filterStyle = :filterStyle WHERE id = :id")
    suspend fun updateFilterStyle(id: String, filterStyle: String)

    @Query("DELETE FROM launcher_items WHERE id = :id")
    suspend fun deleteItemById(id: String)

    @Query("DELETE FROM launcher_items")
    suspend fun deleteAll()
}
