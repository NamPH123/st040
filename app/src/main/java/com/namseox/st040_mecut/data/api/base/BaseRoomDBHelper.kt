package com.namseox.st040_mecut.data.api.base

import android.content.Context
import androidx.room.Room
import com.namseox.st040_mecut.utils.SingletonHolder
//import com.namseox.st040_mecut.data.api.repository.AppDB
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.api.repository.AppDB

open class BaseRoomDBHelper(context: Context) {
    val db = Room.databaseBuilder(context, AppDB::class.java,context.getString(R.string.app_name)).build()
    companion object : SingletonHolder<BaseRoomDBHelper, Context>(::BaseRoomDBHelper)
}