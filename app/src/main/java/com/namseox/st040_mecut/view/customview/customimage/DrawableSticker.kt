package com.namseox.st040_mecut.view.customview.customimage

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable


open class DrawableSticker(override var drawable: Drawable, private val path: String) : Sticker() {

    private val bounds = Rect(0, 0, width, height)
    private var originalDrawable: Drawable? = null

    override val width: Int
        get() = drawable.intrinsicWidth
    override val height: Int
        get() = drawable.intrinsicHeight
    override val drawablePath: String
        get() = path

    init {
        originalDrawable = drawable.constantState!!.newDrawable().mutate()
        clearDirty()
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        canvas.concat(getMatrix())
        drawable.bounds = bounds
        drawable.draw(canvas)
        canvas.restore()
        clearDirty()
    }

    override fun setDrawable(drawable: Drawable): DrawableSticker {
        this.drawable = drawable
        markAsDirty()
        return this
    }

    override fun setAlpha(alpha: Int): Sticker {
        drawable.alpha = alpha
        markAsDirty()
        return this
    }

    fun getOriginalDrawable(): Drawable {
        return this.originalDrawable!!.constantState!!.newDrawable().mutate()
    }

    fun setOriginalDrawable(drawable1: Drawable) {
        this.originalDrawable = drawable1.constantState!!.newDrawable().mutate()
        markAsDirty()
    }

}
