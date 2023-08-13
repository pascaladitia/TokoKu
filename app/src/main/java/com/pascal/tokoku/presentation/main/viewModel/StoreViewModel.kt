package com.pascal.tokoku.presentation.main.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.tokoku.common.utils.Constant
import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.local.entity.StoreEntity
import com.pascal.tokoku.data.repository.LocalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val localRepository: LocalRepository,
) : StoreContract, ViewModel() {
    private val _getStoreResult = MutableStateFlow<Resource<List<StoreEntity>>>(Resource.Empty())
    override val getStoreResult: StateFlow<Resource<List<StoreEntity>>> = _getStoreResult

    private val _getDetailStoreResult = MutableStateFlow<Resource<StoreEntity>>(Resource.Empty())
    override val getDetailStoreResult: StateFlow<Resource<StoreEntity>> = _getDetailStoreResult

    private val _insertStoreResult = MutableStateFlow< Resource<Unit>>(Resource.Empty())
    override val insertStoreResult: StateFlow<Resource<Unit>> = _insertStoreResult

    private val _deleteResult = MutableStateFlow< Resource<Unit>>(Resource.Empty())
    override val deleteResult: StateFlow<Resource<Unit>> = _deleteResult

    override fun getStore() {
        _getStoreResult.value = Resource.Loading()
        viewModelScope.launch {
            localRepository.getStore()
                .collect {
                    try {
                        _getStoreResult.value = Resource.Success(it.data)
                    } catch (e: Exception) {
                        _getStoreResult.value = Resource.Error(
                            exception = e,
                            message = Constant.ERROR_MESSAGE
                        )
                    }
                }
        }
    }

    override fun getDetailStore(id: String) {
        _getDetailStoreResult.value = Resource.Loading()
        viewModelScope.launch {
            localRepository.getStoreDetail(id)
                .collect {
                    try {
                        _getDetailStoreResult.value = Resource.Success(it.data)
                    } catch (e: Exception) {
                        _getDetailStoreResult.value = Resource.Error(
                            exception = e,
                            message = Constant.ERROR_MESSAGE
                        )
                    }
                }
        }
    }

    override fun insertStore(Store: StoreEntity) {
        _insertStoreResult.value = Resource.Loading()
        viewModelScope.launch {
            localRepository.insertStore(Store)
                .collect {
                    try {
                        _insertStoreResult.value = Resource.Success(it.data)
                    } catch (e: Exception) {
                        _insertStoreResult.value = Resource.Error(
                            exception = e,
                            message = Constant.ERROR_MESSAGE
                        )
                    }
                }
        }
    }

    override fun deleteStore() {
        _deleteResult.value = Resource.Loading()
        viewModelScope.launch {
            localRepository.deleteStore()
                .collect {
                    try {
                        _deleteResult.value = Resource.Success(it.data)
                    } catch (e: Exception) {
                        _deleteResult.value = Resource.Error(
                            exception = e,
                            message = Constant.ERROR_MESSAGE
                        )
                    }
                }
        }
    }
}