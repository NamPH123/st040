package com.namseox.st040_mecut.view.customview.customimage

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import com.namseox.st040_mecut.R
import com.namseox.st040_mecut.utils.Constant
import com.namseox.st040_mecut.utils.tryBlock
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.BOTTOM
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.CENTER
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.CLICK
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.DRAG
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.FLIP_HORIZONTALLY
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.FLIP_VERTICALLY
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.ICON
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.LEFT
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.LEFT_BOTTOM
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.NONE
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.RIGHT
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.RIGHT_BOTTOM
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.RIGHT_TOP
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.TOP
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.TOP_LEFT
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.ZOOM_WITH_TWO_FINGER
import java.util.Collections
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt


@SuppressLint("CustomViewStyleable")
open class StickerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr) {

    @IntDef(NONE, DRAG, ZOOM_WITH_TWO_FINGER, ICON, CLICK)
    @Retention(AnnotationRetention.SOURCE)
    protected annotation class ActionMode

    @IntDef(flag = true, value = [FLIP_HORIZONTALLY, FLIP_VERTICALLY])
    @Retention(AnnotationRetention.SOURCE)
    protected annotation class Flip

    val stickerList = ArrayList<Sticker>()
    private val iconList = ArrayList<BitmapStickerIcon>()

    private val undoList = ArrayList<List<Sticker>>()
    private val undoTempList = ArrayList<Sticker>()

    private val borderPaint = Paint()
    private val stickerRect = RectF()

    private val bitmapPoints = FloatArray(8)
    private val bounds = FloatArray(8)
    private val point = FloatArray(2)
    private val currentCenterPoint = PointF()
    private val temp = FloatArray(2)
    private var midPoint = PointF()

    private val sizeMatrix = Matrix()
    private val downMatrix = Matrix()
    private val moveMatrix = Matrix()

    private var isLocked: Boolean = false
    private var constrained: Boolean = false

    private var onStickerListener: OnStickerListener? = null

    private val isShowIcons: Boolean
    private val isShowBorder: Boolean
    private val bringToFrontCurrentSticker: Boolean

    private val touchSlop: Int

    @ActionMode
    private var currentMode = NONE

    private var handlingSticker: Sticker? = null

    private var lastClickTime: Long = 0L
    private var minClickDelayTime = Constant.SINGLE_CLICK_TIME - 300

    private var currentIcon: BitmapStickerIcon? = null
    private var downX: Float = 0f
    private var downY: Float = 0f

    private var oldDistance: Float = 0f
    private var oldRotation: Float = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.StickerView)
        try {
            isShowIcons = typedArray.getBoolean(R.styleable.StickerView_showIcons, false)
            isShowBorder = typedArray.getBoolean(R.styleable.StickerView_showBorder, false)
            bringToFrontCurrentSticker =
                typedArray.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false)

            borderPaint.isAntiAlias = true
            borderPaint.color = typedArray.getColor(
                R.styleable.StickerView_borderColor,
                ContextCompat.getColor(context, R.color.white)
            )
