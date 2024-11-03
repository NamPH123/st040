package com.namseox.st040_mecut.dialog

import android.app.Activity
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.databinding.DialogDeleteBinding
import com.namseox.st040_mecut.dialog.DialogRate.OnPress
import com.namseox.st040_mecut.view.base.BaseDialog

class DialogDelete(context: Activity) : BaseDialog<DialogDeleteBinding>(context, false) {
    private lateinit var onPress: OnPress
    fun init(onPress: OnPress?) {
        this.onPress = onPress!!
    }

    interface OnPress {
        fun cancel()
        fun delete()
    }

    override fun getContentView(): Int = R.layout.dialog_delete

    override fun initView() {
        binding.tvDelete.setOnClickListener {
            onPress.delete()
        }
        binding.tvCancel.setOnClickListener {
            onPress.cancel()
        }
    }

    override fun bindView() {

    }
}