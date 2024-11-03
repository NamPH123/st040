package com.namseox.st040_mecut.data.repository

import android.content.Context
import android.util.Log
import android.view.ViewDebug
import com.namseox.st040_mecut.data.api.base.BaseRoomDBHelper
import com.namseox.st040_mecut.data.model.VideoModel

class VideoRepository(context: Context): BaseRoomDBHelper(context)  {
    fun getAllVideo(): List<VideoModel>{
        return try {
            db.dbDao().getAllTheme()
        } catch (e: Exception) {
            Log.d("TAG", "exception_of_app getAll from roomDB: ${e} ")
            arrayListOf<VideoModel>()
        }
    }
    fun addVideo(idol : VideoModel): Int{
        return try {
            db.dbDao().setTheme(idol).toInt()
        } catch (e: Exception) {
            Log.d("TAG", "exception_of_app addIdol from roomDB: ${e} ")
            -1
        }
    }
    fun deleteVideo(video : VideoModel): Int{
        return try {
            db.dbDao().deleteTheme(video)
        } catch (e: Exception) {
            Log.d("TAG", "exception_of_app addIdol from roomDB: ${e} ")
            -1
        }
    }
    fun deleteAll(): Int{
        return try {
            db.dbDao().deleteAll()
        } catch (e: Exception) {
            Log.d("TAG", "exception_of_app addIdol from roomDB: ${e} ")
            -1
        }
    }
}