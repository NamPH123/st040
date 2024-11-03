package com.namseox.st040_mecut.ui.newproject

import android.view.View
import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ItemNewProjectBinding
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class NewProjectAdapter :
    AbsBaseAdapter<VideoModel, ItemNewProjectBinding>(R.layout.item_new_project, NewProjectDiff()) {
    var onCLick: ((pos : Int) -> Unit)? = null

    class NewProjectDiff : AbsBaseDiffCallBack<VideoModel>() {
        override fun itemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.path == newItem.path
        }

        override fun contentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.check != newItem.check || oldItem.path != newItem.path || oldItem.duration != newItem.duration
        }

    }

    override fun bind(binding: ItemNewProjectBinding, position: Int, data: VideoModel) {
        binding.root.setOnClickListener { onCLick?.invoke(position) }
        if (data.duration != "") {
            binding.tvTime.visibility = View.VISIBLE
            binding.tvTime.text = data.duration
        } else {
            binding.tvTime.visibility = View.GONE
        }
        Glide.with(binding.root).load(data.path).into(binding.imv)
        if(data.checkCut){
            binding.imvTick.visibility = View.GONE
        }else{
            binding.imvTick.visibility = View.VISIBLE
        }
        if(data.checkCut){
            binding.imvTick.visibility = View.GONE
        }else{
            binding.imvTick.visibility = View.VISIBLE
        }
        if (data.check) {
            binding.imvTick.setImageResource(R.drawable.ic_tick_true)
        } else {
            binding.imvTick.setImageResource(R.drawable.ic_tick_false)
        }
    }
}