package com.namseox.st040_mecut.ui.editvideo

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.FuntionModel
import com.namseox.st040_mecut.databinding.ItemFuntionEditVideoBinding
import com.namseox.st040_mecut.utils.changeGradientText
import com.namseox.st040_mecut.utils.changeGradientText2
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class FuntionAdapter : AbsBaseAdapter<FuntionModel, ItemFuntionEditVideoBinding>(
    R.layout.item_funtion_edit_video,
    FuntionDiff()
) {
    var onCLick: ((FuntionModel, Int) -> Unit)? = null

    class FuntionDiff : AbsBaseDiffCallBack<FuntionModel>() {
        override fun itemsTheSame(oldItem: FuntionModel, newItem: FuntionModel): Boolean {
            return oldItem == newItem
        }

        override fun contentsTheSame(oldItem: FuntionModel, newItem: FuntionModel): Boolean {
            return oldItem.name != newItem.name || oldItem.check != newItem.check
        }

    }

    override fun bind(binding: ItemFuntionEditVideoBinding, position: Int, data: FuntionModel) {
        binding.tv.text = data.name
        binding.root.setOnClickListener {
            onCLick?.invoke(data, position)
        }
        if (data.check) {
            changeGradientText(binding.tv)
        } else {
            changeGradientText2(binding.tv,"#535353")
        }
        if (data.check) {
            binding.imv.setImageResource(data.imv2)
        } else {
            binding.imv.setImageResource(data.imv1)
        }
    }
}