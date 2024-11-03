package com.namseox.st040_mecut.data.api.retrofit.apicall

import com.namseox.st040_mecut.data.model.VideoModel
import retrofit2.http.GET

interface APICall {
    @GET("/api/fakevideocall/phonecall")
    suspend fun getAllCall() : List<VideoModel>
}