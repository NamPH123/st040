package com.namseox.st040_mecut.ui.music

import android.view.View
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.MusicModel
import com.namseox.st040_mecut.databinding.ItemMusicBinding
import com.namseox.st040_mecut.utils.fileToBytes
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack
import java.io.File

class MusicAdapter : AbsBaseAdapter<MusicModel, ItemMusicBinding>(R.layout.item_music, MusicDiff()) {
    var onItemClick: ((pos: Int) -> Unit)? = null
    var onPlayClick: ((pos: Int,item: ItemMusicBinding) -> Unit)? = null
    class MusicDiff : AbsBaseDiffCallBack<MusicModel>() {
        override fun itemsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
            return oldItem.path == newItem.path
        }

        override fun contentsTheSame(oldItem: MusicModel, newItem: MusicModel): Boolean {
            return oldItem.path != newItem.path
        }
    }

    override fun bind(binding: ItemMusicBinding, position: Int, data: MusicModel) {
        binding.apply {
            tvName.text = data.name
            tvTime.text = data.time
//            tvName.isSelected = true
            binding.imvVisualizer.updateVisualizer(fileToBytes(data.file))
            if(data.check){
                imvVisualizer.visibility = View.VISIBLE
            }else{
                imvVisualizer.visibility = View.GONE
            }
            if(data.checkPlaying){
                imvPlay.setImageResource(R.drawable.ic_play_select_music)
            }else{
                imvPlay.setImageResource(R.drawable.ic_pause_select_music)
            }
            root.setOnClickListener {
                onItemClick?.invoke(position)
            }
            imvPlay.setOnClickListener {
                onPlayClick?.invoke(position,binding)

            }
        }
    }
}