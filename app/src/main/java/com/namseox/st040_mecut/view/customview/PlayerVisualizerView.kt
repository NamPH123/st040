package com.namseox.st040_mecut.view.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.namseox.st040_mecut.R

class PlayerVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        const val VISUALIZER_HEIGHT = 100
    }

    var drawableLeft: Drawable = context.getDrawable(R.drawable.ic_left_music)!!
    var drawableRight: Drawable = context.getDrawable(R.drawable.ic_right_music)!!
    private var bytes: ByteArray? = null
    private var denseness = 0f

    private val playedStatePainting = Paint().apply {
        strokeWidth = 1f
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }


    var width1 = 0
    var height1 = 0
    var leftHandle = 0f
    var checkLeft = false
    var rightHandle = 0f
    var checkRight = false

    fun updateVisualizer(bytes: ByteArray) {
        this.bytes = bytes
        invalidate()
    }

    fun updatePlayerPercent(percent: Float) {
        denseness = (width1 * percent).coerceIn(0f, width1.toFloat()) + dp(8f)
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width1 = measuredWidth - dp(16f).toInt()
        height1 = measuredHeight
        rightHandle = measuredWidth.toFloat()
    }
    fun changeLeft(i: Float) {
        leftHandle = i
        invalidate()
    }

    fun changeRight(i: Float) {
        rightHandle = i
        invalidate()
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x > leftHandle - dp(20f) && event.x < leftHandle + dp(20f)) {
                    checkLeft = true
                }
                if (event.x > rightHandle - dp(20f) && event.x < rightHandle + dp(20f)) {
                    checkRight = true
                }
            }

            MotionEvent.ACTION_UP -> {
                checkLeft = false
                checkRight = false
            }

            MotionEvent.ACTION_MOVE -> {
                if (checkLeft) {
                    if(event.x<rightHandle - dp(20f)&& event.x>0){
                        changeLeft(event.x)
                    }
                }
                if (checkRight) {
                    if(event.x>leftHandle + dp(20f)&& event.x<measuredWidth.toFloat()){
                        changeRight(event.x)
                    }
                }

            }
        }
        return true
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bytes == null || width1 == 0) return

        val totalBarsCount = width1 / dp(3f)
        if (totalBarsCount <= 0.1f) return

        val samplesCount = bytes!!.size * 8 / 5
        val samplesPerBar = samplesCount / totalBarsCount
        var barCounter = 0f
        var nextBarNum = 0
        val y = (height - dp(VISUALIZER_HEIGHT.toFloat())) / 2
        var barNum = 0

        for (a in 0 until samplesCount) {
            if (a != nextBarNum) continue

            var drawBarCount = 0
            val lastBarNum = nextBarNum
            while (nextBarNum == lastBarNum) {
                barCounter += samplesPerBar
                nextBarNum = barCounter.toInt()
                drawBarCount++
            }

            val bitPointer = a * 5
            val byteNum = bitPointer / Byte.SIZE_BITS
            val byteBitOffset = bitPointer - byteNum * Byte.SIZE_BITS
            val currentByteCount = Byte.SIZE_BITS - byteBitOffset
            val nextByteRest = 5 - currentByteCount
            var value = (bytes!![byteNum].toInt() shr byteBitOffset) and ((1 shl minOf(
                5,
                currentByteCount
            ) - 1) - 1)

            if (nextByteRest > 0) {
                value =
                    (value shl nextByteRest) or (bytes!![byteNum + 1].toInt() and ((1 shl (nextByteRest - 1)) - 1))
            }
            var xxx = drawableToBitmap(drawableLeft)
            canvas.drawBitmap(
                xxx,
                leftHandle,
                y.toFloat() + dp(VISUALIZER_HEIGHT.toFloat()).toFloat() - xxx.height / 2-dp(50f),
                null
            )
            var yyy = drawableToBitmap(drawableRight)
            canvas.drawBitmap(
                yyy,
                rightHandle - xxx.width / 2,
                y.toFloat() + dp(VISUALIZER_HEIGHT.toFloat()).toFloat() - xxx.height / 2-dp(50f),
                null
            )
            val path = Path()
            val radius = context.resources.getDimension(R.dimen.corner_radius)
            val rectF = RectF(
                xxx.width/2 + dp(1f)+ leftHandle ,
                y + dp(VISUALIZER_HEIGHT.toFloat())- dp(VISUALIZER_HEIGHT.toFloat())/2+dp(24f)-dp(50f),
                rightHandle - xxx.width / 2 - dp(1f),
                y.toFloat() + dp(VISUALIZER_HEIGHT.toFloat())+ dp(VISUALIZER_HEIGHT.toFloat())/2-dp(24f)-dp(50f)
            )
            path.addRoundRect(rectF, radius, radius, Path.Direction.CW)
            var note = Paint().apply {
                strokeWidth = dp(1f)
                isAntiAlias = true
                color = ContextCompat.getColor(context, R.color.white)
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
            }
            canvas.drawPath(path, note)


            for (b in 0 until drawBarCount) {
                val x = barNum * dp(3f) + +dp(8f)
                val left = x
                val top = y + dp(
                    VISUALIZER_HEIGHT.toFloat() - maxOf(
                        1f,
                        VISUALIZER_HEIGHT * value / 31.0f
                    )
                )
                val right = x + dp(2f)
                val bottom = y + dp(VISUALIZER_HEIGHT.toFloat())
                val h = (bottom - top) / 2


                val notPlayedStatePainting = Paint().apply {
                    strokeWidth = 1f
                    isAntiAlias = true
                    shader = LinearGradient(
                        0f, top, right, top + dp(VISUALIZER_HEIGHT.toFloat()).toFloat(),
                        intArrayOf(
                            ContextCompat.getColor(context, R.color.startColor),
                            ContextCompat.getColor(context, R.color.endColor),
                            ContextCompat.getColor(context, R.color.startColor),
                            ContextCompat.getColor(context, R.color.endColor),
                        ), floatArrayOf(0f, 0.25f, 0.75f, 1f),
                        Shader.TileMode.CLAMP
                    )

                }

                if (x < denseness && x + dp(2f) < denseness && x>leftHandle && x<rightHandle) {
                    canvas.drawRect(left, top + h-dp(50f), right, bottom + h-dp(50f), notPlayedStatePainting)
                } else {
                    canvas.drawRect(left, top + h-dp(50f), right, bottom + h-dp(50f), playedStatePainting)
                }
                barNum++
            }
        }
    }

    private fun dp(value: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width / 2, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}