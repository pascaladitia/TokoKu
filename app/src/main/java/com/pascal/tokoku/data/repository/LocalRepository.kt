package com.pascal.tokoku.data.repository

import com.pascal.tokoku.common.base.BaseRepository
import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.datasource.LocalDatasource
import com.pascal.tokoku.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface LocalRepository {
    suspend fun insertStore(storeEntity: StoreEntity): Flow<Resource<Unit>>
    suspend fun getStore(): Flow<Resource<List<StoreEntity>>>
    suspend fun getStoreDetail(id: String): Flow<Resource<StoreEntity>>
    suspend fun deleteStore(): Flow<Resource<Unit>>
}

class LocalRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDatasource
) : LocalRepository, BaseRepository() {

    override suspend fun insertStore(storeEntity: StoreEntity): Flow<Resource<Unit>> = flow {
        emit(proceed { localDatasource.insertStore(storeEntity) })
    }

    override suspend fun getStore(): Flow<Resource<List<StoreEntity>>> = flow {
        emit(proceed { localDatasource.getStore().first() })
    }

    override suspend fun getStoreDetail(id: String): Flow<Resource<StoreEntity>> = flow {
        emit(proceed { localDatasource.getStoreDetail(id).first() })
    }

    override suspend fun deleteStore(): Flow<Resource<Unit>> = flow {
        emit(proceed { localDatasource.deleteStore() })
    }
}