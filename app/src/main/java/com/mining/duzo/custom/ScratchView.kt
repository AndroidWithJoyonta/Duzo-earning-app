package com.mining.duzo.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mining.duzo.R
import kotlin.random.Random

class ScratchView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val overlayPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }

    private val clearPath = Path()
    private val clearPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeWidth = 100f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var scratchBitmap: Bitmap? = null
    private var scratchCanvas: Canvas? = null
    private var isFirstScratch = true // Flag for detecting the first scratch
    private var onScratchStartListener: (() -> Unit)? = null
    private var onScratchCompleteListener: (() -> Unit)? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        scratchCanvas = Canvas(scratchBitmap!!)
        scratchCanvas?.drawRect(0f, 0f, w.toFloat(), h.toFloat(), overlayPaint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        scratchBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (isFirstScratch) {
                    isFirstScratch = false
                    onScratchStartListener?.invoke() // Notify the first scratch
                }
                clearPath.lineTo(event.x, event.y)
                scratchCanvas?.drawPath(clearPath, clearPaint)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                clearPath.reset()
                checkScratchCompletion()
            }
        }
        return true
    }

    private fun checkScratchCompletion() {
        val scratchedPixels = IntArray(width * height)
        scratchBitmap?.getPixels(scratchedPixels, 0, width, 0, 0, width, height)
        val totalPixels = scratchedPixels.size
        val clearedPixels = scratchedPixels.count { it == 0 }

        if (clearedPixels > totalPixels * 0.5) {
            onScratchCompleteListener?.invoke() // Notify completion
        }
    }

    fun setOnScratchStartListener(listener: () -> Unit) {
        onScratchStartListener = listener
    }

    fun setOnScratchCompleteListener(listener: () -> Unit) {
        onScratchCompleteListener = listener
    }

    fun resetScratch() {
        scratchBitmap?.eraseColor(Color.TRANSPARENT) // Clear the bitmap
        scratchCanvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint) // Redraw overlay
        clearPath.reset() // Reset the scratch path
        isFirstScratch = true // Reset the scratch flag
        invalidate() // Redraw the view
    }
}