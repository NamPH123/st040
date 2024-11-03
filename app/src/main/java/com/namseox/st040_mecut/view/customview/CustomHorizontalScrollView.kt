package com.namseox.st040_mecut.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class CustomHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    var isScrollEnabled = true  // Biến để bật/tắt cuộn

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (isScrollEnabled) {
            super.onTouchEvent(ev)  // Cho phép cuộn
        } else {
            false  // Vô hiệu hóa cuộn
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (isScrollEnabled) {
            super.onInterceptTouchEvent(ev)  // Cho phép cuộn
        } else {
            false  // Vô hiệu hóa cuộn
        }
    }

    // Phương thức để bật hoặc tắt cuộn
    fun setScrollingEnabled(enabled: Boolean) {
        isScrollEnabled = enabled
    }
}
