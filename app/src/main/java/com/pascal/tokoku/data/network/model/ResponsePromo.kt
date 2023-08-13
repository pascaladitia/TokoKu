package com.pascal.tokoku.data.network.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ResponsePromo(

	@field:SerializedName("rc")
	val rc: String? = null,

	@field:SerializedName("rm")
	val rm: String? = null,
) : Parcelable
