package com.pascal.tokoku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pascal.tokoku.data.local.dao.LocalService
import com.pascal.tokoku.data.local.entity.StoreEntity

@Database(
    entities = [StoreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun localService(): LocalService
}