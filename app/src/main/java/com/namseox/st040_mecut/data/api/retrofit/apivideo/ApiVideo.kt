package com.namseox.st040_mecut.data.api.retrofit.apivideo

import com.namseox.st040_mecut.data.model.VideoModel
import retrofit2.http.GET

interface ApiVideo {
    @GET("/api/fakevideocall/videocall")
    suspend fun getAllVideo() : List<VideoModel>
}