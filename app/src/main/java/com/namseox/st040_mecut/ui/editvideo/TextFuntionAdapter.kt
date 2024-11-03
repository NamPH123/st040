package com.namseox.st040_mecut.ui.editvideo

import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.FuntionModel
import com.namseox.st040_mecut.databinding.ItemTextFuntionBinding
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class TextFuntionAdapter : AbsBaseAdapter<FuntionModel, ItemTextFuntionBinding>(
    R.layout.item_text_funtion,
    TextFuntionDiff()
) {
    var onClick:((Int) -> Unit)? = null
    class TextFuntionDiff : AbsBaseDiffCallBack<FuntionModel>() {
        override fun itemsTheSame(oldItem: FuntionModel, newItem: FuntionModel): Boolean {
            return oldItem.imv1 == newItem.imv1
        }

        override fun contentsTheSame(oldItem: FuntionModel, newItem: FuntionModel): Boolean {
            return oldItem.check != newItem.check
        }

    }

    override fun bind(binding: ItemTextFuntionBinding, position: Int, data: FuntionModel) {
        Glide.with(binding.root).load(if(data.check){data.imv2}else{data.imv1}).into(binding.imv)
        binding.root.onSingleClick {
            onClick?.invoke(position)
        }
    }
}