package com.namseox.st040_mecut.ui.editvideo

import android.view.View
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.ImvModel
import com.namseox.st040_mecut.databinding.ItemFilterBinding
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class FillterAdapter : AbsBaseAdapter<ImvModel, ItemFilterBinding>(R.layout.item_filter, FillterDiff()) {
    var onClick : ((Int) -> Unit)? = null
    class FillterDiff : AbsBaseDiffCallBack<ImvModel>() {
        override fun itemsTheSame(oldItem: ImvModel, newItem: ImvModel): Boolean {
            return oldItem.imv1 == newItem.imv1
        }

        override fun contentsTheSame(oldItem: ImvModel, newItem: ImvModel): Boolean {
            return oldItem.imv1 != newItem.imv1 || oldItem.check != newItem.check
        }
    }
    override fun bind(binding: ItemFilterBinding, position: Int, data: ImvModel) {
        binding.apply {
            imv.setImageResource(data.imv1)
            tv.text = data.text
            if (data.check) {
                binding.bgFill.visibility = View.VISIBLE
            } else {
                binding.bgFill.visibility = View.GONE
            }
            root.setOnClickListener {
                onClick?.invoke(position)
            }
        }
    }
}