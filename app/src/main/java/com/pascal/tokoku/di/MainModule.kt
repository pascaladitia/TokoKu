package com.pascal.tokoku.di

import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.pascal.tokoku.common.utils.Constant
import com.pascal.tokoku.data.local.LocalDatabase
import com.pascal.tokoku.data.local.dao.LocalService
import com.pascal.tokoku.data.datasource.LocalDatasource
import com.pascal.tokoku.data.datasource.LocalDatasourceImpl
import com.pascal.tokoku.data.datasource.NetworkDatasource
import com.pascal.tokoku.data.datasource.NetworkDatasourceImpl
import com.pascal.tokoku.data.network.service.NetworkService
import com.pascal.tokoku.data.repository.LocalRepository
import com.pascal.tokoku.data.repository.LocalRepositoryImpl
import com.pascal.tokoku.data.repository.NetworkRepository
import com.pascal.tokoku.data.repository.NetworkRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

object MainModule {

    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {
        @Provides
        @Singleton
        fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
            return ChuckerInterceptor.Builder(context).build()
        }

        @Provides
        @Singleton
        fun provideNetworkService(chuckerInterceptor: ChuckerInterceptor): NetworkService {
            return NetworkService.invoke(chuckerInterceptor)
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object LocalModule {
        @Provides
        @Singleton
        fun provideLocalDatabase(
            @ApplicationContext context: Context
        ): LocalDatabase = Room
            .databaseBuilder(
                context,
                LocalDatabase::class.java,
                Constant.DB_NAME
            ).build()

        @Provides
        @Singleton
        fun provideLocalService(localDatabase: LocalDatabase) = localDatabase.localService()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object DataSourceModule {

        @Provides
        @Singleton
        fun provideLocalDatasource(
            localService: LocalService
        ): LocalDatasource {
            return LocalDatasourceImpl(localService)
        }

        @Provides
        @Singleton
        fun provideInfoDatasource(networkService: NetworkService): NetworkDatasource {
            return NetworkDatasourceImpl(networkService)
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object RepositoryModule {
        @Provides
        @Singleton
        fun provideLocalRepository(localDatasource: LocalDatasource): LocalRepository {
            return LocalRepositoryImpl(localDatasource)
        }

        @Provides
        @Singleton
        fun provideNetworkRepository(networkDatasource: NetworkDatasource): NetworkRepository {
            return NetworkRepositoryImpl(networkDatasource)
        }
    }
}