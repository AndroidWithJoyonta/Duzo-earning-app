package com.makeeasy.rozfun.custom

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.cos
import kotlin.math.sin

class PielView : View {

    private var mRange = RectF()
    private var mRadius = 0
    private var mArcPaint: Paint? = null
    private var mBackgroundPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private val mStartAngle = 0f
    private var mCenter = 0
    private var mPadding = 0
    private var mTargetIndex = 0
    private var mRoundOfNumber = 4
    private var isRunning = false
    private var defaultBackgroundColor = -1
    private var drawableCenterImage: Drawable? = null
    private var textColor = -0x1
    private var mLuckyItemList: List<LuckyItem>? = null
    private var mPieRotateListener: PieRotateListener? = null

    interface PieRotateListener {
        fun rotateDone(index: Int)
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        elevation = 10f
    }

    fun setPieRotateListener(listener: PieRotateListener?) {
        mPieRotateListener = listener
    }

    private fun init() {
        mArcPaint = Paint()
        mArcPaint!!.isAntiAlias = true
        mArcPaint!!.isDither = true
        mTextPaint = Paint()
        mTextPaint!!.color = textColor
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 20f,
            resources.displayMetrics
        )
        mTextPaint!!.isFakeBoldText = true
        mRange = RectF(
            mPadding.toFloat(),
            mPadding.toFloat(),
            (mPadding + mRadius).toFloat(),
            (mPadding + mRadius).toFloat()
        )
    }

    fun setData(luckyItemList: List<LuckyItem>?) {
        mLuckyItemList = luckyItemList
        invalidate()
    }

    fun setPieBackgroundColor(color: Int) {
        defaultBackgroundColor = color
        invalidate()
    }

    fun setPieCenterImage(drawable: Drawable?) {
        drawableCenterImage = drawable
        invalidate()
    }

    fun setPieTextColor(color: Int) {
        textColor = color
        invalidate()
    }

    private fun drawPieBackgroundWithBitmap(canvas: Canvas, bitmap: Bitmap) {
        canvas.drawBitmap(
            bitmap, null, Rect(
                mPadding / 2, mPadding / 2,
                measuredWidth - mPadding / 2, measuredHeight - mPadding / 2
            ), null
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mLuckyItemList == null) {
            return
        }
        drawBackgroundColor(canvas, defaultBackgroundColor)
        init()
        var tmpAngle = mStartAngle
        val sweepAngle = 360f / mLuckyItemList!!.size
        for (i in mLuckyItemList!!.indices) {
            mArcPaint!!.color = mLuckyItemList!![i].color
            canvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint!!)
            drawText(canvas, tmpAngle, sweepAngle, mLuckyItemList!![i].text)
            drawImage(
                canvas,
                tmpAngle,
                BitmapFactory.decodeResource(resources, mLuckyItemList!![i].icon)
            )
            tmpAngle += sweepAngle
        }
        drawCenterImage(canvas, drawableCenterImage)
    }

    private fun drawBackgroundColor(canvas: Canvas, color: Int) {
        if (color == -1) {
            return
        }
        mBackgroundPaint = Paint()
        mBackgroundPaint!!.color = color
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            mCenter.toFloat(),
            mBackgroundPaint!!
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth.coerceAtMost(measuredHeight)
        mPadding = if (paddingLeft == 0) 10 else paddingLeft
        mRadius = width - mPadding * 2
        mCenter = width / 2
        setMeasuredDimension(width, width)
    }

    private fun drawImage(canvas: Canvas, tmpAngle: Float, bitmap: Bitmap) {
        val imgWidth = mRadius / mLuckyItemList!!.size
        val angle = ((tmpAngle + 360 / mLuckyItemList!!.size / 2) * Math.PI / 180).toFloat()
        val x = (mCenter + mRadius / 2 / 2 * cos(angle.toDouble())).toInt()
        val y = (mCenter + mRadius / 2 / 2 * sin(angle.toDouble())).toInt()
        val rect = Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2)
        canvas.drawBitmap(bitmap, null, rect, null)
    }

    private fun drawCenterImage(canvas: Canvas, drawable: Drawable?) {
        var bitmap = LuckyWheelUtils.drawableToBitmap(drawable!!)
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false)
        val left = measuredWidth / 2 - bitmap.width / 2
        val top = measuredHeight / 2 - bitmap.height / 2
        canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), null)
    }

    private fun drawText(canvas: Canvas, tmpAngle: Float, sweepAngle: Float, mStr: String) {
        val path = Path()
        path.addArc(mRange, tmpAngle, sweepAngle)
        val textWidth = mTextPaint!!.measureText(mStr)
        val hOffset = (mRadius * Math.PI / mLuckyItemList!!.size / 2 - textWidth / 2).toInt()
        val vOffset = mRadius / 2 / 4
        canvas.drawTextOnPath(mStr, path, hOffset.toFloat(), vOffset.toFloat(), mTextPaint!!)
    }

    private val angleOfIndexTarget: Float
        get() {
            val tempIndex = if (mTargetIndex == 0) 1 else mTargetIndex
            return (360 / mLuckyItemList!!.size).toFloat() * tempIndex
        }

    fun setRound(numberOfRound: Int) {
        mRoundOfNumber = numberOfRound
    }

    fun rotateTo(index: Int) {
        if (isRunning) {
            return
        }
        mTargetIndex = index
        rotation = 0f
        val targetAngle =
            360 * mRoundOfNumber + 270 - angleOfIndexTarget + (360 / mLuckyItemList!!.size).toFloat() / 2
        animate().setInterpolator(DecelerateInterpolator())
            .setDuration(mRoundOfNumber * 1000L + 900L)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isRunning = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    isRunning = false
                    if (mPieRotateListener != null) {
                        mPieRotateListener!!.rotateDone(mTargetIndex)
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            .rotation(targetAngle)
            .start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }
}