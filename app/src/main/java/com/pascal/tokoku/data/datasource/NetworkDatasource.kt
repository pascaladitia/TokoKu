package com.pascal.tokoku.data.datasource

import com.pascal.tokoku.data.network.model.ResponseLogin
import com.pascal.tokoku.data.network.model.ResponsePromo
import com.pascal.tokoku.data.network.service.NetworkService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

interface NetworkDatasource {
    suspend fun login(body: MultipartBody): ResponseLogin
}

class NetworkDatasourceImpl @Inject constructor(
    private val networkService: NetworkService
) : NetworkDatasource {
    override suspend fun login(body: MultipartBody): ResponseLogin {
        return networkService.login(body)
    }
}