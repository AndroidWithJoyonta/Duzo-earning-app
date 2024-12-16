package com.makeeasy.rozfun.service

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.makeeasy.rozfun.R
import com.makeeasy.rozfun.model.User
import org.json.JSONArray
import org.json.JSONException
import kotlin.random.Random

class WithdrawSheet(private val ctx: Activity,private val onRequestCompleteListener: OnRequestCompleteListener) : BottomSheetDialogFragment() {

    private var balanceAdjust = false
    private var requestSubmit = false
    interface OnRequestCompleteListener {
        fun onComplete()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.sheet_withdraw, container, false)
        setStyle(STYLE_NORMAL,R.style.CustomSheet)
        val title = v.findViewById<TextView>(R.id.title)
        title.text =tag
        val minimum = v.findViewById<TextView>(R.id.minimum)
        val group = v.findViewById<RadioGroup>(R.id.group)
        val amount = v.findViewById<EditText>(R.id.nn)
        val upiId = v.findViewById<EditText>(R.id.tt)
        val ifsc = v.findViewById<EditText>(R.id.ifsc)
        val holder = v.findViewById<EditText>(R.id.holder)
        minimum.text = "Minimum withdraw is ${Constant.minimumRedeem}"
        group.setOnCheckedChangeListener { radioGroup: RadioGroup, _: Int ->
            when (radioGroup.checkedRadioButtonId) {
                R.id.paytm -> {
                    upiId.hint = getString(R.string.paytm_number)
                    ifsc.visibility = View.GONE
                    holder.visibility = View.GONE
                }
                R.id.upi -> {
                    upiId.hint = getString(R.string.upi_or_vpa)
                    ifsc.visibility = View.GONE
                    holder.visibility = View.GONE
                }
                R.id.bank -> {
                    upiId.hint = getString(R.string.bank_a_c_number)
                    ifsc.visibility = View.VISIBLE
                    holder.visibility = View.VISIBLE
                }
            }
        }
        v.findViewById<LinearLayout>(R.id.button).setOnClickListener {
            if (group.checkedRadioButtonId == R.id.bank) {
                if(amount.text.toString() != "" && upiId.text.toString() != "" && ifsc.text.toString() != "" && holder.text.toString() != "") {
                    if(amount.text.toString().toInt() < Constant.minimumRedeem.toInt()) {
                        Toast.makeText(ctx,"Min redeem ${Constant.minimumRedeem}!",Toast.LENGTH_SHORT).show()
                    } else if(amount.text.toString().toInt() > Constant.user.redeem.toInt()) {
                        Toast.makeText(ctx,"Not enough balance!",Toast.LENGTH_SHORT).show()
                    } else {
                        val data = "${holder.text}, ${upiId.text}, ${ifsc.text}"
                        val rest = Constant.user.redeem.toInt() - amount.text.toString().toInt()
                        v.findViewById<ProgressBar>(R.id.process).visibility = View.VISIBLE
                        updateUsers(Constant.user.point,rest.toString(),Constant.user.status)
                        addTransaction(amount.text.toString(),data)
                    }
                } else {
                    Toast.makeText(ctx,"Fill all fields!", Toast.LENGTH_SHORT).show()
                }
            } else {
                if(amount.text.toString() != "" && upiId.text.toString() != "") {
                    if(amount.text.toString().toInt() < Constant.minimumRedeem.toInt()) {
                        Toast.makeText(ctx,"Min redeem ${Constant.minimumRedeem}!",Toast.LENGTH_SHORT).show()
                    } else if(amount.text.toString().toInt() > Constant.user.redeem.toInt()) {
                        Toast.makeText(ctx,"Not enough balance!",Toast.LENGTH_SHORT).show()
                    } else {
                        val data = upiId.text.toString()
                        val rest = Constant.user.redeem.toInt() - amount.text.toString().toInt()
                        v.findViewById<ProgressBar>(R.id.process).visibility = View.VISIBLE
                        updateUsers(Constant.user.point,rest.toString(),Constant.user.status)
                        addTransaction(amount.text.toString(),data)
                    }
                } else {
                    Toast.makeText(ctx,"Fill all fields!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        isCancelable = true
        return v
    }

    private fun updateUsers(de: String,wi: String,st: String) {
        Volley.newRequestQueue(ctx).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    balanceAdjust = true
                    if(balanceAdjust && requestSubmit) {
                        dismiss()
                        Constant.user = User(Constant.user.id,Constant.user.name,Constant.user.device,
                            de,wi,Constant.user.referBy, Constant.user.referCode,Constant.user.status)
                        onRequestCompleteListener.onComplete()
                    }
                } else {
                    dismiss()
                    CustomToast(ctx,"Error!",CustomToast.RED).show()
                }
            } catch (e: JSONException) {
                dismiss()
                CustomToast(ctx,"Data fetch error!",CustomToast.RED).show()
            }
        },Response.ErrorListener {
            dismiss()
            CustomToast(ctx,"Server error!",CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "updateusr"
                params["id"] = Constant.user.id
                params["depo"] = de
                params["with"] = wi
                params["status"] = st
                return params
            }
        })
    }

    private fun addTransaction(amt: String,des: String) {
        val ord = "odr_${Random.nextInt(999999)}"
        val tra = "txn_${Random.nextInt(999999)}"
        Volley.newRequestQueue(ctx).add(object : StringRequest(Method.POST,API().getBase(),Response.Listener { response ->
            try {
                val resp = JSONArray(response)
                val obj = resp.getJSONObject(0)
                if(obj.getString("success") == "1") {
                    requestSubmit = true
                    if(balanceAdjust && requestSubmit) {
                        dismiss()
                        onRequestCompleteListener.onComplete()
                    }
                } else {
                    dismiss()
                    CustomToast(ctx,"Error!",CustomToast.RED).show()
                }
            } catch (e: JSONException) {
                dismiss()
                CustomToast(ctx,e.message,CustomToast.RED).show()
            }
        },Response.ErrorListener { error ->
            dismiss()
            CustomToast(ctx,error.message,CustomToast.RED).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["method"] = "addtxn"
                params["amo"] = amt
                params["typ"] = "1"
                params["dev"] = Constant.user.device
                params["des"] = des
                params["ord"] = ord
                params["tra"] = tra
                params["sta"] = "0"
                return params
            }
        })
    }
}