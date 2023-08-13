package com.pascal.tokoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pascal.tokoku.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalService {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(storeEntity: StoreEntity)

    @Query("SELECT * FROM store")
    fun getStore(): Flow<List<StoreEntity>>

    @Query("SELECT * FROM store WHERE storeId=:id")
    fun getStoreDetail(id: String): Flow<StoreEntity>

    @Query("DELETE FROM store")
    fun deleteStore()
}