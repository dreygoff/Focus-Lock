package com.gromov.focuslock.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gromov.focuslock.data.local.entity.LockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LockedAppDao {

    @Query("SELECT packageName FROM locked_apps")
    fun getLockedApps(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun lockApp(app: LockedAppEntity)

    @Query("DELETE FROM locked_apps WHERE packageName= :packageName")
    suspend fun unlockApp(packageName: String)
}