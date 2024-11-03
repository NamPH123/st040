package com.namseox.st040_mecut.view.customview.customimage

import android.view.MotionEvent

interface StickerEvent {
    fun onActionDown(stickerView: StickerView?, event: MotionEvent?)
    fun onActionMove(stickerView: StickerView?, event: MotionEvent?)
    fun onActionUp(stickerView: StickerView?, event: MotionEvent?)
}