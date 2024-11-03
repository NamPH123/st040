package com.namseox.st040_mecut.view.customview.customimage

import android.view.MotionEvent

class FlipEvent : StickerEvent {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {}
    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {}
    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        if (stickerView != null && stickerView.getStickerCount() > 0) stickerView.flipCurrentSticker(StickerConstant.FLIP_HORIZONTALLY)
    }
}