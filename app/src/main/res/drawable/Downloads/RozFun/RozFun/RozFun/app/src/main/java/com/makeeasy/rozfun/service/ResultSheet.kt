package com.makeeasy.rozfun.service

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.makeeasy.rozfun.R

class ResultSheet : BottomSheetDialogFragment() {

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_result, container, false)
        setStyle(STYLE_NORMAL, R.style.CustomSheet)
        val img = v.findViewById<View>(R.id.img)
        val title = v.findViewById<TextView>(R.id.title)
        if(tag == "win") {
            img.setBackgroundResource(R.drawable.happy)
            title.text = "Wow! You Won\nReward Claimed Successfully"
        } else {
            img.setBackgroundResource(R.drawable.sad)
            title.text = "Opps! You Loose\nBetter Luck Next Time"
        }
        v.findViewById<CardView>(R.id.button).setOnClickListener {
            dismiss()
        }
        isCancelable = false
        return v
    }
}