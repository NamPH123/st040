package com.namseox.st040_mecut.ui.setting

import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.SettingModel
import com.namseox.st040_mecut.databinding.ItemSettingBinding
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class SettingAdapter: AbsBaseAdapter<SettingModel, ItemSettingBinding>(R.layout.item_setting,SettingDIff()) {
    var onClick:((pos : Int)->Unit)? = null
    class SettingDIff : AbsBaseDiffCallBack<SettingModel>(){
        override fun itemsTheSame(oldItem: SettingModel, newItem: SettingModel): Boolean {
            return oldItem.name == newItem.name
        }

        override fun contentsTheSame(oldItem: SettingModel, newItem: SettingModel): Boolean {
            return oldItem.name != newItem.name
        }

    }

    override fun bind(binding: ItemSettingBinding, position: Int, data: SettingModel) {
        binding.imv.setImageResource(data.imv)
        binding.tv.text = data.name
        binding.root.onSingleClick {
            onClick?.invoke(data.imv)
        }
    }
}