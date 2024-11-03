package com.namseox.st040_mecut.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.VideoModel
import com.namseox.st040_mecut.databinding.ItemHomeRectangleBinding
import com.namseox.st040_mecut.databinding.ItemHomeVerticalBinding
import com.namseox.st040_mecut.utils.formatDate
import com.namseox.st040_mecut.utils.formatDateTime
import com.namseox.st040_mecut.utils.getVideoDuration
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class MainAdapter : AbsBaseAdapter<VideoModel, ItemHomeRectangleBinding>(
    R.layout.item_home_rectangle,
    RectangleDiff()
) {
    var onClickItem: ((Int,String) -> Unit)? = null
    var typeInt: Int = 0
    var checkTick=false
    fun checkTick(i: Boolean) {
        checkTick = i
        notifyDataSetChanged()
    }
    fun setType(i: Int) {
        typeInt = i
    }
    class RectangleDiff : AbsBaseDiffCallBack<VideoModel>() {
        override fun itemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun contentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem.id != newItem.id
        }

    }

    inner class ViewHolder2(val binding: ItemHomeVerticalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind( data: VideoModel,position: Int){
            binding.root.setOnLongClickListener {
//                checkTick = !checkTick
                checkTick(true)
                onClickItem?.invoke(position,"long")
                true
            }
            binding.imvTick.setOnClickListener {
                onClickItem?.invoke(position,"tick")
            }
            binding.root.setOnClickListener {
                onClickItem?.invoke(position,"item")
            }
            binding.tvName.text = data.name
            binding.tvDate.text = formatDateTime(data.date)
            binding.tvSize.text = String.format("%01dMB",data.size) + " | " +  getVideoDuration(data.path)
            Glide.with(binding.root).load(data.path).into(binding.imv)
            if(checkTick){
                binding.imvTick.visibility = View.VISIBLE
                if(data.checkTick){
                    binding.imvTick.setImageResource(R.drawable.ic_select_true)
                }else{
                    binding.imvTick.setImageResource(R.drawable.ic_select_false)
                }
            }else{
                binding.imvTick.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==0){
            var binding = ItemHomeVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder2(binding)
        }else{
            return super.onCreateViewHolder(parent, viewType)
        }
    }
    override fun bind(binding: ItemHomeRectangleBinding, position: Int, data: VideoModel) {
        binding.tvName.text = data.name
        binding.tvDate.text = formatDate(data.date)
        binding.tvTime.text = getVideoDuration(data.path)
        Glide.with(binding.root).load(data.path).into(binding.imv)
        binding.imvTick.setOnClickListener {
            onClickItem?.invoke(position,"tick")
        }
        binding.root.setOnClickListener {
            onClickItem?.invoke(position,"item")
        }
        if(checkTick){
            binding.imvTick.visibility = View.VISIBLE
            if(data.checkTick){
                binding.imvTick.setImageResource(R.drawable.ic_select_true)
            }else{
                binding.imvTick.setImageResource(R.drawable.ic_select_false)
            }
        }else{
            binding.imvTick.visibility = View.GONE
        }
        binding.root.setOnLongClickListener {
//            checkTick = !checkTick
            checkTick(true)
            onClickItem?.invoke(position,"long")
            true
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder2) {
            holder.bind(getItem(position), position)
        } else {
            super.onBindViewHolder(holder, position)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if(typeInt==0){
            0
        }else{
            1
        }
    }
}