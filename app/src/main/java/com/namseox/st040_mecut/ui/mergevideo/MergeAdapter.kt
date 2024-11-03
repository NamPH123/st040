package com.namseox.st040_mecut.ui.mergevideo

import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ItemMergeVideoBinding
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class MergeAdapter: AbsBaseAdapter<VideoModel,ItemMergeVideoBinding>(R.layout.item_merge_video,MergeDiff()) {
    var onItemClick: (Int) -> Unit = {}
    class MergeDiff : AbsBaseDiffCallBack<VideoModel>(){
        override fun itemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.path == newItem.path
        }

        override fun contentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.path != newItem.path
        }

    }

    override fun bind(binding: ItemMergeVideoBinding, position: Int, data: VideoModel) {
        Glide.with(binding.root).load(data.path).into(binding.imv)
        binding.tvTime.text = data.duration
        binding.root.setOnClickListener {
            onItemClick?.invoke(position)
        }
    }
}