//            borderPaint.alpha = typedArray.getInteger(R.styleable.StickerView_borderAlpha, 128)
            borderPaint.strokeWidth = 5f
            borderPaint.pathEffect = DashPathEffect(floatArrayOf(28f, 18f), 0f)

            setupDefaultIcons()
        } finally {
            typedArray.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            stickerRect.left = left.toFloat()
            stickerRect.top = top.toFloat()
            stickerRect.right = right.toFloat()
            stickerRect.bottom = bottom.toFloat()
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawStickers(canvas)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (isLocked) {
            Log.d("onInterceptTouchEvent", "Locking")
            return super.onInterceptTouchEvent(ev)
        }
        Log.d("onInterceptTouchEvent", "Unlocking ")

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y

                return targetCurrentSticker() != null || targetHandlingSticker() != null
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isLocked) {
            Log.d("onTouchEvent", "Locking")
            return super.onTouchEvent(event)
        }
        Log.d("onTouchEvent", "Unlocking")
        when (MotionEventCompat.getActionMasked(event)) {
            MotionEvent.ACTION_DOWN -> {
                if (!touchDown(event)) {
                    return false
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDistance = calcDistance(event)
                oldRotation = calcRotation(event)

                midPoint = calcMidPoint(event)

                if (handlingSticker != null && isFocusSticker(
                        handlingSticker!!,
                        event.getX(1),
                        event.getY(1)
                    ) && targetCurrentSticker() == null
                ) {
                    currentMode = ZOOM_WITH_TWO_FINGER
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (handlingSticker != null) {
                    if (!isLocking()) {
                        handleCurrentMode(event)
                        invalidate()
                    }
                }
            }

            MotionEvent.ACTION_UP -> touchUp(event)
            MotionEvent.ACTION_POINTER_UP -> {
                if (currentMode == ZOOM_WITH_TWO_FINGER && handlingSticker != null) {
                    if (onStickerListener != null) {
                        onStickerListener!!.onZoomFinished(handlingSticker!!)
                    }
                }
                currentMode = NONE
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
    }

    fun removeStickerCurrent(): Boolean {
        onStickerListener?.onHideOptionIcon()
        return remove(handlingSticker)
    }

    fun hideSelect() {
        onStickerListener?.onHideOptionIcon()
        handlingSticker = null
        invalidate()
    }

    fun setOnStickerListener(onStickerListener: OnStickerListener?): StickerView {
        this.onStickerListener = onStickerListener
        return this
    }

    fun getOnStickerListener(): OnStickerListener? {
        return onStickerListener
    }

    fun getStickerCount(): Int {
        return stickerList.size
    }

    fun getStickers(): List<Sticker> {
        return stickerList
    }

    fun rotateZoomCurrentSticker(event: MotionEvent) {
        rotateZoomSticker(handlingSticker, event)
    }

    fun flipCurrentSticker(direction: Int) {
        flip(handlingSticker, direction)
    }

    fun remove(sticker: Sticker?): Boolean {
        if (stickerList.contains(sticker)) {
            stickerList.remove(sticker)
            onStickerListener?.onDeleted(sticker!!)
            if (handlingSticker == sticker) {
                handlingSticker = null
            }

            // save undo khi xóa xong
//            saveStickerState()

            invalidate()

            return true
        } else {
            Log.d("StickerView", "remove: the sticker is not in this StickerView")
            return false
        }
    }

    fun remove(stickers: ArrayList<Sticker>) {
        for (index in stickers.indices) {
            if (stickerList.contains(stickers[index]) && stickers[index].isCharacter) {
                stickerList.remove(stickers[index])
                if (handlingSticker == stickers[index]) {
                    handlingSticker = null
                }
                invalidate()
            }
        }
    }

    fun exchangeLayers(oldP: Int, newP: Int) {
        if (stickerList.size >= oldP && stickerList.size >= newP) {
            Collections.swap(stickerList, oldP, newP)

//            saveStickerState()
            invalidate()
        }
    }

    fun showOrHideSticker(sticker: Sticker, pos: Int) {
        if (stickerList.size > 0) {
            sticker.setHide(!sticker.isHide)

//            saveStickerState()
            invalidate()
        }
    }

    var checkText = false
    var fontFamily = 0
    var editTextSize = 0
    var editTextColor = "#ffffff"
    var editTextAphaColor = 1
    var editTextBG = "#ffffff"
    var editTextAphaBG = 1
    var editTextBorder = "#ffffff"
    var editTextAphaBorder = 1

    fun addSticker(
        sticker: Sticker,
        check: Boolean = false,
        text: String = "",
        size: Float = 0f,
        alpha: Float = 0f,
        margin: Float = 0f
    ): StickerView {
        sticker.alpha = alpha
        sticker.textSize = size
        sticker.margin = margin
        sticker.character = text
        sticker.checkText = check
        return addSticker(sticker, CENTER)
    }

    fun save(): Bitmap {
        handlingSticker = null
        val bitmap = Bitmap.createBitmap(
            if (width == 0) 500 else width,
            if (height == 0) 500 else height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        return bitmap
    }

    fun isLocking(): Boolean {
        return handlingSticker?.isLock ?: false
    }

    fun setConstrained(constrained: Boolean): StickerView {
        this.constrained = constrained
        postInvalidate()
        return this
    }

    fun unSelectCurrentSticker() {
        handlingSticker = null
        invalidate()
    }

    fun selectCurrentSticker(sticker: Sticker) {
        handlingSticker = sticker
//        onStickerListener!!.onRedoAll()
        invalidate()
    }

    fun setLocked(locked: Boolean): StickerView {
        this.isLocked = locked
        invalidate()
        return this
    }

    fun getCurrentSticker(): Sticker? {
        return handlingSticker
    }

    private val redoList = ArrayList<List<Sticker>>()

    fun undo() {
        if (undoList.size > 1) {
            undoTempList.clear()
            undoTempList.addAll(undoList[undoList.size - 1])
            redoList.add(undoList.removeAt(undoList.size - 1))
            val previousSticker = undoList[undoList.size - 1]
            removeAllSticker()
            for (flower in previousSticker) {
                when (flower) {
                    is DrawableSticker -> {
                        val drawableStickerNew = configDrawableSticker(flower, false)
                        stickerList.add(drawableStickerNew)
                    }

                    is TextSticker -> {
                        val textStickerNew = configTextSticker(flower, false)
                        stickerList.add(textStickerNew)
                    }

                    is DrawSticker -> {

                        val drawStickerNew = configDrawSticker(flower, false)
                        stickerList.add(drawStickerNew)
                    }
                }
                invalidate()
            }
        } else {
            removeAllSticker()
            redoList.add(undoList.removeAt(undoList.size - 1))
            undoList.clear()
            if (onStickerListener != null) {
                onStickerListener!!.onUndoDeleteAll()
            }
        }
    }

    fun redo() {
        if (redoList.size > 1) {
            removeAllSticker()
            val redoTempList = redoList[redoList.size - 1]
            undoList.add(redoTempList)
            redoList.removeAt(redoList.size - 1)
            for (flower in redoTempList) {
                when (flower) {
                    is DrawableSticker -> {
                        val drawableStickerNew = configDrawableSticker(flower, false)
                        stickerList.add(drawableStickerNew)
                    }

                    is TextSticker -> {
                        val textStickerNew = configTextSticker(flower, false)
                        stickerList.add(textStickerNew)
                    }

                    is DrawSticker -> {
                        val drawStickerNew = configDrawSticker(flower, false)
                        stickerList.add(drawStickerNew)
                    }
                }
                invalidate()
            }
        } else {
            removeAllSticker()
            val redoTempList = redoList[redoList.size - 1]
            undoList.add(redoTempList)
            redoList.removeAt(redoList.size - 1)
            for (flower in redoTempList) {
                when (flower) {
                    is DrawableSticker -> {
                        val drawableStickerNew = configDrawableSticker(flower, false)
                        stickerList.add(drawableStickerNew)
                    }

                    is TextSticker -> {
                        val textStickerNew = configTextSticker(flower, false)
                        stickerList.add(textStickerNew)
                    }

                    is DrawSticker -> {
                        val drawStickerNew = configDrawSticker(flower, false)
                        stickerList.add(drawStickerNew)
                    }
                }
                invalidate()
            }
            onStickerListener!!.onRedoAll()
        }
    }

    private fun removeAllSticker() {
        stickerList.clear()
        handlingSticker?.release()
        handlingSticker = null
        invalidate()
    }

//    private fun saveStickerState() {
//        val flowerCopy = ArrayList<Sticker>()
//        for (flower in flowerList) {
//            when (flower) {
//                is DrawableSticker -> {
//
//                    val drawableStickerNew = configDrawableSticker(flower, false)
//                    flowerCopy.add(drawableStickerNew)
//                }
//                is TextSticker -> {
//
//                    val textStickerNew = configTextSticker(flower, false)
//                    flowerCopy.add(textStickerNew)
//                }
//                is DrawSticker -> {
//
//                    val drawStickerNew = configDrawSticker(flower, false)
//                    flowerCopy.add(drawStickerNew)
//                }
//            }
//        }
//        undoList.add(flowerCopy)
//    }

    private fun configTextSticker(textSticker: TextSticker, checkClone: Boolean): TextSticker {
        val drawableNew = textSticker.drawable.constantState!!.newDrawable().mutate()
        val textStickerNew = TextSticker(context, drawableNew, textSticker.drawablePath)
        val matrix = Matrix(textSticker.getMatrix())
        if (checkClone) {
            matrix.postTranslate(0.0F, 30.0F)
        }
        textStickerNew.setMatrix(matrix)
        textStickerNew.setText(textSticker.text)
        textStickerNew.setHide(textSticker.isHide)
        textStickerNew.setLock(textSticker.isLock)
        textStickerNew.setFlippedH(textSticker.isFlippedH)
        textStickerNew.setFlippedV(textSticker.isFlippedV)
        textStickerNew.setTextColor(textSticker.textColor)
        textStickerNew.textCheckAlign = (textSticker.textCheckAlign)
        textStickerNew.setTextAlign(textSticker.textAlign)
        textStickerNew.idTypeFace = (textSticker.idTypeFace)
        textStickerNew.setTypeface(textSticker.typeface)
        textStickerNew.resizeText()
        return textStickerNew
    }

    fun configDrawSticker(drawSticker: DrawSticker, checkClone: Boolean): DrawSticker {
        val drawable = drawSticker.drawable.constantState!!.newDrawable().mutate()
        val drawStickerNew = DrawSticker(drawable, drawSticker.drawablePath)
        val matrix = Matrix(drawSticker.getMatrix())
        if (checkClone) {
            matrix.postTranslate(0.0F, 30.0F)
        }
        drawStickerNew.setMatrix(matrix)
        drawStickerNew.setHide(drawSticker.isHide)
        drawStickerNew.setLock(drawSticker.isLock)
        drawStickerNew.setFlippedH(drawSticker.isFlippedH)
        drawStickerNew.setFlippedV(drawSticker.isFlippedV)
        return drawStickerNew
    }

    fun addSticker(sticker: Sticker, alignment: Int): StickerView {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(sticker, alignment)
        } else {
            post {
                addStickerImmediately(sticker, alignment)
            }
        }
        return this
    }

    private fun replaceSticker(
        sticker: Sticker,
        position: Int,
        isText: Boolean = false
    ): StickerView {
        if (ViewCompat.isLaidOut(this)) {
            replaceStickerImmediately(sticker, position, isText)
        } else {
            post {
                replaceStickerImmediately(sticker, position, isText)
            }
        }
        return this
    }

    private fun addStickerImmediately(sticker: Sticker, position: Int) {
        setStickerPosition(sticker, position)

        val widthScaleFactor = 1
        val heightScaleFactor = 1
        val scaleFactor =
            if (widthScaleFactor > heightScaleFactor) heightScaleFactor else widthScaleFactor

        sticker.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f)

        handlingSticker = sticker
        stickerList.add(sticker)

        // save undo khi add xong
//        saveStickerState()

        onStickerListener?.onAdded(sticker)
        invalidate()
    }

    private fun replaceStickerImmediately(
        sticker: Sticker,
        position: Int,
        isText: Boolean = false
    ) {
        if (isText) setStickerPosition(sticker, TOP)
        else setStickerPosition(sticker, CENTER)

        val widthScaleFactor = width.toFloat() / sticker.drawable.intrinsicWidth
        val heightScaleFactor = height.toFloat() / sticker.drawable.intrinsicHeight
        val scaleFactor =
            if (widthScaleFactor > heightScaleFactor) heightScaleFactor else widthScaleFactor

        sticker.getMatrix().postScale(scaleFactor / 2f, scaleFactor / 2f, width / 2f, height / 2f)
//sua o dayy
        handlingSticker = sticker
        stickerList.add(position, sticker)

        // save undo khi add xong
//        saveStickerState()

        onStickerListener?.onReplace(sticker)
        invalidate()
    }

    private fun touchDown(event: MotionEvent): Boolean {
        currentMode = DRAG

        downX = event.x
        downY = event.y

        midPoint = calcMidPoint()
        oldDistance = calcDistance(midPoint.x, midPoint.y, downX, downY)
        oldRotation = calcRotation(midPoint.x, midPoint.y, downX, downY)

        currentIcon = targetCurrentSticker()
        if (currentIcon != null) {
            currentMode = ICON
            currentIcon!!.onActionDown(this, event)
        } else {
            handlingSticker = targetHandlingSticker()
        }
        if (handlingSticker != null) {
            onStickerListener!!.onTouchedDown(handlingSticker!!)
            downMatrix.set(handlingSticker!!.getMatrix())

            if (bringToFrontCurrentSticker) {
                stickerList.remove(handlingSticker!!)
                stickerList.add(handlingSticker!!)
            }
        }

        if (currentIcon == null && handlingSticker == null) {
            Log.d(
                "Function: onTouchDown",
                "CurrentIcon: $currentIcon, HandingSticker: $handlingSticker"
            )
            invalidate()
            return false
        }
        invalidate()
        return true
    }

    var clickDelete: (() -> Unit)? = null
    private fun touchUp(event: MotionEvent) {
        val currentTime = SystemClock.uptimeMillis()

        if (currentMode == ICON && currentIcon != null && handlingSticker != null) {
            currentIcon!!.onActionUp(this, event)
            Log.e("Dunno", "${currentIcon!!} $deleteIcon")
            if (currentIcon!! == deleteIcon) {
                clickDelete?.invoke()
            }

        }

        if (currentMode == DRAG && abs(event.x - downX) < touchSlop && abs(event.y - downY) < touchSlop && handlingSticker != null) {
            currentMode = CLICK
            if (onStickerListener != null) {
                onStickerListener!!.onClicked(handlingSticker!!)
            }
            if (currentTime - lastClickTime < minClickDelayTime) {
                if (onStickerListener != null) {
                    onStickerListener!!.onDoubleTapped(handlingSticker!!)
                }
            }
        }

        if (currentMode == DRAG && handlingSticker != null) {
            if (onStickerListener != null) {
                onStickerListener!!.onDragFinished(handlingSticker!!)

//                saveStickerState()
            }
        }

        currentMode = NONE
        lastClickTime = currentTime
    }

    private fun drawStickers(canvas: Canvas) {
        for (i in 0 until stickerList.size) {
            val sticker = stickerList[i]
            if (!sticker.isHide) {
                tryBlock { sticker.draw(canvas) }?.let { exc ->
                    Log.e(
                        "StickerView",
                        "drawSticker function error: $exc"
                    )
                }
            }
        }

        if (handlingSticker != null && !isLocked && (isShowBorder || isShowIcons)) {
            getStickerPoints(handlingSticker, bitmapPoints)

            val x1 = bitmapPoints[0] - 40
            val y1 = bitmapPoints[1] - 40
            val x2 = bitmapPoints[2] + 40
            val y2 = bitmapPoints[3] - 40
            val x3 = bitmapPoints[4] - 40
            val y3 = bitmapPoints[5] + 40
            val x4 = bitmapPoints[6] + 40
            val y4 = bitmapPoints[7] + 40

            if (isShowBorder) {
                canvas.drawLine(x1, y1, x2, y2, borderPaint)
                canvas.drawLine(x1, y1, x3, y3, borderPaint)
                canvas.drawLine(x2, y2, x4, y4, borderPaint)
                canvas.drawLine(x4, y4, x3, y3, borderPaint)
            }

            // draw icons
            if (isShowIcons) {
                val rotation = calcRotation(x4, y4, x3, y3)
                for (i in 0 until iconList.size) {
                    val icon = iconList[i]
                    when (icon.positionDefault) {
                        TOP_LEFT -> setupMatrix(icon, x1, y1, rotation)
                        RIGHT_TOP -> setupMatrix(icon, x2, y2, rotation)
                        LEFT_BOTTOM -> setupMatrix(icon, x3, y3, rotation)
                        RIGHT_BOTTOM -> setupMatrix(icon, x4, y4, rotation)
                    }
                    icon.draw(canvas, borderPaint)
                }
            }
        }
    }

    private fun setupMatrix(icon: BitmapStickerIcon, x: Float, y: Float, rotation: Float) {
        icon.x = x
        icon.y = y
        icon.getMatrix().reset()

        icon.getMatrix().postRotate(rotation, icon.width / 2f, icon.height / 2f)
        icon.getMatrix().postTranslate(x - icon.width / 2f, y - icon.height / 2f)
    }

    private fun handleCurrentMode(event: MotionEvent) {
        when (currentMode) {
            NONE, CLICK -> {}
            DRAG -> {
                if (handlingSticker != null) {
                    moveMatrix.set(downMatrix)
                    moveMatrix.postTranslate(event.x - downX, event.y - downY)
                    handlingSticker!!.setMatrix(moveMatrix)
                    if (constrained) {
                        constrainSticker(handlingSticker!!)
                    }
                }
            }

            ZOOM_WITH_TWO_FINGER -> {
                if (handlingSticker != null) {
                    val newDistance = calcDistance(event)
                    val newRotation = calcRotation(event)

                    moveMatrix.set(downMatrix)
                    moveMatrix.postScale(
                        newDistance / oldDistance,
                        newDistance / oldDistance,
                        midPoint.x,
                        midPoint.y
                    )
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
                    handlingSticker!!.setMatrix(moveMatrix)
                }
            }

            ICON -> {
                if (handlingSticker != null && currentIcon != null) {
                    currentIcon!!.onActionMove(this, event)
                }
            }
        }
    }

    private fun rotateZoomSticker(sticker: Sticker?, event: MotionEvent) {
        if (sticker != null) {
            val newDistance = calcDistance(midPoint.x, midPoint.y, event.x, event.y)
            val newRotation = calcRotation(midPoint.x, midPoint.y, event.x, event.y)
            moveMatrix.set(downMatrix)
            moveMatrix.postScale(
                newDistance / oldDistance,
                newDistance / oldDistance,
                midPoint.x,
                midPoint.y
            )
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y)
            sticker.setMatrix(moveMatrix)
        }
    }

    private fun constrainSticker(sticker: Sticker) {
        var moveX = 0f
        var moveY = 0f
        val width = width
        val height = height
        sticker.getMappedCenterPoint(currentCenterPoint, point, temp)
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x
        }
        if (currentCenterPoint.x > width) {
            moveX = width - currentCenterPoint.x
        }
        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y
        }
        if (currentCenterPoint.y > height) {
            moveY = height - currentCenterPoint.y
        }
        sticker.getMatrix().postTranslate(moveX, moveY)
    }

    private fun targetCurrentSticker(): BitmapStickerIcon? {
        for (icon in iconList) {
            val x = icon.x - downX
            val y = icon.y - downY
            val distance = x * x + y * y
            if (distance <= (icon.radius + icon.radius).toDouble().pow(2.0)) {
                return icon
            }
        }
        return null
    }

    private fun targetHandlingSticker(): Sticker? {
        for (i in stickerList.indices.reversed()) {
            if (isFocusSticker(stickerList[i], downX, downY)) {
                return stickerList[i]
            }
        }
        return null
    }

    private fun isFocusSticker(sticker: Sticker, downX: Float, downY: Float): Boolean {
        temp[0] = downX
        temp[1] = downY
        return sticker.contains(temp)
    }

    private fun calcMidPoint(event: MotionEvent?): PointF {
        if (event == null || event.pointerCount < 2) {
            midPoint[0f] = 0f
            return midPoint
        }
        val x = (event.getX(0) + event.getX(1)) / 2
        val y = (event.getY(0) + event.getY(1)) / 2
        midPoint[x] = y
        return midPoint
    }

    private fun calcMidPoint(): PointF {
        if (handlingSticker == null) {
            midPoint[0f] = 0f
            return midPoint
        }
        handlingSticker!!.getMappedCenterPoint(midPoint, point, temp)
        return midPoint
    }

    private fun setStickerPosition(sticker: Sticker, position: Int) {
        val width = width.toFloat()
        val height = height.toFloat()
        var offsetX = width - sticker.width
        var offsetY = height - sticker.height
        if (position and TOP > 0) {
            offsetY /= 4f
        } else if (position and BOTTOM > 0) {
            offsetY *= 3f / 4f
        } else {
            offsetY /= 2f
        }
        if (position and LEFT > 0) {
            offsetX /= 4f
        } else if (position and RIGHT > 0) {
            offsetX *= 3f / 4f
        } else {
            offsetX /= 2f
        }
        sticker.getMatrix().postTranslate(offsetX, offsetY)
    }

    private fun getStickerPoints(sticker: Sticker?, dst: FloatArray) {
        if (sticker == null) {
            dst.fill(0f)
            return
        }
        sticker.getBoundPoints(bounds)
        sticker.getMappedPoints(dst, bounds)
    }

    private fun configDrawableSticker(
        drawableSticker: DrawableSticker,
        checkClone: Boolean
    ): DrawableSticker {
        val drawable = drawableSticker.getOriginalDrawable().constantState!!.newDrawable().mutate()
        val drawableStickerNew = DrawableSticker(drawable, drawableSticker.drawablePath)
        val matrix = Matrix(drawableSticker.getMatrix())
        if (checkClone) {
            matrix.postTranslate(0.0F, 30.0F)
        }
        drawableStickerNew.setMatrix(matrix)
        drawableStickerNew.setPagerSelected(drawableSticker.pagerSelected)
        drawableStickerNew.setPositionSelected(drawableSticker.positionSelected)
        drawableStickerNew.setHide(drawableSticker.isHide)
        drawableStickerNew.setLock(drawableSticker.isLock)
        drawableStickerNew.setFlippedH(drawableSticker.isFlippedH)
        drawableStickerNew.setFlippedV(drawableSticker.isFlippedV)
        return drawableStickerNew
    }

    private fun calcRotation(event: MotionEvent?): Float {
        return if (event == null || event.pointerCount < 2) {
            0f
        } else calcRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    private fun calcRotation(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = x1 - x2
        val y = y1 - y2
        val radians = atan2(y.toDouble(), x.toDouble())
        return Math.toDegrees(radians).toFloat()
    }

    private fun calcDistance(event: MotionEvent?): Float {
        if (event == null || event.pointerCount < 2) {
            return 0f
        }
        return calcDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1))
    }

    private fun calcDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val x = x1 - x2
        val y = y1 - y2
        return sqrt(x * x + y * y.toDouble()).toFloat()
    }

    lateinit var deleteIcon: BitmapStickerIcon

    private fun setupDefaultIcons() {
        deleteIcon =
            BitmapStickerIcon(ContextCompat.getDrawable(context, R.drawable.ic_close), RIGHT_TOP)
        deleteIcon.event = DeleteEvent()
        val zoomIcon =
            BitmapStickerIcon(ContextCompat.getDrawable(context, R.drawable.ic_resize), LEFT_BOTTOM)
        zoomIcon.event = ZoomEvent()
        val flipIcon =
            BitmapStickerIcon(ContextCompat.getDrawable(context, R.drawable.ic_flip), TOP_LEFT)
        flipIcon.event = FlipEvent()

        iconList.clear()
        iconList.add(deleteIcon)
        iconList.add(flipIcon)
        iconList.add(zoomIcon)
    }

    private fun flip(sticker: Sticker?, direction: Int) {
        if (sticker != null) {
            if (!sticker.isLock) {
                sticker.getCenterPoint(midPoint)
                if ((direction and FLIP_HORIZONTALLY) > 0) {
                    sticker.getMatrix().preScale(-1f, 1f, midPoint.x, midPoint.y)
                    sticker.setFlippedH(!sticker.isFlippedH)
                }
                if ((direction and FLIP_VERTICALLY) > 0) {
                    sticker.getMatrix().preScale(1f, -1f, midPoint.x, midPoint.y)
                    sticker.setFlippedV(!sticker.isFlippedV)
                }

                if (onStickerListener != null) {
                    onStickerListener!!.onFlipped(sticker)
                }
                invalidate()
            }
        }
    }

    fun replace(stickerOld: Sticker, stickerNew: Sticker, isText: Boolean = false) {
        val index = stickerList.indexOfFirst { it == stickerOld }
        if (index >= 0) {
            stickerList.removeAt(index)
            replaceSticker(stickerNew, index, isText)
            invalidate()
        }
    }

}
