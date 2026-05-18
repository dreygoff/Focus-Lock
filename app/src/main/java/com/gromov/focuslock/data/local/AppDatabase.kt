package com.gromov.focuslock.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gromov.focuslock.data.local.dao.LockedAppDao
import com.gromov.focuslock.data.local.entity.LockedAppEntity

@Database(
    entities = [LockedAppEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lockedAppDao(): LockedAppDao
}