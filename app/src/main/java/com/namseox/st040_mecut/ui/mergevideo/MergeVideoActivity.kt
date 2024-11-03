package com.namseox.st040_mecut.ui.mergevideo

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ClippingMediaSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.recyclerview.widget.GridLayoutManager
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ActivityMergeVideoBinding
import com.namseox.st040_mecut.ui.exportingvideo.ExportingVideoActivity
import com.namseox.st040_mecut.utils.dpToPx
import com.namseox.st040_mecut.utils.newIntent
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import com.namseox.st040_mecut.view.customview.OptiMergeVideo
import com.namseox.st040_mecut.view.customview.OptiTimeLineView
import java.util.concurrent.TimeUnit

class MergeVideoActivity : AbsBaseActivity<ActivityMergeVideoBinding>(false) {
    var arrSellect = arrayListOf<VideoModel>()
    var adapter = MergeAdapter()
    var checkDefault = true
    var arrTime = arrayOf(
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
        arrayOf(0L, 0L, 0L),
    )
    var handle = Handler(Looper.myLooper()!!)
    lateinit var runable: Runnable
    lateinit var exo: ExoPlayer

    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_merge_video

    @UnstableApi
    override fun init() {
        runable = Runnable {
            if (exo.currentPosition < arrTime[pos][1] / 1000 + 100 && exo.currentPosition > arrTime[pos][1] / 1000 - 100) {
                binding.imvPlay.setImageResource(R.drawable.ic_pause)
                exo.pause()
                handle.removeCallbacks(runable)
            } else {
                binding.otlmv.changeLine(exo.currentPosition * (binding.otlmv.measuredWidth) / (arrTime[pos][2] / 1000).toFloat())
                handle.postDelayed(runable, 100)
            }

        }
        arrSellect = intent.getSerializableExtra("data") as ArrayList<VideoModel>
        var time = 0L
        arrSellect.forEachIndexed { index, it ->
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(it.path)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            arrTime[index][1] = durationMs * 1000
            arrTime[index][2] = durationMs * 1000
            retriever.release()
            time += durationMs
            val minutes = TimeUnit.MILLISECONDS.toMinutes(time)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(time) % 60
            binding.tvTime.text = String.format("%02d:%02d", minutes, seconds)
        }
        binding.rcv.adapter = adapter
        binding.rcv.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        adapter.submitList(arrSellect)


        updateTimeVideo()


        try {
            binding.timeLineView1.setVideo(Uri.parse(arrSellect[0].path))
            binding.timeLineView2.setVideo(Uri.parse(arrSellect[1].path))
            binding.timeLineView3.setVideo(Uri.parse(arrSellect[2].path))
            binding.timeLineView4.setVideo(Uri.parse(arrSellect[3].path))
            binding.timeLineView5.setVideo(Uri.parse(arrSellect[4].path))
            binding.timeLineView6.setVideo(Uri.parse(arrSellect[5].path))
            binding.timeLineView7.setVideo(Uri.parse(arrSellect[6].path))
            binding.timeLineView8.setVideo(Uri.parse(arrSellect[7].path))
            binding.timeLineView9.setVideo(Uri.parse(arrSellect[8].path))
            binding.timeLineView10.setVideo(Uri.parse(arrSellect[9].path))
        } catch (e: Exception) {

        }
        binding.apply {
            timeLineView1.setOnClickListener {
                updateSelect(timeLineView1)
            }
            timeLineView2.setOnClickListener {
                updateSelect(timeLineView2)
            }
            timeLineView3.setOnClickListener {
                updateSelect(timeLineView3)
            }
            timeLineView4.setOnClickListener {
                updateSelect(timeLineView4)
            }
            timeLineView5.setOnClickListener {
                updateSelect(timeLineView5)
            }
            timeLineView6.setOnClickListener {
                updateSelect(timeLineView6)
            }
            timeLineView7.setOnClickListener {
                updateSelect(timeLineView7)
            }
            timeLineView8.setOnClickListener {
                updateSelect(timeLineView8)
            }
            timeLineView9.setOnClickListener {
                updateSelect(timeLineView9)
            }
            timeLineView10.setOnClickListener {
                updateSelect(timeLineView10)
            }
        }
        setClick()
    }

    var pos = 0

    @SuppressLint("ClickableViewAccessibility")
    @UnstableApi
    private fun setClick() {
        adapter.onItemClick = {
            pos = it
            binding.otlmv.visibility = View.VISIBLE
            binding.otlmv.setVideo(Uri.parse(arrSellect[it].path))
            if(arrTime[it][1]==arrTime[pos][2]){
                binding.otlmv.rightHandle = arrTime[it][1] * binding.otlmv.measuredWidth/ arrTime[pos][2]
            }else{
                binding.otlmv.rightHandle = arrTime[it][1] * binding.otlmv.measuredWidth/ arrTime[pos][2]+ dpToPx(20f, applicationContext).toLong()
            }
            binding.otlmv.leftHandle = arrTime[it][0] * binding.otlmv.measuredWidth/ arrTime[pos][2]
            updateExo(it)
        }
        binding.apply {
            otlmv.setOnCustomTouchListener(object : OptiMergeVideo.OnCustomTouchListener {
                override fun onTouchEvent(event: MotionEvent?) {
                    arrTime[pos][0] = otlmv.leftHandle * arrTime[pos][2] / otlmv.measuredWidth
                    arrTime[pos][1] = (otlmv.rightHandle - dpToPx(
                        20f,
                        applicationContext
                    ).toLong()) * arrTime[pos][2] / otlmv.measuredWidth
                }

                override fun onChangeLine(i: Float) {
                    exo.seekTo((i * arrTime[pos][2] / 1000 / otlmv.measuredWidth).toLong())
                }

            })
            otlmv.setOnTouchListener { v, event -> true }
            imvTick.setOnClickListener {
                checkDefault = true
                binding.ll2.visibility = View.VISIBLE
                binding.ctv.visibility = View.VISIBLE
                binding.otlmv.visibility = View.GONE
                binding.rcv.visibility = View.GONE
                binding.tvAdjust.visibility = View.GONE
                binding.imvTick.visibility = View.GONE
                updateTimeVideo()
            }
            imvBack.setOnClickListener { finish() }
            imvPlay.setOnClickListener {
                if (exo.isPlaying) {
                    exo.pause()
                    imvPlay.setImageResource(R.drawable.ic_pause)
                    handle.removeCallbacks(runable)
                } else {
                    if (!checkDefault) {
                        exo.seekTo(arrTime[pos][0] / 1000)
                        handle.post(runable)
                    }
                    exo.play()
                    imvPlay.setImageResource(R.drawable.ic_play)
                }
            }
            imvSave.onSingleClick {
                startActivity(newIntent(applicationContext,ExportingVideoActivity::class.java).putExtra("data", arrSellect).putExtra("time",arrTime))
            }
        }
    }

    @UnstableApi
    fun updateExo(it: Int) {
        exo.release()
        exo = ExoPlayer.Builder(applicationContext).build()
        binding.epVideo.player = exo
//        val concatenatingMediaSource = ConcatenatingMediaSource()
        val mediaItem: MediaItem = MediaItem.fromUri(arrSellect[it].path)
        val mediaSource = DefaultMediaSourceFactory(this).createMediaSource(mediaItem)
//        val clippedMediaSource2 = ClippingMediaSource(mediaSource, arrTime[it][0], arrTime[it][1])
//        concatenatingMediaSource.addMediaSource(clippedMediaSource2)
        exo.setMediaSource(mediaSource)
        exo.prepare()
        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        exo.pause()
                        binding.imvPlay.setImageResource(R.drawable.ic_pause)
                        handle.removeCallbacks(runable)

                    }

                }
            }
        })
    }

    fun updateSelect(view: OptiTimeLineView) {
        pos = 0
        checkDefault = false
        binding.ll2.visibility = View.GONE
        binding.ctv.visibility = View.GONE
        binding.rcv.visibility = View.VISIBLE
        binding.tvAdjust.visibility = View.VISIBLE
        binding.imvTick.visibility = View.VISIBLE
    }

    @UnstableApi
    fun updateTimeVideo() {
        if (::exo.isInitialized) {
            exo.release()
        }
        exo = ExoPlayer.Builder(applicationContext).build()
        binding.epVideo.player = exo
        val concatenatingMediaSource = ConcatenatingMediaSource()
        arrSellect.forEachIndexed { index, it ->
            val mediaItem: MediaItem = MediaItem.fromUri(it.path)
            val mediaSource = DefaultMediaSourceFactory(this).createMediaSource(mediaItem)
            val clippedMediaSource2 =
                ClippingMediaSource(mediaSource, arrTime[index][0], arrTime[index][1])
            concatenatingMediaSource.addMediaSource(clippedMediaSource2)
        }
        exo.setMediaSource(concatenatingMediaSource)
        exo.prepare()
        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        exo.pause()
                        exo.seekTo(0)
                        binding.imvPlay.setImageResource(R.drawable.ic_pause)
                        handle.removeCallbacks(runable)
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        exo.release()
    }
}