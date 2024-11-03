package com.namseox.st040_mecut.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.namseox.st040_mecut.utils.getVideoDuration
import kotlinx.parcelize.Parcelize
import java.io.File
import java.io.Serializable
import java.util.Date

@Parcelize  //Parcelable
@Entity
data class VideoModel (
    var path : String,
    var isVideo : Boolean = false,
    var check: Boolean = false,
    var checkCut: Boolean = false,
    var checkTick: Boolean = false
): Parcelable, Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var size : Int = (File(path).length().toDouble()/(1024 * 1024)).toInt()
    var name : String = File(path).name
    var date : Long = File(path).lastModified()
    var duration: String = if(isVideo){
//        getVideoDuration(path)
        ""
    }else{
        ""
    }
}