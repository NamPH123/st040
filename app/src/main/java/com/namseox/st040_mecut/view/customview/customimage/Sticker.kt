package com.namseox.st040_mecut.view.customview.customimage

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.IntDef
import androidx.annotation.IntRange
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.BOTTOM
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.CENTER
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.LEFT
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.RIGHT
import com.namseox.st040_mecut.view.customview.customimage.StickerConstant.TOP
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

abstract class Sticker {
    var checkText = false
    var font = 0
    var textSize = 30f
    var alpha = 100f
    var margin = 0f
    var isCharacter = false
    var character = ""
    @IntDef(flag = true, value = [CENTER, TOP, BOTTOM, LEFT, RIGHT])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Position

    abstract fun draw(canvas: Canvas)
    abstract val width: Int
    abstract val height: Int

    abstract fun setDrawable(drawable: Drawable): Sticker?
    abstract val drawable: Drawable
    abstract val drawablePath: String

    abstract fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int): Sticker

    private val valuesOfMatrix = FloatArray(9)
    private val wrapperCorner = FloatArray(8)
    private val point = FloatArray(2)
    private val boundPoints = FloatArray(8)
    private val boundMap = FloatArray(8)
    private val trappedRect = RectF()
    private val matrix = Matrix()
    val mappedBoundPoints: FloatArray
        get() {
            val dst = FloatArray(8)
            getMappedPoints(dst, getBoundPoints())
            return dst
        }
    val bound: RectF
        get() {
            val bound = RectF()
            getBound(bound)
            return bound
        }
    val mappedBound: RectF
        get() {
            val dst = RectF()
            getMappedBound(dst, bound)
            return dst
        }
    val centerPoint: PointF
        get() {
            val center = PointF()
            getCenterPoint(center)
            return center
        }
    val mappedCenterPoint: PointF
        get() {
            val pointF = centerPoint
            getMappedCenterPoint(pointF, FloatArray(2), FloatArray(2))
            return pointF
        }
    val currentScale: Float
        get() = getMatrixScale(matrix)
    val currentHeight: Float
        get() = getMatrixScale(matrix) * height
    val currentWidth: Float
        get() = getMatrixScale(matrix) * width
    val currentAngle: Float
        get() = getMatrixAngle(matrix)

    var isFlippedH = false
        private set
    var isFlippedV = false
        private set
    var isLock = false
        private set
    var pagerSelected = 0
        private set
    var positionSelected = 0
        private set
    var isHide = false
        private set
    var isDirty = false
        private set

    fun markAsDirty() {
        isDirty = true
    }

    fun clearDirty() {
        isDirty = false
    }

    fun setPagerSelected(pager: Int): Sticker {
        pagerSelected = pager
        return this
    }

    fun setPositionSelected(p: Int): Sticker {
        positionSelected = p
        return this
    }

    fun setLock(lock: Boolean): Sticker {
        isLock = lock
        return this
    }

    fun setHide(hide: Boolean): Sticker {
        isHide = hide
        return this
    }

    fun setFlippedH(state: Boolean): Sticker {
        isFlippedH = state
        return this
    }

    fun setFlippedV(state: Boolean): Sticker {
        isFlippedV = state
        return this
    }

    fun setMatrix(matrix: Matrix?): Sticker {
        markAsDirty()
        this.matrix.set(matrix)
        return this
    }

    fun getMatrix() : Matrix {
        return matrix
    }

    fun getBoundPoints(points: FloatArray) {
        if (!isFlippedH) {
            if (!isFlippedV) {
                points[0] = 0f
                points[1] = 0f
                points[2] = width.toFloat()
                points[3] = 0f
                points[4] = 0f
                points[5] = height.toFloat()
                points[6] = width.toFloat()
                points[7] = height.toFloat()
            } else {
                points[0] = 0f
                points[1] = height.toFloat()
                points[2] = width.toFloat()
                points[3] = height.toFloat()
                points[4] = 0f
                points[5] = 0f
                points[6] = width.toFloat()
                points[7] = 0f
            }
        } else {
            if (!isFlippedV) {
                points[0] = width.toFloat()
                points[1] = 0f
                points[2] = 0f
                points[3] = 0f
                points[4] = width.toFloat()
                points[5] = height.toFloat()
                points[6] = 0f
                points[7] = height.toFloat()
            } else {
                points[0] = width.toFloat()
                points[1] = height.toFloat()
                points[2] = 0f
                points[3] = height.toFloat()
                points[4] = width.toFloat()
                points[5] = 0f
                points[6] = 0f
                points[7] = 0f
            }
        }
    }

    fun getMappedPoints(src: FloatArray): FloatArray {
        val dst = FloatArray(src.size)
        matrix.mapPoints(dst, src)
        return dst
    }

    fun getMappedPoints(dst: FloatArray, src: FloatArray) {
        matrix.mapPoints(dst, src)
    }

    fun getCenterPoint(dst: PointF) {
        dst[width * 1f / 2] = height * 1f / 2
    }

    fun getMappedCenterPoint(dst: PointF, mappedPoints: FloatArray, src: FloatArray) {
        getCenterPoint(dst)
        src[0] = dst.x
        src[1] = dst.y
        getMappedPoints(mappedPoints, src)
        dst[mappedPoints[0]] = mappedPoints[1]
    }

    fun contains(x: Float, y: Float): Boolean {
        return contains(floatArrayOf(x, y))
    }

    private fun getBoundPoints(): FloatArray {
        val points = FloatArray(8)
        getBoundPoints(points)
        return points
    }

    private fun getBound(dst: RectF) {
        dst[0f, 0f, width.toFloat()] = height.toFloat()
    }

    private fun getMappedBound(dst: RectF, bound: RectF) {
        matrix.mapRect(dst, bound)
    }

    private fun getMatrixScale(matrix: Matrix): Float {
        return sqrt(getMatrixValue(matrix, Matrix.MSCALE_X).toDouble().pow(2.0) + getMatrixValue(matrix, Matrix.MSKEW_Y).toDouble().pow(2.0)).toFloat()
    }

    private fun getMatrixAngle(matrix: Matrix): Float {
        return Math.toDegrees(-atan2(getMatrixValue(matrix, Matrix.MSKEW_X).toDouble(), getMatrixValue(matrix, Matrix.MSCALE_X).toDouble())).toFloat()
    }

    private fun getMatrixValue(matrix: Matrix, @IntRange(from = 0, to = 9) valueIndex: Int): Float {
        matrix.getValues(valuesOfMatrix)
        return valuesOfMatrix[valueIndex]
    }

    operator fun contains(point: FloatArray): Boolean {
        val tempMatrix = Matrix()
        tempMatrix.setRotate(-currentAngle)
        getBoundPoints(boundPoints)
        getMappedPoints(boundMap, boundPoints)
        tempMatrix.mapPoints(wrapperCorner, boundMap)
        tempMatrix.mapPoints(this.point, point)
        StickerUtils.toRect(trappedRect, wrapperCorner)
        return trappedRect.contains(this.point[0], this.point[1])
    }

    open fun release() {}
}
