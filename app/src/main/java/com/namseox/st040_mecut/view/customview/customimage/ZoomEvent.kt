package com.namseox.st040_mecut.view.customview.customimage

import android.view.MotionEvent


class ZoomEvent : StickerEvent {
    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {}
    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {
        stickerView!!.rotateZoomCurrentSticker(event!!)
    }
    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        if (stickerView!!.getOnStickerListener() != null) {
            stickerView.getOnStickerListener()!!.onZoomFinished(stickerView.getCurrentSticker()!!)
        }
    }
}
