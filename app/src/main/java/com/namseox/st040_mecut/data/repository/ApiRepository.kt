package com.namseox.st040_mecut.data.repository

import android.util.Log
import com.namseox.st040_mecut.data.api.retrofit.apihelper.ApiHelper
import com.namseox.st040_mecut.data.model.VideoModel
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiHelper: ApiHelper) {
    val TAG = "exceptioncallapi"
    suspend fun getAllVideo(): List<VideoModel>?{
        return try {
            apiHelper.videoApi.getAllVideo()
        }catch (e: Exception){
            Log.d(TAG, "getAllVideo: "+e)
            null
        }
    }
    suspend fun getAllCall(): List<VideoModel>?{
        return try {
            apiHelper.callApi.getAllCall()
        }catch (e: Exception){
            Log.d(TAG, "getAllCall: "+e)
            null
        }
    }
}