package com.namseox.st040_mecut.ui.newproject

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ItemSelectFolderBinding
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import java.io.File

class SelectedFolderAdapter : AbsBaseAdapter<ArrayList<VideoModel>,ItemSelectFolderBinding>(R.layout.item_select_folder,SelectedFolderDiffUtil()) {
    var onCLick: ((pos : Int) -> Unit)? = null
    class SelectedFolderDiffUtil() : DiffUtil.ItemCallback<ArrayList<VideoModel>>(){
        override fun areItemsTheSame(
            oldItem: ArrayList<VideoModel>,
            newItem: ArrayList<VideoModel>
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ArrayList<VideoModel>,
            newItem: ArrayList<VideoModel>
        ): Boolean {
            return oldItem[0].path != newItem[0].path
        }

    }

    override fun bind(
        binding: ItemSelectFolderBinding,
        position: Int,
        data: ArrayList<VideoModel>
    ) {
        if(position==0){
            binding.tvName.text = binding.root.context.getString(R.string.recent)
        }else{
            binding.tvName.text = File(data[0].path).parentFile.name
        }
        binding.tvSize.text = data.size.toString()
        try {
            Glide.with(binding.root).load(data[0].path).into(binding.imv)
        }catch (e : Exception){

        }
        binding.root.setOnClickListener {
            onCLick?.invoke(position)
        }
    }
}