package com.namseox.st040_mecut.data.api.retrofit.apihelper

import android.content.Context
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.namseox.st040_mecut.data.api.base.BaseRetrofitHelper
import com.namseox.st040_mecut.data.api.retrofit.apicall.APICall
import com.namseox.st040_mecut.data.api.retrofit.apivideo.ApiVideo
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiHelper(context: Context) : BaseRetrofitHelper() {
companion object{
    var BASE_URL = "https://www.google.com/"
}
    var videoApi: ApiVideo
    var callApi : APICall

    init {
        GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory()).client(okHttpClient!!).build()
        videoApi = retrofit.create(ApiVideo::class.java)
        callApi = retrofit.create(APICall::class.java)
    }
}