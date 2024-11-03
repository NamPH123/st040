package com.namseox.st040_mecut.view.customview.customimage

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable

class TempSticker @JvmOverloads constructor(private val context: Context, drawable: Drawable? = null, filePath: String? = null) : Sticker() {
    override fun draw(canvas: Canvas) {}

    override val width: Int
        get() = 100
    override val height: Int
        get() = 100

    override fun setDrawable(drawable: Drawable): TempSticker {
        return this
    }

    private var _drawable: Drawable? = null
    override var drawable: Drawable
        get() {
            return _drawable ?: drawable
        }
        set(value) {
            _drawable = value
        }
    override val drawablePath: String
        get() = ""

    override fun setAlpha(alpha: Int): TempSticker {
        return this
    }

}