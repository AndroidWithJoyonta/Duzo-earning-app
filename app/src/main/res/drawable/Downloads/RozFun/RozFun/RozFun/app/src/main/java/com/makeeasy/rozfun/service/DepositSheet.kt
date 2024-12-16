package com.makeeasy.rozfun.service

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.makeeasy.rozfun.R

class DepositSheet(private val ctx: Activity,private val onContinueListener: OnContinueListener) : BottomSheetDialogFragment() {

    interface OnContinueListener {
        fun onContinue(amt:String,gateway:Int)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_deposit,container,false)
        setStyle(STYLE_NORMAL, R.style.CustomSheet)
        val amount = v.findViewById<EditText>(R.id.am)
        val minimum = v.findViewById<TextView>(R.id.minimum)
        minimum.text = "Minimum deposit is ${Constant.minimumAdd}"
        v.findViewById<View>(R.id.r20).setOnClickListener { amount.setText("20") }
        v.findViewById<View>(R.id.r50).setOnClickListener { amount.setText("50") }
        v.findViewById<View>(R.id.r100).setOnClickListener { amount.setText("100") }
        v.findViewById<View>(R.id.r200).setOnClickListener { amount.setText("200") }
        v.findViewById<View>(R.id.r500).setOnClickListener { amount.setText("500") }
        val title = v.findViewById<TextView>(R.id.title)
        title.text =tag
        val spinner = v.findViewById<Spinner>(R.id.spinner)
        val cates: MutableList<String> = ArrayList()
        if(Constant.razorpayID != "") {
            cates.add("Razorpay Gateway")
        }
        if(Constant.paytmMID != "") {
            cates.add("Paytm Gateway")
        }
        if(Constant.cashfreeID != "") {
            cates.add("Cashfree Gateway")
        }
        val dataAdapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, cates)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter
        v.findViewById<View>(R.id.button).setOnClickListener {
            if (amount.text.toString() != "") {
                if(amount.text.toString().toInt() < Constant.minimumAdd.toInt()) {
                    Toast.makeText(ctx,"Minimum amount ${Constant.minimumAdd}!",Toast.LENGTH_SHORT).show()
                } else {
                    dismiss()
                    val gateway = if(spinner.selectedItem.toString() == "Razorpay Gateway") {
                        RAZORPAY
                    } else if(spinner.selectedItem.toString() == "Paytm Gateway") {
                        PAYTM
                    } else {
                        CASHFREE
                    }
                    onContinueListener.onContinue(amount.text.toString(),gateway)
                }
            } else {
                Toast.makeText(ctx,"Fill amount!",Toast.LENGTH_SHORT).show()
            }
        }
        isCancelable = true
        return v
    }

    companion object {
        val RAZORPAY = 0
        val PAYTM = 1
        val CASHFREE = 2
    }
}