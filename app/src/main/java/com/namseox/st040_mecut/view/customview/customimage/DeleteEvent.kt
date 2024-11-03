package com.namseox.st040_mecut.view.customview.customimage

import android.view.MotionEvent


class DeleteEvent : StickerEvent {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {}
    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {}
    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        if (!stickerView!!.isLocking()) {
            stickerView.removeStickerCurrent()
        }
    }
}
