package com.pascal.tokoku.presentation.login

import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.network.model.ResponseLogin
import kotlinx.coroutines.flow.StateFlow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface LoginContract {
    val loginResult: StateFlow<Resource<ResponseLogin>>
    fun login(body: MultipartBody)
}