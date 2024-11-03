package com.namseox.st040_mecut.view.customview.customimage

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import androidx.annotation.IntDef
import com.namseox.st040_mecut.view.customview.customimage.DrawableSticker
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.DEFAULT_RADIUS
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.LEFT_BOTTOM
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.RIGHT_BOTTOM
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.RIGHT_TOP
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.TOP_LEFT
import com.namseox.st040_mecut.view.customview.customimage.StickerEvent
import com.namseox.st040_mecut.view.customview.customimage.StickerView


class BitmapStickerIcon(drawable: Drawable?, @Gravity gravity: Int) : DrawableSticker(drawable!!, "Dunno"),
    StickerEvent {
    @IntDef(*[TOP_LEFT, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Gravity

    var radius = DEFAULT_RADIUS
    var x = 0f
    var y = 0f

    @get:Gravity
    @Gravity
    var positionDefault = TOP_LEFT
    var event: StickerEvent? = null

    init {
        positionDefault = gravity
    }

    override fun onActionDown(stickerView: StickerView?, event: MotionEvent?) {
        if (this.event != null) {
            this.event!!.onActionDown(stickerView, event)
        }
    }

    override fun onActionMove(stickerView: StickerView?, event: MotionEvent?) {
        if (this.event != null) {
            this.event!!.onActionMove(stickerView, event)
        }
    }

    override fun onActionUp(stickerView: StickerView?, event: MotionEvent?) {
        if (this.event != null) {
            this.event!!.onActionUp(stickerView, event)
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.parseColor("#3F3E3F")
        canvas.drawCircle(x, y, radius, paint)
        super.draw(canvas)
    }

}

