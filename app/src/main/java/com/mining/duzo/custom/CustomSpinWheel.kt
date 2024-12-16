package com.mining.duzo.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CustomSpinWheel @JvmOverloads constructor(


    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }
    private var wheelColors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA)
    private var labels = listOf("1$", "0.00003$", "0.0003$", "0.00$", "0.003")
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        rect.set(0f, 0f, width, height)

        val sweepAngle = 360f / labels.size

        for (i in labels.indices) {
            paint.color = wheelColors[i % wheelColors.size]
            canvas.drawArc(rect, i * sweepAngle, sweepAngle, true, paint)

            // Draw text
            val angle = Math.toRadians((i * sweepAngle + sweepAngle / 2).toDouble())
            val textX = (width / 2 + Math.cos(angle) * width / 4).toFloat()
            val textY = (height / 2 + Math.sin(angle) * height / 4).toFloat()
            canvas.drawText(labels[i], textX, textY, textPaint)
        }
    } }