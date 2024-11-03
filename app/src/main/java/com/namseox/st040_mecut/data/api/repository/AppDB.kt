package com.namseox.st040_mecut.data.api.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.namseox.st040_mecut.data.api.dao.Dao
import com.namseox.st040_mecut.data.model.VideoModel
import javax.inject.Singleton

@Singleton
@Database(entities = [VideoModel::class], version = 1, exportSchema = false)
abstract class AppDB: RoomDatabase() {
    abstract fun dbDao(): Dao
}