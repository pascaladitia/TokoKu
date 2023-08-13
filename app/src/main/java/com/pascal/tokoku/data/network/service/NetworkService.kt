package com.pascal.tokoku.data.network.service

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.pascal.tokoku.common.utils.Constant
import com.pascal.tokoku.data.network.model.ResponseLogin
import com.pascal.tokoku.data.network.model.ResponsePromo
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface NetworkService {

    @POST("login/loginTest")
    suspend fun login(@Body body: MultipartBody): ResponseLogin

    companion object {
        @JvmStatic
        operator fun invoke(chuckerInterceptor: ChuckerInterceptor): NetworkService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
//                .addInterceptor(chuckerInterceptor)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(NetworkService::class.java)
        }
    }
}