package com.pascal.tokoku.data.repository

import com.pascal.tokoku.common.base.BaseRepository
import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.datasource.NetworkDatasource
import com.pascal.tokoku.data.network.model.ResponseLogin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

interface NetworkRepository {
    suspend fun login(body: MultipartBody): Flow<Resource<ResponseLogin>>
}

class NetworkRepositoryImpl @Inject constructor(
    private val networkDatasource: NetworkDatasource
): NetworkRepository, BaseRepository() {
    override suspend fun login(body: MultipartBody): Flow<Resource<ResponseLogin>> = flow {
        emit(proceed { networkDatasource.login(body)})
    }
}