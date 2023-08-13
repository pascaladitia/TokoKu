package com.pascal.tokoku.presentation.main.viewModel

import com.pascal.tokoku.common.wrapper.Resource
import com.pascal.tokoku.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.StateFlow

interface StoreContract {
    val getStoreResult: StateFlow<Resource<List<StoreEntity>>>
    fun getStore()

    val getDetailStoreResult: StateFlow<Resource<StoreEntity>>
    fun getDetailStore(id: String)

    val insertStoreResult: StateFlow<Resource<Unit>>
    fun insertStore(Store: StoreEntity)

    val deleteResult: StateFlow<Resource<Unit>>
    fun deleteStore()
}