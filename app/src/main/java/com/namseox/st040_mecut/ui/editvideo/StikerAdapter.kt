package com.namseox.st040_mecut.ui.editvideo

import com.bumptech.glide.Glide
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.ImvModel
import com.namseox.st040_mecut.databinding.ItemStikerBinding
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class StikerAdapter: AbsBaseAdapter<ImvModel,ItemStikerBinding>(R.layout.item_stiker,DiffStiker()) {
    var onClick : ((Int)->Unit)? = null
    class DiffStiker: AbsBaseDiffCallBack<ImvModel>(){
        override fun itemsTheSame(oldItem: ImvModel, newItem: ImvModel): Boolean {
            return oldItem.imv1 == newItem.imv1
        }

        override fun contentsTheSame(oldItem: ImvModel, newItem: ImvModel): Boolean {
            return oldItem.imv1 != newItem.imv1
        }

    }

    override fun bind(binding: ItemStikerBinding, position: Int, data: ImvModel) {
        binding.imv.setImageResource(data.imv1)
        binding.root.setOnClickListener {
            onClick?.invoke(position)
        }
    }
}