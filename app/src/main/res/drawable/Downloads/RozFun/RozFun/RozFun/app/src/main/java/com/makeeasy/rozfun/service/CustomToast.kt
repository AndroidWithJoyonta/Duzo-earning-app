package com.makeeasy.rozfun.service

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.makeeasy.rozfun.R

class CustomToast : Dialog {

    constructor(ctx: Context, msg: String?, type: Int) : super(ctx) {
        setContentView(R.layout.popup)
        setCancelable(false)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val wlp = window!!.attributes
        wlp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window!!.attributes = wlp
        val handler = Handler(Looper.myLooper()!!)
        val runnable = Runnable { dismiss() }
        val message = findViewById<TextView>(R.id.txt)
        when (type) {
            GREEN -> {
                message.setBackgroundColor(Color.parseColor("#00E676"))
            }
            RED -> {
                message.setBackgroundColor(Color.parseColor("#F50057"))
            }
            else -> {
                message.setBackgroundColor(Color.parseColor("#888888"))
            }
        }
        message.text = msg
        setOnShowListener {
            handler.postDelayed(runnable,3000)
        }
    }

    constructor(ctx: Context, msg: String?) : super(ctx) {
        setContentView(R.layout.popup)
        setCancelable(false)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val wlp = window!!.attributes
        wlp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window!!.attributes = wlp
        val handler = Handler(Looper.myLooper()!!)
        val runnable = Runnable { dismiss() }
        val message = findViewById<TextView>(R.id.txt)
        message.setBackgroundColor(Color.GRAY)
        message.text = msg
        setOnShowListener {
            handler.postDelayed(runnable, 3000)
        }
    }

    companion object {
        var GREEN = 0
        var RED = 1
    }
}