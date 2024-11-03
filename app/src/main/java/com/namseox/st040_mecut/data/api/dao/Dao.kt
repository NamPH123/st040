package com.namseox.st040_mecut.data.api.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.namseox.st040_mecut.data.model.VideoModel

@Dao
interface Dao {
    @Query("SELECT * FROM VideoModel")
    fun getAllTheme(): List<VideoModel>

    @Insert
    fun setTheme(sound: VideoModel): Long

    @Delete
    fun deleteTheme(sound: VideoModel): Int

    @Query("SELECT * FROM VideoModel WHERE id = :name")
    fun getItem(name : Int): List<VideoModel>

    @Query("DELETE FROM VideoModel")
    fun deleteAll(): Int
}