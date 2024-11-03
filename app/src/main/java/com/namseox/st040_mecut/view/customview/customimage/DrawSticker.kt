package com.namseox.st040_mecut.view.customview.customimage

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable


class DrawSticker(override var drawable: Drawable, private val filePath: String) : Sticker() {

    private val bounds = Rect(0, 0, width, height)

    override val width: Int
        get() = drawable.intrinsicWidth
    override val height: Int
        get() = drawable.intrinsicHeight

    override fun draw(canvas: Canvas) {
        val matrix = getMatrix()
        canvas.save()
        canvas.concat(matrix)
        drawable.bounds = bounds
        drawable.draw(canvas)
        canvas.restore()
    }

    override fun setDrawable(drawable: Drawable): DrawSticker {
        this.drawable = drawable
        return this
    }

    override val drawablePath: String
        get() = filePath

    override fun setAlpha(alpha: Int): Sticker {
        drawable.alpha = alpha
        return this
    }

}

