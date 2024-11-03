package com.namseox.st040_mecut.ui.music

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.MusicModel
import com.namseox.st040_mecut.databinding.ActivityMusicBinding
import com.namseox.st040_mecut.utils.getAllAudioFiles
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicActivity : AbsBaseActivity<ActivityMusicBinding>(false) {
    lateinit var adapter: MusicAdapter
    var arrPath = arrayListOf<String>()
    var arrMusic = arrayListOf<MusicModel>()
    lateinit var mediaPlayer: MediaPlayer
    var posittion = -1
    var checkPlaying = false
    val handler = Handler()
     var runnable= Runnable{}
    var timeStart = 0
    var timeEnd = 0
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_music

    override fun init() {
        adapter = MusicAdapter()
        binding.rcv.adapter = adapter
        binding.rcv.layoutManager = GridLayoutManager(this, 1)
        binding.rcv.itemAnimator = null
        GlobalScope.launch(Dispatchers.IO) {
            arrPath = getAllAudioFiles(applicationContext) as ArrayList<String>
            arrPath.forEach { arrMusic.add(MusicModel(it)) }
            withContext(Dispatchers.Main) {
                binding.pb.visibility = View.GONE
                adapter.submitList(arrMusic)
            }
        }
        adapter.onItemClick = {
            arrMusic.forEach {
                it.check = false
            }
            arrMusic[it].check = true
            adapter.submitList(arrMusic)

        }
        adapter.onPlayClick = { pos, view ->
            if (posittion != -1 && pos != posittion) {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
            arrMusic.forEach {
                it.checkPlaying = false
            }
            if (pos == posittion) {
                if (!checkPlaying) {
                    arrMusic[pos].checkPlaying = true
                    mediaPlayer.seekTo(timeStart)
                    mediaPlayer.start()
                    checkPlaying = true
                    handler.post(runnable)

                } else {
                    checkPlaying = false
                    mediaPlayer.pause()
                    handler.removeCallbacks(runnable)
                }
            } else {
                mediaPlayer = MediaPlayer.create(applicationContext, Uri.parse(arrMusic[pos].path))
                arrMusic[pos].checkPlaying = true
                mediaPlayer.seekTo(timeStart)
                mediaPlayer.start()
                posittion = pos
                checkPlaying = true
                handler.removeCallbacks(runnable)
                runnable = kotlinx.coroutines.Runnable {
                    if (checkPlaying) {
                        view.imvVisualizer.updatePlayerPercent((mediaPlayer.currentPosition.toFloat() / mediaPlayer.duration.toFloat()))
                        timeStart = (view.imvVisualizer.leftHandle/view.imvVisualizer.width1 * mediaPlayer.duration).toInt()
                        timeEnd = (view.imvVisualizer.leftHandle/view.imvVisualizer.width1 * mediaPlayer.duration).toInt()
                        if(mediaPlayer.currentPosition>timeEnd && mediaPlayer.currentPosition-200<timeEnd){
                            mediaPlayer.pause()
                            checkPlaying = false
                            arrMusic[pos].checkPlaying = false
                            adapter.submitList(arrMusic)
                            handler.removeCallbacks(runnable)
                        }
                    }
                    handler.postDelayed(runnable, 100)
                }
                handler.post(runnable)
            }
            adapter.submitList(arrMusic)

        }
        binding.imvBack.setOnClickListener {
            finish()
        }
    }
}