package com.makeeasy.rozfun.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.custom.PielView.PieRotateListener

class LuckyWheelView : RelativeLayout, PieRotateListener {

    private var mBackgroundColor = 0
    private var mTextColor = 0
    private var mCenterImage: Drawable? = null
    private var mCursorImage: Drawable? = null
    private var pielView: PielView? = null
    private var ivCursorView: ImageView? = null
    private var mLuckyRoundItemSelectedListener: LuckyRoundItemSelectedListener? = null
    override fun rotateDone(index: Int) {
        if (mLuckyRoundItemSelectedListener != null) {
            mLuckyRoundItemSelectedListener!!.LuckyRoundItemSelected(index)
        }
    }

    interface LuckyRoundItemSelectedListener {
        fun LuckyRoundItemSelected(index: Int)
    }

    fun setLuckyRoundItemSelectedListener(listener: LuckyRoundItemSelectedListener?) {
        mLuckyRoundItemSelectedListener = listener
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(ctx: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView)
            mBackgroundColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwBackgroundColor, -0x340000)
            mTextColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwTextColor, -0x1)
            mCursorImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCursor)
            mCenterImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCenterImage)
            typedArray.recycle()
        }
        val inflater = LayoutInflater.from(context)
        val frameLayout = inflater.inflate(R.layout.wheel, this, false) as FrameLayout
        pielView = frameLayout.findViewById<View>(R.id.pieView) as PielView
        ivCursorView = frameLayout.findViewById<View>(R.id.cursorView) as ImageView
        pielView!!.setPieRotateListener(this)
        pielView!!.setPieBackgroundColor(mBackgroundColor)
        pielView!!.setPieCenterImage(mCenterImage)
        pielView!!.setPieTextColor(mTextColor)
        ivCursorView!!.elevation = 15f
        //ivCursorView.setImageDrawable(mCursorImage);
        addView(frameLayout)
    }

    fun setLuckyWheelBackgrouldColor(color: Int) {
        pielView!!.setPieBackgroundColor(color)
    }

    fun setLuckyWheelCursorImage(drawable: Int) {
        ivCursorView!!.setBackgroundResource(drawable)
    }

    fun setLuckyWheelCenterImage(drawable: Drawable?) {
        pielView!!.setPieCenterImage(drawable)
    }

    fun setLuckyWheelTextColor(color: Int) {
        pielView!!.setPieTextColor(color)
    }

    fun setData(data: List<LuckyItem>?) {
        pielView!!.setData(data)
    }

    fun setRound(numberOfRound: Int) {
        pielView!!.setRound(numberOfRound)
    }

    fun startLuckyWheelWithTargetIndex(index: Int) {
        pielView!!.rotateTo(index)
    }
}