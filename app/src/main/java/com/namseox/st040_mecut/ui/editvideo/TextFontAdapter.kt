package com.namseox.st040_mecut.ui.editvideo

import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.data.model.TextFontModel
import com.namseox.st040_mecut.databinding.ItemTextFontBinding
import com.namseox.st040_mecut.utils.changeGradientText
import com.namseox.st040_mecut.utils.changeGradientText2
import com.namseox.st040_mecut.utils.onSingleClick
import com.namseox.st040_mecut.view.base.AbsBaseAdapter
import com.namseox.st040_mecut.view.base.AbsBaseDiffCallBack

class TextFontAdapter: AbsBaseAdapter<TextFontModel,ItemTextFontBinding>(R.layout.item_text_font,TextFontDiff()) {
    var onClick: ((Int)->Unit)? = null
    class TextFontDiff : AbsBaseDiffCallBack<TextFontModel>(){
        override fun itemsTheSame(oldItem: TextFontModel, newItem: TextFontModel): Boolean {
        return oldItem.font == newItem.font
        }

        override fun contentsTheSame(oldItem: TextFontModel, newItem: TextFontModel): Boolean {
            return oldItem.font != newItem.font
        }

    }

    override fun bind(binding: ItemTextFontBinding, position: Int, data: TextFontModel) {
        binding.tv.typeface = ResourcesCompat.getFont(binding.root.context,data.font)
        if(data.check){
            binding.tv.setTextColor(Color.parseColor("#EC9089"))
//            changeGradientText(binding.tv)
        }else{
            binding.tv.setTextColor(Color.parseColor("#ffffff"))
//            changeGradientText2(binding.tv,"#ffffff")
        }
        binding.root.onSingleClick {
            onClick?.invoke(position)
        }
    }
}