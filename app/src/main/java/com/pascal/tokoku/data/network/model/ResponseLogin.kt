package com.pascal.tokoku.data.network.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.pascal.tokoku.data.local.entity.StoreEntity

@Parcelize
data class ResponseLogin(

	@field:SerializedName("stores")
	val stores: List<StoreEntity>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable