package com.namseox.st040_mecut.ui.cutvideo

import android.content.ContentValues.TAG
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ActivityCutVideoBinding
import com.namseox.st040_mecut.ui.exportingvideo.ExportingVideoActivity
import com.namseox.st040_mecut.utils.OptiCutVideo
import com.namseox.st040_mecut.utils.dpToPx
import com.namseox.st040_mecut.utils.newIntent
import com.namseox.st040_mecut.view.base.AbsBaseActivity
import com.namseox.st040_mecut.view.customview.OptiMergeVideo

class CutVideoActivity : AbsBaseActivity<ActivityCutVideoBinding>(false) {
    var data = 0 //0-trim   1-cut
    lateinit var video: VideoModel
    lateinit var exo: ExoPlayer
    var handle = Handler(Looper.myLooper()!!)
    var left = 0L
    var right = 0L
    var leftCut = 0L
    var rightCut = 0L
    var length = 0L
    var lineCut = 0F
    lateinit var runable: Runnable
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_cut_video

    @UnstableApi
    override fun init() {
        video = intent.getSerializableExtra("data") as VideoModel
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(video.path)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = durationStr?.toLongOrNull() ?: 0L
        length = durationMs * 1000

        runable = Runnable {
            if (data == 0) {
                if (exo.currentPosition * 1000 < right + 100* 1000 && exo.currentPosition * 1000 > right - 100* 1000) {
                    binding.imvPlay.setImageResource(R.drawable.ic_pause)
                    exo.pause()
                    handle.removeCallbacks(runable)
                } else {
                    binding.otlmv.changeLine(
                        exo.currentPosition * ((binding.otlCut.measuredWidth) - dpToPx(
                            20f,
                            applicationContext
                        )) / (length / 1000).toFloat()
                    )
                    binding.otlCut.changeLine(
                        exo.currentPosition * ((binding.otlCut.measuredWidth) - dpToPx(
                            20f,
                            applicationContext
                        )) / (length / 1000).toFloat()
                    )
                    handle.postDelayed(runable, 100)
                }
            } else {
                Log.d(TAG, "init....++++: ${exo.currentPosition} ....  ${exo.duration}    ")
                if (exo.currentPosition < exo.duration - 100) {
                    binding.otlCut.changeLine(
                        exo.currentPosition * ((binding.otlCut.measuredWidth) - dpToPx(
                            20f,
                            applicationContext
                        )) / (length / 1000).toFloat()
                    )
                    binding.otlmv.changeLine(
                        exo.currentPosition * ((binding.otlCut.measuredWidth) - dpToPx(
                            20f,
                            applicationContext
                        )) / (length / 1000).toFloat()
                    )
                    handle.postDelayed(runable, 100)
                } else {
                    binding.imvPlay.setImageResource(R.drawable.ic_pause)
                    handle.removeCallbacks(runable)
                }
            }
        }
        exo = ExoPlayer.Builder(applicationContext).build()
        binding.epVideo.player = exo
        val mediaItem: MediaItem = MediaItem.fromUri(video.path)
        val mediaSource = DefaultMediaSourceFactory(this).createMediaSource(mediaItem)
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
        binding.apply {
            binding.tvTime.text = video.duration
            binding.otlmv.setVideo(Uri.parse(video.path))
            binding.otlCut.setVideo(Uri.parse(video.path))

            imvPlay.setOnClickListener {
                if (exo.isPlaying) {
                    exo.pause()
                    imvPlay.setImageResource(R.drawable.ic_pause)
                    handle.removeCallbacks(runable)
                } else {
                    right = (binding.otlmv.rightHandle - dpToPx(
                        12f,
                        applicationContext
                    ).toLong()) * length / binding.otlmv.measuredWidth
                    if (data == 0) {
                        exo.seekTo(left / 1000)
                    } else {
                        exo.seekTo((lineCut * length / 1000 / otlmv.measuredWidth).toLong())
                    }
                    exo.play()
                    handle.post(runable)
                    imvPlay.setImageResource(R.drawable.ic_play)
                }
            }
            tvTrim.setOnClickListener {
                binding.imvTrue.setBackgroundResource(R.drawable.bg_card_border8)
                binding.imvFalse.setBackgroundResource(R.drawable.bg_card_border9)
                data = 0
                binding.otlmv.visibility = View.VISIBLE
                binding.otlCut.visibility = View.INVISIBLE
            }
            binding.tvCut.setOnClickListener {
                binding.imvTrue.setBackgroundResource(R.drawable.bg_card_border9)
                binding.imvFalse.setBackgroundResource(R.drawable.bg_card_border8)
                data = 1
                binding.otlCut.visibility = View.VISIBLE
                binding.otlmv.visibility = View.INVISIBLE
            }


            otlmv.setOnCustomTouchListener(object : OptiMergeVideo.OnCustomTouchListener {
                override fun onTouchEvent(event: MotionEvent?) {
                    left = otlmv.leftHandle * length / otlmv.measuredWidth
                    right = (otlmv.rightHandle - dpToPx(
                        12f,
                        applicationContext
                    ).toLong()) * length / otlmv.measuredWidth
                }

                override fun onChangeLine(i: Float) {
                    exo.seekTo((i * length / 1000 / otlmv.measuredWidth).toLong())
                }

            })
            otlCut.setOnCustomTouchListener(object : OptiCutVideo.OnCustomTouchListener {
                override fun onTouchEvent(event: MotionEvent?) {
                    leftCut = otlmv.leftHandle * length / otlmv.measuredWidth
                    rightCut = (otlmv.rightHandle - dpToPx(
                        12f,
                        applicationContext
                    ).toLong()) * length / otlmv.measuredWidth
                }

                override fun onChangeLine(i: Float) {
                    if (data != 0) {
                        lineCut = i
                    }
                    exo.seekTo((i * length / 1000 / otlmv.measuredWidth).toLong())
                }

            })
            otlmv.setOnTouchListener { v, event -> true }
            otlCut.setOnTouchListener { v, event -> true }
            binding.imvSave.setOnClickListener {
                if (data == 0) {
                    right = (binding.otlmv.rightHandle ) * length / binding.otlmv.measuredWidth
                    left = otlmv.leftHandle * length / otlmv.measuredWidth
                    startActivity(
                        newIntent(
                            applicationContext,
                            ExportingVideoActivity::class.java
                        ).putExtra("data", arrayListOf(video)).putExtra(
                            "time",
                            arrayOf(arrayOf(left, right, length))
                        )
                    )
                } else {

                    startActivity(
                        newIntent(
                            applicationContext,
                            ExportingVideoActivity::class.java
                        ).putExtra("data", arrayListOf(video, video)).putExtra(
                            "time",
                            arrayOf(
                                arrayOf(0, otlmv.leftHandle * length / otlmv.measuredWidth, length),
                                arrayOf(
                                    (binding.otlmv.rightHandle) * length / binding.otlmv.measuredWidth,
                                    (otlmv.measuredWidth - dpToPx(
                                        20f,
                                        applicationContext
                                    ).toLong()) * length / otlmv.measuredWidth,
                                    length
                                )
                            )
                        )
                    )
                }
            }
            binding.imvBack.setOnClickListener { finish() }
        }
    }
}