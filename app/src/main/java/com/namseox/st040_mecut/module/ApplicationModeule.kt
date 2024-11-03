package com.namseox.st040_mecut.module

import android.content.Context
import com.namseox.st040_mecut.data.repository.ApiRepository
import com.namseox.st040_mecut.data.api.retrofit.apihelper.ApiHelper
import com.namseox.st040_mecut.data.repository.VideoRepository
import com.namseox.st040_mecut.utils.SharedPreferenceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModeule {
    @Singleton
    @Provides
    fun providerSharedPreference(@ApplicationContext appContext: Context): SharedPreferenceUtils {
        return SharedPreferenceUtils.getInstance(appContext)
    }

    @Singleton
    @Provides
    fun providerRepository(@ApplicationContext context: Context): VideoRepository {
        return VideoRepository(context)
    }

//    @Singleton
//    @Provides
//    fun providerApi(@ApplicationContext context: Context) : ApiHelper {
//        return ApiHelper(context)
//    }
//
//    @Singleton
//    @Provides
//    fun providerApiRepository( apiHelper: ApiHelper): ApiRepository {
//        return ApiRepository(apiHelper)
//    }
}