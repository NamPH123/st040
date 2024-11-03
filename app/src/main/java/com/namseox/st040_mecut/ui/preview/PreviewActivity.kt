package com.namseox.st040_mecut.ui.preview

import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.databinding.ActivityPreviewBinding
import com.namseox.st040_mecut.utils.formatDuration
import com.namseox.st040_mecut.view.base.AbsBaseActivity

class PreviewActivity : AbsBaseActivity<ActivityPreviewBinding>(false) {
    lateinit var exoPlayer: ExoPlayer
    var check = false
    var handel = Handler(Looper.myLooper()!!)
    var duration = 0L
    var checkPlay = false
    lateinit var runnable: Runnable
    override fun getFragmentID(): Int = 0

    override fun getLayoutId(): Int = R.layout.activity_preview

    override fun init() {
        var path = intent.getStringExtra("data")
        runnable = kotlinx.coroutines.Runnable {
            if (checkPlay) {
                binding.tvTimePlay.text = formatDuration(exoPlayer.currentPosition)
                binding.sb.progress = (exoPlayer.currentPosition * 100 / duration).toInt()
                handel.postDelayed(runnable, 500)
            } else {
               handel.removeCallbacks(runnable)
            }
        }
        exoPlayer = ExoPlayer.Builder(applicationContext).build()
        binding.epVideo.player = exoPlayer
        val mediaItem: MediaItem = MediaItem.fromUri(path!!)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
//exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        exoPlayer.pause()
                        exoPlayer.seekTo(0)
                        binding.imvPlay.setImageResource(R.drawable.ic_pause)
                    }
                    Player.STATE_READY->{
                        duration = exoPlayer.duration
                        Log.d(TAG, "init_________:${duration} ")

                        binding.tvDuration.text = formatDuration(duration)
                    }
                }
            }
        })
        binding.imvBack.setOnClickListener { finish() }
        binding.sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (check) {
                    exoPlayer.seekTo(progress*exoPlayer.duration/100)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                check = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                check = false
            }
        })
        binding.imvPlay.setOnClickListener {
            if(checkPlay){
                checkPlay = false
               exoPlayer.pause()
                Log.d(TAG, "init---------: ${exoPlayer.isPlaying}")
                binding.imvPlay.setImageResource(R.drawable.ic_pause)
                handel.removeCallbacks(runnable)
            }else{
                checkPlay = true
                exoPlayer.play()
                Log.d(TAG, "init---------: ${exoPlayer.isPlaying}")
                binding.imvPlay.setImageResource(R.drawable.ic_play)
                handel.post(runnable)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.pause()
        checkPlay = false
        handel.removeCallbacks(runnable)
    }
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        handel.removeCallbacks(runnable)
    }
}