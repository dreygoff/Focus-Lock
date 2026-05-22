package com.gromov.focuslock.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gromov.focuslock.data.local.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {

    @Query("SELECT packageName FROM blocked_apps")
    fun getBlockedApps(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun blockApp(app: BlockedAppEntity)

    @Query("DELETE FROM blocked_apps WHERE packageName= :packageName")
    suspend fun unblockApp(packageName: String)
}