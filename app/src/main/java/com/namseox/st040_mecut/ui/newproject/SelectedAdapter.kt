package com.namseox.st040_mecut.ui.newproject

import android.view.View
import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ItemSelectBinding
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class SelectedAdapter: AbsBaseAdapter<VideoModel, ItemSelectBinding>(R.layout.item_select, SelectedDiff()) {
    var onCLick: ((pos : Int) -> Unit)? = null
    class SelectedDiff : AbsBaseDiffCallBack<VideoModel>(){
        override fun itemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.path == newItem.path
        }

        override fun contentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.path != newItem.path || oldItem.check != newItem.check
        }

    }

    override fun bind(binding: ItemSelectBinding, position: Int, data: VideoModel) {
        binding.root.setOnClickListener { onCLick?.invoke(position) }
        if (data.duration != "") {
            binding.tvTime.visibility = View.VISIBLE
            binding.tvTime.text = data.duration
        } else {
            binding.tvTime.visibility = View.GONE
        }
        Glide.with(binding.root).load(data.path).into(binding.imv)
    }
}