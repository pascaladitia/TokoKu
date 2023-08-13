package com.pascal.tokoku.data.datasource

import com.pascal.tokoku.data.local.dao.LocalService
import com.pascal.tokoku.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LocalDatasource {
    suspend fun insertStore(storeEntity: StoreEntity)
    suspend fun getStore(): Flow<List<StoreEntity>>
    suspend fun getStoreDetail(id: String): Flow<StoreEntity>
    suspend fun deleteStore()
}

class LocalDatasourceImpl @Inject constructor(
    private val localService: LocalService
): LocalDatasource {
    override suspend fun insertStore(storeEntity: StoreEntity) {
        return localService.insertStore(storeEntity)
    }

    override suspend fun getStore(): Flow<List<StoreEntity>> {
        return localService.getStore()
    }

    override suspend fun getStoreDetail(id: String): Flow<StoreEntity> {
        return localService.getStoreDetail(id)
    }

    override suspend fun deleteStore() {
        return localService.deleteStore()
    }
}