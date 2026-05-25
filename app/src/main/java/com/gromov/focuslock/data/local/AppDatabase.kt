package com.gromov.focuslock.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gromov.focuslock.data.local.dao.BlockedAppDao
import com.gromov.focuslock.data.local.entity.BlockedAppEntity

@Database(
    entities = [BlockedAppEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
}