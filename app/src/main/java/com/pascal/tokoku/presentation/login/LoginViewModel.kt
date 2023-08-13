package com.pascal.tokoku.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.tokoku.common.utils.Constant
import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.network.model.ResponseLogin
import com.pascal.tokoku.data.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : LoginContract, ViewModel() {

    private val _loginResult = MutableStateFlow<Resource<ResponseLogin>>(Resource.Empty())
    override val loginResult: StateFlow<Resource<ResponseLogin>> = _loginResult

    override fun login(body: MultipartBody) {
        _loginResult.value = Resource.Loading()
        viewModelScope.launch {
            networkRepository.login(body)
                .collect {
                    try {
                        _loginResult.value = Resource.Success(it.data)
                    } catch (e: Exception) {
                        _loginResult.value = Resource.Error(
                            exception = e,
                            message = Constant.ERROR_MESSAGE
                        )
                    }
                }
        }
    }
}