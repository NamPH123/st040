package com.namseox.st040_mecut.ui.exportingvideo

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ActivityExportingVideoBinding
import com.namseox.st040_mecut.ui.savesuccessful.SaveSuccessfulActivity
import com.namseox.st040_mecut.utils.cutVideo
import com.namseox.st040_mecut.utils.mergeVideos
import com.namseox.st040_mecut.utils.newIntent
import com.namseox.st040_mecut.utils.showToast
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import java.io.File

class ExportingVideoActivity : AbsBaseActivity<ActivityExportingVideoBinding>(false) {
    var arrTime: Array<Array<Long>> = arrayOf()
    var arrSellect = arrayListOf<VideoModel>()

    var list = arrayListOf<String>()
    var pos = 0

    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_exporting_video

    override fun init() {
        var output = filesDir.absolutePath + "/output/"
        var file = File(output)
        file.mkdirs()
        val mergedOutput =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath + "/" + System.currentTimeMillis() + ".mp4"
        arrSellect = intent.getSerializableExtra("data") as ArrayList<VideoModel>
        arrTime = intent.getSerializableExtra("time") as Array<Array<Long>>
        Glide.with(applicationContext).load(arrSellect[0].path).into(binding.imv)
        var a = 0
        arrSellect.forEachIndexed { index, videoModel ->
            list.add("$output$index.mp4")
            cutVideo(
                videoModel.path,
                "$output$index.mp4",
                (arrTime[index][0] / 1000000f).toString(),
                (arrTime[index][1] / 1000000f).toString()
            ) { it ->
                if (!it) {
                    list.forEach {
                        var file = File(it)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    runOnUiThread {
                        showToast(applicationContext, R.string.merge_failed)
                        finish()
                    }

                } else {
                    a++
                    if (a == arrSellect.size) {
                        mergeVideos(applicationContext, list, mergedOutput) { successMerge ->
                            if (!successMerge) {
                                Log.d(TAG, "mergeVideoszzzzz:4 ")
                                list.forEach {
                                    var file = File(it)
                                    if (file.exists()) {
                                        file.delete()
                                    }
                                }
                                runOnUiThread {
                                    showToast(applicationContext, R.string.merge_failed)
                                    finish()
                                }

                            } else {
                                Log.d(TAG, "mergeVideoszzzzz:5 ")
                                list.forEach {
                                    var file = File(it)
                                    if (file.exists()) {
                                        file.delete()
                                    }
                                }
                                runOnUiThread {
                                    showToast(applicationContext, R.string.merge_successful)
                                    MediaScannerConnection.scanFile(
                                        applicationContext,
                                        arrayOf(mergedOutput),
                                        null,
                                        null
                                    );
                                    startActivity(
                                        newIntent(
                                            applicationContext,
                                            SaveSuccessfulActivity::class.java
                                        ).putExtra("data", mergedOutput)
                                    )
                                    finish()
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }
}