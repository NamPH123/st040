package com.namseox.st040_mecut.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File


fun cutVideo(inputPath: String, outputPath: String, startTime: String, endTime: String, callback: (Boolean) -> Unit) {

    val ffmpegCommand = "-ss $startTime -to $endTime -i $inputPath -c copy $outputPath"

    FFmpegKit.executeAsync(ffmpegCommand) { session ->
        val returnCode = session.returnCode
        if (ReturnCode.isSuccess(returnCode)) {
            callback(true)
        } else {
            callback(false)
        }
    }
}

fun createFileList(context: Context, videoPaths: List<String>): File {
    val listFile = File(context.filesDir.absolutePath, "filelist.txt")
    Log.d(TAG, "createFileList___: file$videoPaths"  + videoPaths.size)
    listFile.bufferedWriter().use { writer ->
        Log.d(TAG, "createFileList___: 1file$videoPaths"  + videoPaths.size)
        videoPaths.forEach { path ->
            Log.d(TAG, "createFileList___: file '$path'\n")
            writer.write("file '$path'\n")
        }
    }
    return listFile
}

fun mergeVideos(context: Context, videoPaths: List<String>, outputVideoPath: String, callback: (Boolean) -> Unit) {
    val listFile = createFileList(context, videoPaths)
    val ffmpegCommand = "-f concat -safe 0 -i ${listFile.absolutePath} -c copy $outputVideoPath"
    FFmpegKit.executeAsync(ffmpegCommand) { session ->
        Log.d(TAG, "mergeVideoszzzzz:3 ")

        val returnCode = session.returnCode
        if (ReturnCode.isSuccess(returnCode)) {
            Log.d(TAG, "mergeVideoszzzzz:1 ")
            callback(true)
            listFile.delete()
        } else {
            Log.d(TAG, "mergeVideoszzzzz:2 ")
            callback(false)
            listFile.delete()
        }
    }
}


